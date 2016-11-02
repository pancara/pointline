/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.history;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Trisna
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(id.hardana.service.history.AutoDebetReportService.class);
        resources.add(id.hardana.service.history.MerchantReportBillService.class);
        resources.add(id.hardana.service.history.NewCrossOriginResourceSharingFilter.class);
        resources.add(id.hardana.service.history.PayerReportBillService.class);
        resources.add(id.hardana.service.history.PersonalReportBillService.class);
    }
    
}
