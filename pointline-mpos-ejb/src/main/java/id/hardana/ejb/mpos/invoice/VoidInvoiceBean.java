/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.invoice;

import id.hardana.ejb.mpos.holder.ItemRequestHolder;
import id.hardana.ejb.mpos.inventory.InventoryBean;
import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class VoidInvoiceBean {

    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private InventoryBean inventoryBean;

    public JSONObject voidInvoice(String merchantCode, String userName,
            String outletId, String invoiceNumber, String clientInvoiceId) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        try {
            outletIdLong = Long.parseLong(outletId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o WHERE o.merchantId = :merchantId "
                + "AND o.status = :status AND o.isDeleted = :isDeleted AND o.id = :id", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .setParameter("id", outletIdLong)
                .getResultList();
        if (listOutlet.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        Operator operatorSearch = operatorsSearch.get(0);
        Long operatorId = Long.parseLong(operatorSearch.getId());

        List<Invoice> listInvoice = em.createQuery("SELECT i FROM Invoice i "
                + "WHERE i.number = :number AND i.outletId = :outletId", Invoice.class)
                .setParameter("number", invoiceNumber)
                .setParameter("outletId", outletIdLong)
                .getResultList();
        if (listInvoice.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
            return response;
        }

        Invoice invoiceSearch = listInvoice.get(0);
        InvoiceStatusEnum status = invoiceSearch.getStatus();
        if (status.equals(InvoiceStatusEnum.CANCELED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_CANCELED.getResponseStatus());
            return response;
        } else if (status.equals(InvoiceStatusEnum.PAID) || status.equals(InvoiceStatusEnum.UNFINISHED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_PAID.getResponseStatus());
            return response;
        }
        invoiceSearch.setOperatorId(operatorId);
        invoiceSearch.setStatus(InvoiceStatusEnum.CANCELED);
        em.merge(invoiceSearch);

        Long invoiceId = Long.parseLong(invoiceSearch.getId());
        List<ItemRequestHolder> listOldItemRequest = new ArrayList<>();
        List<InvoiceItems> listOldInvoiceItems = em.createQuery("SELECT i FROM InvoiceItems i WHERE "
                + "i.invoiceId = :invoiceId", InvoiceItems.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        if (!listOldInvoiceItems.isEmpty()) {
            for (InvoiceItems invoiceItems : listOldInvoiceItems) {
                ItemRequestHolder itemRequestHolder = new ItemRequestHolder();
                itemRequestHolder.setItemId(Long.parseLong(invoiceItems.getItemId()));
                itemRequestHolder.setItemName(invoiceItems.getItemName());
                itemRequestHolder.setStock(Long.parseLong(invoiceItems.getItemQuantity()));
                listOldItemRequest.add(itemRequestHolder);
            }
        }
        inventoryBean.changeStock(merchantId, outletIdLong, operatorId, listOldItemRequest, 
                InventoryLogTypeEnum.VOID_INVOICE, invoiceId, new Date());

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_STATUS_KEY, invoiceSearch.getStatus());
        response.put(INVOICE_NUMBER_KEY, invoiceNumber);
        response.put(CLIENT_INVOICE_ID_KEY, clientInvoiceId);

        return response;
    }

}
