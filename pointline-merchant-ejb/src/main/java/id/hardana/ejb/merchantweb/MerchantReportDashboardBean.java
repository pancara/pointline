/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.merchantweb.extension.CategoryRevenueHolder;
import id.hardana.ejb.merchantweb.extension.FavouriteItemsHolder;
import id.hardana.ejb.merchantweb.extension.MerchantDashboardHolder;
import id.hardana.ejb.merchantweb.extension.PaymentTypeRevenueHolder;
import id.hardana.ejb.merchantweb.extension.TopupRevenueHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class MerchantReportDashboardBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String DASHBOARD_DATA_KEY = "dashboardData";
    
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject merchantReportDashboard(String merchantCode, String limit, String page,
            String startDateString, String endDateString, String outletId, String operatorId) {

        JSONObject response = new JSONObject();
        Integer pageInteger;
        Date startDate;
        Date endDate;
        JSONArray outletIdArray;
        JSONArray operatorIdArray;
        try {
            pageInteger = Integer.parseInt(page);
            startDate = DATE_FORMAT.parse(startDateString);
            endDate = DATE_FORMAT.parse(endDateString);
            outletIdArray = new JSONArray(outletId);
            operatorIdArray = new JSONArray(operatorId);
        } catch (NumberFormatException | ParseException | JSONException e) {
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
        };
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DASHBOARD_DATA_KEY, getMerchantDataFromDashboard(merchantCode, startDate, endDate, outletIdList, operatorIdList));
              
        return response;
    }
 
    //new feature
    private JSONObject getMerchantDataFromDashboard(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds) {
        MerchantDashboardHolder dashboardData = new MerchantDashboardHolder();
        
        Object[] values = getInvoiceTotalAverageCount(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
       
       // BigDecimal totalBill = (BigDecimal)(values[0] == null ? BigDecimal.ZERO : values[0]);
       // dashboardData.setTotalBill(totalBill);
        
        Double averageBill = (Double)(values[1] == null ? 0.0 : values[1]);
        dashboardData.setAverageBill(averageBill);
        
        Long totalInvoice = (Long)(values[2] == null ? 0L : values[2]);
        dashboardData.setTotalInvoice(totalInvoice);
        
        //add for reversal
        Object[] valuesReversal = getInvoiceTotalAverageCount(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        BigDecimal totalBillReversal= (BigDecimal)(valuesReversal[0] == null ? BigDecimal.ZERO : valuesReversal[0]);
        dashboardData.setTotalBillReversal(totalBillReversal);
        
        Double averageBillReversal = (Double)(valuesReversal[1] == null ? 0.0 : valuesReversal[1]);
        dashboardData.setAverageBillReversal(averageBillReversal);
        
        Long totalInvoiceReversal = (Long)(valuesReversal[2] == null ? 0L : valuesReversal[2]);
        dashboardData.setTotalInvoiceReversal(totalInvoiceReversal);
        
        BigDecimal totalBillTemp =(BigDecimal) (values[0] == null ? BigDecimal.ZERO : values[0]);
        BigDecimal totalBill = totalBillTemp.subtract(totalBillReversal);
        if(totalBill.compareTo(BigDecimal.ZERO) == -1){
            totalBill = BigDecimal.ZERO;
        }
        dashboardData.setTotalBill(totalBill);
        //
       
        BigDecimal totalDiscount = getTotalDiscount(merchantCode, startDate, endDate, outletIds, operatorIds);
        dashboardData.setTotalDiscount(totalDiscount);
        
        // get total item revenue all (include reversal)
        BigDecimal totalItemsRevenuePaid = getTotalItemsRevenue(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
        BigDecimal totalItemsRevenueReversal = getTotalItemsRevenue(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        BigDecimal totalItemRevenueAll = totalItemsRevenuePaid.subtract(totalItemsRevenueReversal);
        if(totalItemRevenueAll.compareTo(BigDecimal.ZERO) == -1){
            totalItemRevenueAll = BigDecimal.ZERO;
        }
        dashboardData.setTotalItemsRevenue(totalItemRevenueAll); 
        
        BigDecimal totalPricingRevenue = getTotalPricingRevenue(merchantCode, startDate, endDate, outletIds, operatorIds);
        dashboardData.setTotalPricingRevenue(totalPricingRevenue);        
        
        Long totalItemsPaid = getTotalItems(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
        Long totalItemsReversal = getTotalItems(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        Long totalItemAll = totalItemsPaid - totalItemsReversal;
        if(totalItemAll < 0L){
            totalItemAll = 0L;
        }
        dashboardData.setTotalItems(totalItemAll);    
        
        TopupRevenueHolder topupRevenue = getTopupRevenue(merchantCode, startDate, endDate, outletIds, operatorIds);
        dashboardData.setTopupRevenue(topupRevenue);
        
        List<PaymentTypeRevenueHolder> paymentRevenueList = getPaymentTypeRevenueList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
        List<PaymentTypeRevenueHolder> paymentRevenueListReversal = getPaymentTypeRevenueList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        
        if(!paymentRevenueListReversal.isEmpty()) {
            paymentRevenueList.addAll(paymentRevenueListReversal);  //add for reversal
        }
        dashboardData.setPaymentTypeRevenueList(paymentRevenueList);
        // get CategoryRevenueList  all (include reversal)
        List<CategoryRevenueHolder> categoryRevenueListPaid = getCategoryRevenueList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
        List<CategoryRevenueHolder> categoryRevenueListReversal = getCategoryRevenueList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        List<CategoryRevenueHolder> lastCategoryRevenueList = new ArrayList<CategoryRevenueHolder>();
       
        for (CategoryRevenueHolder tempPaid : categoryRevenueListPaid) {
            for (CategoryRevenueHolder tempReversal : categoryRevenueListReversal) {
                if(tempPaid.getCategoryName().equalsIgnoreCase(tempReversal.getCategoryName())){
                    BigDecimal lastRevenue = tempPaid.getRevenue().subtract(tempReversal.getRevenue());
                    if(lastRevenue.compareTo(BigDecimal.ZERO) == -1){
                        lastRevenue = BigDecimal.ZERO;
                    }
                    tempPaid.setRevenue(lastRevenue);
                }
            }
            lastCategoryRevenueList.add(tempPaid);
        }
        dashboardData.setCategoryRevenueList(lastCategoryRevenueList); 
        //end
        
        List<FavouriteItemsHolder> favoriteItemRevenueListPaid = getFavouriteItemsList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.PAID);
        List<FavouriteItemsHolder> favoriteItemRevenueListReversal = getFavouriteItemsList(merchantCode, startDate, endDate, outletIds, operatorIds, InvoiceStatusEnum.REVERSAL);
        List<FavouriteItemsHolder> favoriteItemRevenueListAll = new ArrayList<FavouriteItemsHolder>();
       
        for (FavouriteItemsHolder tempPaid : favoriteItemRevenueListPaid) {
            for (FavouriteItemsHolder tempReversal : favoriteItemRevenueListReversal) {
                if(tempPaid.getItemName().equalsIgnoreCase(tempReversal.getItemName())){
                    Long lastQuantity = tempPaid.getQuantity() - tempReversal.getQuantity();
                    if(lastQuantity < 0L){
                        lastQuantity = 0L;
                    }
                    tempPaid.setQuantity(lastQuantity);
                }
            }
            favoriteItemRevenueListAll.add(tempPaid);
        }

        dashboardData.setFavouriteItemsList(favoriteItemRevenueListAll);
        
        JSONObject response = new JSONObject(new Gson().toJson(dashboardData));
        
        return response;        
    }
   
    // get invoice summary (total bill, average bill, count of invoice)
    private Object[] getInvoiceTotalAverageCount(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {
        String qlInvoice = "SELECT SUM(i.amount), AVG(i.amount), COUNT(i.id) "
                + "FROM Invoice i JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "; 
         
        if (!outletIds.isEmpty()) {
            qlInvoice += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlInvoice += " AND (i.operatorId IN :operatorIds)";
        }
                        
        Query query =  em.createQuery(qlInvoice)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }      
        
        return (Object[]) query.getSingleResult();
    }
    
    private BigDecimal getTotalItemsRevenue(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {
        String qlItemsRevenue = "SELECT SUM(it.itemSubTotal) FROM InvoiceItems it"
            + " JOIN Invoice i ON i.id = it.invoiceId"
            + " JOIN Merchant m ON m.id = i.merchantId"
            + " WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus)"
            + " AND (i.dateTime BETWEEN :startDate AND :endDate)";
        
        if (!outletIds.isEmpty()) {
            qlItemsRevenue += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlItemsRevenue += " AND (i.operatorId IN :operatorIds)";
        }
        
        TypedQuery<BigDecimal> query = em.createQuery(qlItemsRevenue, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        
        BigDecimal itemRevenue = query.getSingleResult();
        
        return  itemRevenue == null ? BigDecimal.ZERO : itemRevenue; 
    }
    
    private BigDecimal getTotalPricingRevenue(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds) {
        String qlPricingRevenue = "SELECT SUM(p.pricingAmount) FROM InvoicePricing p "
                + "JOIN Invoice i ON i.id = p.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
         
        if (!outletIds.isEmpty()) {
            qlPricingRevenue += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlPricingRevenue += " AND (i.operatorId IN :operatorIds)";
        }
        
        TypedQuery<BigDecimal> query = em.createQuery(qlPricingRevenue, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        BigDecimal pricingRevenue = query.getSingleResult(); 
        
        return pricingRevenue == null ? BigDecimal.ZERO : pricingRevenue;
    }
    
    private Long getTotalItems(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {                
        String qlTotalItems = "SELECT SUM(it.itemQuantity) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        
        if (!outletIds.isEmpty()) {
            qlTotalItems += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlTotalItems += " AND (i.operatorId IN :operatorIds)";
        }
        
        TypedQuery<Long> query = em.createQuery(qlTotalItems, Long.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          

        Long totalItems = query.getSingleResult(); 
        return totalItems == null ? 0L : totalItems;
    }  
    
    private List<PaymentTypeRevenueHolder> getPaymentTypeRevenueList(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {
        String qlPaymentTypeRevenue =  "SELECT tp.type, SUM(t.amount) FROM TransactionTbl t "
               + "JOIN TransactionPayment tp ON t.id = tp.transactionId "
               + "WHERE tp.invoiceId IN (SELECT i.id FROM Invoice i "
               + "JOIN Merchant m ON m.id = i.merchantId "
               + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
               + "AND (i.dateTime BETWEEN :startDate AND :endDate)";
        
        if (!outletIds.isEmpty()) {
            qlPaymentTypeRevenue += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlPaymentTypeRevenue += " AND (i.operatorId IN :operatorIds)";
        }
 
        qlPaymentTypeRevenue += ")";
        
        qlPaymentTypeRevenue +=  " GROUP BY tp.type ORDER BY tp.type ASC";            
        
        Query query  = em.createQuery(qlPaymentTypeRevenue)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
               
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        
        // populate data
        List<PaymentTypeRevenueHolder> result = new LinkedList<>();
        for (Object row : query.getResultList()) {
            Object[] values = (Object[]) row;

            TransactionPaymentTypeEnum paymentType = (TransactionPaymentTypeEnum) values[0];
            BigDecimal revenue= (BigDecimal) (values[1] == null ? BigDecimal.ZERO : values[1]);
            
            PaymentTypeRevenueHolder holder = new PaymentTypeRevenueHolder(paymentType, revenue);
            result.add(holder);
        }
        
        return result;
    }
    
    private List<PaymentTypeRevenueHolder> getPaymentReversal(String merchantCode, Date startDate, Date endDate) {
        String qlIds = "SELECT i.id FROM Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode "
                + "AND i.status = :invoiceStatusReversal " 
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        
        List<Long> getIdList = em.createQuery(qlIds, Long.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatusReversal", InvoiceStatusEnum.REVERSAL) 
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        
        String qlPaymentMethodRevenueReversal = "SELECT NEW id.hardana.ejb.merchantweb.extension.PaymentTypeRevenueHolder"
                + "(tp.type, SUM(t.amount)) FROM TransactionTbl t "
                + "JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "WHERE tp.invoiceId IN :idList "
                + "GROUP BY tp.type ORDER BY tp.type ASC";
         
        List<PaymentTypeRevenueHolder> paymentTypeRevenueList = new ArrayList<>();
        if(!getIdList.isEmpty()) {
            paymentTypeRevenueList = em.createQuery(qlPaymentMethodRevenueReversal, PaymentTypeRevenueHolder.class)
                    .setParameter("idList", getIdList)  
                    .getResultList();
        }
        
        return paymentTypeRevenueList;
    }
    
    private List<FavouriteItemsHolder> getFavouriteItemsList(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {
        String qlFavouriteItem = "SELECT it.itemName, SUM(it.itemQuantity) FROM InvoiceItems it "
            + "JOIN Invoice i ON i.id = it.invoiceId "
            + "JOIN Merchant m ON m.id = i.merchantId "
            + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
            + "AND (i.dateTime BETWEEN :startDate AND :endDate) ";
          
        if (!outletIds.isEmpty()) {
            qlFavouriteItem += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlFavouriteItem += " AND (i.operatorId IN :operatorIds)";
        }
        
        qlFavouriteItem += " GROUP BY it.itemName ORDER BY it.itemName ASC";    
        
        Query query  = em.createQuery(qlFavouriteItem)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
               
        
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        
        // populate data
        List<FavouriteItemsHolder> result = new LinkedList<>();
        for (Object row : query.getResultList()) {
            Object[] values = (Object[]) row;

            String itemName = (String) values[0];
            Long qty = (Long) (values[1] == null ? 0L : values[1]);
            
            result.add(new FavouriteItemsHolder(itemName, qty));
        }
        
        return result;
    }
    
    private List<CategoryRevenueHolder> getCategoryRevenueList(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds, InvoiceStatusEnum invoiceStatus) {
       String qlCategoryRevenue = "SELECT c.name, SUM(it.itemSubTotal) FROM Category c "
             + "JOIN Items items ON c.id = items.categoryId "
             + "JOIN InvoiceItems it ON items.id = it.itemId "
             + "JOIN Invoice i ON i.id = it.invoiceId "
             + "JOIN Merchant m ON m.id = i.merchantId "
             + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
             + "AND (i.dateTime BETWEEN :startDate AND :endDate) ";
          
        if (!outletIds.isEmpty()) {
            qlCategoryRevenue += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlCategoryRevenue += " AND (i.operatorId IN :operatorIds)";
        }
        
        qlCategoryRevenue += " GROUP BY c.id ORDER BY c.name ASC"; 
        
        Query query  = em.createQuery(qlCategoryRevenue)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        
        // populate data
        List<CategoryRevenueHolder> result = new LinkedList<>();
        for (Object row : query.getResultList()) {
            Object[] values = (Object[]) row;

            String categoryName = (String) values[0];
            BigDecimal amount = (BigDecimal) (values[1] == null ? BigDecimal.ZERO : values[1]);
            
            CategoryRevenueHolder holder = new CategoryRevenueHolder(categoryName, amount);
            result.add(holder);
        }
        
        return result;
    }
    
    private TopupRevenueHolder getTopupRevenue(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds) {     
        String qlTopupRevenue = "SELECT COUNT(t.id), "
                + "SUM(CASE WHEN (tmt.type IN :reversalTypes) THEN -t.amount ELSE t.amount END) "
            + "FROM TransactionTbl t LEFT JOIN TransactionMerchantTopup tmt ON t.id = tmt.transactionId "
            + "LEFT JOIN Merchant m ON m.id = tmt.merchantId "
            + "WHERE (m.code = :merchantCode) AND (t.status = :transactionStatus) "
            + "AND (t.dateTime BETWEEN :startDate AND :endDate) ";
        
        if (!outletIds.isEmpty()) {
            qlTopupRevenue += " AND (tmt.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlTopupRevenue += " AND (tmt.operatorId IN :operatorIds)";
        }
           
        List<TransactionMerchantTopupTypeEnum> reversalTypes = new ArrayList<>();
        reversalTypes.add(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD);
        reversalTypes.add(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD);
        
        Query query = em.createQuery(qlTopupRevenue)
                .setParameter("merchantCode", merchantCode)
                .setParameter("transactionStatus", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("reversalTypes", reversalTypes);
                
        if (!outletIds.isEmpty()) {
            query.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            query.setParameter("operatorIds", operatorIds);
        }          
        
        Object[] result = (Object[]) query.getSingleResult();
        
        Long freq = (Long)(result[0] == null ? 0L : result[0]);
        BigDecimal amount = (BigDecimal)(result[1] == null ? BigDecimal.ZERO : result[1]);
        
        return new TopupRevenueHolder(freq, amount);
    }

    private BigDecimal getTotalDiscount(String merchantCode, Date startDate, Date endDate, List<Long> outletIds, List<Long> operatorIds) {                
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // get item discount
        String qlItemDiscount = "SELECT SUM(d.discountAmount) FROM InvoiceItemDiscount d "
                + "JOIN InvoiceItems it ON it.id = d.invoiceItemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
 
                + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                + "AND (i.dateTime BETWEEN :startDate AND :endDate) ";
         
        
        if (!outletIds.isEmpty()) {
            qlItemDiscount += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlItemDiscount += " AND (i.operatorId IN :operatorIds)";
        }
        
        TypedQuery<BigDecimal> itemDiscountQuery = em.createQuery(qlItemDiscount, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            itemDiscountQuery.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            itemDiscountQuery.setParameter("operatorIds", operatorIds);
        }          
        
        BigDecimal itemDiscountAmount = itemDiscountQuery.getSingleResult(); 
        if (itemDiscountAmount != null) {
            totalDiscount = totalDiscount.add(itemDiscountAmount);
        }
        
         // get transaction discount
        String qlTransactionDiscount = "SELECT SUM(d.discountAmount) FROM InvoiceTransactionDiscount d "
                + "JOIN Invoice i ON i.id = d.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                + "AND (i.dateTime BETWEEN :startDate AND :endDate) ";
         
        
        if (!outletIds.isEmpty()) {
            qlTransactionDiscount += " AND (i.outletId IN :outletIds)";
        }
        
        if (!operatorIds.isEmpty()) {
            qlTransactionDiscount += " AND (i.operatorId IN :operatorIds)";
        }
        
        TypedQuery<BigDecimal> trxDiscountQuery = em.createQuery(qlTransactionDiscount, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
                
        if (!outletIds.isEmpty()) {
            trxDiscountQuery.setParameter("outletIds", outletIds);            
        }
        if (!operatorIds.isEmpty()) {
            trxDiscountQuery.setParameter("operatorIds", operatorIds);
        }          
        
        BigDecimal trxDiscountAmount = trxDiscountQuery.getSingleResult(); 
        if (trxDiscountAmount != null) {
            totalDiscount = totalDiscount.add(trxDiscountAmount);
        }
        
        return totalDiscount;
    }  
    
}
