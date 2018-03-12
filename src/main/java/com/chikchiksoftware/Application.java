package com.chikchiksoftware;

import com.chikchiksoftware.service.TimeConversionService;
import com.chikchiksoftware.service.Timer;
import com.chikchiksoftware.service.UserInteractions;

import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

public class Application {

    public static void main(String[] args) {

        final UserInteractions interactions = new UserInteractions();
        final ThreadGroup producers = new ThreadGroup("Producers");
        final ThreadGroup consumers = new ThreadGroup("Consumers");

        interactions.producersQuantityInput();
        interactions.consumersQuantityInput();
        interactions.dataGenerationFrequencyInput();
        interactions.producerTimeToWorkInput();
        interactions.close();

        final long start = System.currentTimeMillis();

        final FifoFileBuffer<Timestamp> buffer = new FifoFileBuffer<>(1024, false);
        Timer serviceTimer = new Timer(buffer, start, interactions.getProducerTimeToWork());

        for(int i = 0; i < interactions.getProducersCount(); i++) {
            Thread thread = new Thread(producers, new Producer(
                    buffer,
                    interactions.getFrequency(),
                    interactions.getProducerTimeToWork())
            );
            thread.setDaemon(true);
            thread.start();
        }

        Thread timerDaemon = new Thread(serviceTimer);
        timerDaemon.setDaemon(true);
        timerDaemon.start();

        for(int i = 0; i < interactions.getConsumersCount(); i++) {
            Thread thread = new Thread(consumers, new Consumer(buffer));
            thread.setDaemon(true);
            thread.start();
        }

        Runnable finalStatistics = () -> {
            while(producers.activeCount() > 0 || (!buffer.isEmpty() && consumers.activeCount() > 0)) {
                try {
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    System.err.println();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("==========================================");
            System.out.println("Totals:");
            System.out.println("Producer generated data count: " + Timer.getProducedItems());
            System.out.println("Consumer taken data count: " + Timer.getConsumedItems());
            System.out.println("Buffer added total count: " + buffer.getProduced());
            System.out.println("Buffer taken total count: " + buffer.getConsumed());
            System.out.println("Buffer count value: " + buffer.getCount());
            System.out.println("Buffer offset value: " + buffer.getOffset());
            System.out.println("Time elapsed: " + TimeConversionService.millisToDHMS(end - start));
            System.out.println("Data file length: " + (Math.round(buffer.getDataFileLength() / 1024)) + " Kb");
            System.out.println("==========================================");
        };

        Thread finalStatisticsService = new Thread(finalStatistics);
        finalStatisticsService.start();

    }
}
