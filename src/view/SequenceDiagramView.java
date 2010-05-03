package view;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.xml.ws.handler.MessageContext.Scope;

public class SequenceDiagramView /*implements Scrollable*/ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int sequenceDiagramObjectID = 0;
	private final String frameName = "DynamicUML - Sequence Diagram";
	private boolean isVisibleOnInit = true;
	private long refreshStep = 300;
	
	private int initWindowWidth = 1024;
	private int initWindowHeight = 700;
	public static final int objectPanelWidth = 200;
	
	private Scrollbar horizontalScrollBar;
	private Scrollbar verticalScrollBar;
	
	private JFrame principalFrame = new JFrame();
	private RefreshingThread refreshingThread;
	
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	
	public SequenceDiagramView (){
		initialization();
		refreshingThread = new RefreshingThread(sequenceDiagramObjectList, refreshStep/*, 
				horizontalScrollBar, verticalScrollBar*/);
		refreshingThread.start();
	}
	
	/**
	 * Creates an internal sequence diagram object and returns the id for that object
	 * @return id
	 */
	public int createSequenceDiagramObject(final String objectName, 
			final String objectClass){

		int objectID = newSequenceDiagramObjectID();
		SequenceDiagramObject newObject = new SequenceDiagramObject(objectPanelWidth, 
				objectID, objectName, objectClass, objectID, principalFrame.getContentPane());
		sequenceDiagramObjectList.add(newObject);
		newObject.drawWholeObject();
		
		return objectID;
	}
	
	/**
	 * Decides what will be the id for the created object
	 * @return id
	 */
	private int newSequenceDiagramObjectID(){
		return sequenceDiagramObjectID++;
	}
	
	/**
	 * Configures primary window
	 */
	private void initialization(){
		principalFrame.setTitle(frameName);
		principalFrame.setSize(initWindowWidth, initWindowHeight);
		principalFrame.setLayout(new BorderLayout());
		
		/*
		principalFrame.add(new JScrollBar(JScrollBar.VERTICAL), BorderLayout.EAST);
		principalFrame.add(new JScrollBar(JScrollBar.HORIZONTAL), BorderLayout.SOUTH);
		*/
		//JScrollPane scrollPane = new JScrollPane();
		/*principalFrame*/ //add(scrollPane);
		
		/*
		horizontalScrollBar = new Scrollbar(Scrollbar.HORIZONTAL, 10, 0, 
				initWindowWidth, objectPanelWidth*sequenceDiagramObjectList.size());
		principalFrame.add(horizontalScrollBar, BorderLayout.SOUTH);
		verticalScrollBar = new Scrollbar(Scrollbar.VERTICAL, 10, 0, initWindowHeight, 
				1000);
		principalFrame.add(verticalScrollBar, BorderLayout.EAST);
		*/
		
		principalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		principalFrame.setVisible(isVisibleOnInit);
	}
	
	/**
	 * Test main method, not used in the final version
	 * @param args
	 */
	public static void main(String[] args) {
		
		SequenceDiagramView view = new SequenceDiagramView();
		for(int i = 0; i < 10; i++){
			view.createSequenceDiagramObject("Object " + (i+1), "Class 1");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	/*
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}
	*/

}
