package untraceable.view;

public class SequenceDiagramCall {

	private int callID;
	private String methodName;
	private SequenceDiagramObject caller;
	private SequenceDiagramObject callee;
	
	public SequenceDiagramCall(final int callID, final String methodName,
			final SequenceDiagramObject caller, final SequenceDiagramObject callee) {
		this.callID = callID;
		this.methodName = methodName;
		this.caller = caller;
		this.callee = callee;
	}

	public int getID() {
		return callID;
	}

	public String getMethodName() {
		return methodName;
	}

	public SequenceDiagramObject getCaller() {
		return caller;
	}

	public SequenceDiagramObject getCallee() {
		return callee;
	}
	
}
