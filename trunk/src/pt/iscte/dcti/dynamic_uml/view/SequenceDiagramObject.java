package pt.iscte.dcti.dynamic_uml.view;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
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

/**
 * This class implements a representation of a sequence diagram object
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class SequenceDiagramObject extends JLabel{

	private static final long serialVersionUID = 1L;

	public static final int initObjectDrawableSpaceHeight = 1000;
	public static final int northBorder = 20;
	private static final float objectBoxWidthRatio = 0.5f;
	private static final float objectBoxHeightRatio = objectBoxWidthRatio/2;
	private static final float objectCrossWidthRatio = objectBoxHeightRatio/2;
	private static final float objectCrossHeightRatio = objectBoxHeightRatio/2;
	private static final float callLinkCircleRatio = 0.015f;
	private static final float objectControlLineWidthRatio = 0.1f;

	private static final float objectBoxWidth = objectBoxWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectBoxHeight = objectBoxHeightRatio*SequenceDiagramView.objectPanelWidth;
	private static final float objectCrossWidth = objectCrossWidthRatio*SequenceDiagramView.objectPanelWidth;
	public static final float objectCrossHeight = objectCrossHeightRatio*SequenceDiagramView.objectPanelWidth;
	private static final float callLinkCircleRadius = callLinkCircleRatio*SequenceDiagramView.objectPanelWidth;
	private static final float objectControlLineWidth = objectControlLineWidthRatio*SequenceDiagramView.objectPanelWidth;

	private int labelWidth;
	private int initTime;
	private int destructionTime = -1;
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
	 * @param initTime
	 * @param objectName
	 * @param objectClass
	 * @param objectID
	 * @param mouseListener
	 * @param dragAndDropController
	 * @param eventTimeController
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
		callList.add(new SequenceDiagramObjectCall("new", initTime, CallWay.Right, CallType.NewReceive, activeCalls));

		setPreferredSize(new Dimension(labelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth()));
		drawableSpace = new BufferedImage(labelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth(), BufferedImage.TYPE_INT_ARGB);
		setIcon(new ImageIcon(drawableSpace));
		pen = (Graphics2D)drawableSpace.getGraphics();

		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setToolTipText("<html>ID: " + objectID + "<br>Name: " + this.objectName + "<br>Class: " + this.objectClass + "</html>");
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

	/**
	 * Refreshes the object life line
	 */
	public void refreshObjectLifeLine(){
		drawObjectLifeLine();
	}

	/**
	 * Simulates the destruction of the object
	 * @param destructionTime
	 */
	public void destroy(final int destructionTime){
		this.destructionTime = destructionTime;
		drawObjectDestructionCross();
	}

	/**
	 * Creates a new representation of a call that can passes through the object
	 * @param callName
	 * @param time
	 * @param way
	 * @param type
	 */
	public void newCall(final String callName, final int time, final CallWay way, final CallType type){
		SequenceDiagramObjectCall newCall = new SequenceDiagramObjectCall(callName, time, way, type, activeCalls);
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

	/**
	 * Getter for the ID of the object
	 * @return
	 */
	public int getID(){
		return objectID;
	}

	/**
	 * Setter for the object name field
	 * @param new_name
	 */
	public void setObjectName(String new_name) {
		objectName = new_name;
		drawObjectBox();
	}

	/**
	 * Setter for the object class field
	 * @param new_class
	 */
	public void setObjectClass(String new_class) {
		objectClass = new_class;
		drawObjectBox();
	}

	/**
	 * Sets a new size for the object's drawable space
	 */
	public void setNewDrawableSpaceSize(){
		int newHeigth = eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth();
		setPreferredSize(new Dimension(labelWidth, newHeigth));
		drawableSpace = new BufferedImage(labelWidth, newHeigth, BufferedImage.TYPE_INT_ARGB);
		pen = (Graphics2D)drawableSpace.getGraphics();
		drawWholeObject();
	}

	/**
	 * Prepares the object for re-arrangement. This method is used for re-arranging
	 * the object's calls after drag and drop actions
	 */
	public void prepareForRearrangement(){
		callList.clear();
		controlLineList.clear();
		drawWholeObject();
	}

	/**
	 * Draws the whole object
	 */
	public void drawWholeObject(){
		cleanObjectSpace();
		drawObjectBox();
		drawObjectLifeLine();
		drawObjectControlLines();
		drawObjectCalls();
		if (!isAlive())
			drawObjectDestructionCross();
	}

	/**
	 * Draws the object box
	 */
	private void drawObjectBox(){
		pen.setColor(Color.black);
		pen.drawRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime, 
				(int)(objectBoxWidth), (int)(objectBoxHeight));
		pen.setColor(Color.orange);
		pen.fillRect((int)(labelWidth*(1-objectBoxWidthRatio))/2, 
				initTime, 
				(int)(objectBoxWidth), (int)(objectBoxHeight));
		pen.setColor(Color.black);
		pen.drawString(objectID + " - " + objectName, 
				((int)(labelWidth*(1-objectBoxWidthRatio))/2) + 1, 
				initTime + ((int)(objectBoxHeight)/2));
		refreshDrawing();
	}

	/**
	 * Draws the object life line
	 */
	private void drawObjectLifeLine(){
		int lifeLineEndTime;
		if(isAlive())
			lifeLineEndTime = eventTimeController.getCurrentTime();
		else
			lifeLineEndTime = destructionTime;
		pen.setColor(Color.black);
		pen.drawLine(labelWidth/2, (int)(initTime+objectBoxHeight), labelWidth/2, 
				lifeLineEndTime);
		refreshDrawing();
	}

	/**
	 * Draws the object life lines
	 */
	private void drawObjectControlLines(){
		for(SequenceDiagramObjectControlLine controlLine: controlLineList)
			drawControlLine(controlLine);
		refreshDrawing();
	}

	/**
	 * Draws the object calls
	 */
	private void drawObjectCalls(){
		for(SequenceDiagramObjectCall call: callList)
			drawCall(call);
		refreshDrawing();
	}

	/**
	 * Draws the object drop selection
	 */
	private void drawObjectDropSelection(){
		pen.setColor(Color.lightGray);
		pen.fillRect(0, 1, (int)(objectBoxHeight)/4, (int)(objectBoxHeight)/4);
		pen.drawLine(0, 1, 0, (int)(eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth()));
		pen.drawLine(0, 1, (int)(objectBoxWidth), 1);
		refreshDrawing();
	}

	/**
	 * Draws the object destruction cross
	 */
	private void drawObjectDestructionCross(){
		pen.setColor(Color.red);
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), destructionTime-(int)(objectCrossHeight)/2, 
				labelWidth/2 + ((int)(objectCrossWidth)), 
				(destructionTime) + ((int)(objectCrossHeight)/2));
		pen.drawLine(labelWidth/2 - (int)(objectCrossWidth), (destructionTime) + 
				((int)(objectCrossHeight)/2), labelWidth/2 + ((int)(objectCrossWidth)), 
				destructionTime-(int)(objectCrossHeight)/2);
		refreshDrawing();
	}

	/**
	 * Draws the given call
	 * @param call
	 */
	private void drawCall(final SequenceDiagramObjectCall call){
		int right = 0;
		if(call.getWay().equals(CallWay.Right))
			right = 1;
		int time = call.getTime();
		int activeCalls = call.getNumberOfActiveCalls();
		String name = call.getName();
		float dashed[] = {5.0f,5.0f,5.0f};
		BasicStroke normalStroke = new BasicStroke();
		BasicStroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, dashed, 10.0f);
		pen.setColor(Color.black);
		if(call.getType().equals(CallType.NewSend) || 
				call.getType().equals(CallType.CallSend) || 
				call.getType().equals(CallType.ReturnSend)){
			if (call.getType().equals(CallType.ReturnSend)){
				pen.setStroke(dashedStroke);
			}
			if(!call.getWay().equals(CallWay.Self)){
				pen.drawLine(labelWidth/2-((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), time, 
						labelWidth*right, time);
			}else{
				pen.drawLine((int)((labelWidth/2) + (activeCalls)*((int)objectControlLineWidth/2)), time, 
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
			if(call.getType().equals(CallType.CallReceive)){
				activeCalls++;
			}
			if (call.getType().equals(CallType.ReturnReceive)){
				pen.setStroke(dashedStroke);
			}
			if(!call.getWay().equals(CallWay.Self)){
				pen.drawString(name+"( )", (labelWidth/2)*(1-right)+
						(int)objectControlLineWidth/2, 
						time-(int)callLinkCircleRadius);
				pen.drawLine(labelWidth*(1-right), time, 
						labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), time);
			}else{
				right = 0;
				int time0 = time;
				time = time+(int)callLinkCircleRadius*2;
				pen.drawLine(labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), time, 
						((labelWidth/4)*3), time);
				pen.drawLine(((labelWidth/4)*3), time0, ((labelWidth/4)*3), time);
			}
			pen.setStroke(normalStroke);
			pen.drawLine(labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), time, (int)(labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls))+
					(Math.pow(-1, right)*((int)callLinkCircleRadius*2))), 
					time-((int)callLinkCircleRadius));
			pen.drawLine(labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls)), time, (int)(labelWidth/2+((int)Math.pow(-1, right)*((int)(objectControlLineWidth/2)*activeCalls))+
					(Math.pow(-1, right)*((int)callLinkCircleRadius*2))), 
					time+((int)callLinkCircleRadius));
		}else if(call.getType().equals(CallType.CallPass)){
			pen.drawLine(0, time, labelWidth, time);
		}
		pen.setStroke(normalStroke);
		refreshDrawing();
	}

	/**
	 * Draws the given control line
	 * @param controlLine
	 */
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

	/**
	 * Returns the last active control line for the object
	 * @return lastActiveControlLine
	 */
	private SequenceDiagramObjectControlLine getLastActiveControlLine(){
		//activeCalls = 0;
		SequenceDiagramObjectControlLine lastActiveControlLine = null;
		for(SequenceDiagramObjectControlLine cl: controlLineList){
			if(cl.isActive())
				lastActiveControlLine = cl;
		}
		return lastActiveControlLine;
	}

	/**
	 * Returns the number of active control lines for the object
	 * @return activeCalls
	 */
	private int getNumberOfActiveControlLines(){
		int activeCalls = 0;
		for(SequenceDiagramObjectControlLine cl: controlLineList){
			if(cl.isActive())
				activeCalls++;
		}
		return activeCalls;
	}

	/**
	 * Cleans the object's drawable space
	 */
	private void cleanObjectSpace(){
		pen.setColor(backgroundColor);
		pen.fillRect(0, 0, labelWidth, drawableSpace.getHeight());
		refreshDrawing();
	}

	/**
	 * Cleans the object's drop selection
	 */
	private void cleanObjectDropSelection(){
		drawWholeObject();
		refreshDrawing();
	}

	/**
	 * Returns true if the object hasn't been destroyed 
	 * @return boolean
	 */
	private boolean isAlive(){
		return destructionTime == -1;
	}

	/**
	 * Refreshes the object's drawable space
	 */
	private void refreshDrawing(){
		setIcon(new ImageIcon(drawableSpace));
	}

}
