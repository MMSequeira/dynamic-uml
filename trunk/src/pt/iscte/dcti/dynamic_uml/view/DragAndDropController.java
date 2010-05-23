package pt.iscte.dcti.dynamic_uml.view;

/**
 * This class implements a controller for the drag and drop action
 * @author Filipe Casal Ribeiro nº27035, José Monteiro nº11911, Luís Serrano nº11187
 *
 */
public class DragAndDropController {

	private boolean dragged = false;
	private int[] dragItemDropItem = {-1,-1};
	private SequenceDiagramView sequenceDiagramView;
	
	/**
	 * Constructor
	 * @param sequenceDiagramView
	 */
	public DragAndDropController(final SequenceDiagramView sequenceDiagramView){
		this.sequenceDiagramView = sequenceDiagramView;
	}
	
	/**
	 * Getter for the dragged item index
	 * @return dragItemIndex
	 */
	public int getDragItem() {
		return dragItemDropItem[0];
	}
	
	/**
	 * Getter for the dropped item index
	 * @return dropItemIndex
	 */
	public int getDropItem() {
		return dragItemDropItem[1];
	}
	
	/**
	 * Sets the index for the dragged item
	 * @param dragItem
	 */
	public void setDragItem(final int dragItem) {
		if(!dragged)
			this.dragItemDropItem[0] = dragItem;
		dragged = true;
	}
	
	/**
	 * Sets the index for the dropped item
	 * @param dragItem
	 */
	public void setDropItem(final int dropItem) {
		this.dragItemDropItem[1] = dropItem;
	}
	
	/**
	 * Resets the indexes and moves the objects
	 */
	public void reset(){
		if(dragItemDropItem[0] != dragItemDropItem[1])
			sequenceDiagramView.moveObject();
		dragged = false;
		dragItemDropItem[0] = -1;
		dragItemDropItem[1] = -1;
	}
	
}
