package pt.iscte.dcti.dynamic_uml.view;


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
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

public class SequenceDiagramObject extends JLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int initObjectDrawableSpaceHeigth = 1000;
	public static final int northBorder = 20;
	private static final float objectBoxWidthRatio = 0.5f;
	private static final float objectBoxHeigthRatio = objectBoxWidthRatio/2;
	private static final float objectCrossWidthRatio = objectBoxHeigthRatio/2;
	private static final float objectCrossHeigthRatio = objectBoxHeigthRatio/2;
	private static final float callLinkCircleRatio = 0.015f;
	private static final float objectControlLineWidthRatio = 0.1f;
	
	private static final float objectBoxWidth = objectBoxWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectBoxHeigth = objectBoxHeigthRatio*SequenceDiagramView.objectPanelWidth;
	private static final float objectCrossWidth = objectCrossWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectCrossHeigth = objectCrossHeigthRatio*SequenceDiagramView.objectPanelWidth;
	private static final float callLinkCircleRadius = callLinkCircleRatio*SequenceDiagramView.objectPanelWidth;
	private static final float objectControlLineWidth = objectControlLineWidthRatio*SequenceDiagramView.objectPanelWidth;
	
	private int labelWidth;
	private int initTime = 0;
	private int destructTime = -1;
	private String objectName;
	private String objectClass;
	private int objectID;
	private DragAndDropController dragAndDropController;
	private EventTimeController eventTimeController;
	private LinkedList<SequenceDiagramObjectCall> callList;
	private LinkedList<SequenceDiagramObjectControlLine> controlLineList;
	private int activeCalls = 0;
	
	private Color backgroundColor = Color.white;
	
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
			final EventTimeController eventTimeController) {
		this.labelWidth = SequenceDiagramView.objectPanelWidth;
		this.initTime = initTime;
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectID = objectID;
		this.dragAndDropController = dragAndDropController;
		this.eventTimeController = eventTimeController;
		callList = new LinkedList<SequenceDiagramObjectCall>();
		controlLineList = new LinkedList<SequenceDiagramObjectControlLine>();
		callList.add(new SequenceDiagramObjectCall("new", initTime, CallWay.Right, CallType.NewReceive));
		
		setPreferredSize(new Dimension(labelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth()));
		drawableSpace = new BufferedImage(labelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth(), BufferedImage.TYPE_INT_ARGB);
		setIcon(new ImageIcon(drawableSpace));
		pen = (Graphics2D)drawableSpace.getGraphics();
		
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
				dragAndDropController.reset();
			}
			
			@Override
			public void dragOver(DropTargetDragEvent event) {
			}
			
			@Override
			public void dragExit(DropTargetEvent event) {
				cleanObjectDropSelection();
			}
			
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				drawObjectDropSelection();
				dragAndDropController.setDragItem(objectID);
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
	
	public void newCall(final String callName, final int time, final CallWay way, final CallType type){
		SequenceDiagramObjectCall newCall = new SequenceDiagramObjectCall(callName, time, way, type);
		callList.add(newCall);
		if(type.equals(CallType.CallReceive)){
			SequenceDiagramObjectControlLine newControlLine = new SequenceDiagramObjectControlLine(time,activeCalls++,way);
			controlLineList.add(newControlLine);
		}else if(type.equals(CallType.ReturnSend)){
			SequenceDiagramObjectControlLine lastActiveControlLine = 
				getLastActiveControlLine();
			activeCalls--;
			lastActiveControlLine.setEndTime(time);
		}
		drawObjectControlLines();
		drawObjectCalls();
	}
	
	public int getID(){
		return objectID;
	}
	
	public void drawWholeObject(){
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
		for(SequenceDiagramObjectControlLine controlLine: controlLineList)
			drawControlLine(controlLine);
		refreshDrawing();
	}
	
	private void drawObjectCalls(){
		for(SequenceDiagramObjectCall call: callList)
			drawCall(call);
		refreshDrawing();
	}
	
	private void drawObjectDropSelection(){
		pen.setColor(Color.lightGray);
		pen.fillRect(0, 1, (int)(objectBoxHeigth)/4, (int)(objectBoxHeigth)/4);
		pen.drawLine(0, 1, 0, (int)(eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth()));
		pen.drawLine(0, 1, (int)(objectBoxWidth), 1);
		refreshDrawing();
	}
	 
	private void drawObjectDestructCross(){
		pen.setColor(Color.red);
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), destructTime-(int)(objectCrossHeigth)/2, 
				labelWidth/2 + ((int)(objectCrossWidth)), 
				(destructTime) + ((int)(objectCrossHeigth)/2));
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), (destructTime) + 
				((int)(objectCrossHeigth)/2), labelWidth/2 + ((int)(objectCrossWidth)), 
				destructTime-(int)(objectCrossHeigth)/2);
		refreshDrawing();
	}
	
	private void drawCall(final SequenceDiagramObjectCall call){
		int right = 0;
		if(call.getWay().equals(CallWay.Right))
			right = 1;
		int time = call.getTime();
		String name = call.getName();
		pen.setColor(Color.black);
		if(call.getType().equals(CallType.NewSend) || 
				call.getType().equals(CallType.CallSend) || 
				call.getType().equals(CallType.ReturnSend)){
			pen.drawOval(labelWidth/2-(int)callLinkCircleRadius, 
					time-(int)callLinkCircleRadius, (int)callLinkCircleRadius*2, 
					(int)callLinkCircleRadius*2);
			pen.fillOval(labelWidth/2-(int)callLinkCircleRadius, 
					time-(int)callLinkCircleRadius, (int)callLinkCircleRadius*2, 
					(int)callLinkCircleRadius*2);
			if(!call.getWay().equals(CallWay.Self)){
				pen.drawLine(labelWidth/2, time, 
						labelWidth*right, time);
			}else{
				pen.drawLine(labelWidth/2, time, 
						((labelWidth/4)*3), time);
				pen.drawString(name+"( )", (labelWidth/2)+(int)(objectControlLineWidth/2), 
						time-(int)callLinkCircleRadius);
			}
			
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
			pen.drawString(name+"( )", (labelWidth/2)*(1-right)+(int)objectControlLineWidth/2, time-(int)callLinkCircleRadius);
		
		}else if(call.getType().equals(CallType.CallReceive) || 
				call.getType().equals(CallType.ReturnReceive)){
			if(!call.getWay().equals(CallWay.Self)){
				pen.drawString(name+"( )", (labelWidth/2)*(1-right)+
						(int)objectControlLineWidth/2, 
						time-(int)callLinkCircleRadius);
				pen.drawLine(labelWidth*(1-right), time, 
						labelWidth/2, time);
			}else{
				right = 0;
				int time0 = time;
				time = time+(int)callLinkCircleRadius*2;
				pen.drawLine(labelWidth/2, time, 
						((labelWidth/4)*3), time);
				pen.drawLine(((labelWidth/4)*3), time0, ((labelWidth/4)*3), time);
			}
			pen.drawLine(labelWidth/2, time, (int)(labelWidth/2+
					(Math.pow(-1, right)*((int)callLinkCircleRadius*2))), 
					time-((int)callLinkCircleRadius));
			pen.drawLine(labelWidth/2, time, (int)(labelWidth/2+
					(Math.pow(-1, right)*((int)callLinkCircleRadius*2))), 
					time+((int)callLinkCircleRadius));
		}else if(call.getType().equals(CallType.CallPass)){
			pen.drawLine(0, time, labelWidth, time);
		}
		refreshDrawing();
	}
	
	private void drawControlLine(SequenceDiagramObjectControlLine controlLine){
		int startTime = controlLine.getStartTime();
		int endTime = controlLine.getEndTime();
		int activeCalls = controlLine.getActiveCalls();
		if(controlLine.isActive())
			endTime = eventTimeController.getCurrentTime();
		int right = 1;
		if(controlLine.getCallWay().equals(CallWay.Left) || controlLine.getCallWay().equals(CallWay.Self))
			right = 0;
		pen.setColor(Color.lightGray);
		pen.fillRect(labelWidth/2-(int)(objectControlLineWidth/2)+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), startTime, (int)(objectControlLineWidth), endTime-startTime);
		pen.setColor(Color.gray);
		pen.drawRect(labelWidth/2-(int)(objectControlLineWidth/2)+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), startTime, (int)(objectControlLineWidth), endTime-startTime);
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
	
	private SequenceDiagramObjectControlLine getLastActiveControlLine(){
		//activeCalls = 0;
		SequenceDiagramObjectControlLine lastActiveControlLine = null;
		for(SequenceDiagramObjectControlLine cl: controlLineList){
			if(cl.isActive())
				lastActiveControlLine = cl;
		}
		return lastActiveControlLine;
	}
	
	private void refreshDrawing(){
		setIcon(new ImageIcon(drawableSpace));
	}
	
	public void setNewDrawableSpaceSize(){
		int newHeigth = eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth();
		setPreferredSize(new Dimension(labelWidth, newHeigth));
		drawableSpace = new BufferedImage(labelWidth, newHeigth, BufferedImage.TYPE_INT_ARGB);
		pen = (Graphics2D)drawableSpace.getGraphics();
		drawWholeObject();
	}
	
	public void prepareToReajustment(){
		callList.clear();
		controlLineList.clear();
		drawWholeObject();
	}
	
	public void refreshObject(){
		refreshDrawing();
	}
	
}
