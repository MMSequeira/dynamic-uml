package view;

import java.util.LinkedList;
import java.awt.Scrollbar;

public class RefreshingThread extends Thread {

	private LinkedList<SequenceDiagramObject> objectList;
	private long refreshStep;
	/*
	private Scrollbar horizontalScrollbar;
	private Scrollbar verticalScrollbar;
	*/

	public RefreshingThread(final LinkedList<SequenceDiagramObject> objectList,
			final long refreshStep/*, final Scrollbar horizontalScrollBar, 
			final Scrollbar verticalScrollBar*/){
		this.objectList = objectList;
		this.refreshStep = refreshStep;
		/*
		this.horizontalScrollbar = horizontalScrollBar;
		this.verticalScrollbar = verticalScrollBar;
		*/
	}
	
	public void run(){
		while(true){
			int listSize = objectList.size();
			//horizontalScrollbar.setMaximum(listSize*SequenceDiagramView.objectPanelWidth);
			for (int i = 0; i < listSize; i++){
				objectList.get(i).drawWholeObject();
			}
			try {
				Thread.sleep(refreshStep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setRefreshStep(final long newRefreshStep){
		refreshStep = newRefreshStep;
	}
	
	
}
