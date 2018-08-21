package com.chikchiksoftware;

import com.chikchiksoftware.service.DefaultLogger;
import com.chikchiksoftware.service.RollingFileAppender;
import com.chikchiksoftware.service.Timer;
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
public class Producer implements Runnable {

    private final FifoFileBuffer buffer;
    private final long generateFrequencySeconds;
    private final long timeToWork;
    private static Logger logger;

    public Producer(FifoFileBuffer buffer, long generateFrequencySeconds, long timeToWork) {
        this.buffer = buffer;
        this.generateFrequencySeconds = generateFrequencySeconds;
        this.timeToWork = timeToWork;
        initLogger();
    }

    public void run() {
        final long start = System.currentTimeMillis();
        long end = 0;

        try {
            while((end - start) <= timeToWork) {
                buffer.put(new Timestamp(System.currentTimeMillis()));
                logger.info("Produced new object");
                Timer.incProducedItems();
                Thread.sleep(generateFrequencySeconds);
                end = System.currentTimeMillis();
            }
        }catch(InterruptedException e) {
            logger.error("Producer is down: ", e);
        }
    }

    private static void initLogger() {
        final String layoutPattern = "%d{dd-MM-yyyy HH:mm:ss,SSS} [%t] %p: %m %n";
        final String logfile = "/home/kattaris/Documents/logs/" + Producer.class.getSimpleName() + ".out";
        final int logLevel = 5000;
        PatternLayout layout = new PatternLayout(layoutPattern);

        try {
            RollingFileAppender fileAppender = new RollingFileAppender(layout, logfile, false);
            fileAppender.setName("Producer");
            fileAppender.setMaxFileSize("100MB");
            fileAppender.setThreshold(Level.TRACE);


            org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Producer.class);
            log.addAppender(fileAppender);

            logger = LoggerFactory.getLogger(Producer.class);
            DefaultLogger.setLogger(logger);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
