package pt.iscte.dcti.instrumentation.aspects;

import java.util.HashMap;

import untraceable.view.SequenceDiagramView;
//import java.io.;
public privileged aspect TracerAddon extends AbstractTracer {
	
	private SequenceDiagramView sequence_diagram_view;
	private HashMap<Integer, Integer> hash_map;
	
	public TracerAddon() {
		sequence_diagram_view = new SequenceDiagramView();
		hash_map= new HashMap();
	}
	
	//POINTCUTs
	public pointcut codeInsideMyProject() : within((pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..* || untraceable.*..*) && !pt.iscte.dcti.instrumentation.examples..*) || cflow(execution(String *.toString()));
	//private pointcut codeInsideMyProject() : within((pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..* || untraceable.*..*) && !pt.iscte.dcti.instrumentation.examples..*);
	
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
		System.out.println("id: "+id_object);
		
		String object_class_name = object.getClass().getName();
		Class class_type = object.getClass();
		//String object_name = (class_type)object;
		if(!(object instanceof Enum<?>)) {
			System.out.println("--Constructor Call-- "+object.toString());
			int id = sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, 0);
			hash_map.put(System.identityHashCode(object), id);
		}
		
		return object;
	}
	
	//--Catch Finalize Calls--
	//TODO Remove usage of package (examples..*)
	declare parents: !(!(examples..*)||hasmethod(public void finalize()) || Enum+ ) implements HandledFinalize;
	
	/*
	after() : execution(public void *.finalize()) {
		System.out.println("MORRI");
	}
	*/
	
	void around() : execution(public void *.finalize()) {
		proceed();
		int id = System.identityHashCode(thisJoinPoint.getThis());
		System.out.println("--Finalize Call--");
		System.out.println("The object: "+thisJoinPoint.getThis()+"  with squence id: "+hash_map.get(id)+" is going to be destroyed.");
		sequence_diagram_view.killSequenceDiagramObject(hash_map.get(id), 0);
		hash_map.remove(id);
		//sequence_diagram_view.killSequenceDiagramObject(id, 0);
	}
	
	/*
	before() : weavableClass() && finalizeCall() { //finalizeCall() {
		System.out.println("--Morte de objecto--");
		System.out.println("O objecto: "+thisJoinPoint.getThis()+" morreu.");
		//proceed();
	}
	*/
	
}
