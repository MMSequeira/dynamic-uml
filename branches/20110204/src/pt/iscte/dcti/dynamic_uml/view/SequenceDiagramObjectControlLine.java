package pt.iscte.dcti.dynamic_uml.view;

/**
 * This class implements a representation of a control line of the object
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class SequenceDiagramObjectControlLine {
	
	private int startTime;
	private int endTime;
	private int activeCalls;
	private CallWay callWay;
	
	/**
	 * Constructor
	 * @param startTime
	 * @param activeCalls
	 * @param way
	 */
	public SequenceDiagramObjectControlLine(final int startTime, final int activeCalls, final CallWay way) {
		this.startTime = startTime;
		this.endTime = -1;
		this.activeCalls = activeCalls;
		this.callWay = way;
	}
	
	/**
	 * Getter for the start time
	 * @return startTime
	 */
	public int getStartTime(){
		return startTime;
	}
	
	/**
	 * Getter for the end time
	 * @return endTime
	 */
	public int getEndTime(){
		return endTime;
	}
	
	/**
	 * Getter for the number of active calls in the object, when this 
	 * control line was created
	 * @return startTime
	 */
	public int getActiveCalls(){
		return activeCalls;
	}
	
	/**
	 * Getter for the way of the call associated with this control line
	 * @return callWay
	 */
	public CallWay getCallWay(){
		return callWay;
	}
	
	/**
	 * Returns true if this control line is still active
	 * @return boolean
	 */
	public boolean isActive(){
		return endTime == -1;
	}
	
	/**
	 * Sets the end time given as the end time for this control time
	 * @param endTime
	 */
	public void setEndTime(final int endTime){
		this.endTime = endTime;
	}

}
