package examples;

public class Cook extends Thread {

    private Table table;
    private int id;

    public Cook(final Table table, final int id) {
        this.table = table;
        this.id = id;
    }

    public void run() {
        for (int i = 0; i != 10; i++) {
            try {
                table.put();
            } catch (final InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String toString() {
        return "Cozinheiro" + id;
    }
}
