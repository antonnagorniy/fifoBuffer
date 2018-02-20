import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class FifoFileBuffer<T> {
    private final Object lock = new Object();
    private File dataFile;
    private FileWriter fileWriter;
    private long produceCount = 0L;
    private long consumeCount = 0L;
    BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    public FifoFileBuffer() {
        try {
            this.dataFile = File.createTempFile("data", "tmp");
            this.fileWriter = new FileWriter(this.dataFile);
        }catch(IOException e) {
            e.printStackTrace();
        }

    }

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
            System.out.println(Thread.currentThread().getName() + " Produced " + produceCount);
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
            System.out.println(Thread.currentThread().getName() + " Consumed " + consumeCount);
            data = null;
            lock.notifyAll();
            return result;
        }
    }
}
