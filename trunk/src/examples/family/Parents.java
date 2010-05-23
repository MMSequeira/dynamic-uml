package examples.family;

import java.util.LinkedList;

public class Parents {

	private String fatherFirstName;
	private String motherFirstName;
	private String fatherLastName;
	private String motherLastName;
	private Child child0;
	private Child child1;
	//private LinkedList<Child> children;
	
	public Parents(String fatherFirstName, String motherFirstName,
			String fatherLastName, String motherLastName) {
		this.fatherFirstName = fatherFirstName;
		this.motherFirstName = motherFirstName;
		this.fatherLastName = fatherLastName;
		this.motherLastName = motherLastName;
		//children = new LinkedList<Child>();
	}
	
	public void initiateLife(){
		/*children.add(haveChild("Johny",this));
		children.add(haveChild("Bobby",this));*/
		child0 = new Child("Johny", this);
		child1 = new Child("Bobby", this);
		killOlderChild();
	}
	
	private void killOlderChild() {
		//children.removeFirst();
		child0 = null;
	}

	public Child haveChild(final String childName, final Parents parents){
		return new Child(childName,parents);
	}

	public String getFatherFirstName() {
		return fatherFirstName;
	}

	public String getMotherFirstName() {
		return motherFirstName;
	}

	public String getFatherLastName() {
		return fatherLastName;
	}

	public String getMotherLastName() {
		return motherLastName;
	}

	public String toString() {
		return "Parents [fatherFirstName=" + fatherFirstName
				+ ", fatherLastName=" + fatherLastName + ", motherFirstName="
				+ motherFirstName + ", motherLastName=" + motherLastName + "]";
	}
	
}
