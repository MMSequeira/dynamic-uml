package examples.nested_constructor2;

public class Cell {
    @SuppressWarnings("unused")
	private MicroCell micro_cell1;
    @SuppressWarnings("unused")
	private MicroCell micro_cell2;
	
	public Cell() {
		createMicroCells();
	}
	
	private void createMicroCells() {
		micro_cell1 = new MicroCell();
		micro_cell2 = new MicroCell();
	}
	
}
