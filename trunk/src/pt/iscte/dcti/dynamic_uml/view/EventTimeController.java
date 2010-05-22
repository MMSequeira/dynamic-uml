package pt.iscte.dcti.dynamic_uml.view;

import java.util.LinkedList;

public class EventTimeController {
	
	private final static float sequenceDiagramObjectDrawableSpaceGrowthRatio = 1.3f;
	private final static float eventTimeGuardRatio = 0.05f;
	private final static int eventTimeGuard = (int)(eventTimeGuardRatio*SequenceDiagramView.objectPanelWidth);
	private int sequenceDiagramObjectDrawableSpaceHeigth = SequenceDiagramObject.initObjectDrawableSpaceHeigth;
	private int previousTime;
	private int time;
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList;
	private final int[] eventTime = {(int)(SequenceDiagramObject.objectBoxHeigth)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectCrossHeigth)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectBoxHeigth/2)+eventTimeGuard};
	
	public EventTimeController(final LinkedList<SequenceDiagramObject> sequenceDiagramObjectList){
		previousTime = time;
		time = SequenceDiagramObject.northBorder;
		this.sequenceDiagramObjectList = sequenceDiagramObjectList;
	}
	
	public int eventTime(final SequenceDiagramEvent event){
		int drawTime = time;
		int index = 0;
		for(int i = 0; i < SequenceDiagramEvent.values().length; i++)
			if(SequenceDiagramEvent.values()[i].equals(event))
				index = i;
		previousTime = time;
		time = time + eventTime[index];
		if(time > sequenceDiagramObjectDrawableSpaceHeigth){
			sequenceDiagramObjectDrawableSpaceHeigth = (int)(sequenceDiagramObjectDrawableSpaceHeigth*sequenceDiagramObjectDrawableSpaceGrowthRatio);
			refreshObjectsDrawableSpace();
		}
		return drawTime;
	}

	public int getCurrentTime(){
		return time;
	}
	
	public int getSequenceDiagramObjectDrawableSpaceHeigth(){
		return sequenceDiagramObjectDrawableSpaceHeigth;
	}
	
	public int getPreviousTime() {
		return previousTime;
	}
	
	private void refreshObjectsDrawableSpace() {
		for(SequenceDiagramObject obj: sequenceDiagramObjectList){
			obj.setNewDrawableSpaceSize();
		}
	}
	
}
