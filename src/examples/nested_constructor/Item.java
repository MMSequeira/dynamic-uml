package examples.nested_constructor;

public class Item {

	private String name;
	private Label label1;
	private Label label2;
	
	public Item(String name) {
		this.name = name;
		fillLabel();
	}

	private void fillLabel() {
		label1 = new Label("Label 1");
		label2 = new Label("Label 2");
	}
	
}