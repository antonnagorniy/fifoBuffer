package com.chikchiksoftware.service;

import com.chikchiksoftware.FifoFileBuffer;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 24.02.2018.
 */
public class Timer implements Runnable {
    private final FifoFileBuffer buffer;
    private final long start;
    private long end;
    private long workingTime;
    private static long producedItems;
    private static long consumedItems;

    public Timer(FifoFileBuffer buffer, long start, long workingTime) {
        this.buffer = buffer;
        this.start = start;
        this.workingTime = workingTime;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(10000);
            }catch(InterruptedException e) {
                System.err.println(e.getMessage());
            }

            end = System.currentTimeMillis();
            System.out.println("==========================================");
            System.out.println("Produced: " + getProducedItems());
            System.out.println("Consumed: " + getConsumedItems());
            System.out.println("Working time: " + TimeConversionService.millisToDHMS(end - start));
            System.out.println("Producers remaining time to work: " + TimeConversionService.millisToDHMS((start + workingTime) - System.currentTimeMillis()));
            System.out.println("==========================================");
        }
    }

    public synchronized static long getProducedItems() {
        return producedItems;
    }

    public synchronized static void incProducedItems() {
        Timer.producedItems++;
    }

    public synchronized static long getConsumedItems() {
        return consumedItems;
    }

    public synchronized static void incConsumedItems() {
        Timer.consumedItems++;
    }
}
