

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Main {
    public static void main(String[] args) {
        FifoBuffer buffer = new FifoBuffer();
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        new Thread(producer).start();

        new Thread(consumer).start();
    }
}
