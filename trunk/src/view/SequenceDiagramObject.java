package view;


import javax.swing.JLabel;

public class SequenceDiagramObject extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int labelWidth;
	private int initTime = 0;
	private String objectName;
	private String objectClass;
	private int objectID;
	
	/**
	 * Constructor for an object of the sequence diagram
	 * 
	 * @param labelWidth
	 * @param initTime
	 * @param objectName
	 * @param objectClass
	 */
	public SequenceDiagramObject(final int labelWidth, final int initTime,
			final String objectName, final String objectClass, final int objectID) {
		this.labelWidth = labelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
	}
	
	public void drawWholeObject(){
		drawObjectBox();
		drawObjectLifeLine();
		drawObjectControlLine();
	}
	
	private void drawObjectBox(){
		
	}
	
	private void drawObjectLifeLine(){
		
	}
	
	private void drawObjectControlLine(){
		
	}
	
	
}
