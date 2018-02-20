import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class FifoBuffer<T> {
    private final Object lock = new Object();
    private final File file = new File("/file.obj");
    private long produceCount = 0L;
    private long consumeCount = 0L;
    BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    private T data = null;

    public void put(T data) {
        synchronized(lock) {
            while(this.data != null) {
                try {
                    lock.wait();
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.data = data;
            produceCount++;
            System.out.println("Produced " + produceCount);
            lock.notifyAll();
        }

    }

    public T poll() {
        synchronized(lock) {
            while(this.data == null) {
                try {
                    lock.wait();
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T result = this.data;
            consumeCount++;
            System.out.println("Consumed " + consumeCount);
            data = null;
            lock.notifyAll();
            return result;
        }
    }
}
