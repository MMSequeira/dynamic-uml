package view;


import java.util.LinkedList;

import javax.swing.JFrame;

public class SequenceDiagramView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int sequenceDiagramObjectID = 0;
	private final String frameName = "DynamicUML - Sequence Diagram";
	private boolean isVisibleOnInit = true;
	
	private int initWindowWidth = 1024;
	private int initWindowHeight = 700;
	private int objectPanelWidth = 100;
	
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	
	public SequenceDiagramView (){
		setTitle(frameName);
		setSize(initWindowWidth, initWindowHeight);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(isVisibleOnInit);
	}
	
	/**
	 * Creates an internal sequence diagram object and returns the id for that object
	 * @return id
	 */
	public int createSequenceDiagramObject(final String objectName, 
			final String objectClass){

		int objectID = newSequenceDiagramObjectID();
		SequenceDiagramObject object = new SequenceDiagramObject(objectPanelWidth, 
				0, objectName, objectClass, objectID);
		sequenceDiagramObjectList.add(object);
		object.drawWholeObject();
		
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
	 * Test main method, not used in the final version
	 * @param args
	 */
	public static void main(String[] args) {
		new SequenceDiagramView();
	}

}
