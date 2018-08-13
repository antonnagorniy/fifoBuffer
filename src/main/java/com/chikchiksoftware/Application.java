package com.chikchiksoftware;


import com.chikchiksoftware.service.*;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

public class Application {

    private static Logger logger;

    public static void main(String[] args) {

        initLogger();
        logger = DefaultLogger.getLogger();

        final UserInteractions interactions = new UserInteractions();
        final ThreadGroup producers = new ThreadGroup("Producers");
        final ThreadGroup consumers = new ThreadGroup("Consumers");


        interactions.producersQuantityInput();
        interactions.consumersQuantityInput();
        interactions.dataGenerationFrequencyInput();
        interactions.producerTimeToWorkInput();
        interactions.close();

        final long start = System.currentTimeMillis();

        final FifoFileBuffer<Timestamp> buffer = new FifoFileBuffer<>(30000000, false);
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
                    logger.info("Final stats goes to sleep");
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    logger.error("Statistics service failed: ",  e);
                    System.err.println("Statistics service failed: " + e.getMessage());
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

    private static void initLogger() {
        EdgeRollingFileAppender fileAppender = null;
        final String DEFAULT_LAYOUT = "%d{dd MMM yyyy HH:mm:ss,SSS} [%t] %p: %m %n";
        final String DEFAULT_LOG_FILE = "/home/kattaris/Documents/logs/logger.out";
        final int DEFAULT_LOG_LEVEL = 25000;

        try {
            fileAppender = new EdgeRollingFileAppender(
                    new PatternLayout(DEFAULT_LAYOUT), DEFAULT_LOG_FILE, true);
        }catch(IOException e) {
            e.printStackTrace();
        }

        if(fileAppender != null) {
            fileAppender.setName("FILE");
            fileAppender.setMaxFileSize("1KB");
            fileAppender.setThreshold(Level.TRACE);

        }

        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Application.class);
        log.addAppender(fileAppender);
        log.setLevel(Level.toLevel(20000));

        logger = LoggerFactory.getLogger(Application.class);
        DefaultLogger.setLogger(logger);
    }
}
