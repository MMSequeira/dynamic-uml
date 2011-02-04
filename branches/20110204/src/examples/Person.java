package examples;
public class Person {

    private String givenName;
    private String familyName;
    private int age;
    private Person parent;

    // private Table table = new Table();
    // private static int personNumber = 0;

    public Person(final String givenName, final String familyName, final int age, final Person parent) {
        this.setFirstName(givenName);
        this.setFamilyName(familyName);
        this.setParent(parent);
        this.setAge(age);
    }

    public void setFirstName(final String newGivenName) {
        this.givenName = newGivenName;
    }

    public String getFirstName() {
        return givenName;
    }

    public void setFamilyName(final String newFamilyName) {
        this.familyName = newFamilyName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setAge(final int idade) {
        this.age = idade;
    }

    public int getAge() {
        return age;
    }

    public void setParent(final Person parent) {
        this.parent = parent;
    }

    public Person getPai() {
        return parent;
    }
}
