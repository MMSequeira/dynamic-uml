package pt.iscte.dcti.dynamic_uml.view;

public class SequenceDiagramCall {

	private int callID;
	private String methodName;
	private SequenceDiagramObject caller;
	private SequenceDiagramObject callee;
	private int callTime;
	private CallWay callWay;
	private boolean returned;
	
	public SequenceDiagramCall(final int callID, final String methodName,
			final SequenceDiagramObject caller, final SequenceDiagramObject callee, 
			final int callTime,  final CallWay callWay) {
		this.callID = callID;
		this.methodName = methodName;
		this.caller = caller;
		this.callee = callee;
		this.callTime = callTime;
		this.callWay = callWay;
		returned = false;
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
	
	public int getCallTime(){
		return callTime;
	}
	
	public CallWay getCallWay(){
		return callWay;
	}
	
	public boolean hasReturned(){
		return returned;
	}
	
	public void returned(){
		returned = true;
	}
	
	public String toString(){
		String callerString;
		if(caller == null)
			callerString = "Main";
		else
			callerString = ""+caller.getID();
		String calleeString;
		if(caller == null)
			calleeString = "Main";
		else
			calleeString = ""+caller.getID();
		return "CallID: " + callID + "; \tCallerID: " + callerString + "; \tCalleeID: " + calleeString + " \tMethodName: " + methodName;
	}
	
}
