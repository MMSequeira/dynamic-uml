package view;

import java.util.LinkedList;
import javax.swing.JFrame;

public class RefreshingThread extends Thread {

	private LinkedList<SequenceDiagramObject> objectList;
	private long refreshStep;
	private JFrame principalFrame;
	private boolean isFrameAlive = true;

	public RefreshingThread(final LinkedList<SequenceDiagramObject> objectList, 
			final long refreshStep, final JFrame principalFrame){
		this.objectList = objectList;
		this.refreshStep = refreshStep;
		this.principalFrame = principalFrame;
	}
	
	public void run(){
		while(isFrameAlive){
			/*int listSize = objectList.size();
			for (int i = 0; i < listSize; i++){
				objectList.get(i).drawWholeObject();
			}*/
			principalFrame.setContentPane(principalFrame.getContentPane());
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
	
	public void frameAliveness(final boolean aliveStatus){
		isFrameAlive = aliveStatus;
	}
	
}
