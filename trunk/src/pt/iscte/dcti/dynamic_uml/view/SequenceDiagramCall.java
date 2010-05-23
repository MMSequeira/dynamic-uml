package pt.iscte.dcti.dynamic_uml.view;

/**
 * This class implements a representation a call between two objects
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class SequenceDiagramCall {

	private int callID;
	private String methodName;
	private SequenceDiagramObject caller;
	private SequenceDiagramObject callee;
	private int callTime;
	private CallWay callWay;
	private CallType callType;
	private boolean returned;
	
	/**
	 * Constructor
	 * @param callID
	 * @param methodName
	 * @param caller
	 * @param callee
	 * @param callTime
	 * @param callWay
	 * @param callType
	 */
	public SequenceDiagramCall(final int callID, final String methodName,
			final SequenceDiagramObject caller, final SequenceDiagramObject callee, 
			final int callTime,  final CallWay callWay, final CallType callType) {
		this.callID = callID;
		this.methodName = methodName;
		this.caller = caller;
		this.callee = callee;
		this.callTime = callTime;
		this.callWay = callWay;
		this.callType = callType;
		returned = false;
	}

	/**
	 * Getter for the call ID
	 * @return callID
	 */
	public int getID() {
		return callID;
	}

	/**
	 * Getter for the method name
	 * @return methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Getter for the caller
	 * @return caller
	 */
	public SequenceDiagramObject getCaller() {
		return caller;
	}

	/**
	 * Getter for the callee
	 * @return callee
	 */
	public SequenceDiagramObject getCallee() {
		return callee;
	}
	
	/**
	 * Getter for the call time
	 * @return callTime
	 */
	public int getCallTime(){
		return callTime;
	}
	
	/**
	 * Getter for the call way
	 * @return callWay
	 */
	public CallWay getCallWay(){
		return callWay;
	}
	
	/**
	 * Getter for the callType
	 * @return callType
	 */
	public CallType getCallType(){
		return callType;
	}
	
	/**
	 * Returns true if the call has already been returned
	 * @return boolean
	 */
	public boolean hasReturned(){
		return returned;
	}
	
	/**
	 * Sets the status for the call to "returned"
	 */
	public void returned(){
		returned = true;
	}
	
}
