/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author trisna
 */
public class SystemLog {

    private static Logger l;
    private static SystemLog instance;

    private SystemLog() {
        l = Logger.getLogger(SystemLog.class.getName());
    }

    public static SystemLog getInstance() {
        if (instance == null) {
            instance = new SystemLog();
        }
        return instance;
    }

    public void setUpLoggingFile(String logDirectory) {
        File f = new File(System.getProperty("com.sun.aas.instanceRoot"));
        String logDir = f.getAbsolutePath() + File.separator + "logs" + File.separator + logDirectory;
        File logF = new File(logDir);
        if (!logF.exists()) {
            logF.mkdir();
        }
        setUpHandler(logDir);
    }

    private void setUpHandler(String path) {
        try {
            LevelBasedFileHandler handlerStream = new LevelBasedFileHandler(path + "/stream.log", Level.INFO);
            LevelBasedFileHandler handlerDebug = new LevelBasedFileHandler(path + "/debug.log", Level.WARNING);
            LevelBasedFileHandler handlerError = new LevelBasedFileHandler(path + "/error.log", Level.SEVERE);

            SimpleFormatter formatterTxt = new SimpleFormatter();

            handlerStream.setFormatter(formatterTxt);
            handlerError.setFormatter(formatterTxt);
            handlerDebug.setFormatter(formatterTxt);

            l.addHandler(handlerStream);
            l.addHandler(handlerDebug);
            l.addHandler(handlerError);

            l.setUseParentHandlers(false);

        } catch (IOException | SecurityException ex) {
            Logger.getLogger(SystemLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logError(String message) {
        l.severe(message);
    }

    public void logDebug(String message) {
        l.warning(message);
    }

    public void logStream(String message) {
        l.info(message);
    }
}
