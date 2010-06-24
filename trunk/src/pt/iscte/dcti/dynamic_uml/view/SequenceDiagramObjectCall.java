package pt.iscte.dcti.dynamic_uml.view;

/**
 * This class implements a representation of the part of a call 
 * that "passes" through the object
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class SequenceDiagramObjectCall {

	private String callName;
	private int time;
	private int activeCalls;
	private CallWay way;
	private CallType type;
	
	/**
	 * Constructor
	 * @param callName
	 * @param time
	 * @param way
	 * @param type
	 * @param activeCalls 
	 */
	public SequenceDiagramObjectCall(final String callName, final int time, final CallWay way, final CallType type, final int activeCalls){
		this.callName = callName;
		this.time = time;
		this.activeCalls = activeCalls;
		this.way = way;
		this.type = type;
	}

	/**
	 * Getter for the name of the call
	 * @return callName
	 */
	public String getName(){
		return callName;
	}
	
	/**
	 * Getter for the time of the call
	 * @return time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Getter for the number of active calls
	 * @return time
	 */
	public int getNumberOfActiveCalls() {
		return activeCalls;
	}

	/**
	 * Getter for the way of the call
	 * @return way
	 */
	public CallWay getWay() {
		return way;
	}

	/**
	 * Getter for the type of the call
	 * @return type
	 */
	public CallType getType() {
		return type;
	}
	
}
