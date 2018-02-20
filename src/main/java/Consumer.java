/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoBuffer buffer;

    public Consumer(FifoBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while(true) {
            System.out.println(buffer.poll());
        }
    }
}
