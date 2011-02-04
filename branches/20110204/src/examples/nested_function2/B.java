package examples.nested_function2;

public class B {

	private C c;
	
	public B() {
		c = new C();
	}
	
	public void someMethodB() {
		c.someMethodC();
	}
	
}
