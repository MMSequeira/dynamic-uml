package examples;



public class MainDesreferencia {
    @SuppressWarnings("unused")
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
	    @SuppressWarnings("unused")
		Object o = new Object();
		//o.finalize();
		o = null;
		System.gc();
		if(true) {
		    @SuppressWarnings("unused")
			String s = new String("Olá");
			//System.out.println(s);
		}
		System.out.flush();
		System.gc();
	    @SuppressWarnings("unused")
		Object u = new Object();
		u = new Object();
		u = new Object();
		u = new Object();
		System.gc();
		u = new Object();
		u = null;
		u = new Object();
		
	    @SuppressWarnings("unused")
		MainDesreferencia m = new MainDesreferencia();
		m = new MainDesreferencia();
		m = new MainDesreferencia();
		m = new MainDesreferencia();
		System.gc();
	}

}
