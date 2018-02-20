
import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Producer implements Runnable {
    private final long produceFrequency;

    public Producer(long produceFrequency) {
        this.produceFrequency = produceFrequency;
    }

    public void run() {

    }
}
