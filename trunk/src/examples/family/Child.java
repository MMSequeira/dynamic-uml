package examples.family;

public class Child {

	private String firstName;
	private String lastNames;
	private Parents parents;
	
	public Child(final String firstName, final Parents parents){
		this.firstName = firstName;
		this.parents = parents;
		setLastNames();
		printChildName();
	}
	
	private void printChildName() {
		System.out.println(firstName + " " + lastNames);
	}

	private void setLastNames(){
		lastNames = parents.getMotherLastName() + " " + parents.getMotherLastName();
	}
	
}
