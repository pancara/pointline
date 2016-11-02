
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.merchantweb.extension.CategoryRevenueHolder;
import id.hardana.ejb.merchantweb.extension.InvoiceHolder;
import id.hardana.ejb.merchantweb.extension.InvoiceItemDiscountHolder;
import id.hardana.ejb.merchantweb.extension.InvoiceItemHolder;
import id.hardana.ejb.merchantweb.extension.InvoicePricingHolder;
import id.hardana.ejb.merchantweb.extension.InvoiceTransactionDiscountHolder;
import id.hardana.ejb.merchantweb.extension.ItemsReportHolder;
import id.hardana.ejb.merchantweb.extension.TransactionPaymentHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.enums.ProductStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class MerchantReportBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String HISTORY_KEY = "invoice";
    private final String COUNT_KEY = "countInvoice";
    private final String ITEMS_KEY = "items";
    private final String ALL_ITEMS_KEY = "allItems";
    private final String DASHBOARD_DATA_KEY = "dashboardData";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private MerchantDashboardBean mdb;
    private final String queryOutletId = "AND i.outletId IN :outletIdList ";
    private final String queryOperatorId = "AND i.operatorId IN :operatorIdList ";   

    public JSONObject merchantReport(String merchantCode, String limit, String page,
            String startDateString, String endDateString, String outletId, String operatorId) {
        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;
        Date startDate;
        Date endDate;
        JSONArray outletIdArray;
        JSONArray operatorIdArray;
        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
            startDate = DATE_FORMAT.parse(startDateString);
            endDate = DATE_FORMAT.parse(endDateString);
            outletIdArray = new JSONArray(outletId);
            operatorIdArray = new JSONArray(operatorId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (pageInteger < 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        Type listType = new TypeToken<List<Long>>() {}.getType();
        List<Long> outletIdList = new Gson().fromJson(outletIdArray.toString(), listType);
        List<Long> operatorIdList = new Gson().fromJson(operatorIdArray.toString(), listType);

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        JSONObject responseReportInvoice = invoiceReport(merchantId, limitInteger, pageInteger,
                startDate, endDate, outletIdList, operatorIdList);
               
        List<ItemsReportHolder> itemReportPaid = getItemsReport(merchantCode, startDate, endDate, outletIdList, operatorIdList, InvoiceStatusEnum.PAID);
        List<ItemsReportHolder> itemReportReversal = getItemsReport(merchantCode, startDate, endDate, outletIdList, operatorIdList, InvoiceStatusEnum.REVERSAL);
        List<ItemsReportHolder> itemReportAll = new ArrayList<ItemsReportHolder>();
        
         for (ItemsReportHolder tempPaid : itemReportPaid) {
            for (ItemsReportHolder tempReversal : itemReportReversal) {
                if(tempPaid.getItemName().equalsIgnoreCase(tempReversal.getItemName())){
                    Long lastQuantity = tempPaid.getQuantity() - tempReversal.getQuantity();
                    BigDecimal lastAmount = tempPaid.getAmount().subtract(tempReversal.getAmount());
                    
                    if(lastQuantity < 0L){
                        lastQuantity = 0L;
                    }
                    if(lastAmount.compareTo(BigDecimal.ZERO) == -1){
                        lastAmount = BigDecimal.ZERO;
                    }
                    tempPaid.setQuantity(lastQuantity);
                    tempPaid.setAmount(lastAmount);
                }
            }
            itemReportAll.add(tempPaid);
        }
         
        responseReportInvoice.put(ITEMS_KEY, itemReportAll);
        //        outletIdList, operatorIdList));   //
        responseReportInvoice.put(ALL_ITEMS_KEY, getAllItems(merchantCode));
              
        return responseReportInvoice;
    }

    private List<ItemsReportHolder> getAllItems(String merchantCode) {
        String queryAllItems = "SELECT NEW id.hardana.ejb.merchantweb.extension.ItemsReportHolder"
                + "(i.id, i.name) FROM Items i JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.isDeleted = :isDeleted AND i.status = :status "
                + "ORDER BY i.id";
        List<ItemsReportHolder> allItems = em.createQuery(queryAllItems, ItemsReportHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("isDeleted", false)
                .setParameter("status", ProductStatusEnum.ACTIVE)
                .getResultList();
        return allItems;
    }

    private List<ItemsReportHolder> getItemsReport(String merchantCode, Date startDate,
            Date endDate, List<Long> outletIdList, List<Long> operatorIdList, InvoiceStatusEnum invoiceStatus) {

        String queryGroupAndOrder = "GROUP BY FUNCTION('DATE', i.dateTime), it.itemName, it.itemId "
                + "ORDER BY FUNCTION('DATE', i.dateTime), it.itemId ASC ";

        String queryItems = "SELECT NEW id.hardana.ejb.merchantweb.extension.ItemsReportHolder"
                + "(FUNCTION('DATE', i.dateTime), it.itemId, it.itemName, SUM(it.itemQuantity), SUM(it.itemSubTotal)) "
                + "FROM InvoiceItems it JOIN Invoice i ON i.id = it.invoiceId JOIN Merchant m "
                + "ON m.id = i.merchantId WHERE m.code = :merchantCode AND i.status = :status "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";

        if (!outletIdList.isEmpty()) {
            queryItems += queryOutletId;
        }
        if (!operatorIdList.isEmpty()) {
            queryItems += queryOperatorId;
        }
        queryItems += queryGroupAndOrder;

        Query queryItemsReport = em.createQuery(queryItems, ItemsReportHolder.class);
        queryItemsReport.setParameter("merchantCode", merchantCode);
        queryItemsReport.setParameter("status", invoiceStatus);
        queryItemsReport.setParameter("startDate", startDate);
        queryItemsReport.setParameter("endDate", endDate);
        if (!outletIdList.isEmpty()) {
            queryItemsReport.setParameter("outletIdList", outletIdList);
        }
        if (!operatorIdList.isEmpty()) {
            queryItemsReport.setParameter("operatorIdList", operatorIdList);
        }

        List<ItemsReportHolder> itemsReport = queryItemsReport.getResultList();
        return itemsReport;
    }

    private JSONObject invoiceReport(Long merchantId, Integer limitInteger, Integer pageInteger,
            Date startDate, Date endDate, List<Long> outletIdList, List<Long> operatorIdList) {
       
        JSONObject response = new JSONObject();
        int firstResult = ((pageInteger - 1) * limitInteger);
        String queryInvoiceHistoryCount = "SELECT COUNT(i.id) FROM Invoice i "
                + "WHERE i.merchantId = :merchantId AND i.dateTime BETWEEN :startDate AND :endDate ";
        
        if (!outletIdList.isEmpty()) {
            queryInvoiceHistoryCount += queryOutletId;
        }
        if (!operatorIdList.isEmpty()) {
            queryInvoiceHistoryCount += queryOperatorId;
        }
        Query queryInvoiceCount = em.createQuery(queryInvoiceHistoryCount, Long.class);
        queryInvoiceCount.setParameter("merchantId", merchantId);
        queryInvoiceCount.setParameter("startDate", startDate);
        queryInvoiceCount.setParameter("endDate", endDate);
        
        if (!outletIdList.isEmpty()) {
            queryInvoiceCount.setParameter("outletIdList", outletIdList);
        }
        if (!operatorIdList.isEmpty()) {
            queryInvoiceCount.setParameter("operatorIdList", operatorIdList);
        }
        
        Long invoiceHistoryCount = (Long) queryInvoiceCount.getSingleResult();

        List<InvoiceHolder> listInvoice = null;
        if (invoiceHistoryCount > 0) {
            String queryInvoiceHistoryNoReversal = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceHolder"
                    + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, o.userName, "
                    + "ot.name, ot.latitude, ot.longitude) "
                    + "FROM Invoice i LEFT JOIN Operator o ON i.operatorId = o.id "
                    + "LEFT JOIN Outlet ot ON ot.id = i.outletId WHERE i.merchantId = :merchantId "
                    + "AND i.dateTime BETWEEN :startDate AND :endDate ";
            
            String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceHolder"
                    + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, o.userName, "
                    + "ot.name, ot.latitude, ot.longitude, ii.number) "
                    + "FROM Invoice i LEFT JOIN Operator o ON i.operatorId = o.id "
                    + "LEFT JOIN Outlet ot ON ot.id = i.outletId "
                    + "LEFT JOIN InvoiceReversal ir ON ir.invoiceId = i.id "
                    + "LEFT JOIN Invoice ii ON ii.id = ir.invoiceIdReference "
                    + "WHERE i.merchantId = :merchantId "
                    + "AND i.dateTime BETWEEN :startDate AND :endDate ";

            if (!outletIdList.isEmpty()) {
                queryInvoiceHistory += "AND i.outletId IN :outletIdList ";
            }
            if (!operatorIdList.isEmpty()) {
                queryInvoiceHistory += "AND i.operatorId IN :operatorIdList ";
            }
            
            queryInvoiceHistory += "ORDER BY i.id DESC ";

            Query queryInvoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class);
            queryInvoice.setParameter("merchantId", merchantId);
            queryInvoice.setParameter("startDate", startDate);
            queryInvoice.setParameter("endDate", endDate);
            if (!outletIdList.isEmpty()) {
                queryInvoice.setParameter("outletIdList", outletIdList);
            }
            if (!operatorIdList.isEmpty()) {
                queryInvoice.setParameter("operatorIdList", operatorIdList);
            }
            
            listInvoice = queryInvoice.setFirstResult(firstResult)
                    .setMaxResults(limitInteger)
                    .getResultList();

            for (InvoiceHolder invoice : listInvoice) {
                String queryInvoiceItems = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceItemHolder(i.id, i.itemId, "
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
                
                String queryInvoicePricing = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoicePricingHolder(p.name, "
                        + "i.pricingType, i.pricingValue, i.pricingAmount, i.pricingLevel) "
                        + "FROM InvoicePricing i LEFT JOIN Pricing p ON i.pricingId = p.id WHERE i.invoiceId = :invoiceId";
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

}

