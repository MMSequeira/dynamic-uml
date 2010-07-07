package pt.iscte.dcti.dynamic_uml.aspects;

import java.util.HashMap;
import java.util.Stack;

import pt.iscte.dcti.dynamic_uml.view.SequenceDiagramView;
import pt.iscte.dcti.instrumentation.aspects.AbstractTracer;

public privileged aspect TracerAddon extends AbstractTracer {
	
	private final boolean DISPLAY_CONSOLE_MESSAGES = false;
	private final boolean DISPLAY_INTERNAL_DEBUG_MESSAGES = false;
	private final int SYSTEM_OBJECT_NUMBER_ID = -1;
	
	private SequenceDiagramView sequence_diagram_view;
	//HashMap<System.identityHashCode, id from the sequence diagram view>
	private HashMap<Integer, Integer> hash_map;
	//HashMap<System.identityHashCode object (AFTER proceed), System.identityHashCode caller>
	private HashMap<Integer, Integer> hash_object_caller;
	
	
	private Stack<Integer> stack_constructor_callers;
	//private HashMap<Object, Object> hash_object_caller_objects;
	
	private int id_system_in = System.identityHashCode(System.in);
	private int id_system_out = System.identityHashCode(System.out);
	private int id_system_err = System.identityHashCode(System.err);
	
	//private Object caller = null;
	
	public TracerAddon() {
		sequence_diagram_view = new SequenceDiagramView();
		hash_map = new HashMap<Integer, Integer>();
		hash_object_caller = new HashMap<Integer, Integer>();
		stack_constructor_callers = new Stack<Integer>();
		//hash_object_caller_objects = new HashMap<Object, Object>();
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
	// thisJoinPoint.getThis() - The object calling the function (creating)
	// id - The object id (after it has been partially been created (proceed)
	// thisJoinPoint.getTarget() - The object whose function is being called (typically null here, because the object doesn't exist before constructor is complete)
	// TODO How to avoid this being applied when there is a this?
	Object around() : constructorCall() {
		printInternalDebugLine("$$$ Constructor Call "+thisJoinPoint.getThis()+"$$$");
		Object caller = thisJoinPoint.getThis();
		int id_caller = System.identityHashCode(caller);
		Object object = proceed();
		int id_object = System.identityHashCode(object);
		hash_object_caller.put(id_object, id_caller);
		
		int diagram_id_caller;
		if(id_caller == 0) //The hash code for the null reference is 0
			diagram_id_caller = SYSTEM_OBJECT_NUMBER_ID;
		else
			diagram_id_caller = hash_map.get(id_caller);
		
		
		printInternalDebugLine("id_caller: "+id_caller+" id_object: "+id_object);
		printInternalDebugLine("diagram_id_caller: "+diagram_id_caller);
		
		
		//System.out.println("construct - diagram_id_creator: "+diagram_id_creator);
		
		//printInternalDebugLine("diagram_id_creator: "+diagram_id_creator);
		
		String object_class_name = object.getClass().getName();
		//Class class_type = object.getClass();
		
		if(!(object instanceof Enum<?>)) {
			if(DISPLAY_CONSOLE_MESSAGES)
				System.out.println("--Constructor Call-- "+object.toString());
			//System.out.println("hashMap object caller: "+hash_object_caller);
			//System.out.println("hashMap map: "+hash_map);
			
			//if(diagram_id_creator == SYSTEM_OBJECT_NUMBER_ID || sequence_diagram_view.getSequenceDiagramObject(hash_map.get(hash_post_pre.get(System.identityHashCode(object)))) == null) {//if(sequence_diagram_view.getSequenceDiagramObject(last_created_diagram_id) == null) {
			System.out.println("ID OBJECT: "+id_object);
			System.out.println("hash_map: "+hash_map);
			System.out.println("hash_object_caller: "+hash_object_caller);
			//If the object doesn't exist already in the diagram representation, we must create it's representation and lifeline.
			if(sequence_diagram_view.getSequenceDiagramObject(id_object) == null && !hash_map.containsKey(id_object)) {
				int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, diagram_id_caller);
				hash_map.put(id_object, id); 
			}
		}
		printInternalDebugLine("$$$ Constructor Call "+thisJoinPoint.getThis()+"(End) $$$");
		return object;
	}
	
/*
	after() : constructorExecution() {
		Object object = thisJoinPoint.getThis();
		int id = System.identityHashCode(object);
		
		//String object_class_name = object.getClass().getName();
		//Class class_type = object.getClass();
		
		//System.out.println("A Constructor of object "+object+" was executed, and his id is "+id);
		if(!hash_map.containsKey(id)) {
			//System.out.println("Last mentioned id was added to the hashmap because it wasn't there");
			//sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, SYSTEM_OBJECT_NUMBER_ID);
			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), SYSTEM_OBJECT_NUMBER_ID);
		}
	}
*/
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
	
	void around() : methodCalls() {
		printInternalDebugLine("### Method Call -> Signature: "+thisJoinPoint.getSignature()+" This: "+thisJoinPoint.getThis()+" ID_This: "+System.identityHashCode(thisJoinPoint.getThis())+" - Target: "+thisJoinPoint.getTarget()+" ID_Target: "+System.identityHashCode(thisJoinPoint.getTarget()));

		//System IDs
		int id_this = System.identityHashCode(thisJoinPoint.getThis());
		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
		printInternalDebugLine("Method - System IDs - id_this = "+id_this+" id_target = "+id_target);
		printInternalDebugLine("Method - Hashmap<System.code, id> , and the get returned-> this: "+hash_map.get(id_this)+" , target: "+hash_map.get(id_target));
		printInternalDebugLine("Hashmap<object, caller>: "+hash_object_caller);
		printInternalDebugLine("Hashmap<sys_id, diagram_id>: "+hash_map);
		
		//If object is not a system object and is not in the hashmap<sys_id, diagram_id> we need to add it earlier than it naturally would!
		//This is where we consider we may have to be able to manage an object before it was completly constructed.
		if(id_this != 0 && !hash_map.containsKey(id_this))//hash_map.get(id_this) == null)
		{
			Object object = thisJoinPoint.getThis();
			System.out.println(object);
			
			System.out.println("***making in diagram, in special condition "+thisJoinPoint.getThis());
			
			//This is a method from the object being created...
			//If the source of the call does not exist in the diagram...
			//If the id_caller is not in the hash_map it is because it wasn't drawn graphically yet, so we must draw it.
			//The caller's caller will be assumed as -1 (System)
			//TODO CHECK THIS WITH MORE EXAMPLES (IT DOESNT WORK. WILL REQUIRE AN HASHMAP/LINKED LIST WITH PAIRS...)
			//TODO CHECK the differences between Box's main and Main from the MacroCells package... they are alike, but results are different here...
			int id_caller = id_this;
			int id_callers_caller;
			if(stack_constructor_callers.empty())
				id_callers_caller = SYSTEM_OBJECT_NUMBER_ID;
			else {
				id_callers_caller = stack_constructor_callers.peek();
				id_callers_caller = hash_map.get(id_callers_caller); 
			}
			String object_class_name = object.getClass().getName();
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, id_callers_caller);
			hash_map.put(id_caller, id);
			stack_constructor_callers.push(id_caller);
			
//			System.out.println("-- Id this: "+id_this);
//			System.out.println("hash_map: "+hash_map);
//			System.out.println("hash_object_caller: "+hash_object_caller);
//			System.out.println("--");
			
			//if(thisJoinPoint.getThis().equals(thisJoinPoint.getTarget()))
				//id_caller = 
			//int id_caller = hash_object_caller.get(object);
			
		
			/*
			
			int id_caller_in_diagram = sequence_diagram_view.createSequenceDiagramObject(thisJoinPoint.getThis().toString(), thisJoinPoint.getThis().getClass().getName(), SYSTEM_OBJECT_NUMBER_ID);
			hash_map.put(id_caller, id_caller_in_diagram);
			*/
			/*
			int diagram_id_caller;
			if(id_caller == 0) //The hash code for the null reference is 0
				diagram_id_caller = SYSTEM_OBJECT_NUMBER_ID;
			else
				diagram_id_caller = hash_map.get(id_caller); //id_caller;
			*/
			
			
			printInternalDebugLine("(Method) Creating sequence diagram object in special conditions, for: "+object.toString()+" from class: "+object_class_name+" diagram id from creator: "+id_callers_caller);
			//int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, hash_map.get(diagram_id_caller));
			//hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), id);
		}
		//Now that the object is assuredly in the diagram and in the hash_map, we can continue processing the call
		
		//Diagram IDs
		int diagram_id_this;
		if(id_this == 0 || id_this == id_system_in || id_this == id_system_out || id_this == id_system_err) //No object associated... static main method...
			diagram_id_this = SYSTEM_OBJECT_NUMBER_ID; //Value for the static main class ID
		else
			diagram_id_this = hash_map.get(id_this);// hash_object_caller.get(id_this);//hash_map.get(id_this);
		//System.out.println("diagram_id_this = "+diagram_id_this);
		int diagram_id_target;
		if(id_target == 0 || id_target == id_system_in || id_target == id_system_out || id_target == id_system_err)
			diagram_id_target = SYSTEM_OBJECT_NUMBER_ID;
		else
			diagram_id_target = hash_map.get(id_target);//hash_object_caller.get(id_target);//hash_map.get(id_target);
		//System.out.println("diagram_id_target = "+diagram_id_target);
		
		printInternalDebugLine("Corrected Method - Hashmap<System.code, diagram_id> -> this: "+diagram_id_this+" , target: "+diagram_id_target);
		
		//Create Call in Sequence Diagram
		int method_call = 0; // We set a value just for compiler reasons
		//If the call does not target the system, then it is because it is a normal call, or a call from the system to the program.
		boolean id_is_not_from_system = (diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
		//boolean id_is_not_from_system = (diagram_id_this != SYSTEM_OBJECT_NUMBER_ID || diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
		if(id_is_not_from_system) {
			printInternalDebugLine("----CREATING NEW METHOD CALL IN DIAGRAM----");
			printInternalDebugLine("I will call .createCall("+thisJoinPoint.getSignature().getName()+", "+diagram_id_this+", "+diagram_id_target+")");
			method_call = sequence_diagram_view.createCall(thisJoinPoint.getSignature().getName(), diagram_id_this, diagram_id_target);
			printInternalDebugLine("-FINISHED METHOD CALL CREATION IN DIAGRAM-");
		} 
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--METHOD CALL--");
			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called method: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
			System.out.println("-----------------");
		}
		
		proceed();
		if(!stack_constructor_callers.empty())
			stack_constructor_callers.pop();
		//Create Return in Sequence Diagram
		if(id_is_not_from_system)
			sequence_diagram_view.createReturn(method_call);
		
		printInternalDebugLine("### Method Call Finished -> Sig: "+thisJoinPoint.getSignature()+" ###");
		//System.out.println("METHOD DEBUG POINT END");
	}
	
	
	
//	void around() : methodCalls() { //&& !within(java.lang.*) { //&& !execution(* *..System..*(..)) { //&& !cflow(execution(* *..System..*(..))) { //!withincode(* *..System..*(..)) {
//		//System.out.println("METHOD DEBUG POINT");
//		printInternalDebugLine("### Method Call -> Signature: "+thisJoinPoint.getSignature()+" This: "+thisJoinPoint.getThis()+" ID_This: "+System.identityHashCode(thisJoinPoint.getThis())+" - Target: "+thisJoinPoint.getTarget()+" ID_Target: "+System.identityHashCode(thisJoinPoint.getTarget()));
//		
//		//System IDs
//		int id_this = System.identityHashCode(thisJoinPoint.getThis());
//		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
//		printInternalDebugLine("Method - System IDs - id_this = "+id_this+" id_target = "+id_target);
//		printInternalDebugLine("Method - Hashmap<System.code, id> , and the get returned-> this: "+hash_map.get(id_this)+" , target: "+hash_map.get(id_target));
//		printInternalDebugLine("Hashmap: "+hash_map);
//		//Special case, for calls within an object construction
//		//System.out.println("id_this = "+id_this);
//		//System.out.println("hash_map.get(id_this) = "+hash_map.get(id_this));
//		
//		System.out.println("hash_map.get(id_this) = "+hash_map.get(id_this));
//		
//		//If object is not a system object and is not in the hashmap... we need to add it earlier than it naturally would!
//		if(id_this != 0 && hash_map.get(id_this) == null)
//		{
//			Object object = thisJoinPoint.getThis();
//			Object object_builder = caller;
//			
//			System.out.println("making in diagram, in special condition "+thisJoinPoint.getThis());
//			
//			//int id_creator = System.identityHashCode(thisJoinPoint.getThis());
//			int id_creator = System.identityHashCode(object_builder);
//			
//			int diagram_id_creator;
//			if(id_creator == 0) //The hash code for the null reference is 0
//				diagram_id_creator = SYSTEM_OBJECT_NUMBER_ID;
//			else
//				diagram_id_creator = id_creator;
//			
//			String object_class_name = object.getClass().getName();
//			
//			printInternalDebugLine("(Method) Creating sequence diagram object in special conditions, for: "+object.toString()+" from class: "+object_class_name+" diagram id from creator: "+diagram_id_creator);
//			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, hash_map.get(diagram_id_creator));
//			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), id);
//			//last_created_diagram_id = id;
//		}
//		//Diagram IDs
//		int diagram_id_this;
//		if(id_this == 0 || id_this == id_system_in || id_this == id_system_out || id_this == id_system_err) //No object associated... static main method...
//			diagram_id_this = -1; //Value for the static main class ID
//		else
//			diagram_id_this = hash_map.get(id_this);
//		//System.out.println("diagram_id_this = "+diagram_id_this);
//		int diagram_id_target;
//		if(id_target == 0 || id_target == id_system_in || id_target == id_system_out || id_target == id_system_err)
//			diagram_id_target = -1;
//		else
//			diagram_id_target = hash_map.get(id_target);
//		//System.out.println("diagram_id_target = "+diagram_id_target);
//		
//		printInternalDebugLine("Corrected Method - Hashmap<System.code, id> -> this: "+diagram_id_this+" , target: "+diagram_id_target);
//		
//		//Create Call in Sequence Diagram
//		int method_call = 0;
//		//If the call does not target the system, then it is because it is a normal call, or a call from the system to the program.
//		boolean id_is_not_from_system = (diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
//		//boolean id_is_not_from_system = (diagram_id_this != SYSTEM_OBJECT_NUMBER_ID || diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
//		if(id_is_not_from_system) {
//			printInternalDebugLine("----CREATING NEW METHOD CALL IN DIAGRAM----");
//			printInternalDebugLine("I will call .createCall("+thisJoinPoint.getSignature().getName()+", "+diagram_id_this+", "+diagram_id_target+")");
//			method_call = sequence_diagram_view.createCall(thisJoinPoint.getSignature().getName(), diagram_id_this, diagram_id_target);
//			printInternalDebugLine("-FINISHED METHOD CALL CREATION IN DIAGRAM-");
//		} 
//		if(DISPLAY_CONSOLE_MESSAGES) {
//			System.out.println("--METHOD CALL--");
//			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called method: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
//			System.out.println("-----------------");
//		}
//		proceed();
//		//Create Return in Sequence Diagram
//		if(id_is_not_from_system)
//			sequence_diagram_view.createReturn(method_call);
//		
//		printInternalDebugLine("### Method Call Finished ###");
//		//System.out.println("METHOD DEBUG POINT END");
//	}
//	
//	Object around() : functionCalls() {
//		//System.out.println("FUNCTION DEBUG POINT");
//		printInternalDebugLine("### Function Call -> Signature: "+thisJoinPoint.getSignature()+" This: "+thisJoinPoint.getThis()+" ID_This: "+System.identityHashCode(thisJoinPoint.getThis())+" - Target: "+thisJoinPoint.getTarget()+" ID_Target: "+System.identityHashCode(thisJoinPoint.getTarget()));
//		
//		//System IDs
//		int id_this = System.identityHashCode(thisJoinPoint.getThis());
//		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
//		printInternalDebugLine("Function - System IDs - id_this = "+id_this+" id_target = "+id_target);
//		printInternalDebugLine("Function - Hashmap<System.code, id> , and the get returned-> this: "+hash_map.get(id_this)+" , target: "+hash_map.get(id_target));
//		printInternalDebugLine("Hashmap: "+hash_map);
//		
//		//If object is not a system object and is not in the hashmap... we need to add it earlier than it naturally would!
//		if(id_this != 0 && hash_map.get(id_this) == null)
//		{
//			Object object = thisJoinPoint.getThis();
//			Object object_builder = caller;
//			
//			//int id_creator = System.identityHashCode(thisJoinPoint.getThis());
//			int id_creator = System.identityHashCode(object_builder);
//			
//			int diagram_id_creator;
//			if(id_creator == 0) //The hash code for the null reference is 0
//				diagram_id_creator = SYSTEM_OBJECT_NUMBER_ID;
//			else
//				diagram_id_creator = id_creator;
//			
//			String object_class_name = object.getClass().getName();
//			
//			printInternalDebugLine("(Function) Creating sequence diagram object in special conditions, for: "+object.toString()+" from class: "+object_class_name+" diagram id from creator: "+diagram_id_creator);
//			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, hash_map.get(diagram_id_creator));
//			hash_map.put(System.identityHashCode(thisJoinPoint.getThis()), id);
//			//last_created_diagram_id = id;
//		}
//		
//		//Diagram IDs
//		int diagram_id_this;
//		if(id_this == 0 || id_this == id_system_in || id_this == id_system_out || id_this == id_system_err) //No object associated... static main method... or System object
//			diagram_id_this = -1; //Value for the static main class ID
//		else
//			diagram_id_this = hash_map.get(id_this);
//		//System.out.println(diagram_id_this);
//		int diagram_id_target;
//		if(id_target == 0 || id_target == id_system_in || id_target == id_system_out || id_target == id_system_err)
//			diagram_id_target = -1;
//		else
//			diagram_id_target = hash_map.get(id_target);
//		//System.out.println(diagram_id_target);
//		
//		printInternalDebugLine("Corrected Function - Hashmap<System.code, id> -> this: "+diagram_id_this+" , target: "+diagram_id_target);
//		
//		//Create Call in Sequence Diagram
//		int function_call = 0;
//		//If the call does not target the system, then it is because it is a normal call, or a call from the system to the program.
//		boolean id_is_not_from_system = (diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
//		//boolean id_is_not_from_system = (diagram_id_this != SYSTEM_OBJECT_NUMBER_ID || diagram_id_target != SYSTEM_OBJECT_NUMBER_ID);
//		if(id_is_not_from_system) {
//			printInternalDebugLine("----CREATING NEW FUNCTION CALL IN DIAGRAM----");
//			printInternalDebugLine("I will call .createCall("+thisJoinPoint.getSignature().getName()+", "+diagram_id_this+", "+diagram_id_target+")");
//			function_call = sequence_diagram_view.createCall(thisJoinPoint.getSignature().getName(), diagram_id_this, diagram_id_target);	
//			printInternalDebugLine("-FINISHED FUNCTION CALL CREATION IN DIAGRAM-");
//		}
//		if(DISPLAY_CONSOLE_MESSAGES) {
//			System.out.println("--FUNCTION CALL--");
//			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called function: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
//			System.out.println("-----------------");
//		}
//		Object object = proceed();
//		//Create Return in Sequence Diagram
//		if(id_is_not_from_system)
//			sequence_diagram_view.createReturn(function_call);
//		printInternalDebugLine("### Function Call Finished ###");
//		//System.out.println("FUNCTION DEBUG POINT END");
//		return object;
//	}
	
	/**
	 * Method that prints a message related with the internal debug of Dynamic UML.
	 * It exists for the sole purpose of easing the Debugging of Dynamic UML during developpment.
	 * @param message
	 */
	private void printInternalDebugLine(String message) {
		if(DISPLAY_INTERNAL_DEBUG_MESSAGES)
			System.out.println(message);
	}
	
}
