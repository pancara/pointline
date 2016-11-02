/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.history.log;

import org.apache.log4j.Logger;

/**
 *
 * @author Trisna
 */
public class LogUtil {

    private static Logger logStream = Logger.getLogger("streamHistoryLogger");

    public static void stream(String message) {
        logStream.info(message);
    }

}
