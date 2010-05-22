package pt.iscte.dcti.dynamic_uml.view;

public class SequenceDiagramObjectControlLine {
	
	private int startTime;
	private int endTime;
	private int activeCalls;
	private CallWay callWay;
	
	public SequenceDiagramObjectControlLine(final int startTime, final int activeCalls, final CallWay way) {
		this.startTime = startTime;
		this.endTime = -1;
		this.activeCalls = activeCalls;
		this.callWay = way;
	}
	
	public int getStartTime(){
		return startTime;
	}
	
	public int getEndTime(){
		return endTime;
	}
	
	public int getActiveCalls(){
		return activeCalls;
	}
	
	public CallWay getCallWay(){
		return callWay;
	}
	
	public boolean isActive(){
		return endTime == -1;
	}
	
	public void setEndTime(final int endTime){
		this.endTime = endTime;
	}

}
