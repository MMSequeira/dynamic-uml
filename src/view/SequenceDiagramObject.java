package view;


import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class SequenceDiagramObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int northBorder = 20;
	private static final float objectBoxWidthRatio = 0.5f;
	private static final float objectBoxHeigthRatio = objectBoxWidthRatio/2;
	
	private int labelWidth;
	private int initTime = 0;
	private int destructTime = -1;
	private String objectName;
	private String objectClass;
	private int objectID;
	
	private Container drawableSpace;
	private Graphics2D pen;
	
	/**
	 * Constructor for an object of the sequence diagram
	 * 
	 * @param labelWidth
	 * @param initTime
	 * @param objectName
	 * @param objectClass
	 * @param objectID
	 * @param drawableSpace
	 */
	public SequenceDiagramObject(final int labelWidth, final int initTime,
			final String objectName, final String objectClass, final int objectID, 
			final Container drawableSpace) {
		this.labelWidth = labelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
		this.drawableSpace = drawableSpace;
		System.out.println("Fui criado: " + objectID);
	}
	
	public void drawWholeObject(){
		drawObjectBox();
		drawObjectLifeLine();
		drawObjectControlLines();
	}
	
	public void destruct(final int destructTime){
		this.destructTime = destructTime;
	}
	
	private void drawObjectBox(){
		pen = (Graphics2D)drawableSpace.getGraphics();
		pen.setColor(Color.black);
		pen.drawRect((objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2), 
				initTime + northBorder, (int)(labelWidth*objectBoxWidthRatio), 
				(int)(labelWidth*objectBoxHeigthRatio));
		pen.setColor(Color.orange);
		pen.fillRect((objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2), 
				initTime + northBorder, (int)(labelWidth*objectBoxWidthRatio), 
				(int)(labelWidth*objectBoxHeigthRatio));
		
		pen.setColor(Color.black);
		pen.drawString(objectID + " - " + objectName, 
				(objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 1, 
				initTime + northBorder + ((int)(labelWidth*objectBoxHeigthRatio)/2));

	}
	
	private void drawObjectLifeLine(){
		
	}
	
	private void drawObjectControlLines(){
		
	}
	
	private boolean isAlive(){
		return destructTime == -1;
	}
	
	
}
