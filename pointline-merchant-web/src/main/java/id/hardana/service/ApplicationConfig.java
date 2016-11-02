/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service;

import id.hardana.service.profile.merchantweb.MerchantDiscount;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author hanafi
 */
@javax.ws.rs.ApplicationPath("emo")
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
        resources.add(id.hardana.service.payment.PaymentReversal.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantDashboard.class);
        resources.add(MerchantDiscount.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantInventory.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantInvoiceHistory.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantReport.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantReportDashboard.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantTopupHistory.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantWeb.class);
        resources.add(id.hardana.service.profile.merchantweb.MerchantWebSales.class);
        resources.add(id.hardana.service.profile.merchantweb.NewCrossOriginResourceSharingFilter.class);
        resources.add(id.hardana.service.profile.merchantweb.ViewerService.class);
    }
    
}
