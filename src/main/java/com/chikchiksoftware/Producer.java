package com.chikchiksoftware;

import java.sql.Timestamp;


/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Producer implements Runnable {

    private final FifoFileBuffer<Timestamp> buffer;
    private final long generateFrequencySeconds;
    private final long timeToWork;

    public Producer(FifoFileBuffer<Timestamp> buffer, long generateFrequencySeconds, long timeToWork) {
        this.buffer = buffer;
        this.generateFrequencySeconds = generateFrequencySeconds;
        this.timeToWork = timeToWork;
    }

    public void run() {
        long start = System.currentTimeMillis();
        long end = 0;

        while((end - start) <= timeToWork) {
            try {
                buffer.put(new Timestamp(System.currentTimeMillis()));
                Thread.sleep(generateFrequencySeconds);
                end = System.currentTimeMillis();
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
