package examples.nested_constructor2;

public class MacroCell {

    @SuppressWarnings("unused")
	private Cell cell1;
    @SuppressWarnings("unused")
	private Cell cell2;
	
	public MacroCell() {
		createCells();
	}
	
	private void createCells() {
		cell1 = new Cell();
		cell2 = new Cell();
	}
	
	public void print() {
		System.out.println("This is a MacroCell!");
	}

}
