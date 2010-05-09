package examples;



public class MainDesreferencia {
	private int id;
	
	public MainDesreferencia () {
		id = 5;
	}
	
	/*
	public void finalize() {
		System.out.println("Adeus mundo cruel.");
	}
	*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Object o = new Object();
		//o.finalize();
		o = null;
		System.gc();
		if(true) {
			String s = new String("Olá");
			//System.out.println(s);
		}
		System.out.flush();
		System.gc();
		Object u = new Object();
		u = new Object();
		u = new Object();
		u = new Object();
		System.gc();
		u = new Object();
		u = null;
		u = new Object();
		
		MainDesreferencia m = new MainDesreferencia();
		m = new MainDesreferencia();
		m = new MainDesreferencia();
		m = new MainDesreferencia();
		System.gc();
	}

}
