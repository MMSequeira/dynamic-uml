package examples.simple;

public class ComplaintLetter {

    @SuppressWarnings("unused")
	private String title;
    @SuppressWarnings("unused")
	private String message;
	
	public ComplaintLetter(String title, String message) {
		this.title = title;
		this.message = message;
	}
	
	public void send() {
		System.out.println(this);
	}
}
