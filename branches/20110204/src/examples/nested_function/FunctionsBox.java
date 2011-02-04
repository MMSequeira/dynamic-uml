package examples.nested_function;

public class FunctionsBox {
	
	
	public FunctionsBox() {
		
	}
	
	private void callFunctions() {
		callFunctionA();
		callFunctionD();
	}
	
	public void callAllFunctions() {
		callFunctions();
	}
	
	private void callFunctionA() {
		functionB();
		functionC();
	}
	
	private void functionB() {
		
	}
	
	private void functionC() {
		
	}
	
	private void callFunctionD() {
		
	}
}
