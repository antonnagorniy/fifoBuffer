package com.chikchiksoftware.service;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLogger {
    private static Logger logger = null;

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
}
