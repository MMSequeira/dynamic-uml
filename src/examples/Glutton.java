package examples;

public class Glutton extends Thread {

    private Table table;
    private int id;

    public Glutton(final Table table, final int id) {
        this.table = table;
        this.id = id;
    }

    public void run() {
        for (int i = 0; i != 10; i++) {
            try {
                table.take();
            } catch (final InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String toString() {
        return "Glutton" + id;
    }
}
