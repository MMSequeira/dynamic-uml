package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class SequenceDiagramView {

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
	
	private JFrame principalFrame = new JFrame();
	private JPanel principalPanel = new JPanel();
	private JScrollPane scroll;
	
	private RefreshingThread refreshingThread;
	
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	
	/**
	 * Constructor for the user interface
	 */
	public SequenceDiagramView (){
		initialization();
		refreshingThread = new RefreshingThread(sequenceDiagramObjectList, refreshStep, 
				principalFrame);
		refreshingThread.start();
	}
	
	/**
	 * Creates an internal sequence diagram object and returns the id for that object
	 * @return id
	 */
	public int createSequenceDiagramObject(final String objectName, final String objectClass, 
			final int initTime){

		int objectID = newSequenceDiagramObjectID();
		SequenceDiagramObject newObject = new SequenceDiagramObject(objectPanelWidth, 
				initTime, objectName, objectClass, objectID);
		sequenceDiagramObjectList.add(newObject);
		principalPanel.add(newObject,objectID);
		//Remove jlabels padding as needed
		if(principalPanel.getComponentCount() > sequenceDiagramObjectList.size())
			principalPanel.remove(principalPanel.getComponentCount()-1);
		//
		newObject.drawWholeObject();
		return objectID;
	}

	/**
	 * Configures primary window
	 */
	private void initialization(){
		addWindowListenerToPrincipalFrame();
		principalFrame.setTitle(frameName);
		principalFrame.setSize(initWindowWidth, initWindowHeight);
		principalFrame.setLayout(new GridLayout(1,1));
		principalPanel.setLayout(new GridLayout());
		//Blank jlabels padding
		JLabel padLabel;
		for (int i = 0; i < (int)(initWindowWidth/objectPanelWidth); i++){
			padLabel = new JLabel(" ");
			padLabel.setPreferredSize(new Dimension(objectPanelWidth, 1000));
			principalPanel.add(padLabel);
		}
		//
		scroll = new JScrollPane(principalPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		principalFrame.add(scroll);
		principalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		principalFrame.setVisible(isVisibleOnInit);
	}
	
	/**
	 * Adds a window specific windows listened to the principal frame
	 */
	private void addWindowListenerToPrincipalFrame() {
		principalFrame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				refreshingThread.frameAliveness(false);
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
	}

	/**
	 * Decides what will be the id for the created object
	 * @return id
	 */
	private int newSequenceDiagramObjectID(){
		return sequenceDiagramObjectID++;
	}
	
	/**
	 * Test main method, not used in the final version
	 * @param args
	 */
	public static void main(String[] args) {
		
		SequenceDiagramView view = new SequenceDiagramView();
		for(int i = 0; i < 10; i++){
			view.createSequenceDiagramObject("Object " + (i+1), "Class 1", 0);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
