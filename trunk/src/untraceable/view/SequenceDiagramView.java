package untraceable.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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




public class SequenceDiagramView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int sequenceDiagramObjectID = 0;
	private static int sequenceDiagramCallID = 0;
	private final String frameName = "DynamicUML - Sequence Diagram";
	private boolean isVisibleOnInit = true;
	private long refreshStep = 300;
	
	private int initWindowWidth = 1024;
	private int initWindowHeight = 700;
	public static final int objectPanelWidth = 250;
	
	private JFrame principalFrame = new JFrame();
	private JPanel principalPanel = new JPanel();
	private JScrollPane scroll;
	
	private RefreshingThread refreshingThread;
	private boolean enableRefreshingThread = false;
	
	private DragAndDropController dragAndDropController = new DragAndDropController(this);
	private EventTimeController eventTimeController = new EventTimeController();
	
	//not to be public
	public LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	//not to be public
	private LinkedList<SequenceDiagramCall> sequenceDiagramCallList =
		new LinkedList<SequenceDiagramCall>();
	
	private MouseListener mouseListener = new DragMouseAdapter();
	
	/**
	 * Constructor for the user interface
	 */
	public SequenceDiagramView (){
		initialization();
		if(enableRefreshingThread){
			refreshingThread = new RefreshingThread(sequenceDiagramObjectList, refreshStep, 
					principalFrame);
			refreshingThread.start();
		}
	}
	
	/**
	 * Creates an internal sequence diagram object and returns the id for that object
	 * @param objectName
	 * @param objectClass
	 * @param initTime
	 * @return
	 */
	public int createSequenceDiagramObject(final String objectName, final String objectClass, 
			final int callerObjectID){

		int objectID = newSequenceDiagramObjectID();
		int callID = newSequenceDiagramCallID();
		
		int initTime = eventTimeController.eventTime(SequenceDiagramEvent.NewObject);
		//int initTime = 0;
		SequenceDiagramObject newObject = new SequenceDiagramObject(initTime, objectName, objectClass, objectID, mouseListener, 
				dragAndDropController, eventTimeController, refreshingThread);
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
				CallWay.Right));
		
		passCallsIfNeeded(eventTimeController.getPreviousTime(),callerObjectID,objectID);
		refreshObjectsLifeLines();
		refreshContentPane();
		return objectID;
	}

	/**
	 * 
	 * @param objectId
	 */
	public int killSequenceDiagramObject(final int objectID){
		SequenceDiagramObject object = getSequenceDiagramObject(objectID);
		if(object != null){
			object.destruct(eventTimeController.eventTime(SequenceDiagramEvent.KillObject));
			refreshObjectsLifeLines();
			refreshContentPane();
			return 1;
		}else{
			return -1;
		}
	}
	
	public int createCall(final String methodName, final int callerID, final int calleeID){
		int callID = newSequenceDiagramCallID();
		SequenceDiagramObject caller;
		int callerIndex;
		if(callerID >= 0 && callerID <= sequenceDiagramObjectID){
			caller = getSequenceDiagramObject(callerID);
			callerIndex = getSequenceDiagramObjectIndex(callerID);
		}else{
			caller = null;
			callerIndex = -1;
		}
		SequenceDiagramObject callee = getSequenceDiagramObject(calleeID);
		int callTime = eventTimeController.eventTime(SequenceDiagramEvent.Call);
		int calleeIndex = getSequenceDiagramObjectIndex(calleeID);
		CallWay way;
		if(callerIndex < calleeIndex){
			way = CallWay.Right;
		}else if(callerIndex == calleeIndex){
			//Case where the object calls a method from itself
			way = CallWay.Self;
		}else{
			way = CallWay.Left;
		}
		SequenceDiagramCall newCall = new SequenceDiagramCall(callID, methodName, 
				caller, callee, callTime, way);
		sequenceDiagramCallList.add(newCall);
		if(callerID >= 0 && callerID <= sequenceDiagramObjectID)
			caller.newCall(methodName, callTime, way, CallType.CallSend);
		callee.newCall(methodName, callTime, way, CallType.CallReceive);
		passCallsIfNeeded(callTime, callerID, calleeID);
		
		return callID;
	}
	
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
			else
				callWay = CallWay.Right;
			SequenceDiagramCall newCall = new SequenceDiagramCall(newCallID, methodName, 
					newCaller, newCallee, callTime, callWay);
			sequenceDiagramCallList.add(newCall);
			newCaller.newCall("return "+methodName, callTime, callWay, CallType.ReturnSend);
			int calleeID;
			if(newCallee != null){
				newCallee.newCall("return "+methodName, callTime, callWay, CallType.ReturnReceive);
				calleeID = newCallee.getID();
			}else{
				calleeID = -1;
			}
			passCallsIfNeeded(callTime, newCaller.getID(), calleeID);
			return 1;
		}else{
			return -1;
		}
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
		BufferedImage padImage = new BufferedImage(objectPanelWidth,1000,BufferedImage.TYPE_INT_ARGB);
		Graphics2D pen = (Graphics2D)padImage.getGraphics();
		pen.setColor(Color.white);
		pen.fillRect(0, 0, objectPanelWidth, SequenceDiagramObject.initObjectDrawableSpaceHeigth);
		for (int i = 0; i < (int)(initWindowWidth/objectPanelWidth); i++){
			padLabel = new JLabel(new ImageIcon(padImage));
			padLabel.setPreferredSize(new Dimension(objectPanelWidth, SequenceDiagramObject.initObjectDrawableSpaceHeigth));
			principalPanel.add(padLabel);
		}
		//
		scroll = new JScrollPane(principalPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		principalFrame.add(scroll);
		principalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		principalFrame.setVisible(isVisibleOnInit);
	}
	
	private SequenceDiagramObject getSequenceDiagramObject(final int objectID){
		for(SequenceDiagramObject object: sequenceDiagramObjectList)
			if (object.getID() == objectID)
				return object;
		return null;
	}
	
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
	
	private SequenceDiagramCall getSequenceDiagramCall(final int callID){
		for(SequenceDiagramCall call: sequenceDiagramCallList)
			if (call.getID() == callID)
				return call;
		return null;
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
				/*
				refreshingThread.frameAliveness(false);
				*/
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
	}
	
	private void refreshObjectsLifeLines(){
		for(SequenceDiagramObject object: sequenceDiagramObjectList){
			object.refreshObjectLifeLine();
		}
	}
	
	private void refreshContentPane(){
		principalFrame.setContentPane(principalFrame.getContentPane());
	}

	/**
	 * Decides what will be the id for the created object
	 * @return id
	 */
	private int newSequenceDiagramObjectID(){
		return sequenceDiagramObjectID++;
	}
	
	private int newSequenceDiagramCallID(){
		return sequenceDiagramCallID++;
	}
	
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
	 * Test main method, not used in the final version
	 * @param args
	 */
	public static void main(String[] args) {
		
		SequenceDiagramView view = new SequenceDiagramView();

		/*view.createSequenceDiagramObject("Objecto 0", "Classe 0", -1);
		view.createSequenceDiagramObject("Objecto 1", "Classe 1", 0);
		view.createSequenceDiagramObject("Objecto 2", "Classe 2", 1);
		view.createSequenceDiagramObject("Objecto 3", "Classe 3", 2);
		int callID = view.createCall("coiso", 2, 3);
		view.createReturn(callID);
		callID = view.createCall("coisinho", 0, 3);
		view.createReturn(callID);
		view.killSequenceDiagramObject(0);
		view.killSequenceDiagramObject(1);
		view.killSequenceDiagramObject(2);
		view.killSequenceDiagramObject(3);*/

		view.createSequenceDiagramObject("Objecto 0", "Classe 0", -1);
		view.createSequenceDiagramObject("Objecto 1", "Classe 1", -1);
		view.createSequenceDiagramObject("Objecto 2", "Classe 2", 1);
		
		int callID0 = view.createCall("funcao0", -1, 1);
		int callID1 = view.createCall("funcao1", 1, 2);
		view.createReturn(callID1);
		int callID2 = view.createCall("funcao2", 1, 2);
		view.createReturn(callID2);
		view.createReturn(callID0);
		int callID3 = view.createCall("funcao3", -1, 2);
		view.createReturn(callID3);
		
		view.killSequenceDiagramObject(0);
		view.killSequenceDiagramObject(1);
		view.killSequenceDiagramObject(2);
		
		//view.printCalls();
	}
	
	private class DragMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.COPY);
        }
    }

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
		System.out.println("Drag index " + indexDrag + " on index " + indexDrop);
		if(draggedObject != null){
			principalPanel.add(draggedObject, indexDrop);
		}else{
			JOptionPane.showMessageDialog(null, "Error: Drag and Drop");
		}
		
		refreshContentPane();
	}
	
	//not to be in the final version
	public void printCalls(){
		for(SequenceDiagramCall c: sequenceDiagramCallList){
			System.out.println(c.toString());
		}
	}
	
}
