package pt.iscte.dcti.dynamic_uml.aspects;

import java.util.HashMap;

import pt.iscte.dcti.dynamic_uml.view.SequenceDiagramView;
import pt.iscte.dcti.instrumentation.aspects.AbstractTracer;

public privileged aspect TracerAddon extends AbstractTracer {
	
	private final boolean DISPLAY_CONSOLE_MESSAGES = true;
	private final int SYSTEM_OBJECT_NUMBER_ID = -1;
	
	private SequenceDiagramView sequence_diagram_view;
	private HashMap<Integer, Integer> hash_map;
	
	private int id_system_in = System.identityHashCode(System.in);
	private int id_system_out = System.identityHashCode(System.out);
	private int id_system_err = System.identityHashCode(System.err);
	
	public TracerAddon() {
		sequence_diagram_view = new SequenceDiagramView();
		hash_map = new HashMap();
	}
	
	//POINTCUTs
	public pointcut codeInsideMyProject() : within(pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..* || pt.iscte.dcti.dynamic_uml.*..*) || cflow(execution(String *.toString()));
	
	//public pointcut mainCall() : execution(public static void *..main(..));

	private pointcut constructorCall() : call(*.new(..)) && !codeInsideMyProject();
	private pointcut constructorExecution() : execution(*.new(..)) && !codeInsideMyProject();
	
	private pointcut finalizeCall() : call(void *.finalize(..)) && !codeInsideMyProject();
    
	//Page 114
	//Page 131
	//Page 191 (238) from Manning
	public static interface HandledFinalize {
		 public void finalize();
	}
	public void HandledFinalize.finalize() throws Throwable {
		super.finalize();
	}
		
	/*
	Object around() : mainCall() {
		int id_this = System.identityHashCode(thisJoinPoint.getThis());//Main ID
		System.out.println(id_this);
		System.out.println("Caught a main!");
		return new Object();
	}
	*/
	
	//--Catch Constructor Calls--
	Object around() : constructorCall() {
		// TODO How to avoid this being applied when there is a this?
		Object object = proceed();
		int id_object = System.identityHashCode(object);
		
		int id_creator = System.identityHashCode(thisJoinPoint.getThis());
		int diagram_id_creator;
		if(id_creator == 0) //The hash code for the null reference is 0
			diagram_id_creator = SYSTEM_OBJECT_NUMBER_ID;
		else
			diagram_id_creator = hash_map.get(id_creator);
		//System.out.println("id: "+id_object);
		//System.out.println("diagram_id_creator: "+diagram_id_creator);
		
		String object_class_name = object.getClass().getName();
		Class class_type = object.getClass();
		
		if(!(object instanceof Enum<?>)) {
			if(DISPLAY_CONSOLE_MESSAGES)
				System.out.println("--Constructor Call-- "+object.toString());
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, diagram_id_creator);
			hash_map.put(System.identityHashCode(object), id);
		}
		
		return object;
	}
	
	
	after() : constructorExecution() {
		Object object = thisJoinPoint.getThis();
		int id = System.identityHashCode(object);
		
		//String object_class_name = object.getClass().getName();
		//Class class_type = object.getClass();
		
		//System.out.println("A Constructor of object "+object+" was executed, and his id is "+id);
		if(!hash_map.containsKey(id)) {
			//System.out.println("Last mentioned id was added to the hashmap");
			//sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, SYSTEM_OBJECT_NUMBER_ID);
			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), SYSTEM_OBJECT_NUMBER_ID);
		}
	}

	//--Catch Finalize Calls--
	//TODO Remove usage of package (examples..*)
	declare parents: !(!(examples..*)||hasmethod(public void finalize()) || Enum+ ) implements HandledFinalize;
	
	void around() : execution(public void *.finalize()) {
		proceed();
		int id = System.identityHashCode(thisJoinPoint.getThis());
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--Finalize Call--");
			System.out.println("The object: "+thisJoinPoint.getThis()+"  with squence id: "+hash_map.get(id)+" is going to be destroyed.");
		}
		sequence_diagram_view.killSequenceDiagramObject(hash_map.get(id));
		hash_map.remove(id);
	}
	
	//--Catch Generic Function and Method Calls--
	//TODO Find a way to get the system object ids and the system calls through pointcuts...?
	private pointcut methodCalls() : call(void *..*(..)) && !codeInsideMyProject();
	private pointcut functionCalls() : call(* *..*(..)) && !methodCalls() && !codeInsideMyProject();
	
	void around() : methodCalls() { //&& !within(java.lang.*) { //&& !execution(* *..System..*(..)) { //&& !cflow(execution(* *..System..*(..))) { //!withincode(* *..System..*(..)) {
		//System.out.println("METHOD DEBUG POINT");
		
		//System IDs
		int id_this = System.identityHashCode(thisJoinPoint.getThis());
		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
		//System.out.println("Method - System IDs - id_this = "+id_this+" id_target = "+id_target);
		//System.out.println("Method - Hashmap<System.code, id> , and the get returned-> this: "+hash_map.get(id_this)+" , target: "+hash_map.get(id_target));
		//System.out.println("Hashmap: "+hash_map);
		//Special case, for calls within an object construction
		//System.out.println("id_this = "+id_this);
		//System.out.println("hash_map.get(id_this) = "+hash_map.get(id_this));
		
		//If object is not a system object and is not in the hashmap... we need to add it earlier than it naturally would!
		if(id_this != 0 && hash_map.get(id_this) == null)
		{
			Object object = thisJoinPoint.getThis();
			
			int id_creator = System.identityHashCode(thisJoinPoint.getThis());
			
			int diagram_id_creator;
			if(id_creator == 0) //The hash code for the null reference is 0
				diagram_id_creator = SYSTEM_OBJECT_NUMBER_ID;
			else
				diagram_id_creator = id_creator;
			
			String object_class_name = object.getClass().getName();
			
			//System.out.println("(Method) Creating sequence diagram object in special conditions, for: "+object.toString()+" from class: "+object_class_name+" diagram id: "+diagram_id_creator);
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, diagram_id_creator);
			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), id);
		}
		//Diagram IDs
		int diagram_id_this;
		if(id_this == 0 || id_this == id_system_in || id_this == id_system_out || id_this == id_system_err) //No object associated... static main method...
			diagram_id_this = -1; //Value for the static main class ID
		else
			diagram_id_this = hash_map.get(id_this);
		//System.out.println("diagram_id_this = "+diagram_id_this);
		int diagram_id_target;
		if(id_target == 0 || id_target == id_system_in || id_target == id_system_out || id_target == id_system_err)
			diagram_id_target = -1;
		else
			diagram_id_target = hash_map.get(id_target);
		//System.out.println("diagram_id_target = "+diagram_id_target);
		
		//System.out.println("Corrected Method - Hashmap<System.code, id> -> this: "+diagram_id_this+" , target: "+diagram_id_target);
		
		//Create Call in Sequence Diagram
		int method_call = 0;
		boolean id_is_not_from_system = (diagram_id_this != SYSTEM_OBJECT_NUMBER_ID || diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
		if(id_is_not_from_system) {
			//System.out.println("----CREATING NEW METHOD CALL IN DIAGRAM----");
			//System.out.println("I will call .createCall("+thisJoinPoint.getSignature().getName()+", "+diagram_id_this+", "+diagram_id_target+")");
			method_call = sequence_diagram_view.createCall(thisJoinPoint.getSignature().getName(), diagram_id_this, diagram_id_target);
			//System.out.println("-FINISHED-");
		} 
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--METHOD CALL--");
			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called method: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
		}
		proceed();
		//Create Return in Sequence Diagram
		if(id_is_not_from_system)
			sequence_diagram_view.createReturn(method_call);
		
		//System.out.println("METHOD DEBUG POINT END");
	}
	
	Object around() : functionCalls() {
		//System.out.println("FUNCTION DEBUG POINT");
		
		//System IDs
		int id_this = System.identityHashCode(thisJoinPoint.getThis());
		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
		//System.out.println("Function - System IDs - id_this = "+id_this+" id_target = "+id_target);
		//System.out.println("Function - Hashmap<System.code, id> , and the get returned-> this: "+hash_map.get(id_this)+" , target: "+hash_map.get(id_target));
		//System.out.println("Hashmap: "+hash_map);
		
		//If object is not a system object and is not in the hashmap... we need to add it earlier than it naturally would!
		if(id_this != 0 && hash_map.get(id_this) == null)
		{
			Object object = thisJoinPoint.getThis();
			
			int id_creator = System.identityHashCode(thisJoinPoint.getThis());
			
			int diagram_id_creator;
			if(id_creator == 0) //The hash code for the null reference is 0
				diagram_id_creator = SYSTEM_OBJECT_NUMBER_ID;
			else
				diagram_id_creator = id_creator;
			
			String object_class_name = object.getClass().getName();
			
			//System.out.println("(Function) Creating sequence diagram object in special conditions, for: "+object.toString()+" from class: "+object_class_name+" diagram id: "+diagram_id_creator);
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, diagram_id_creator);
			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), id);
		}
		
		//Diagram IDs
		int diagram_id_this;
		if(id_this == 0 || id_this == id_system_in || id_this == id_system_out || id_this == id_system_err) //No object associated... static main method... or System object
			diagram_id_this = -1; //Value for the static main class ID
		else
			diagram_id_this = hash_map.get(id_this);
		//System.out.println(diagram_id_this);
		int diagram_id_target;
		if(id_target == 0 || id_target == id_system_in || id_target == id_system_out || id_target == id_system_err)
			diagram_id_target = -1;
		else
			diagram_id_target = hash_map.get(id_target);
		//System.out.println(diagram_id_target);
		
		//System.out.println("Corrected Function - Hashmap<System.code, id> -> this: "+diagram_id_this+" , target: "+diagram_id_target);
		
		//Create Call in Sequence Diagram
		int function_call = 0;
		boolean id_is_not_from_system = (diagram_id_this != SYSTEM_OBJECT_NUMBER_ID || diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
		if(id_is_not_from_system) {
			//System.out.println("----CREATING NEW FUNCTION CALL IN DIAGRAM----");
			//System.out.println("I will call .createCall("+thisJoinPoint.getSignature().getName()+", "+diagram_id_this+", "+diagram_id_target+")");
			function_call = sequence_diagram_view.createCall(thisJoinPoint.getSignature().getName(), diagram_id_this, diagram_id_target);	
			//System.out.println("-FINISHED-");
		}
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--FUNCTION CALL--");
			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called function: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
		}
		Object object = proceed();
		//Create Return in Sequence Diagram
		if(id_is_not_from_system)
			sequence_diagram_view.createReturn(function_call);
		//System.out.println("FUNCTION DEBUG POINT END");
		return object;
	}
	
}
