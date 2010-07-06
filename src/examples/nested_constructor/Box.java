package examples.nested_constructor;

public class Box {

	private String name;
	private Item item1 = null;
	private Item item2 = null;
	
	
	public Box(String name) {
		this.name = name;
		fill();
	}
	
	private void fill() {
		item1 = new Item("Item_1");
		item2 = new Item("Item_2");
	}
	
	public static void main(String[] args) {
		Box box = new Box("Box");
		//box.fill();
	}

}
