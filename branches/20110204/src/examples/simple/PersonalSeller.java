package examples.simple;

public class PersonalSeller {

	private String name;
    @SuppressWarnings("unused")
	private boolean attention = false;
	
	public PersonalSeller(String name) {
		this.name = name;
	}
	
	public int processRequest(String request) {
		int aux = doMaths();
		attention = false;
		return aux;
	}
	
	public int doMaths() {
		int x;
		x = 14+27*3;
		return x;
	}
	
	public void setAttention(boolean new_attention) {
		attention = new_attention;
	}
	
	public String getName() {
		return name;
	}
	
	public static void main(String[] args) {

	}

}
