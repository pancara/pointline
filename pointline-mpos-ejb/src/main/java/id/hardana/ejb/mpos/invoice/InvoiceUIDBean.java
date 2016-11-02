/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.invoice;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.invoice.InvoiceUID;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class InvoiceUIDBean {

    private final String STATUS_KEY = "status";
    private final String LIST_UID_KEY = "listUID";
    private final Integer maxUIDCount = 20;
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject getInvoiceUID(String merchantCode, String outletId, String count) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        Integer countInteger;
        try {
            outletIdLong = Long.parseLong(outletId);
            countInteger = Integer.parseInt(count);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        if (countInteger > maxUIDCount) {
            countInteger = maxUIDCount;
        }

        Date nowDate = new Date();

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

        Set<String> setUIDGenerated = new HashSet<>();
        List<InvoiceUID> listInvoiceUID = null;
        boolean areAllUIDUnique = false;

        while (!areAllUIDUnique) {
            while (setUIDGenerated.size() < countInteger) {
                setUIDGenerated.add(generateUID());
            }
            listInvoiceUID = em.createQuery("SELECT i FROM InvoiceUID i WHERE i.merchantId = :merchantId "
                    + "AND i.outletId = :outletId AND i.generatedDate = :generatedDate AND i.uniqueId IN :setUniqueId ",
                    InvoiceUID.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("generatedDate", nowDate, TemporalType.DATE)
                    .setParameter("setUniqueId", setUIDGenerated)
                    .getResultList();
            if (listInvoiceUID.isEmpty()) {
                areAllUIDUnique = true;
            } else {
                for (InvoiceUID transactionUID : listInvoiceUID) {
                    setUIDGenerated.remove(transactionUID.getUniqueId());
                }
            }
        }

        Iterator<String> iteratorUID = setUIDGenerated.iterator();
        while (iteratorUID.hasNext()) {
            String UIDGenerated = iteratorUID.next();
            InvoiceUID invoiceUID = new InvoiceUID();
            invoiceUID.setMerchantId(merchantId);
            invoiceUID.setOutletId(outletIdLong);
            invoiceUID.setGeneratedDate(nowDate);
            invoiceUID.setIsUsed(false);
            invoiceUID.setUniqueId(UIDGenerated);
            em.persist(invoiceUID);
        }

        List<String> listUIDGenerated = new ArrayList<>();
        listUIDGenerated.addAll(setUIDGenerated);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(LIST_UID_KEY, listUIDGenerated);

        return response;
    }

    private String generateUID() {
        String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }

        return sb.toString();
    }

}
