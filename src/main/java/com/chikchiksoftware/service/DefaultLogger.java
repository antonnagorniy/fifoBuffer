package com.chikchiksoftware.service;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLogger {
    private static Logger logger = null;
    private final static int DEFAULT_LOG_LEVEL = 5000;
    private final static String DEFAULT_LOG_FILE = "/home/kattaris/Documents/logs/logger.out";
    private final static String DEFAULT_LAYOUT = "%d{dd MMM yyyy HH:mm:ss,SSS} [%t] %p %m %n";
    private static DailyRollingFileAppender fileAppender;

    public DefaultLogger() {
    }


    public static void setLogger(Logger newLog) {
        logger = newLog;
    }

    public static Logger getLogger() {
        return logger == null ? getDefaultLogger() : logger;
    }

    private static Logger getDefaultLogger() {

        PatternLayout layout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} [%t] %p %m %n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        consoleAppender.setName("CONSOLE");
        consoleAppender.setThreshold(Level.TRACE);
        consoleAppender.activateOptions();

        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("");
        log.addAppender(consoleAppender);
        log.setLevel(Level.toLevel(5000));


        return LoggerFactory.getLogger("");
    }

    /*private static Logger getDefaultLogger() {
        PatternLayout layout = new PatternLayout(DEFAULT_LAYOUT);

        try {
            fileAppender = new DailyRollingFileAppender(layout, DEFAULT_LOG_FILE, "'.' yyyy-MM-dd-a");
        }catch(IOException e) {
            e.printStackTrace();
        }

        fileAppender.setName("FILE");
        fileAppender.setAppend(false);

        org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();
        log.addAppender(fileAppender);
        log.setLevel(Level.toLevel(DEFAULT_LOG_LEVEL));

        return LoggerFactory.getLogger("FILE");
    }*/

}
