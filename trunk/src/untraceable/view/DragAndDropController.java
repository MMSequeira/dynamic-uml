package untraceable.view;

public class DragAndDropController {

	private boolean dragged = false;
	private int[] dragItemDropItem = {-1,-1};
	private SequenceDiagramView sequenceDiagramView;
	
	public DragAndDropController(final SequenceDiagramView sequenceDiagramView){
		this.sequenceDiagramView = sequenceDiagramView;
	}
	
	public int getDragItem() {
		return dragItemDropItem[0];
	}
	
	public int getDropItem() {
		return dragItemDropItem[1];
	}
	
	public void setDragItem(final int dragItem) {
		if(!dragged)
			this.dragItemDropItem[0] = dragItem;
		dragged = true;
	}
	
	public void setDropItem(final int dropItem) {
		this.dragItemDropItem[1] = dropItem;
	}
	
	public void reset(){
		if(dragItemDropItem[0] != dragItemDropItem[1])
			sequenceDiagramView.moveObject();
		dragged = false;
		dragItemDropItem[0] = -1;
		dragItemDropItem[1] = -1;
	}
	
}
