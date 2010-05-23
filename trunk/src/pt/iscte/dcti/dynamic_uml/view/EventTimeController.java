package pt.iscte.dcti.dynamic_uml.view;

import java.util.LinkedList;

/**
 * This class implements a controller for the discrete time
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class EventTimeController {
	
	private final static float sequenceDiagramObjectDrawableSpaceGrowthRatio = 1.3f;
	private final static float eventTimeGuardRatio = 0.05f;
	private final static int eventTimeGuard = (int)(eventTimeGuardRatio*SequenceDiagramView.objectPanelWidth);
	private int sequenceDiagramObjectDrawableSpaceHeight = SequenceDiagramObject.initObjectDrawableSpaceHeight;
	private int previousTime;
	private int time;
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList;
	private final int[] eventTime = {(int)(SequenceDiagramObject.objectBoxHeight)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectCrossHeight)+eventTimeGuard,
			(int)(SequenceDiagramObject.objectBoxHeight/2)+eventTimeGuard};
	
	/**
	 * Constructor
	 * @param sequenceDiagramObjectList
	 */
	public EventTimeController(final LinkedList<SequenceDiagramObject> sequenceDiagramObjectList){
		previousTime = time;
		time = SequenceDiagramObject.northBorder;
		this.sequenceDiagramObjectList = sequenceDiagramObjectList;
	}
	
	/**
	 * Returns the draw time and prepares the next draw time, according to the
	 * given event
	 * @param event
	 * @return drawTime
	 */
	public int eventTime(final SequenceDiagramEvent event){
		if(time > sequenceDiagramObjectDrawableSpaceHeight){
			sequenceDiagramObjectDrawableSpaceHeight = (int)(sequenceDiagramObjectDrawableSpaceHeight*sequenceDiagramObjectDrawableSpaceGrowthRatio);
			setNewObjectsDrawableSpace();
		}
		int drawTime = time;
		int index = 0;
		for(int i = 0; i < SequenceDiagramEvent.values().length; i++)
			if(SequenceDiagramEvent.values()[i].equals(event))
				index = i;
		previousTime = time;
		time = time + eventTime[index];
		return drawTime;
	}

	/**
	 * Getter for the current draw time
	 * @return time
	 */
	public int getCurrentTime(){
		return time;
	}
	
	/**
	 * Getter for the drawable space height
	 * @return sequenceDiagramObjectDrawableSpaceHeigth;
	 */
	public int getSequenceDiagramObjectDrawableSpaceHeigth(){
		return sequenceDiagramObjectDrawableSpaceHeight;
	}
	
	/**
	 * Getter for the previous draw time
	 * @return previousTime
	 */
	public int getPreviousTime() {
		return previousTime;
	}
	
	/**
	 * Set the new drawable space size for the sequence diagram objects
	 */
	private void setNewObjectsDrawableSpace() {
		for(SequenceDiagramObject obj: sequenceDiagramObjectList)
			obj.setNewDrawableSpaceSize();
	}
	
}
