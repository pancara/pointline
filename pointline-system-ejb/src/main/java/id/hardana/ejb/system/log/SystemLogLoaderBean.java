  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.log;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author trisna
 */
@Singleton
@Startup
public class SystemLogLoaderBean {
    
    private final String logDirectrory = "System-EJB";

    @PostConstruct
    protected void startupLog() {
        SystemLog.getInstance().setUpLoggingFile(logDirectrory);
    }
}
