package examples.simple;

public class ComplaintLetter {

	private String title;
	private String message;
	
	public ComplaintLetter(String title, String message) {
		this.title = title;
		this.message = message;
	}
	
	public void send() {
		System.out.println(this);
	}
}
