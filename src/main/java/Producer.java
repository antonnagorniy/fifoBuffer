
import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Producer implements Runnable {

    private final FifoFileBuffer buffer;

    public Producer(FifoFileBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while(true) {
            try {
                buffer.put(new Timestamp(System.currentTimeMillis()));
                Thread.sleep(3000);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
