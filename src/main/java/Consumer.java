/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer buffer;

    public Consumer(FifoFileBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while(true) {
            System.out.println(buffer.poll());
        }
    }
}
