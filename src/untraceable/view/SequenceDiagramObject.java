package untraceable.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

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
	private DragAndDropController dragAndDropController;
	private RefreshingThread refreshingThread;
	
	private Color backgroundColor = Color.white;
	
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
	 * @param mouseListener 
	 * @param dragAndDropController 
	 * @param refreshingThread 
	 * @param drawableSpace
	 */
	public SequenceDiagramObject(final int labelWidth, final int initTime,
			final String objectName, final String objectClass, final int objectID, 
			final MouseListener mouseListener, final DragAndDropController dragAndDropController, 
			final RefreshingThread refreshingThread) {
		this.labelWidth = labelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
		this.dragAndDropController = dragAndDropController;
		this.refreshingThread = refreshingThread;
		
		setPreferredSize(new Dimension(labelWidth, 1000));
		drawableSpace = new BufferedImage(labelWidth, 1000, BufferedImage.TYPE_INT_ARGB);
		setIcon(new ImageIcon(drawableSpace));
		pen = (Graphics2D)drawableSpace.getGraphics();
		
		//setName(""+objectID);
		addMouseListener(mouseListener);
		setTransferHandler(new TransferHandler("icon"));
		setDropTarget(new DropTarget(this, new DropTargetListener() {
			
			@Override
			public void dropActionChanged(DropTargetDragEvent event) {
			}
			
			@Override
			public void drop(DropTargetDropEvent event) {
				cleanObjectDropSelection();
				dragAndDropController.setDropItem(objectID);
				System.out.println("Sou o " + objectID + "/" + 
						dragAndDropController.getDropItem() + " e droparam-me em cima o " + 
						dragAndDropController.getDragItem());
				dragAndDropController.reset();
				//refreshingThread.refreshPrincipalFrame();
			}
			
			@Override
			public void dragOver(DropTargetDragEvent event) {
			}
			
			@Override
			public void dragExit(DropTargetEvent event) {
				System.out.println("Saí do " + objectID);
				cleanObjectDropSelection();
				//refreshingThread.refreshPrincipalFrame();
			}
			
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				System.out.println("Entrei no " + objectID);
				drawObjectDropSelection();
				dragAndDropController.setDragItem(objectID);
				System.out.println("Pegaste no " + dragAndDropController.getDragItem());
				//refreshingThread.refreshPrincipalFrame();
			}
		}));
		drawWholeObject();
	}
	
	public void drawWholeObject(){
		cleanObjectSpace();
		drawObjectBox();
		drawObjectLifeLine();
		drawObjectControlLines();
		if (!isAlive())
			drawObjectDestructCross();
	}
	
	public void destruct(final int destructTime){
		this.destructTime = destructTime;
		drawObjectDestructCross();
		
	}
	
	public int getID(){
		return objectID;
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
		refreshDrawing();
	}
	
	private void drawObjectLifeLine(){
		
		refreshDrawing();
	}
	
	private void drawObjectControlLines(){
		
		refreshDrawing();
	}
	
	private void drawObjectDropSelection(){
		pen.setColor(Color.lightGray);
		pen.fillRect(0, 1, (int)(labelWidth*objectBoxHeigthRatio)/4, 
				(int)(labelWidth*objectBoxHeigthRatio)/4);
		pen.drawLine(0, 1, 0, (int)(labelWidth*objectBoxWidthRatio*4));
		pen.drawLine(0, 1, (int)(labelWidth*objectBoxWidthRatio), 1);
		refreshDrawing();
	}
	 
	private void cleanObjectSpace(){
		pen.setColor(backgroundColor);
		pen.fillRect(0, 0, labelWidth, drawableSpace.getHeight());
		refreshDrawing();
	}
	
	private void cleanObjectDropSelection(){
		drawWholeObject();
		refreshDrawing();
	}
	
	private void drawObjectDestructCross(){
		pen.setColor(Color.red);
		pen.drawLine((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder, 
				((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 
				((int)(labelWidth*objectBoxWidthRatio)), 
				(initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder) + 
				((int)(labelWidth*objectBoxHeigthRatio)));
		pen.drawLine((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				(initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder) + 
				((int)(labelWidth*objectBoxHeigthRatio)), 
				((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 
				((int)(labelWidth*objectBoxWidthRatio)), 
				initTime*(int)(labelWidth*objectBoxHeigthRatio) + northBorder);
		refreshDrawing();
	}
	
	private boolean isAlive(){
		return destructTime == -1;
	}
	
	private void refreshDrawing(){
		setIcon(new ImageIcon(drawableSpace));
	}
	
	
}
