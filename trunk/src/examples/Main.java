package examples;

public class Main {

    enum Test {
        SingleThread, // - Test single thread execution
        MultiThread   // - Test multi thread execution
    }

    public static void main(final String[] arguments) throws InterruptedException {
        // Choose the test here:
        Test testOption = Test.MultiThread;

        if (testOption.equals(Test.SingleThread)) {
            final Person parent = new Person("John", "Doe", 50, null);
            final Person person = new Person("John", "Doe Jr.", 21, parent);
            person.setAge(22);
            parent.setAge(100);
        } else if (testOption.equals(Test.MultiThread)) {
            Table table = new Table();
            Cook server = new Cook(table, 1);
            Glutton served = new Glutton(table, 1);
            server.start();
            served.start();

            new Cook(table, 2).start();
            new Cook(table, 3).start();
            new Glutton(table, 2).start();
            new Glutton(table, 3).start();
        }
    }
    /*
    public String toString() {
    	return new String("Hello. I'm the main class of the program...");
    }
    */
}
