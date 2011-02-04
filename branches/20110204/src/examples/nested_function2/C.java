package examples.nested_function2;

public class C {

	private D d;
	
	public C() {
		d = new D();
	}

	public void someMethodC() {
		d.someMethodD();
	}
	
}
