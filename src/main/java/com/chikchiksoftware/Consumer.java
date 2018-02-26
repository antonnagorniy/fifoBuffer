package com.chikchiksoftware;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer<Timestamp> buffer;

    public Consumer(FifoFileBuffer<Timestamp> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while(!buffer.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
            }
        }catch(NoSuchElementException | IllegalStateException ignore) {}
    }
}
