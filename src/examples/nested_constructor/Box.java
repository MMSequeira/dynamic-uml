package examples.nested_constructor;

public class Box {

    @SuppressWarnings("unused")
	private String name;
    @SuppressWarnings("unused")
	private Item item1 = null;
    @SuppressWarnings("unused")
	private Item item2 = null;
    @SuppressWarnings("unused")
	private String color;
	
	public Box(String name) {
		this.name = name;
		fill();
	}
	
	private void fill() {
		item1 = new Item("Item_1");
		item2 = new Item("Item_2");
		color();
	}
	
	private void color() {
		color = "Yellow";
	}
	
	public static void main(String[] args) {
	    @SuppressWarnings("unused")
		Box box = new Box("Box");
		//box.fill();
	}

}
