package pt.iscte.dcti.instrumentation.aspects;

import java.util.HashMap;
import untraceable.view.SequenceDiagramView;

public privileged aspect TracerAddon extends AbstractTracer {
	
	private final boolean DISPLAY_CONSOLE_MESSAGES = true;
	
	private SequenceDiagramView sequence_diagram_view;
	private HashMap<Integer, Integer> hash_map;
	
	public TracerAddon() {
		sequence_diagram_view = new SequenceDiagramView();
		hash_map = new HashMap();
	}
	
	//POINTCUTs
	public pointcut codeInsideMyProject() : within(pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..* || untraceable.*..*) || cflow(execution(String *.toString()));
	
	//private pointcut weavableClass() : within(examples..*);

	private pointcut constructorCall() : call(*.new(..)) && !codeInsideMyProject();
	private pointcut constructorExecution() : execution(*.new(..)) && !codeInsideMyProject();
	
	private pointcut finalizeCall() : call(void *.finalize(..)) && !codeInsideMyProject();
    
	//Pag 114
	//Pag 131
	//Pagina 191 (238) do Manning
	public static interface HandledFinalize {
		 public void finalize();
	}
	public void HandledFinalize.finalize() throws Throwable {
		super.finalize();
	}
		
	//--Catch Constructor Calls--
	Object around() : constructorCall() {
		// TODO How to avoid this being applied when there is a this?
		Object object = proceed();
		int id_object = System.identityHashCode(object);
		//System.out.println("id: "+id_object);
		
		String object_class_name = object.getClass().getName();
		Class class_type = object.getClass();
		//String object_name = (class_type)object;
		if(!(object instanceof Enum<?>)) {
			if(DISPLAY_CONSOLE_MESSAGES)
				System.out.println("--Constructor Call-- "+object.toString());
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, 0);
			hash_map.put(System.identityHashCode(object), id);
		}
		
		return object;
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
	
	private pointcut methodCalls() : call(void *..*(..)) && !codeInsideMyProject();
	private pointcut functionCalls() : call(* *..*(..)) && !methodCalls() && !codeInsideMyProject();
	
	void around() : methodCalls() {
		int id_this = System.identityHashCode(thisJoinPoint.getThis());
		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--METHOD CALL--");
			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called method: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
		}
		proceed();
	}
	
	Object around() : functionCalls() {
		int id_this = System.identityHashCode(thisJoinPoint.getThis());
		int id_target = System.identityHashCode(thisJoinPoint.getTarget());
		if(DISPLAY_CONSOLE_MESSAGES) {
			System.out.println("--FUNCTION CALL--");
			System.out.println("The object: "+thisJoinPoint.getThis()+" with sequence id: "+hash_map.get(id_this)+" called function: "+thisJoinPoint.getSignature()+" from object: "+thisJoinPoint.getTarget()+" with sequence id: "+hash_map.get(id_target));
		}
		Object object = proceed();
		return object;
	}
	
}
