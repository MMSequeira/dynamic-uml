package untraceable.view;

public class SequenceDiagramObjectControlLine {
	
	private int startTime;
	private int endTime;
	
	public SequenceDiagramObjectControlLine(final int startTime) {
		this.startTime = startTime;
		this.endTime = -1;
	}
	
	public int getStartTime(){
		return startTime;
	}
	
	public int getEndTime(){
		return endTime;
	}
	
	public boolean isActive(){
		return endTime == -1;
	}
	
	public void setEndTime(final int endTime){
		this.endTime = endTime;
	}

}
