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
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;



public class SequenceDiagramView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int sequenceDiagramObjectID = 0;
	private final String frameName = "DynamicUML - Sequence Diagram";
	private boolean isVisibleOnInit = true;
	private long refreshStep = 300;
	
	private int initWindowWidth = 1024;
	private int initWindowHeight = 700;
	public static final int objectPanelWidth = 200;
	
	private JFrame principalFrame = new JFrame();
	private JPanel principalPanel = new JPanel();
	private JScrollPane scroll;
	
	private RefreshingThread refreshingThread;
	private boolean enableRefreshingThread = false;
	
	private DragAndDropController dragAndDropController = new DragAndDropController(this);
	
	private LinkedList<SequenceDiagramObject> sequenceDiagramObjectList =
		new LinkedList<SequenceDiagramObject>();
	
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
			final int initTime, final int givenObjectID){

		int objectID = newSequenceDiagramObjectID();
		//int objectID = givenObjectID;
		SequenceDiagramObject newObject = new SequenceDiagramObject(objectPanelWidth, 
				initTime, objectName, objectClass, objectID, mouseListener, 
				dragAndDropController, refreshingThread);
		sequenceDiagramObjectList.add(newObject);
		principalPanel.add(newObject, objectID);
		//Remove jlabels padding as needed
		if(principalPanel.getComponentCount() > sequenceDiagramObjectList.size())
			principalPanel.remove(principalPanel.getComponentCount()-1);
		//
		//newObject.drawWholeObject();
		refreshContentPane();
		return objectID;
	}

	/**
	 * 
	 * @param objectId
	 */
	public int killSequenceDiagramObject(final int objectID, final int destructTime){
		SequenceDiagramObject object = getSequenceDiagramObject(objectID);
		if(object != null){
			object.destruct(destructTime);
			refreshContentPane();
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
		pen.fillRect(0, 0, objectPanelWidth, 1000);
		for (int i = 0; i < (int)(initWindowWidth/objectPanelWidth); i++){
			padLabel = new JLabel(new ImageIcon(padImage));
			padLabel.setPreferredSize(new Dimension(objectPanelWidth, 1000));
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
	
	/**
	 * Returns the current SequenceDiagramObjectID used for the last object added.
	 * @return current_id
	 */
	public int getSequenceDiagramObjectID() {
		return sequenceDiagramObjectID;
	}
	
	/**
	 * Test main method, not used in the final version
	 * @param args
	 */
	public static void main(String[] args) {
		
		SequenceDiagramView view = new SequenceDiagramView();
		for(int i = 0; i < 10; i++){
			view.createSequenceDiagramObject("Object " + (i), "Class 1", 0, i);
			if(i > 3){
				view.killSequenceDiagramObject(i-1, 1);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class DragMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.COPY);
            
        }
    }

	/*
	public void addaptDiagramToChanges() {
		System.out.println("vou fazer o que tenho a fazer");
		int id = ((SequenceDiagramObject)(principalPanel.getComponent(0))).getID();
		System.out.println(id);
	}
	*/
	
	public void moveObject(){
		int numberOfComponents = principalPanel.getComponentCount();
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
	
}
