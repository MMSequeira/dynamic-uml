package examples.nested_constructor2;

public class MicroCell {
	
    @SuppressWarnings("unused")
	private float frequency;
	
	public MicroCell() {
		initializeMicroCell();
	}
	
	private void initializeMicroCell() {
		frequency = 25.50f;
	}
}
