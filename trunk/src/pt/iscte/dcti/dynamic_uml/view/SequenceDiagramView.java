package pt.iscte.dcti.dynamic_uml.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;


import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

/**
 * This is the main class for the DynamicUML - Sequence Diagram Viewer
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class SequenceDiagramView {

	private static int sequenceDiagramObjectID = 0;
	private static int sequenceDiagramCallID = 0;
	private final String frameName = "DynamicUML - Sequence Diagram";
	private boolean isVisibleOnInit = true;

	private int initWindowWidth = 1024;
	private int initWindowHeight = 700;
	public static final int objectPanelWidth = 250;

	private JFrame principalFrame = new JFrame();
	private JPanel principalPanel = new JPanel();
	private JScrollPane scroll;

	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	private LinkedList<SequenceDiagramCall> sequenceDiagramCallList =
		new LinkedList<SequenceDiagramCall>();

	private DragAndDropController dragAndDropController = new DragAndDropController(this);
	private EventTimeController eventTimeController = new EventTimeController(sequenceDiagramObjectList);

	private MouseListener mouseListener = new DragMouseAdapter();

	/**
	 * Constructor for the user interface
	 */
	public SequenceDiagramView (){
		initialization();
	}

	/**
	 * Creates an internal representation of a sequence diagram object 
	 * and returns the id for that object
	 * @param objectName
	 * @param objectClass
	 * @param callerObjectID
	 * @return objectID
	 */
	public int createSequenceDiagramObject(final String objectName, final String objectClass, 
			final int callerObjectID){

		int objectID = newSequenceDiagramObjectID();
		int callID = newSequenceDiagramCallID();

		int initTime = eventTimeController.eventTime(SequenceDiagramEvent.NewObject);
		SequenceDiagramObject newObject = new SequenceDiagramObject(initTime, objectName, objectClass, objectID, mouseListener, 
				dragAndDropController, eventTimeController);
		sequenceDiagramObjectList.add(newObject);
		principalPanel.add(newObject, objectID);
		//Remove jlabels padding as needed
		if(principalPanel.getComponentCount() > sequenceDiagramObjectList.size())
			principalPanel.remove(principalPanel.getComponentCount()-1);
		//
		SequenceDiagramObject callerObject;
		if(callerObjectID >= 0 && callerObjectID <= sequenceDiagramObjectID){
			callerObject = getSequenceDiagramObject(callerObjectID);
			callerObject.newCall("new",eventTimeController.getPreviousTime(), 
					CallWay.Right, CallType.NewSend);
		}else{
			callerObject = null;
		}
		sequenceDiagramCallList.add(new SequenceDiagramCall(callID, "new", 
				getSequenceDiagramObject(callerObjectID), newObject, initTime, 
				CallWay.Right, CallType.NewSend));

		passCallsIfNeeded(eventTimeController.getPreviousTime(),callerObjectID,objectID);
		refreshObjectsLifeLines();
		refreshContentPane();
		return objectID;
	}

	/**
	 * Destroys the object which has the given ID
	 * @param objectID
	 * @return code
	 */
	public int killSequenceDiagramObject(final int objectID){
		SequenceDiagramObject object = getSequenceDiagramObject(objectID);
		if(object != null){
			object.destroy(eventTimeController.eventTime(SequenceDiagramEvent.KillObject));
			refreshObjectsLifeLines();
			refreshContentPane();
			return 1;
		}else{
			return -1;
		}
	}

	/**
	 * Creates an internal representation of a call between two objects
	 * @param methodName
	 * @param callerID
	 * @param calleeID
	 * @return callID
	 */
	public int createCall(final String methodName, final int callerID, final int calleeID){
		int callID = newSequenceDiagramCallID();
		SequenceDiagramObject caller;
		SequenceDiagramObject callee;
		int callerIndex;
		int calleeIndex;
		if(callerID >= 0 && callerID <= sequenceDiagramObjectID){
			caller = getSequenceDiagramObject(callerID);
			callerIndex = getSequenceDiagramObjectIndex(callerID);
		}else{
			caller = null;
			callerIndex = -1;
		}
		if(calleeID >= 0 && calleeID <= sequenceDiagramObjectID){
			callee = getSequenceDiagramObject(calleeID);
			calleeIndex = getSequenceDiagramObjectIndex(calleeID);
		}else{
			callee = null;
			calleeIndex = -1;
		}
		int callTime = eventTimeController.eventTime(SequenceDiagramEvent.Call);
		CallWay way;
		if(callerIndex < calleeIndex)
			way = CallWay.Right;
		else if(callerIndex == calleeIndex)
			way = CallWay.Self;
		else
			way = CallWay.Left;

		SequenceDiagramCall newCall = new SequenceDiagramCall(callID, methodName, 
				caller, callee, callTime, way, CallType.CallSend);
		sequenceDiagramCallList.add(newCall);
		if(callerID >= 0 && callerID <= sequenceDiagramObjectID)
			caller.newCall(methodName, callTime, way, CallType.CallSend);
		if(calleeID >= 0 && calleeID <= sequenceDiagramObjectID)
			callee.newCall(methodName, callTime, way, CallType.CallReceive);
		passCallsIfNeeded(callTime, callerID, calleeID);

		return callID;
	}

	/**
	 * Creates an internal representation of a return from the call with the
	 * given ID
	 * @param callID
	 * @return code
	 */
	public int createReturn(final int callID){
		SequenceDiagramCall call = getSequenceDiagramCall(callID);
		if(call != null){
			SequenceDiagramObject newCaller = call.getCallee();
			SequenceDiagramObject newCallee = call.getCaller();
			int newCallID = newSequenceDiagramCallID();
			String methodName = call.getMethodName();
			int callTime = eventTimeController.eventTime(SequenceDiagramEvent.Call);
			CallWay callWay = call.getCallWay();
			if(callWay.equals(CallWay.Right))
				callWay = CallWay.Left;
			else if (callWay.equals(CallWay.Left))
				callWay = CallWay.Right;
			else
				callWay = CallWay.Self;
			SequenceDiagramCall newCall = new SequenceDiagramCall(newCallID, methodName, 
					newCaller, newCallee, callTime, callWay, CallType.ReturnSend);
			sequenceDiagramCallList.add(newCall);
			int callerID;
			if(newCaller != null){
				newCaller.newCall("return "+methodName, callTime, callWay, CallType.ReturnSend);
				callerID = newCaller.getID();
			}else{
				callerID = -1;
			}
			int calleeID;
			if(newCallee != null){
				newCallee.newCall("return "+methodName, callTime, callWay, CallType.ReturnReceive);
				calleeID = newCallee.getID();
			}else{
				calleeID = -1;
			}
			passCallsIfNeeded(callTime, callerID, calleeID);
			return 1;
		}else{
			return -1;
		}
	}

	/**
	 * Configures primary window
	 */
	private void initialization(){
		principalFrame.setTitle(frameName);
		principalFrame.setSize(initWindowWidth, initWindowHeight);
		principalFrame.setLayout(new GridLayout(1,1));
		principalPanel.setLayout(new GridLayout());
		//Blank jlabels padding
		JLabel padLabel;
		BufferedImage padImage = new BufferedImage(objectPanelWidth,SequenceDiagramObject.initObjectDrawableSpaceHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D pen = (Graphics2D)padImage.getGraphics();
		pen.setColor(Color.white);
		pen.fillRect(0, 0, objectPanelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth());
		for (int i = 0; i < (((int)(initWindowWidth/objectPanelWidth))+1); i++){
			padLabel = new JLabel(new ImageIcon(padImage));
			padLabel.setPreferredSize(new Dimension(objectPanelWidth, eventTimeController.getSequenceDiagramObjectDrawableSpaceHeigth()));
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
	 * Returns the sequence diagram object with the given ID
	 * @param objectID
	 * @return SequenceDiagramObject
	 */
	public SequenceDiagramObject getSequenceDiagramObject(final int objectID){
		for(SequenceDiagramObject object: sequenceDiagramObjectList)
			if (object.getID() == objectID)
				return object;
		return null;
	}

	/**
	 * Returns the current index of the sequence diagram object 
	 * with the given ID
	 * @param objectID
	 * @return index
	 */
	private int getSequenceDiagramObjectIndex(final int objectID){
		int numberOfComponents = sequenceDiagramObjectList.size();
		for(int i = 0; i < numberOfComponents; i++){
			if(((SequenceDiagramObject)((principalPanel.getComponent(i)))).getID() == 
				objectID){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the sequence diagram call with the given ID
	 * @param callID
	 * @return SequenceDiagramCall
	 */
	private SequenceDiagramCall getSequenceDiagramCall(final int callID){
		for(SequenceDiagramCall call: sequenceDiagramCallList)
			if (call.getID() == callID)
				return call;
		return null;
	}

	/**
	 * This method allows the objects between the caller and the callee to
	 * draw the call line
	 * @param time
	 * @param callerID
	 * @param calleeID
	 */
	private void passCallsIfNeeded(final int time, final int callerID, final int calleeID){
		int callerIndex = getSequenceDiagramObjectIndex(callerID);
		int calleeIndex = getSequenceDiagramObjectIndex(calleeID);
		//tests if they objects have other objects between them
		int difference = calleeIndex-callerIndex;
		if(Math.abs(difference) > 1){
			if(difference > 0){
				for(int i = (callerIndex+1); i < calleeIndex; i++){
					((SequenceDiagramObject)(principalPanel.getComponent(i))).
					newCall("", time, CallWay.Right, 
							CallType.CallPass);
				}
			}else{
				for(int i = (calleeIndex+1); i < callerIndex; i++){
					((SequenceDiagramObject)(principalPanel.getComponent(i))).
					newCall("", time, CallWay.Right, 
							CallType.CallPass);
				}
			}
		}
	}

	/**
	 * Decides what will be the id for the created object
	 * @return objectID
	 */
	private int newSequenceDiagramObjectID(){
		return sequenceDiagramObjectID++;
	}

	/**
	 * Decides what will be the id for the created call
	 * @return callID
	 */
	private int newSequenceDiagramCallID(){
		return sequenceDiagramCallID++;
	}

	/**
	 * Refreshes all the sequence diagram object's life lines
	 */
	private void refreshObjectsLifeLines(){
		for(SequenceDiagramObject object: sequenceDiagramObjectList)
			object.refreshObjectLifeLine();
	}

	/**
	 * Refreshes the principal frame's content pane
	 */
	private void refreshContentPane(){
		principalFrame.setContentPane(principalFrame.getContentPane());
	}

	/**
	 * This class allows the objects to be dragged and dropped
	 * 
	 */
	private class DragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	/**
	 * Moves the objects, according to the dragItem and dropItem of the
	 * DragAndDropController
	 */
	public void moveObject(){
		int numberOfComponents = sequenceDiagramObjectList.size();
		int indexDrag = -1;
		int indexDrop = -1;
		boolean gotDrag = false;
		boolean gotDrop = false;
		SequenceDiagramObject draggedObject = null;
		for(int i = 0; i < numberOfComponents; i++){
			if(((SequenceDiagramObject)((principalPanel.getComponent(i)))).getID() == 
				dragAndDropController.getDragItem()){
				indexDrag = i;
				draggedObject = (SequenceDiagramObject)((principalPanel.getComponent(i)));
				gotDrag = true;
			}
			if(((SequenceDiagramObject)((principalPanel.getComponent(i)))).getID() == 
				dragAndDropController.getDropItem()){
				indexDrop = i;
				gotDrop = true;
			}
			if(gotDrag && gotDrop)
				i = numberOfComponents;
		}
		if(draggedObject != null)
			principalPanel.add(draggedObject, indexDrop);
		else
			JOptionPane.showMessageDialog(null, "Fatal Error: Drag and Drop");

		if(indexDrag != indexDrop)
			addaptDiagramToChanges(indexDrag,indexDrop);

		refreshContentPane();
	}

	/**
	 * Makes the needed changes in the diagram calls, according with the 
	 * dragged and dropped items
	 * @param indexDrag
	 * @param indexDrop
	 */
	private void addaptDiagramToChanges(final int indexDrag, final int indexDrop) {
		int minorIndex = indexDrop;
		int majorIndex = indexDrag;

		if(indexDrag < indexDrop){
			majorIndex = indexDrop;
			minorIndex = indexDrag;
		}

		SequenceDiagramObject object;

		for(int i = minorIndex; i <= majorIndex; i++){
			object = ((SequenceDiagramObject)(principalPanel.getComponent(i)));
			object.prepareForRearrangement();
		}

		SequenceDiagramCall call;
		SequenceDiagramCall newCall;
		LinkedList<SequenceDiagramCall> newCallList = new LinkedList<SequenceDiagramCall>();

		for(int i = 0; i < sequenceDiagramCallList.size(); i++){
			call = sequenceDiagramCallList.get(i);
			newCall = changeCall(call);
			applyCall(newCall);
			newCallList.add(newCall);
		}

		for(int i = 0; i < sequenceDiagramObjectList.size(); i++){
			object = ((SequenceDiagramObject)(principalPanel.getComponent(i)));
			object.drawWholeObject();
		}

		sequenceDiagramCallList = newCallList;
	}

	/**
	 * Creates a new call, based on the old one, with the needed changes due to the 
	 * drag and drop event
	 * @param call
	 * @return SequenceDiagramCall
	 */
	private SequenceDiagramCall changeCall(final SequenceDiagramCall call) {
		int callID = call.getID();
		String methodName = call.getMethodName();
		SequenceDiagramObject caller = call.getCaller();
		SequenceDiagramObject callee = call.getCallee();
		int callTime = call.getCallTime();
		CallWay callWay;
		CallType callType = call.getCallType();
		int callerIndex;
		int calleeIndex;
		if(caller != null)
			callerIndex = getSequenceDiagramObjectIndex(caller.getID());
		else
			callerIndex = -1;
		if(callee != null)
			calleeIndex = getSequenceDiagramObjectIndex(callee.getID());
		else
			calleeIndex = -1;

		if(callerIndex < calleeIndex)
			callWay = CallWay.Right;
		else if(callerIndex == calleeIndex)
			callWay = CallWay.Self;
		else
			callWay = CallWay.Left;

		return new SequenceDiagramCall(callID, methodName, caller, callee, callTime, callWay, callType);
	}

	/**
	 * Applies (draws) the call. This method is inly used for drag and drop purposes
	 * @param newCall
	 */
	private void applyCall(final SequenceDiagramCall newCall) {
		String callName = newCall.getMethodName();
		SequenceDiagramObject caller = newCall.getCaller();
		SequenceDiagramObject callee = newCall.getCallee();
		int callTime = newCall.getCallTime();
		CallWay way = newCall.getCallWay();
		CallType type = newCall.getCallType();
		int callerID;
		int calleeID;

		if(caller != null){
			callerID = caller.getID();
			String callNameCopy = callName;
			if(type.equals(CallType.ReturnSend) && way.equals(CallWay.Self))
				callName = "return "+callName;
			caller.newCall(callName, callTime, way, type);
			callName = callNameCopy;
		}else
			callerID = -1;

		if(callee != null){
			calleeID = callee.getID();
			CallType calleeCallType;

			if(type.equals(CallType.CallSend))
				calleeCallType = CallType.CallReceive;
			else if(type.equals(CallType.ReturnSend)){
				calleeCallType = CallType.ReturnReceive;
				callName = "return "+callName;
			}else
				calleeCallType = CallType.NewReceive;

			callee.newCall(callName, callTime, way, calleeCallType);
		}else
			calleeID = -1;

		passCallsIfNeeded(callTime, callerID, calleeID);
	}

	/**
	 * Test main method
	 * @param args
	 */
	public static void main(String[] args) {

		SequenceDiagramView view = new SequenceDiagramView();
		view.createSequenceDiagramObject("Objecto 0", "Classe 0", -1);
		view.createReturn(view.createCall("selfFunction", 0, 0));
		view.createSequenceDiagramObject("Objecto 1", "Classe 1", -1);
		view.createSequenceDiagramObject("Objecto 2", "Classe 2", 1);
		int callID0 = view.createCall("funcao0", -1, 1);
		int callID1 = view.createCall("funcao1", 1, 2);
		view.createReturn(callID1);
		int callID4 = view.createCall("funcao2", 1, 1);
		int callID6 = view.createCall("funcao6", 1, 2);
		view.createReturn(view.createCall("Test", 2, 2));
		int callID5 = view.createCall("funcao5", 2, 1);
		view.createReturn(view.createCall("TestLeftControlLine", 1, 2));
		view.createReturn(callID5);
		view.createReturn(callID6);
		view.createReturn(callID4);
		int callID2 = view.createCall("funcao3", 1, 2);
		view.createReturn(callID2);
		view.createReturn(callID0);
		int callID3 = view.createCall("funcao4", -1, 2);
		int callID7 = view.createCall("funcaoMain", 2, -1);
		view.createReturn(callID7);
		view.createReturn(callID3);
		view.killSequenceDiagramObject(0);
		view.killSequenceDiagramObject(1);
		view.killSequenceDiagramObject(2);
		view.createSequenceDiagramObject("Objecto 3", "Classe 3", -1);
		view.createSequenceDiagramObject("Objecto 4", "Classe 4", 3);
		int callID9 = view.createCall("Funcao", -1, 3);
		int callID8 = view.createCall("Funcao", 3, 4);
		view.createReturn(view.createCall("Funcao", 4, 3));
		view.createReturn(callID8);
		view.createReturn(callID9);
		view.killSequenceDiagramObject(3);
		view.killSequenceDiagramObject(4);

	}

}
