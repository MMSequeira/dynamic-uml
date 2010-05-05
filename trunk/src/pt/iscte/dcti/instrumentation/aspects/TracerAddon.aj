package pt.iscte.dcti.instrumentation.aspects;

import untraceable.view.SequenceDiagramView;


public aspect TracerAddon {
	
	private SequenceDiagramView sequence_diagram_view; 
	
	public TracerAddon() {
		sequence_diagram_view = new SequenceDiagramView();
	}
	
	//POINTCUTs
	private pointcut codeInsideMyProject() : within((pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..* || untraceable.*..*) && !pt.iscte.dcti.instrumentation.examples..*) || cflow(execution(String *.toString()));
	
	private pointcut constructorCall() : call(*.new(..)) && !codeInsideMyProject();
	private pointcut constructorExecution() : execution(*.new(..)) && !codeInsideMyProject();
	
	//before (Object thiz, Object target) : constructorCall() && this(thiz) && target(target) {
	//before (Object caller) : constructorCall() && this(caller) {
	
	Object around() : constructorCall() {
		// TODO How to avoid this being applied when there is a this?
		Object object = proceed();
		
		String object_class_name = object.getClass().getName();
		Class class_type = object.getClass();
		//String object_name = (class_type)object;
		if(!(object instanceof Enum<?>)) {
			System.out.println("--CHAMADA A CONSTRUTOR-- "+object);
			sequence_diagram_view.createSequenceDiagramObject(object.toString(), object_class_name, 0);
		}
		
		return object;
	}
	
	
	
	//private pointcut tracerConstructorCall() : call(* pt.iscte.dcti.instrumentation.aspects.Tracer.*(..));
}
