package examples.simple;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PersonalSeller seller_alice = new PersonalSeller("Alice");
		Client client_bob = new Client("Bob", seller_alice);
		client_bob.think();
		
		client_bob.askAttention();
		
		client_bob.askQuestion("Tell me the price, please.");
		
		String seller_name = client_bob.askName();
		
		PersonalSeller seller_tina = new PersonalSeller("Tina");
		client_bob.changePersonalSeller(seller_tina);
		client_bob.writeComplaintLetter();
		client_bob = null;
		System.gc();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
