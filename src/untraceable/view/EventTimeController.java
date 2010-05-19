package untraceable.view;

public class EventTimeController {
	
	private final static float eventTimeGuardRatio = 0.03f;
	private final static int eventTimeGuard = (int)(eventTimeGuardRatio*SequenceDiagramView.objectPanelWidth);
	private int previousTime;
	private int time;
	private final int[] eventTime = {(int)(SequenceDiagramObject.objectBoxHeigth)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectCrossHeigth)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectBoxHeigth/3)+eventTimeGuard};
	
	public EventTimeController(){
		previousTime = time;
		time = SequenceDiagramObject.northBorder;
	}
	
	public int eventTime(final SequenceDiagramEvent event){
		int drawTime = time;
		int index = 0;
		for(int i = 0; i < SequenceDiagramEvent.values().length; i++)
			if(SequenceDiagramEvent.values()[i].equals(event))
				index = i;
		previousTime = time;
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

	public int getPreviousTime() {
		return previousTime;
	}
	
}
