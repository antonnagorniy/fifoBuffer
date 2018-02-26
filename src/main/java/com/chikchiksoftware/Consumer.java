package com.chikchiksoftware;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer buffer;
    private final ThreadGroup producers;

    public Consumer(FifoFileBuffer buffer, ThreadGroup producers) {
        this.buffer = buffer;
        this.producers = producers;
    }

    @Override
    public void run() {
        while(producers.activeCount() >= 0 && buffer.getConsumedItems() != buffer.getProducedItems()){
            try {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
            }catch(IOException | NullPointerException | NoSuchElementException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
