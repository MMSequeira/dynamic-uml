package examples.nested_function2;

public class A {

	private B b;
	
	public A() {
		b = new B();
	}
	
	public void someMethodA() {
		b.someMethodB();
	}
	
	public static void main(String[] args) {
		A a = new A();
		a.someMethodA();
	}

}
