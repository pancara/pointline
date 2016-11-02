/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.mpos.holder.AllItemsHolder;
import id.hardana.ejb.mpos.holder.CategoryRevenueHolder;
import id.hardana.ejb.mpos.holder.FavouriteItemsHolder;
import id.hardana.ejb.mpos.holder.ItemsDiscountHolder;
import id.hardana.ejb.mpos.holder.SaleItemsHolder;
import id.hardana.ejb.mpos.holder.PaymentTypeRevenueHolder;
import id.hardana.ejb.mpos.holder.PricingsHolder;
import id.hardana.ejb.mpos.holder.TopupRevenueHolder;
import id.hardana.ejb.mpos.holder.TransactionsDiscountHolder;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arya
 */
@Stateless
@LocalBean
public class PrintSaleBean {

    private final String STATUS_KEY = "status";
    private final String SALE_KEY = "saleData";
    private final String queryOperatorIdList = "AND i.operatorId IN :operatorIdList ";

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    public JSONObject printSale(String merchantCode, String outletId,
            String operatorId, String strStartDate, String strEndDate, String startTime, String endTime) {
        
        int startOfHour = Integer.parseInt(startTime.substring(0, 2));
        int startOfMinute = Integer.parseInt(startTime.substring(2, 4));
        int endOfHour = Integer.parseInt(endTime.substring(0, 2));
        int endOfMinute = Integer.parseInt(endTime.substring(2, 4));
        
        Calendar date = new GregorianCalendar();
        int iStartDate = Integer.parseInt(strStartDate.substring(0, 2));
        int iStartMonth = Integer.parseInt(strStartDate.substring(3, 5));
        int iStartYear = Integer.parseInt(strStartDate.substring(6, 10));
        date.set(Calendar.DATE, iStartDate);
        date.set(Calendar.MONTH, iStartMonth - 1);
        date.set(Calendar.YEAR, iStartYear);
        
        date.set(Calendar.HOUR_OF_DAY, startOfHour);
        date.set(Calendar.MINUTE, startOfMinute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date startDate = date.getTime();
        
        int iEndDate = Integer.parseInt(strEndDate.substring(0, 2));
        int iEndMonth = Integer.parseInt(strEndDate.substring(3, 5));
        int iEndYear = Integer.parseInt(strEndDate.substring(6, 10));
        date.set(Calendar.HOUR_OF_DAY, endOfHour);
        date.set(Calendar.MINUTE, endOfMinute);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 0);
        
        date.set(Calendar.DATE, iEndDate);
        date.set(Calendar.MONTH, iEndMonth - 1);
        date.set(Calendar.YEAR, iEndYear);
        Date endDate = date.getTime();
        return getSale(merchantCode, outletId, operatorId, startDate, endDate);
    }

    private JSONObject getSale(String merchantCode, String outletId, String operatorId, Date startDate, Date endDate) {
        JSONObject response = new JSONObject();
        JSONArray operatorIdArray;
        Long outletIdLong;
        
        try {
            outletIdLong = Long.parseLong(outletId);        
            operatorIdArray = new JSONArray(operatorId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        Type listType = new TypeToken<List<Long>>() {}.getType();
        List<Long> operatorIdList = new Gson().fromJson(operatorIdArray.toString(), listType);
        
        SaleItemsHolder saleData = new SaleItemsHolder();
        //disable temporary
        Object[] values = getTotalAverageCountInvoice(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        
        BigDecimal totalBill =(BigDecimal) (values[0] == null ? BigDecimal.ZERO : values[0]);
        saleData.setTotalBill(totalBill);
        
        Double averageBill = (Double)(values[1] == null ? 0.0 : values[1]);
        saleData.setAverageBill(averageBill);
        
        Long totalInvoice = (Long) (values[2] == null ? 0L : values[2]);
        saleData.setTotalInvoice(totalInvoice);
        
        saleData.setPricings(getPricings(merchantCode, outletIdLong, operatorIdList, startDate, endDate));
        
        TopupRevenueHolder topupRevenue = getTopupRevenue(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        saleData.setTopupRevenue(topupRevenue);

        List<PaymentTypeRevenueHolder> paymentTypeRevenueList = getPaymentTypeRevenueList(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        saleData.setPaymentTypeRevenueList(paymentTypeRevenueList);

        List<CategoryRevenueHolder> categoryRevenueList = getCategoryRevenue(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        saleData.setCategoryRevenueList(categoryRevenueList);

        List<FavouriteItemsHolder> favouriteItemsList = getFavouriteItemList(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        saleData.setFavouriteItemsList(favouriteItemsList);
        
        List<ItemsDiscountHolder> itemSaleDiscountList = getItemDiscounts(merchantCode, startDate, endDate, outletIdLong, operatorIdList);
        saleData.setItemsDiscounts(itemSaleDiscountList);
        
        List<TransactionsDiscountHolder> transactionSaleDiscountList = getTransactionDiscounts(merchantCode, startDate, endDate, outletIdLong, operatorIdList);
        saleData.setTransactionDiscounts(transactionSaleDiscountList);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SALE_KEY, new JSONObject(new Gson().toJson(saleData)));
        return response;
    }
    
    private TopupRevenueHolder getTopupRevenue(String merchantCode, Long outletId, List<Long> operatorIdList, Date startDate, Date endDate) {
        // populate revenue
        List<TransactionMerchantTopupTypeEnum> reversalType = new ArrayList<>();
        reversalType.add(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD);
        reversalType.add(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD);
        String jql = "SELECT COUNT(t.id), SUM(CASE WHEN (tmt.type IN :reversalType) THEN -t.amount ELSE t.amount END) "
                + "FROM TransactionTbl t "
                + "JOIN TransactionMerchantTopup tmt ON t.id = tmt.transactionId "
                + "JOIN Merchant m ON m.id = tmt.merchantId "
                + "WHERE m.code = :merchantCode AND t.status = :transactionStatus "
                + "AND t.dateTime BETWEEN :startDate AND :endDate "
                + "AND tmt.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            jql += "AND tmt.operatorId IN :operatorIdList ";
        }
        
        Query query = em.createQuery(jql)
                .setParameter("merchantCode", merchantCode)
                .setParameter("transactionStatus", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("reversalType", reversalType)
                .setParameter("outletId", outletId);
        
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
        }
        Object[] values = null;
        try {
            values = (Object[]) query.getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
        Long freq = (Long) (values[0] == null ? 0L : values[0]);
        BigDecimal amount = (BigDecimal)(values[1] == null ? BigDecimal.ZERO : values[1]);
        
        return new TopupRevenueHolder(freq, amount);
    }

    private List<PricingsHolder> getPricings(String merchantCode, Long outletIdLong, List<Long> operatorIdList, Date startDate, Date endDate) {
        List<PricingsHolder> result;
        List<String> resultPricingName;
        List<PricingsHolder> lastResult = new ArrayList<>();
        List<PricingsHolder> allPricings = new ArrayList<>();
        
        String qlPricingsName = "SELECT DISTINCT pr.name FROM InvoicePricing p "
                + "JOIN Pricing pr ON pr.id = p.pricingId "
                + "JOIN Invoice i ON i.id = p.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        String qlPricings = "SELECT NEW id.hardana.ejb.mpos.holder.PricingsHolder"
                + "(pr.name, p.pricingValue, p.pricingAmount) FROM InvoicePricing p "
                + "JOIN Pricing pr ON pr.id = p.pricingId "
                + "JOIN Invoice i ON i.id = p.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            qlPricings += queryOperatorIdList;
            qlPricingsName += queryOperatorIdList;
        }
        
        Query queryPricingsName = em.createQuery(qlPricingsName, String.class)
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("outletId", outletIdLong);
        
        Query query = em.createQuery(qlPricings, PricingsHolder.class)
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("outletId", outletIdLong);
        
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
            queryPricingsName.setParameter("operatorIdList", operatorIdList);
        }
        
        try {
                resultPricingName = queryPricingsName.getResultList();
                result = query.getResultList();
            } catch(NoResultException e) {
                return null;
            }
        
        BigDecimal totalPricingAmount = BigDecimal.ZERO;
        int k=0;

        for (String strPricingsName : resultPricingName) {
            k=0;
            for(PricingsHolder pricingsHolder : result) { 
                PricingsHolder addPricingHolder = new PricingsHolder();
                k=k+1;
                if (pricingsHolder.getPricingName().equalsIgnoreCase(strPricingsName)) {
                    totalPricingAmount = totalPricingAmount.add(pricingsHolder.getPricingAmount());
                    addPricingHolder.setPricingName(pricingsHolder.getPricingName());
                    addPricingHolder.setPricingValue(pricingsHolder.getPricingValue());
                    addPricingHolder.setPricingAmount(totalPricingAmount);
                    lastResult.add(addPricingHolder);
                }
            }
            int m=0;
            for(PricingsHolder pricingsHolder2 : lastResult) {
                m=m+1;    
                PricingsHolder addItemHolder2 = new PricingsHolder();
                if(m == lastResult.size()){
                    addItemHolder2.setPricingName(pricingsHolder2.getPricingName());
                    addItemHolder2.setPricingValue(pricingsHolder2.getPricingValue());
                    addItemHolder2.setPricingAmount(pricingsHolder2.getPricingAmount());

                    List<PricingsHolder> lastResult02 = new ArrayList<>();
                    lastResult02.add(addItemHolder2);
                    allPricings.addAll(lastResult02);
                }
            }
            lastResult.clear();
            totalPricingAmount = BigDecimal.ZERO;
        }
        return allPricings;
    }

    private Object[] getTotalAverageCountInvoice(String merchantCode, Long outletId, List<Long> operatorIdList, Date startDate, Date endDate) {
        // total bill, average bill, count invoice
        String queryTotalOverage = "SELECT SUM(i.amount), AVG(i.amount), COUNT(i.id) FROM  Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            queryTotalOverage += queryOperatorIdList;
        }
        Query query = em.createQuery(queryTotalOverage)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletId);
        
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
        }
        Object[] result = (Object[]) query.getSingleResult();
        System.out.println("result: " + result);
        return result;
    }

    private List<FavouriteItemsHolder> getFavouriteItemList(String merchantCode, Long outletIdLong, List<Long> operatorIdList, Date startDate, Date endDate) {
        // favorite items
        String qlFavItems = "SELECT NEW id.hardana.ejb.mpos.holder.FavouriteItemsHolder"
                + "(it.itemName, SUM(it.itemQuantity)) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            qlFavItems += queryOperatorIdList;
        }
        qlFavItems += "GROUP BY it.itemName ORDER BY it.itemName ASC";
        
        Query query = em.createQuery(qlFavItems, FavouriteItemsHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletIdLong);
        
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
        }
        List<FavouriteItemsHolder> result;
        try {
            result = query.getResultList(); 
        } catch(NoResultException e) {
            return null;
        }
        return result;
    }

    private List<CategoryRevenueHolder> getCategoryRevenue(String merchantCode, Long outletIdLong, List<Long> operatorIdList, Date startDate, Date endDate) {
        // category revenue
        String qlCategoryRevenue = "SELECT NEW id.hardana.ejb.mpos.holder.CategoryRevenueHolder"
                + "(c.name, SUM(it.itemSubTotal)) FROM Category c "
                + "JOIN Items items ON c.id = items.categoryId "
                + "JOIN InvoiceItems it ON items.id = it.itemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            qlCategoryRevenue += queryOperatorIdList;
        }
        qlCategoryRevenue += "GROUP BY c.id ORDER BY c.name ASC";
        
        Query query = em.createQuery(qlCategoryRevenue,
                CategoryRevenueHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletIdLong);
       
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
        }
        List<CategoryRevenueHolder> result;
        try {
            result = query.getResultList();
        } catch(NoResultException e) {
            return null;
        }
        return result;
    }

    private List<PaymentTypeRevenueHolder> getPaymentTypeRevenueList(String merchantCode, Long outletId, List<Long> operatorIdList, Date startDate, Date endDate) {
        // payment method revenue
        String qlPaymentMethodRevenue = "SELECT NEW id.hardana.ejb.mpos.holder.PaymentTypeRevenueHolder"
                + "(tp.type, SUM(t.amount)) FROM TransactionTbl t "
                + "JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "WHERE tp.invoiceId IN (SELECT i.id FROM Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            qlPaymentMethodRevenue += queryOperatorIdList + ") ";
        } else {
            qlPaymentMethodRevenue += ") ";
        }
        qlPaymentMethodRevenue += "GROUP BY tp.type ORDER BY tp.type ASC ";
        Query query = em.createQuery(qlPaymentMethodRevenue, PaymentTypeRevenueHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletId);
        
        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
        }
        
        List<PaymentTypeRevenueHolder> paymentTypeRevenueList;
        try {
            paymentTypeRevenueList = query.getResultList();
        } catch(NoResultException e) {
            return null;
        }
        return paymentTypeRevenueList;
    }
    
    private List<ItemsDiscountHolder> getItemDiscounts(String merchantCode, Date startDate, Date endDate, Long outletId, List<Long> operatorIdList) {  
        List<ItemsDiscountHolder> result;
        List<String> resultDiscountName;
        List<ItemsDiscountHolder> lastResult = new ArrayList<>();
        List<ItemsDiscountHolder> allItemsDiscount = new ArrayList<>();
        // get item discount
        String qlItemDiscount = "SELECT NEW id.hardana.ejb.mpos.holder.ItemsDiscountHolder"
                + "(d.discountName, d.discountValue, d.discountAmount) FROM InvoiceItemDiscount d "
                + "JOIN InvoiceItems it ON it.id = d.invoiceItemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                + "AND (i.dateTime BETWEEN :startDate AND :endDate) "
                + "AND i.outletId = :outletId ";

        String qlItemDiscountName = "SELECT DISTINCT "
                + "d.discountName FROM InvoiceItemDiscount d "
                + "JOIN InvoiceItems it ON it.id = d.invoiceItemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                + "AND (i.dateTime BETWEEN :startDate AND :endDate) "
                + "AND i.outletId = :outletId ";

        if (!operatorIdList.isEmpty()) {
            qlItemDiscount += queryOperatorIdList;
            qlItemDiscountName += queryOperatorIdList;
        }
        Query query = em.createQuery(qlItemDiscount, ItemsDiscountHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletId);

        Query queryDiscountName = em.createQuery(qlItemDiscountName, String.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletId);

        if (!operatorIdList.isEmpty()) {
            query.setParameter("operatorIdList", operatorIdList);
            queryDiscountName.setParameter("operatorIdList", operatorIdList);
        }   

        try {
            result = query.getResultList();
            resultDiscountName = queryDiscountName.getResultList();
        } catch(NoResultException e) {
            return null;
        }

        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        int k=0;
        for (String strDiscountName : resultDiscountName) {
            k=0;
            for(ItemsDiscountHolder saleItemHolder : result) { 
                ItemsDiscountHolder addItemHolder = new ItemsDiscountHolder();
                k=k+1;
                if (saleItemHolder.getItemDiscountName().equalsIgnoreCase(strDiscountName)) {
                    totalDiscountAmount = totalDiscountAmount.add(saleItemHolder.getItemDiscountAmount());
                    addItemHolder.setItemDiscountName(saleItemHolder.getItemDiscountName());
                    addItemHolder.setItemDiscountValue(saleItemHolder.getItemDiscountValue());
                    addItemHolder.setItemDiscountAmount(totalDiscountAmount);

                   lastResult.add(addItemHolder);
                }
            }
            int m=0;
            for(ItemsDiscountHolder saleItemHolder2 : lastResult) {
                m=m+1;    
                ItemsDiscountHolder addItemHolder2 = new ItemsDiscountHolder();
                if(m == lastResult.size()){
                    addItemHolder2.setItemDiscountName(saleItemHolder2.getItemDiscountName());
                    addItemHolder2.setItemDiscountValue(saleItemHolder2.getItemDiscountValue());
                    addItemHolder2.setItemDiscountAmount(saleItemHolder2.getItemDiscountAmount());

                    List<ItemsDiscountHolder> lastResult02 = new ArrayList<>();
                    lastResult02.add(addItemHolder2);
                    allItemsDiscount.addAll(lastResult02);
                }
            }
            lastResult.clear();
            totalDiscountAmount = BigDecimal.ZERO;
        }

        return allItemsDiscount;
    }  
    
    private List<TransactionsDiscountHolder> getTransactionDiscounts(String merchantCode, Date startDate, Date endDate, Long outletId, List<Long> operatorIdList) {  
        List<TransactionsDiscountHolder> result;
        List<String> resultDiscountName;
        List<TransactionsDiscountHolder> lastResult = new ArrayList<>();
        List<TransactionsDiscountHolder> allTransactionsDiscount = new ArrayList<>();
        try {
             // get transaction discount
            String qlTransactionDiscount = "SELECT NEW id.hardana.ejb.mpos.holder.TransactionsDiscountHolder"
                    + "(d.discountName, d.discountValue, d.discountAmount) FROM InvoiceTransactionDiscount d "
                    + "JOIN Invoice i ON i.id = d.invoiceId "
                    + "JOIN Merchant m ON m.id = i.merchantId "
                    + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                    + "AND (i.dateTime BETWEEN :startDate AND :endDate) "
                    + "AND i.outletId = :outletId ";
            
            String qlTransactionDiscountName = "SELECT DISTINCT "
                    + "d.discountName FROM InvoiceTransactionDiscount d "
                    + "JOIN Invoice i ON i.id = d.invoiceId "
                    + "JOIN Merchant m ON m.id = i.merchantId "
                    + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                    + "AND (i.dateTime BETWEEN :startDate AND :endDate) "
                    + "AND i.outletId = :outletId ";

            if (!operatorIdList.isEmpty()) {
                qlTransactionDiscount += queryOperatorIdList;
                qlTransactionDiscountName += queryOperatorIdList;
            }

            Query query = em.createQuery(qlTransactionDiscount, TransactionsDiscountHolder.class)
                    .setParameter("merchantCode", merchantCode)
                    .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setParameter("outletId", outletId);
            
            Query queryDiscountName = em.createQuery(qlTransactionDiscountName, String.class)
                    .setParameter("merchantCode", merchantCode)
                    .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setParameter("outletId", outletId);

            if (!operatorIdList.isEmpty()) {
                query.setParameter("operatorIdList", operatorIdList);
                queryDiscountName.setParameter("operatorIdList", operatorIdList);
            }          
            
            try {
                result = query.getResultList();
                resultDiscountName = queryDiscountName.getResultList();
            } catch(NoResultException e) {
                return null;
            }
             
        } catch(NoResultException e) {
            return null;
        }
        
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        int k=0;

        for (String strDiscountName : resultDiscountName) {
            k=0;
            for (TransactionsDiscountHolder saleTransactionHolder : result) { 
                TransactionsDiscountHolder addTransactionHolder = new TransactionsDiscountHolder();
                k=k+1;
                if (saleTransactionHolder.getTransactionDiscountName().equalsIgnoreCase(strDiscountName)) {
                    totalDiscountAmount = totalDiscountAmount.add(saleTransactionHolder.getTransactionDiscountAmount());
                    addTransactionHolder.setTransactionDiscountName(saleTransactionHolder.getTransactionDiscountName());
                    addTransactionHolder.setTransactionDiscountValue(saleTransactionHolder.getTransactionDiscountValue());
                    addTransactionHolder.setTransactionDiscountAmount(totalDiscountAmount);
                    lastResult.add(addTransactionHolder);
                }
            }
            int m=0;
            for(TransactionsDiscountHolder saleTransactionHolder2 : lastResult) {
                m=m+1;    
                TransactionsDiscountHolder addTransactionHolder2 = new TransactionsDiscountHolder();
                if(m == lastResult.size()){
                    addTransactionHolder2.setTransactionDiscountName(saleTransactionHolder2.getTransactionDiscountName());
                    addTransactionHolder2.setTransactionDiscountValue(saleTransactionHolder2.getTransactionDiscountValue());
                    addTransactionHolder2.setTransactionDiscountAmount(saleTransactionHolder2.getTransactionDiscountAmount());

                    List<TransactionsDiscountHolder> lastResult02 = new ArrayList<>();
                    lastResult02.add(addTransactionHolder2);
                    allTransactionsDiscount.addAll(lastResult02);
                }
            }
            lastResult.clear();
            totalDiscountAmount = BigDecimal.ZERO;
        }
        return allTransactionsDiscount;
    } 
    
}
