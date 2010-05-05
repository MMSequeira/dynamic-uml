package examples;
public class Table {

    private int numberOfElements = 0;
    public static final int MAXIMUM_NUMBER_OF_ELEMENTS = 3;

    public synchronized void put() throws InterruptedException {
        while (numberOfElements == MAXIMUM_NUMBER_OF_ELEMENTS) {
            System.out.println(Thread.currentThread().toString()
                               + ": I'm going to sleep");
            wait();
            System.out.println(Thread.currentThread().toString()
                               + ": I've woken up");
        }

        System.out.println(Thread.currentThread().toString()
                           + ": I've served a boar");
        numberOfElements++;
        notifyAll();
    }

    public synchronized void take() throws InterruptedException {
        while (numberOfElements == 0) {
            System.out.println(Thread.currentThread().toString()
                               + ": I'm going to sleep");
            wait();
            System.out.println(Thread.currentThread().toString()
                               + ": I've woken up");
        }

        System.out.println(Thread.currentThread().toString()
                           + ": I've eaten a boar");
        numberOfElements--;
        notifyAll();
    }
}
