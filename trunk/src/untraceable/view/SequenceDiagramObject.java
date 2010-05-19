package untraceable.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

public class SequenceDiagramObject extends JLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int initObjectDrawableSpaceHeigth = 6000;
	public static final int northBorder = 20;
	private static final float objectBoxWidthRatio = 0.5f;
	private static final float objectBoxHeigthRatio = objectBoxWidthRatio/2;
	private static final float objectCrossWidthRatio = objectBoxHeigthRatio/2;
	private static final float objectCrossHeigthRatio = objectBoxHeigthRatio/2;
	private static final float callLinkCircleRatio = 0.015f;
	
	private static final float objectBoxWidth = objectBoxWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectBoxHeigth = objectBoxHeigthRatio*SequenceDiagramView.objectPanelWidth;
	private static final float objectCrossWidth = objectCrossWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectCrossHeigth = objectCrossHeigthRatio*SequenceDiagramView.objectPanelWidth;
	private static final float callLinkCircleRadius = callLinkCircleRatio*SequenceDiagramView.objectPanelWidth;
	
	private int labelWidth;
	private int initTime = 0;
	private int destructTime = -1;
	private String objectName;
	private String objectClass;
	private int objectID;
	private DragAndDropController dragAndDropController;
	private EventTimeController eventTimeController;
	private RefreshingThread refreshingThread;
	private LinkedList<SequenceDiagramCall> callList;
	
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
	public SequenceDiagramObject(final int initTime,
			final String objectName, final String objectClass, final int objectID, 
			final MouseListener mouseListener, final DragAndDropController dragAndDropController, 
			final EventTimeController eventTimeController, final RefreshingThread refreshingThread) {
		this.labelWidth = SequenceDiagramView.objectPanelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
		this.dragAndDropController = dragAndDropController;
		this.eventTimeController = eventTimeController;
		this.refreshingThread = refreshingThread;
		callList = new LinkedList<SequenceDiagramCall>();
		callList.add(new SequenceDiagramCall(initTime, CallWay.Right, CallType.NewReceive));
		
		setPreferredSize(new Dimension(labelWidth, initObjectDrawableSpaceHeigth));
		drawableSpace = new BufferedImage(labelWidth, initObjectDrawableSpaceHeigth, BufferedImage.TYPE_INT_ARGB);
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
				System.out.println("Sa� do " + objectID);
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
	
	public void refreshObjectLifeLine(){
		drawObjectLifeLine();
	}
	
	public void destruct(final int destructTime){
		this.destructTime = destructTime;
		drawObjectDestructCross();
	}
	
	public void newCall(final int time, final CallWay way, final CallType type){
		SequenceDiagramCall newCall = new SequenceDiagramCall(time, way, type);
		drawCall(newCall);
		callList.add(newCall);
	}
	
	public int getID(){
		return objectID;
	}
	
	private void drawWholeObject(){
		cleanObjectSpace();
		drawObjectBox();
		drawObjectLifeLine();
		drawObjectControlLines();
		drawObjectCalls();
		if (!isAlive())
			drawObjectDestructCross();
	}

	private void drawObjectBox(){
		pen.setColor(Color.black);
		pen.drawRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime, 
				(int)(objectBoxWidth), (int)(objectBoxHeigth));
		pen.setColor(Color.orange);
		pen.fillRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime, 
				(int)(objectBoxWidth), (int)(objectBoxHeigth));
		pen.setColor(Color.black);
		pen.drawString(objectID + " - " + objectName, 
				((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 1, 
				initTime + ((int)(objectBoxHeigth)/2));
		refreshDrawing();
	}
	
	private void drawObjectLifeLine(){
		int lifeLineEndTime;
		if(isAlive())
			lifeLineEndTime = eventTimeController.getCurrentTime();
		else
			lifeLineEndTime = destructTime;
		pen.setColor(Color.black);
		pen.drawLine(labelWidth/2, (int)(initTime+objectBoxHeigth), labelWidth/2, 
				lifeLineEndTime);
		refreshDrawing();
	}
	
	private void drawObjectControlLines(){
		
		refreshDrawing();
	}
	
	private void drawObjectCalls(){
		for(SequenceDiagramCall call: callList){
			drawCall(call);
		}
		refreshDrawing();
	}
	
	private void drawObjectDropSelection(){
		pen.setColor(Color.lightGray);
		pen.fillRect(0, 1, (int)(objectBoxHeigth)/4, (int)(objectBoxHeigth)/4);
		pen.drawLine(0, 1, 0, (int)(initObjectDrawableSpaceHeigth));
		pen.drawLine(0, 1, (int)(objectBoxWidth), 1);
		refreshDrawing();
	}
	 
	private void drawObjectDestructCross(){
		pen.setColor(Color.red);
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), destructTime, 
				labelWidth/2 + ((int)(objectCrossWidth)), 
				(destructTime) + ((int)(objectCrossHeigth)));
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), (destructTime) + 
				((int)(objectCrossHeigth)), labelWidth/2 + ((int)(objectCrossWidth)), 
				destructTime);
		refreshDrawing();
	}
	
	private void drawCall(final SequenceDiagramCall call){
		int right = 0;
		if(call.getWay().equals(CallWay.Right))
			right = 1;
		int time = call.getTime();
		pen.setColor(Color.black);
		if(call.getType().equals(CallType.NewSend)){
			pen.drawOval(labelWidth/2-(int)callLinkCircleRadius, 
					time-(int)callLinkCircleRadius, (int)callLinkCircleRadius*2, 
					(int)callLinkCircleRadius*2);
			pen.fillOval(labelWidth/2-(int)callLinkCircleRadius, 
					time-(int)callLinkCircleRadius, (int)callLinkCircleRadius*2, 
					(int)callLinkCircleRadius*2);
			pen.drawLine(labelWidth/2, time, 
					labelWidth*right, time);
			
		}else if(call.getType().equals(CallType.NewReceive)){
			pen.drawLine(labelWidth*(1-right), time, 
					(int)(((labelWidth*(1-objectBoxWidthRatio))/2)+
							((1-right)*objectBoxWidth)), time);
			pen.drawLine((int)(((labelWidth*(1-objectBoxWidthRatio))/2)+
							((1-right)*objectBoxWidth)), time, 
							(int)(((int)(((labelWidth*(1-objectBoxWidthRatio))/2)+
									((1-right)*objectBoxWidth)))+
									(Math.pow(-1, right)*
											((int)callLinkCircleRadius*2))), 
											time-((int)callLinkCircleRadius));
			pen.drawLine((int)(((labelWidth*(1-objectBoxWidthRatio))/2)+
					((1-right)*objectBoxWidth)), time, 
					(int)(((int)(((labelWidth*(1-objectBoxWidthRatio))/2)+
							((1-right)*objectBoxWidth)))+
							(Math.pow(-1, right)*
									((int)callLinkCircleRadius*2))), 
									time+((int)callLinkCircleRadius));
			
		}else if(call.getType().equals(CallType.CallSend)){
			
		}else if(call.getType().equals(CallType.CallReceive)){
			
		}else if(call.getType().equals(CallType.ReturnSend)){
			
		}else if(call.getType().equals(CallType.ReturnReceive)){
			
		}else if(call.getType().equals(CallType.CallPass)){
			pen.drawLine(0, time, labelWidth, time);
		}
		
		
		
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
	
	private boolean isAlive(){
		return destructTime == -1;
	}
	
	private void refreshDrawing(){
		setIcon(new ImageIcon(drawableSpace));
	}
	
	//not to be in the final version
	public void printCalls(){
		System.out.println("Object: " + objectName + " calls:");
		for(SequenceDiagramCall c: callList)
			System.out.println(c.toString());
	}
	
	
}
