package com.chikchiksoftware.service;

import com.chikchiksoftware.FifoFileBuffer;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 24.02.2018.
 */
public class Timer implements Runnable {
    private final FifoFileBuffer buffer;
    private final Instant start;
    private Instant end = null;

    public Timer(FifoFileBuffer buffer, Instant start) {
        this.buffer = buffer;
        this.start = start;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(10000);
            }catch(InterruptedException e) {
                System.err.println(e.getMessage());
            }

            end = Instant.now();
            System.out.println("==========================================");
            System.out.println("Produced: " + buffer.getProducedItems());
            System.out.println("Consumed: " + buffer.getConsumedItems());
            System.out.println("Working time: " + Duration.between(start, end).toString().replaceAll("PT", ""));
            System.out.println("==========================================");
        }
    }
}
