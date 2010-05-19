package untraceable.view;

public class SequenceDiagramObjectCall {

	private String callName;
	private int time;
	private CallWay way;
	private CallType type;
	
	public SequenceDiagramObjectCall(final String callName, final int time, final CallWay way, final CallType type){
		this.callName = callName;
		this.time = time;
		this.way = way;
		this.type = type;
	}

	public String getName(){
		return callName;
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
