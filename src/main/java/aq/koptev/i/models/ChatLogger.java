package aq.koptev.i.models;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ChatLogger {

    private static Logger infoLogger;
    private static Logger errLogger;
    private static Logger consoleLogger;

    static {
        PropertyConfigurator.configure("src/main/resources/logs/configs/log4j.properties");
        infoLogger = Logger.getLogger("infoLogger");
        errLogger = Logger.getLogger("errLogger");
        consoleLogger = Logger.getLogger("consoleAppender");
    }

    private ChatLogger() {}

    public static void infoFile(String message) {
        infoLogger.info(message);
    }

    public static void infoFile(String message, Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        logger.info(message);
    }

    public static void errorFile(String message) {
        errLogger.error(message);
    }

    public static void errorFile(String message, Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        logger.error(message);
    }

    public static void infoConsole(String message) {
        consoleLogger.info(message);
    }

    public static void infoConsole(String message, Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        logger.info(message);
    }
}
