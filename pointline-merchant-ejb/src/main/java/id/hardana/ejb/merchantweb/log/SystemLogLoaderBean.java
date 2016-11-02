  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.log;

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
    
    private final String logDirectrory = "MerchantWeb-EJB";

    @PostConstruct
    protected void startupLog() {
        SystemLog.getInstance().setUpLoggingFile(logDirectrory);
    }
}
