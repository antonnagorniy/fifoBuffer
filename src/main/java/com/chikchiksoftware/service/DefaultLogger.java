package com.chikchiksoftware.service;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

public class DefaultLogger {
    private static Logger logger;
    private final static int DEFAULT_LOG_LEVEL = 5000;
    private final static String DEFAULT_LOG_FILE = "/home/kattaris/Documents/logger.out";
    private final static String DEFAULT_LAYOUT = "%d{dd MMM yyyy HH:mm:ss,SSS} [%t] %p %m %n";
    private final static ConsoleAppender DEFAULT_CONSOLE_APPENDER = new ConsoleAppender();
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
        PatternLayout layout = new PatternLayout(DEFAULT_LAYOUT);

        DEFAULT_CONSOLE_APPENDER.setName("CONSOLE");
        DEFAULT_CONSOLE_APPENDER.setWriter(new PrintWriter(System.out));
        DEFAULT_CONSOLE_APPENDER.setLayout(layout);
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();
        log.addAppender(DEFAULT_CONSOLE_APPENDER);
        log.setLevel(Level.toLevel(DEFAULT_LOG_LEVEL));


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
        log.setLevel(Level.toLevel(DEFAULT_LOG_LEVEL));
        log.addAppender(fileAppender);

        return LoggerFactory.getLogger("");
    }*/
}
