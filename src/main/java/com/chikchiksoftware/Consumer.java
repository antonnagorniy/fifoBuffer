package com.chikchiksoftware;

import com.chikchiksoftware.service.DefaultLogger;
import com.chikchiksoftware.service.RollingFileAppender;
import com.chikchiksoftware.service.Timer;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer buffer;
    private static Logger logger;

    public Consumer(FifoFileBuffer buffer) {
        this.buffer = buffer;
        initLogger();
    }

    @Override
    public void run() {
        try {
            while(true) {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
                logger.info(Thread.currentThread().getName() + " Consumed " + buffer.take());
                Timer.incConsumedItems();
            }
        }catch(IOException e){
            logger.error("Consumer down", e);
        }
    }

    private static void initLogger() {
        final String layoutPattern = "%d{dd-MM-yyyy HH:mm:ss,SSS} [%t] %p: %m %n";
        final String logfile = "/home/kattaris/Documents/logs/" + Consumer.class.getSimpleName() + ".out";
        final int logLevel = 5000;
        PatternLayout layout = new PatternLayout(layoutPattern);

        try {
            RollingFileAppender fileAppender = new RollingFileAppender(layout, logfile, false);
            fileAppender.setName("Consumer");
            fileAppender.setMaxFileSize("100MB");
            fileAppender.setThreshold(Level.TRACE);


            org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Consumer.class);
            log.addAppender(fileAppender);

            logger = LoggerFactory.getLogger(Consumer.class);
            DefaultLogger.setLogger(logger);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
