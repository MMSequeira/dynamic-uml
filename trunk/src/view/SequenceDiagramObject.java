package view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class SequenceDiagramObject extends JLabel{

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
	
	//private Container drawableSpace;
	private Graphics2D pen;
	private BufferedImage drawableSpace;
	
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
			final String objectName, final String objectClass, final int objectID) {
		this.labelWidth = labelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
		setPreferredSize(new Dimension(labelWidth, 1000));
		drawableSpace = new BufferedImage(labelWidth, 1000, BufferedImage.TYPE_INT_ARGB);
		setIcon(new ImageIcon(drawableSpace));
		pen = (Graphics2D)drawableSpace.getGraphics();
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
		
		/*
		pen = (Graphics2D)drawableSpace.getGraphics();
		pen.setColor(Color.black);
		pen.drawRect((objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2), 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder, (int)(labelWidth*objectBoxWidthRatio), 
				(int)(labelWidth*objectBoxHeigthRatio));
		pen.setColor(Color.orange);
		pen.fillRect((objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2), 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder, (int)(labelWidth*objectBoxWidthRatio), 
				(int)(labelWidth*objectBoxHeigthRatio));
		
		pen.setColor(Color.black);
		pen.drawString(objectID + " - " + objectName, 
				(objectID*labelWidth)+((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 1, 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder + ((int)(labelWidth*objectBoxHeigthRatio)/2));
		*/
		
		//pen = (Graphics2D)drawableSpace.getGraphics();
		pen.setColor(Color.black);
		pen.drawRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder, 
				(int)(labelWidth*objectBoxWidthRatio), (int)(labelWidth*objectBoxHeigthRatio));
		pen.setColor(Color.orange);
		pen.fillRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder, 
				(int)(labelWidth*objectBoxWidthRatio), (int)(labelWidth*objectBoxHeigthRatio));
		
		pen.setColor(Color.black);
		pen.drawString(objectID + " - " + objectName, 
				((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 1, 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder +
				((int)(labelWidth*objectBoxHeigthRatio)/2));
		
		//TESTE
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(objectID == 0){
			pen.drawOval(labelWidth/2, 300, 50, 50);
			pen.fillOval(labelWidth/2, 300, 50, 50);
		}*/
		//TESTE
	}
	
	private void drawObjectLifeLine(){
		
	}
	
	private void drawObjectControlLines(){
		
	}
	
	private boolean isAlive(){
		return destructTime == -1;
	}
	
	
}
