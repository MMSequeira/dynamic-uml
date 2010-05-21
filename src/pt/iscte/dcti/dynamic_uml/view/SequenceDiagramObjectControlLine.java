package pt.iscte.dcti.dynamic_uml.view;

public class SequenceDiagramObjectControlLine {
	
	private int startTime;
	private int endTime;
	private int activeCalls;
	
	public SequenceDiagramObjectControlLine(final int startTime, final int activeCalls) {
		this.startTime = startTime;
		this.endTime = -1;
		this.activeCalls = activeCalls;
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
	
	public boolean isActive(){
		return endTime == -1;
	}
	
	public void setEndTime(final int endTime){
		this.endTime = endTime;
	}

}
