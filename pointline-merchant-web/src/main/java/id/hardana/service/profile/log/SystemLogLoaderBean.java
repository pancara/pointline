  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.log;

import id.hardana.ejb.system.log.SystemLog;
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
    
    private final String logDirectrory = "MerchantWeb-WAR";

    @PostConstruct
    protected void startupLog() {
        SystemLog.getInstance().setUpLoggingFile(logDirectrory);
    }
}
