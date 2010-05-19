package untraceable.view;

public class SequenceDiagramCall {

	private int time;
	private CallWay way;
	private CallType type;
	
	public SequenceDiagramCall(final int time, final CallWay way, final CallType type){
		this.time = time;
		this.way = way;
		this.type = type;
	}

	public int getTime() {
		return time;
	}

	public CallWay getWay() {
		return way;
	}

	public CallType getType() {
		return type;
	}
	
	public String toString(){
		return "------\nTime: " + time + "\nWay: " + way + "\nType: " + type + "\n------";
	}
	
}
