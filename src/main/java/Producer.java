
import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Producer implements Runnable {

    private final FifoFileBuffer buffer;
    private final int generateFrequencySeconds;

    public Producer(FifoFileBuffer buffer, int generateFrequencySeconds) {
        this.buffer = buffer;
        this.generateFrequencySeconds = generateFrequencySeconds;
    }

    public void run() {
        while(true) {
            try {
                buffer.put(new Timestamp(System.currentTimeMillis()));
                Thread.sleep(generateFrequencySeconds);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
