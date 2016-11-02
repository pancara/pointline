package id.atv.pointline.logging;

import org.apache.log4j.Logger;

/**
 * Created by pancara on 11/2/16.
 */
public class LogWriter {

    private static Logger logger = Logger.getLogger("streamHistoryLogger");

    public static void info(String message) {
        logger.info(message);
    }

}
