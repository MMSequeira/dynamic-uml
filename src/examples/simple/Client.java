package examples.simple;

public class Client {

	private String name;
	private PersonalSeller personal_seller;
	private int thoughts_taken = 0;
	
	public Client(String name, PersonalSeller personal_seller) {
		this.name = name;
		this.personal_seller = personal_seller;
	}
	
	public void think() {
		thoughts_taken++;
		System.out.println("Bob thought...");
	}
	
	public void askAttention() {
		personal_seller.setAttention(true);
	}
	
	public int askQuestion(String question) {
		int answer = personal_seller.processRequest(question);
		return answer;
	}
	
	public String askName() {
		return personal_seller.getName();
	}
	
	public void changePersonalSeller(PersonalSeller new_seller) {
		personal_seller = new_seller;
		think();
		askAttention();
	}
	
	public void writeComplaintLetter() {
		ComplaintLetter letter = new ComplaintLetter("A Complaint", "There was a mistake in the bill.");
		letter.send();
	}
	
	public static void main(String[] args) {

	}

}
