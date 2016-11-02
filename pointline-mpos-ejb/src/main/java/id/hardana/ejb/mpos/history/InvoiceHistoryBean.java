/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import id.hardana.ejb.mpos.holder.InvoiceHolder;
import id.hardana.ejb.mpos.holder.InvoicePricingHolder;
import id.hardana.ejb.mpos.holder.InvoiceItemHolder;
import id.hardana.ejb.mpos.holder.InvoiceItemDiscountHolder;
import id.hardana.ejb.mpos.holder.InvoiceTransactionDiscountHolder;
import id.hardana.ejb.mpos.holder.TransactionPaymentHolder;
import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class InvoiceHistoryBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final int HISTORY_NEW_VALUE = 0;
    private final int HISTORY_BEFORE_VALUE = -1;
    private final int HISTORY_AFTER_VALUE = 1;
    private final String STATUS_KEY = "status";
    private final String HISTORY_KEY = "history";
    private final String COUNT_KEY = "count";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject invoiceHistoryNow(String merchantCode, String outletId,
            String limit) {
        return invoiceHistory(merchantCode, outletId, limit, null, HISTORY_NEW_VALUE);
    }

    public JSONObject invoiceOutletData(String merchantCode, String outletId,
            String limit) {
        return invoiceOutletData(merchantCode, outletId, limit, null, HISTORY_NEW_VALUE);
    }

    public JSONObject invoiceHistoryBefore(String merchantCode, String outletId,
            String limit, String dateTime) {
        return invoiceHistory(merchantCode, outletId, limit, dateTime, HISTORY_BEFORE_VALUE);
    }

    public JSONObject invoiceHistoryAfter(String merchantCode, String outletId,
            String limit, String dateTime) {
        return invoiceHistory(merchantCode, outletId, limit, dateTime, HISTORY_AFTER_VALUE);
    }

    public JSONObject invoiceHistoryNew(String merchantCode, String limit, String page,
            String startDateString, String endDateString) {
        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;
        Date startDate;
        Date endDate;
        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
            startDate = DATE_FORMAT.parse(startDateString);
            endDate = DATE_FORMAT.parse(endDateString);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (pageInteger < 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        int firstResult = ((pageInteger - 1) * limitInteger);

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        String queryInvoiceHistoryCount = "SELECT COUNT(i.id) FROM Invoice i "
                + "WHERE i.merchantId = :merchantId AND i.dateTime BETWEEN :startDate AND :endDate";
        Long invoiceHistoryCount = em.createQuery(queryInvoiceHistoryCount, Long.class)
                .setParameter("merchantId", merchantId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        List<InvoiceHolder> listInvoice = null;
        String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceHolder"
                + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, o.userName, "
                + "ot.name, ot.latitude, ot.longitude) "
                + "FROM Invoice i LEFT JOIN Operator o ON i.operatorId = o.id "
                + "LEFT JOIN Outlet ot ON ot.id = i.outletId WHERE i.merchantId = :merchantId "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ORDER BY i.id DESC";

        if (invoiceHistoryCount > 0) {
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setFirstResult(firstResult)
                    .setMaxResults(limitInteger)
                    .getResultList();

            for (InvoiceHolder invoice : listInvoice) {
                String queryInvoiceItems = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceItemHolder(i.id, i.itemId, "
                        + "i.itemName, i.itemSupplyPrice, i.itemSalesPrice, "
                        + "i.itemQuantity, i.itemSubTotal) FROM InvoiceItems i "
                        + "WHERE i.invoiceId = :invoiceId";
                
                List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                
                //populate item discount
                for(InvoiceItemHolder invoiceItemHolder : listItems) {
                    try {
                        String jql = "SELECT i FROM InvoiceItemDiscount i WHERE i.invoiceItemId = :invoiceItemId";
                        InvoiceItemDiscount itemDiscount = em.createQuery(jql, InvoiceItemDiscount.class)
                                .setParameter("invoiceItemId", invoiceItemHolder.getId())
                                .setMaxResults(1)
                                .getSingleResult();
                    
                        InvoiceItemDiscountHolder holder = new InvoiceItemDiscountHolder();
                        
                        holder.setDiscountId(itemDiscount.getDiscountId());
                        holder.setDiscountName(itemDiscount.getDiscountName());
                        
                        holder.setDiscountAmount(itemDiscount.getDiscountAmount());
                        
                        holder.setDiscountApplyType(itemDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                        holder.setDiscountCalculationType(itemDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());
                        
                        holder.setDiscountValue(itemDiscount.getDiscountValue());
                        holder.setDiscountValueType(itemDiscount.getDiscountValueType().getDiscountValueTypeId());
                        
                        holder.setDiscountDescription(itemDiscount.getDiscountDescription());
                        
                        invoiceItemHolder.setItemDiscount(holder);
                    } catch(NoResultException nre ) {
                        // if not result just ignore.
                    }
                }
                
                invoice.setInvoiceItems(listItems);

                String queryInvoicePricing = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoicePricingHolder(p.name, "
                        + "i.pricingType, i.pricingValue, i.pricingAmount, i.pricingLevel) "
                        + "FROM InvoicePricing i JOIN Pricing p ON i.pricingId = p.id WHERE i.invoiceId = :invoiceId";
                List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setInvoicePricing(listPricing);

                String queryTransaction = "SELECT NEW id.hardana.ejb.merchantweb.extension.TransactionPaymentHolder(tp.type, "
                        + "t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                        + "t.totalAmount, t.status, t.dateTime, c.pan, c.cardHolderName, p.account, p.firstName, "
                        + "p.lastName, tpcc.cardType, tpcc.approvalCode) FROM TransactionPayment tp LEFT JOIN TransactionTbl t ON tp.transactionId = t.id "
                        + "LEFT JOIN Card c ON tp.sourceId = c.id LEFT JOIN PersonalInfo p ON tp.sourceId = p.id "
                        + "LEFT JOIN TransactionPaymentCreditCard tpcc ON tpcc.transactionPaymentId = tp.id "
                        + " WHERE tp.invoiceId = :invoiceId";
                List<TransactionPaymentHolder> listTransaction = em.createQuery(queryTransaction, TransactionPaymentHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setTransactions(listTransaction);
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(COUNT_KEY, invoiceHistoryCount);
        response.put(HISTORY_KEY, listInvoice);
        return response;
    }

    private JSONObject invoiceHistory(String merchantCode, String outletId,
            String limit, String dateTime, int historyValue) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        Integer limitInteger;
        try {
            outletIdLong = Long.parseLong(outletId);
            limitInteger = Integer.parseInt(limit);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        Date invoiceDateTime = null;
        if (historyValue != HISTORY_NEW_VALUE) {
            try {
                invoiceDateTime = DATE_FORMAT.parse(dateTime);
            } catch (ParseException ex) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
                return response;
            }
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

        List<InvoiceStatusEnum> invoiceStatusList = new ArrayList<>();
        invoiceStatusList.add(InvoiceStatusEnum.PAID);
        invoiceStatusList.add(InvoiceStatusEnum.CANCELED);

        List<InvoiceHolder> listInvoice = null;
        String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.mpos.holder.InvoiceHolder(i.id, "
                + "i.number, i.amount, i.dateTime, "
                + "i.tableNumber, i.status, o.userName) FROM Invoice i LEFT JOIN Operator "
                + "o ON i.operatorId = o.id WHERE i.merchantId = :merchantId "
                + "AND i.outletId = :outletId AND i.status IN :statusList";

        if (historyValue == HISTORY_NEW_VALUE) {
            queryInvoiceHistory += " ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_BEFORE_VALUE) {
            queryInvoiceHistory += " AND i.dateTime < :dateTime ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setParameter("dateTime", invoiceDateTime)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_AFTER_VALUE) {
            queryInvoiceHistory += " AND i.dateTime > :dateTime ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setParameter("dateTime", invoiceDateTime)
                    .setMaxResults(limitInteger)
                    .getResultList();
        }

        if (!listInvoice.isEmpty()) {
            for (InvoiceHolder invoice : listInvoice) {
                String queryInvoiceItems = "SELECT NEW id.hardana.ejb.mpos.holder.InvoiceItemHolder(i.id, i.itemId, "
                        + "i.itemName, i.itemSupplyPrice, i.itemSalesPrice, "
                        + "i.itemQuantity, i.itemSubTotal) FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
                List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                
                // populate item discount 
                for(InvoiceItemHolder invoiceItemHolder : listItems) {
                    try {
                        String jql = "SELECT i FROM InvoiceItemDiscount i WHERE i.invoiceItemId = :invoiceItemId";
                        InvoiceItemDiscount itemDiscount = em.createQuery(jql, InvoiceItemDiscount.class)
                                .setParameter("invoiceItemId", invoiceItemHolder.getId())
                                .setMaxResults(1)
                                .getSingleResult();
                    
                        InvoiceItemDiscountHolder holder = new InvoiceItemDiscountHolder();
                        
                        holder.setDiscountId(itemDiscount.getDiscountId());
                        holder.setDiscountName(itemDiscount.getDiscountName());
                        
                        holder.setDiscountAmount(itemDiscount.getDiscountAmount());
                        
                        holder.setDiscountApplyType(itemDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                        holder.setDiscountCalculationType(itemDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());
                        
                        holder.setDiscountValue(itemDiscount.getDiscountValue());
                        holder.setDiscountValueType(itemDiscount.getDiscountValueType().getDiscountValueTypeId());
                        
                        holder.setDiscountDescription(itemDiscount.getDiscountDescription());
                        
                        invoiceItemHolder.setItemDiscount(holder);
                    } catch(NoResultException nre ) {
                        // if not result just ignore.
                    }
                }
                
                invoice.setInvoiceItems(listItems);

                // get transaction discount
                String queryTrxDiscount = "SELECT trxd FROM InvoiceTransactionDiscount trxd WHERE trxd.invoiceId = :invoiceId";
                try {
                    InvoiceTransactionDiscount trxDiscount = em.createQuery(queryTrxDiscount, InvoiceTransactionDiscount.class)
                            .setParameter("invoiceId", invoice.getId())
                            .setMaxResults(1)
                            .getSingleResult();
                
                    InvoiceTransactionDiscountHolder trxDiscountHolder = new InvoiceTransactionDiscountHolder();
                    trxDiscountHolder.setDiscountId(trxDiscount.getDiscountId());
                    trxDiscountHolder.setDiscountName(trxDiscount.getDiscountName());
                    trxDiscountHolder.setDiscountDescription(trxDiscount.getDiscountDescription());
                    
                    trxDiscountHolder.setDiscountValue(trxDiscount.getDiscountValue());
                    trxDiscountHolder.setDiscountValueType(trxDiscount.getDiscountValueType().getDiscountValueTypeId());
                    trxDiscountHolder.setDiscountAmount(trxDiscount.getDiscountAmount());
                    
                    trxDiscountHolder.setDiscountApplyType(trxDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                    trxDiscountHolder.setDiscountCalculationType(trxDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());
                    
                    invoice.setTransactionDiscount(trxDiscountHolder);
                } catch(NoResultException nre) {
                    
                }
                
                String queryInvoicePricing = "SELECT NEW id.hardana.ejb.mpos.holder.InvoicePricingHolder(p.name, "
                        + "i.pricingType, i.pricingValue, i.pricingAmount, i.pricingLevel) "
                        + "FROM InvoicePricing i JOIN Pricing p ON i.pricingId = p.id WHERE i.invoiceId = :invoiceId";
                List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setInvoicePricing(listPricing);

                String queryTransaction = "SELECT NEW id.hardana.ejb.mpos.holder.TransactionPaymentHolder(tp.type, "
                        + "t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                        + "t.totalAmount, t.status, t.dateTime, c.pan, c.cardHolderName, p.account, p.firstName, "
                        + "p.lastName, tpcc.cardType, tpcc.approvalCode) FROM TransactionPayment tp LEFT JOIN TransactionTbl t ON tp.transactionId = t.id "
                        + "LEFT JOIN Card c ON tp.sourceId = c.id LEFT JOIN PersonalInfo p ON tp.sourceId = p.id "
                        + "LEFT JOIN TransactionPaymentCreditCard tpcc ON tpcc.transactionPaymentId = tp.id "
                        + " WHERE tp.invoiceId = :invoiceId";
                List<TransactionPaymentHolder> listTransaction = em.createQuery(queryTransaction, TransactionPaymentHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setTransactions(listTransaction);
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(HISTORY_KEY, listInvoice);
        return response;
    }

    private JSONObject invoiceOutletData(String merchantCode, String outletId,
            String limit, String dateTime, int historyValue) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        Integer limitInteger;
        try {
            outletIdLong = Long.parseLong(outletId);
            limitInteger = Integer.parseInt(limit);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        Date invoiceDateTime = null;
        if (historyValue != HISTORY_NEW_VALUE) {
            try {
                invoiceDateTime = DATE_FORMAT.parse(dateTime);
            } catch (ParseException ex) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
                return response;
            }
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

        List<InvoiceStatusEnum> invoiceStatusList = new ArrayList<>();
        invoiceStatusList.add(InvoiceStatusEnum.PAID);
        invoiceStatusList.add(InvoiceStatusEnum.CANCELED);

        List<InvoiceHolder> listInvoice = null;
        String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.mpos.holder.InvoiceHolder(i.id, "
                + "i.number, i.amount, i.dateTime, "
                + "i.tableNumber, i.status, o.userName) FROM Invoice i LEFT JOIN Operator "
                + "o ON i.operatorId = o.id WHERE i.merchantId = :merchantId "
                + "AND i.outletId = :outletId AND i.status IN :statusList";

        if (historyValue == HISTORY_NEW_VALUE) {
            queryInvoiceHistory += " ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_BEFORE_VALUE) {
            queryInvoiceHistory += " AND i.dateTime < :dateTime ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setParameter("dateTime", invoiceDateTime)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_AFTER_VALUE) {
            queryInvoiceHistory += " AND i.dateTime > :dateTime ORDER BY i.id DESC";
            listInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("statusList", invoiceStatusList)
                    .setParameter("dateTime", invoiceDateTime)
                    .setMaxResults(limitInteger)
                    .getResultList();
        }

        if (!listInvoice.isEmpty()) {
            for (InvoiceHolder invoice : listInvoice) {
                String queryInvoiceItems = "SELECT NEW id.hardana.ejb.mpos.holder.InvoiceItemHolder(i.id, i.itemId, "
                        + "i.itemName, i.itemSupplyPrice, i.itemSalesPrice, "
                        + "i.itemQuantity, i.itemSubTotal) FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
                List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                
                for(InvoiceItemHolder invoiceItemHolder : listItems) {
                    try {
                        String jql = "SELECT i FROM InvoiceItemDiscount i WHERE i.invoiceItemId = :invoiceItemId";
                        InvoiceItemDiscount itemDiscount = em.createQuery(jql, InvoiceItemDiscount.class)
                                .setParameter("invoiceItemId", invoiceItemHolder.getId())
                                .setMaxResults(1)
                                .getSingleResult();
                    
                        InvoiceItemDiscountHolder holder = new InvoiceItemDiscountHolder();
                        
                        holder.setDiscountId(itemDiscount.getDiscountId());
                        holder.setDiscountName(itemDiscount.getDiscountName());
                        
                        holder.setDiscountAmount(itemDiscount.getDiscountAmount());
                        
                        holder.setDiscountApplyType(itemDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                        holder.setDiscountCalculationType(itemDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());
                        
                        holder.setDiscountValue(itemDiscount.getDiscountValue());
                        holder.setDiscountValueType(itemDiscount.getDiscountValueType().getDiscountValueTypeId());
                        
                        holder.setDiscountDescription(itemDiscount.getDiscountDescription());
                        
                        invoiceItemHolder.setItemDiscount(holder);
                    } catch(NoResultException nre ) {
                        // if no result just ignore.
                    }
                }
                
                invoice.setInvoiceItems(listItems);

                String queryInvoicePricing = "SELECT NEW id.hardana.ejb.mpos.holder.InvoicePricingHolder(p.name, "
                        + "i.pricingType, i.pricingValue, i.pricingAmount, i.pricingLevel) "
                        + "FROM InvoicePricing i JOIN Pricing p ON i.pricingId = p.id WHERE i.invoiceId = :invoiceId";
                List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setInvoicePricing(listPricing);

                String queryTransaction = "SELECT NEW id.hardana.ejb.mpos.holder.TransactionPaymentHolder(tp.type, "
                        + "t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                        + "t.totalAmount, t.status, t.dateTime, c.pan, c.cardHolderName, p.account, p.firstName, "
                        + "p.lastName, tpcc.cardType, tpcc.approvalCode) FROM TransactionPayment tp LEFT JOIN TransactionTbl t ON tp.transactionId = t.id "
                        + "LEFT JOIN Card c ON tp.sourceId = c.id LEFT JOIN PersonalInfo p ON tp.sourceId = p.id "
                        + "LEFT JOIN TransactionPaymentCreditCard tpcc ON tpcc.transactionPaymentId = tp.id "
                        + " WHERE tp.invoiceId = :invoiceId";
                List<TransactionPaymentHolder> listTransaction = em.createQuery(queryTransaction, TransactionPaymentHolder.class)
                        .setParameter("invoiceId", invoice.getId())
                        .getResultList();
                invoice.setTransactions(listTransaction);
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(HISTORY_KEY, listOutlet);
        return response;
    }

}
