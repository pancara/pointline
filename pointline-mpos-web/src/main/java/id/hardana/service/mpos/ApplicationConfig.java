/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Trisna
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
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(id.hardana.service.mpos.AdminTransactionService.class);
        resources.add(id.hardana.service.mpos.DeleteFileConfigService.class);
        resources.add(id.hardana.service.mpos.DownloadFileConfigService.class);
        resources.add(InvetoryService.class);
        resources.add(id.hardana.service.mpos.InvoiceHistoryPendingService.class);
        resources.add(id.hardana.service.mpos.InvoiceHistoryService.class);
        resources.add(InvoiceService.class);
        resources.add(id.hardana.service.mpos.InvoiceUIDGeneratorService.class);
        resources.add(id.hardana.service.mpos.ListFileConfigService.class);
        resources.add(id.hardana.service.mpos.MultiPayInvoiceService.class);
        resources.add(MultiPayService.class);
        resources.add(id.hardana.service.mpos.NewCrossOriginResourceSharingFilter.class);
        resources.add(PrintSaleService.class);
        resources.add(id.hardana.service.mpos.SinglePayInvoiceService.class);
        resources.add(SinglePayService.class);
        resources.add(TopupCardService.class);
        resources.add(id.hardana.service.mpos.TopupHistoryService.class);
        resources.add(id.hardana.service.mpos.TransferToCardHistoryService.class);
        resources.add(id.hardana.service.mpos.TransferToCardService.class);
        resources.add(ViewService.class);
        
        
    }

}
