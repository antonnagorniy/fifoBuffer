package com.chikchiksoftware;

import com.chikchiksoftware.service.FileCleaningService;
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

public class Main {

    final static ThreadGroup producers = new ThreadGroup("Producers");

    public static void main(String[] args) {

        final UserInteractions interactions = new UserInteractions();
        final ThreadGroup consumers = new ThreadGroup("Consumers");

        interactions.producersQuantityInput();
        interactions.consumersQuantityInput();
        interactions.dataGenerationFrequencyInput();
        interactions.producerTimeToWorkInput();
        interactions.close();

        final long start = System.currentTimeMillis();

        FifoFileBuffer<Timestamp> buffer = new FifoFileBuffer<>();
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


        Thread fileCleaningDaemon = new Thread(new FileCleaningService(buffer));
        fileCleaningDaemon.setDaemon(true);
        fileCleaningDaemon.start();

        Runnable statistics = () -> {
            while(producers.activeCount() > 0 || !buffer.isEmpty()) {
                try {
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    System.err.println();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("===================================");
            System.out.println("Totals:");
            System.out.println("Produced: " + buffer.getProducedItems());
            System.out.println("Consumed: " + buffer.getConsumedItems());
            System.out.println("Time elapsed: " + TimeConversionService.millisToDHMS(end - start));
            System.out.println("Data file length: " + (Math.round(buffer.getDataFileLength() / 1024)) + " Kb");
            System.out.println("===================================");
            buffer.finish();
        };

        Thread finalStatisticsService = new Thread(statistics);
        finalStatisticsService.start();

    }
}
