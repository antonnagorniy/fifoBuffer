package com.chikchiksoftware.service;

import com.chikchiksoftware.FifoFileBuffer;

import java.util.concurrent.TimeUnit;

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
            System.out.println("Produced: " + buffer.getProducedItems());
            System.out.println("Consumed: " + buffer.getConsumedItems());
            System.out.println("Working time: " + millisToDHMS(end - start));
            System.out.println("Producers remaining time to work: " + millisToDHMS((start + workingTime) - System.currentTimeMillis()));
            System.out.println("==========================================");
        }
    }

    public String millisToDHMS(long duration) {
        String result;

        if(duration < 0) {
            return "00:00:00";
        }
        long days  = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            result = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return result;
    }
}
