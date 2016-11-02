/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import com.google.gson.Gson;
import id.hardana.ejb.merchantweb.extension.CategoryRevenueHolder;
import id.hardana.ejb.merchantweb.extension.FavouriteItemsHolder;
import id.hardana.ejb.merchantweb.extension.MerchantDashboardHolder;
import id.hardana.ejb.merchantweb.extension.PaymentTypeRevenueHolder;
import id.hardana.ejb.merchantweb.extension.TopupRevenueHolder;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
public class MerchantDashboardBean {

    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "dashboardData";

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject dailyMerchantDashboard(String merchantCode) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date startDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        return merchantDashboard(merchantCode, startDate, endDate);
    }

    public JSONObject monthlyMerchantDashboard(String merchantCode) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -31);
        Date startDate = date.getTime();
        return merchantDashboard(merchantCode, startDate, endDate);
    }

    public JSONObject weekToDateMerchantDashboard(String merchantCode) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            date.add(Calendar.DAY_OF_MONTH, -1);
        }
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = date.getTime();
        return merchantDashboard(merchantCode, startDate, endDate);
    }

    public JSONObject monthToDateMerchantDashboard(String merchantCode) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = date.getTime();    
        return merchantDashboard(merchantCode, startDate, endDate);
    }

    public JSONObject yearToDateMerchantDashboard(String merchantCode) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.set(Calendar.DAY_OF_YEAR, 1);
        Date startDate = date.getTime();
        return merchantDashboard(merchantCode, startDate, endDate);
    }

    private JSONObject merchantDashboard(String merchantCode, Date startDate, Date endDate) {
        
        JSONObject response = new JSONObject();
        MerchantDashboardHolder dashboardData = new MerchantDashboardHolder();
        
        Object[] values = getTotalAverageCountInvoice(merchantCode, startDate, endDate, InvoiceStatusEnum.PAID);
        
        Double averageBill = (Double)(values[1] == null ? 0.0 : values[1]);
        dashboardData.setAverageBill(averageBill);
        
        Long totalInvoice = (Long) (values[2] == null ? 0L : values[2]);
        dashboardData.setTotalInvoice(totalInvoice);
        
        //add for reversal
        Object[] valuesReversal = getTotalAverageCountInvoice(merchantCode, startDate, endDate, InvoiceStatusEnum.REVERSAL);
        
        BigDecimal totalBillReversal =(BigDecimal) (valuesReversal[0] == null ? BigDecimal.ZERO : valuesReversal[0]);
        dashboardData.setTotalBillReversal(totalBillReversal);
        
        Double averageBillReversal= (Double)(valuesReversal[1] == null ? 0.0 : valuesReversal[1]);
        dashboardData.setAverageBillReversal(averageBillReversal);
        
        Long totalInvoiceReversal = (Long) (valuesReversal[2] == null ? 0L : valuesReversal[2]);
        dashboardData.setTotalInvoiceReversal(totalInvoiceReversal);
        
        BigDecimal totalBillTemp =(BigDecimal) (values[0] == null ? BigDecimal.ZERO : values[0]);
        BigDecimal totalBill = totalBillTemp.subtract(totalBillReversal);
        if(totalBill.compareTo(BigDecimal.ZERO) == -1){
            totalBill = BigDecimal.ZERO;
        }
        dashboardData.setTotalBill(totalBill);
        
        BigDecimal totalDiscount = getTotalDiscount(merchantCode, startDate, endDate);
        dashboardData.setTotalDiscount(totalDiscount);
        
       // get ItemRevenue  all (include reversal)
        BigDecimal itemRevenuePaid = getItemRevenue(merchantCode, startDate, endDate, InvoiceStatusEnum.PAID);
        BigDecimal itemRevenueReversal = getItemRevenue(merchantCode, startDate, endDate, InvoiceStatusEnum.REVERSAL);
        BigDecimal itemRevenueAll = itemRevenuePaid.subtract(itemRevenueReversal);
        if(itemRevenueAll.compareTo(BigDecimal.ZERO) == -1){
            itemRevenueAll = BigDecimal.ZERO;
        }
        dashboardData.setTotalItemsRevenue(itemRevenueAll);

        Long totalItemPaid = getTotalItem(merchantCode, startDate, endDate, InvoiceStatusEnum.PAID);
        Long totalItemReversal = getTotalItem(merchantCode, startDate, endDate, InvoiceStatusEnum.REVERSAL);
        Long totalItemAll = totalItemPaid - totalItemReversal;
        if(totalItemAll < 0L){
            totalItemAll = 0L;
        }
        dashboardData.setTotalItems(totalItemAll);

        BigDecimal totalPricingRevenue = getPricingRevenue(merchantCode, startDate, endDate);
        dashboardData.setTotalPricingRevenue(totalPricingRevenue);
        
        TopupRevenueHolder topupRevenue = getTopupRevenue(merchantCode, startDate, endDate);
        dashboardData.setTopupRevenue(topupRevenue);
       
        List<PaymentTypeRevenueHolder> paymentTypeRevenueList = getPaymentTypeRevenueList(merchantCode, startDate, endDate);
        List<PaymentTypeRevenueHolder> lastPaymentReversalList = new ArrayList<PaymentTypeRevenueHolder>();
       
        for (PaymentTypeRevenueHolder temp : paymentTypeRevenueList) {
            if(temp.getPaymentType().equals(TransactionPaymentTypeEnum.CASHCARD)){
                if(totalBillReversal != BigDecimal.ZERO){
                    BigDecimal lastRevenue = temp.getRevenue().subtract(totalBillReversal);
                    if(lastRevenue.compareTo(BigDecimal.ZERO) == -1){
                        lastRevenue = BigDecimal.ZERO;
                    }
                    temp.setRevenue(lastRevenue);
                }
            }
            lastPaymentReversalList.add(temp);
        }
        dashboardData.setPaymentTypeRevenueList(lastPaymentReversalList);
        // get CategoryRevenueList  all (include reversal)
        List<CategoryRevenueHolder> categoryRevenueListPaid = getCategoryRevenue(merchantCode, startDate, endDate, InvoiceStatusEnum.PAID);
        List<CategoryRevenueHolder> categoryRevenueListReversal = getCategoryRevenue(merchantCode, startDate, endDate, InvoiceStatusEnum.REVERSAL);
        List<CategoryRevenueHolder> lastCategoryRevenueList = new ArrayList<CategoryRevenueHolder>();
        for (CategoryRevenueHolder tempPaid : categoryRevenueListPaid) {
            for (CategoryRevenueHolder tempReversal : categoryRevenueListReversal) {
                if(tempPaid.getCategoryName().equalsIgnoreCase(tempReversal.getCategoryName())){
                    BigDecimal lastRevenue = tempPaid.getRevenue().subtract(tempReversal.getRevenue());
                    tempPaid.setRevenue(lastRevenue);
                }
            }
            lastCategoryRevenueList.add(tempPaid);
        }
        
        dashboardData.setCategoryRevenueList(lastCategoryRevenueList);

        List<FavouriteItemsHolder> favouriteItemsListPaid = getFavouriteItemList(merchantCode, startDate, endDate, InvoiceStatusEnum.PAID);
        List<FavouriteItemsHolder> favouriteItemsListReversal = getFavouriteItemList(merchantCode, startDate, endDate, InvoiceStatusEnum.REVERSAL);
        List<FavouriteItemsHolder> favouriteItemsListAll = new ArrayList<FavouriteItemsHolder>();
        for (FavouriteItemsHolder tempPaid : favouriteItemsListPaid) {
            for (FavouriteItemsHolder tempReversal : favouriteItemsListReversal) {
                if(tempPaid.getItemName().equalsIgnoreCase(tempReversal.getItemName())){
                    Long lastQuantity = tempPaid.getQuantity() - tempReversal.getQuantity();
                    if(lastQuantity < 0L){
                        lastQuantity = 0L;
                    }
                    tempPaid.setQuantity(lastQuantity);
                }
            }
            favouriteItemsListAll.add(tempPaid);
        }
        
        dashboardData.setFavouriteItemsList(favouriteItemsListAll);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(dashboardData)));
        return response;
    }

    private TopupRevenueHolder getTopupRevenue(String merchantCode, Date startDate, Date endDate) {
        // populate revenue
        List<TransactionMerchantTopupTypeEnum> reversalType = new ArrayList<>();
        reversalType.add(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD);
        reversalType.add(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD);
        String jql = "SELECT COUNT(t.id), SUM(CASE WHEN (tmt.type IN :reversalType) THEN -t.amount ELSE t.amount END) "
                + "FROM TransactionTbl t "
                + "JOIN TransactionMerchantTopup tmt ON t.id = tmt.transactionId "
                + "JOIN Merchant m ON m.id = tmt.merchantId "
                + "WHERE m.code = :merchantCode AND t.status = :transactionStatus "
                + "AND t.dateTime BETWEEN :startDate AND :endDate ";
        
        Object[] values = (Object[]) em.createQuery(jql)
                .setParameter("merchantCode", merchantCode)
                .setParameter("transactionStatus", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("reversalType", reversalType)
                .getSingleResult();
        
        
        Long freq = (Long) (values[0] == null ? 0L : values[0]);
        BigDecimal amount = (BigDecimal)(values[1] == null ? BigDecimal.ZERO : values[1]);
        
        return new TopupRevenueHolder(freq, amount);
    }

    private Long getTotalItem(String merchantCode, Date startDate, Date endDate, InvoiceStatusEnum invoiceStatus) {
        String qlItemRevenue = "SELECT SUM(it.itemQuantity) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        Long totalItem = em.createQuery(qlItemRevenue, Long.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
        
        return totalItem == null ? 0L : totalItem;
    }

    private BigDecimal getPricingRevenue(String merchantCode, Date startDate, Date endDate) {
        // total pricing revenue
        String qlPricingRevenue = "SELECT SUM(p.pricingAmount) FROM InvoicePricing p "
                + "JOIN Invoice i ON i.id = p.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        BigDecimal totalPricingRevenue = em.createQuery(qlPricingRevenue, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
        return totalPricingRevenue == null ? BigDecimal.ZERO : totalPricingRevenue;
    }

    private BigDecimal getItemRevenue(String merchantCode, Date startDate, Date endDate, InvoiceStatusEnum invoiceStatus) {
        // item revenue
        String qlItemsRevenue = "SELECT SUM(it.itemSubTotal) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        
        BigDecimal itemRevenue = em.createQuery(qlItemsRevenue, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
        return itemRevenue == null ? BigDecimal.ZERO : itemRevenue;
    }

    private Object[] getTotalAverageCountInvoice(String merchantCode, Date startDate, Date endDate, InvoiceStatusEnum invoiceStatus) {
        // total bill, average bill, count invoice
        String qlMerchantDashboard = "SELECT SUM(i.amount), AVG(i.amount), COUNT(i.id) FROM  Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ";
        
        Object[] result = (Object[]) em.createQuery(qlMerchantDashboard)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
        
        return result;
    }

    private List<FavouriteItemsHolder> getFavouriteItemList(String merchantCode, Date startDate, Date endDate, InvoiceStatusEnum invoiceStatus) {
        // favorite items
        String qlFavItems = "SELECT NEW id.hardana.ejb.merchantweb.extension.FavouriteItemsHolder"
                + "(it.itemName, SUM(it.itemQuantity)) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "GROUP BY it.itemName ORDER BY it.itemName ASC";
        
        return em.createQuery(qlFavItems, FavouriteItemsHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList(); 
    }

    private List<CategoryRevenueHolder> getCategoryRevenue(String merchantCode, Date startDate, Date endDate, InvoiceStatusEnum invoiceStatus) {
        // category revenue
        String qlCategoryRevenue = "SELECT NEW id.hardana.ejb.merchantweb.extension.CategoryRevenueHolder"
                + "(c.name, SUM(it.itemSubTotal)) FROM Category c "
                + "JOIN Items items ON c.id = items.categoryId "
                + "JOIN InvoiceItems it ON items.id = it.itemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "GROUP BY c.id ORDER BY c.name ASC";
        
        return em.createQuery(qlCategoryRevenue,
                CategoryRevenueHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", invoiceStatus)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    private List<PaymentTypeRevenueHolder> getPaymentTypeRevenueList(String merchantCode, Date startDate, Date endDate) {
        // payment method revenue
        String qlPaymentMethodRevenue = "SELECT NEW id.hardana.ejb.merchantweb.extension.PaymentTypeRevenueHolder"
                + "(tp.type, SUM(t.amount)) FROM TransactionTbl t "
                + "JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "WHERE tp.invoiceId IN (SELECT i.id FROM Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate) "
                + "GROUP BY tp.type ORDER BY tp.type ASC";
        
        List<PaymentTypeRevenueHolder> paymentTypeRevenueList = em.createQuery(qlPaymentMethodRevenue, PaymentTypeRevenueHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        
        return paymentTypeRevenueList;
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
 

    private BigDecimal getTotalDiscount(String merchantCode, Date startDate, Date endDate) {                
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // get item discount
        
        String qlItemDiscount = "SELECT SUM(d.discountAmount) FROM InvoiceItemDiscount d "
                + "JOIN InvoiceItems it ON it.id = d.invoiceItemId "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE (m.code = :merchantCode) AND (i.status = :invoiceStatus) "
                + "AND (i.dateTime BETWEEN :startDate AND :endDate) ";
         
        TypedQuery<BigDecimal> itemDiscountQuery = em.createQuery(qlItemDiscount, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        
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

        
        TypedQuery<BigDecimal> trxDiscountQuery = em.createQuery(qlTransactionDiscount, BigDecimal.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        
        BigDecimal trxDiscountAmount = trxDiscountQuery.getSingleResult(); 
        if (trxDiscountAmount != null) {
            totalDiscount = totalDiscount.add(trxDiscountAmount);
        }
        
        return totalDiscount;
    }  
}
