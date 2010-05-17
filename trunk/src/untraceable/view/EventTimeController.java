package untraceable.view;

public class EventTimeController {
	
	private final static int eventTimeGuard = 5;
	private int time;
	private final int[] eventTime = {(int)(SequenceDiagramObject.objectBoxHeigth)+
			eventTimeGuard,(int)(SequenceDiagramObject.objectCrossHeigth)+eventTimeGuard};
	
	public EventTimeController(){
		time = SequenceDiagramObject.northBorder;
	}
	
	public int eventTime(final SequenceDiagramEvent event){
		int drawTime = time;
		int index = 0;
		for(int i = 0; i < SequenceDiagramEvent.values().length; i++)
			if(SequenceDiagramEvent.values()[i].equals(event))
				index = i;
		time = time + eventTime[index];
		return drawTime;
	}
	
	public int getCurrentTime(){
		return time;
	}
	
	public static void main(String[] args) {
		EventTimeController e = new EventTimeController();
		System.out.println(e.eventTime(SequenceDiagramEvent.NewObject));
		System.out.println(e.eventTime(SequenceDiagramEvent.NewObject));
		System.out.println(e.eventTime(SequenceDiagramEvent.KillObject));
	}
	
}
