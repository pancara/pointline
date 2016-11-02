/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import com.google.gson.Gson;
import id.hardana.ejb.merchantweb.extension.InvoiceHolder;
import id.hardana.ejb.merchantweb.extension.MerchantWebHolderSalesMonthToDate;
import id.hardana.ejb.merchantweb.extension.MerchantWebHolderSalesWeekToDate;
import id.hardana.ejb.merchantweb.extension.MerchantWebHolderSalesYearToDate;
import id.hardana.ejb.merchantweb.extension.MerchantWebHolderSalesToday;
import id.hardana.ejb.merchantweb.extension.MerchantWebHolderSalesMonthly;
import id.hardana.ejb.merchantweb.extension.SalesMonthlyByDateHolder;
import id.hardana.ejb.merchantweb.extension.SalesYearToDateHolder;
import id.hardana.ejb.merchantweb.extension.PeriodExt;
import id.hardana.ejb.merchantweb.extension.TransactionMerchantTopupHolder;
import id.hardana.ejb.merchantweb.extension.SalesWeekToDateHolder;
import id.hardana.ejb.merchantweb.extension.SalesTodayHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author hanafi
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MerchantSalesFacade {
   
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    protected EntityManager getEntityManager(){
        return em;
    }
    
    public MerchantSalesFacade(){
    }

    private final String WEEKLYPERIOD = "WEEKLY";
    private final String MONTHLYPERIOD = "MONTHLY";
    private final String YEARLYPERIOD = "YEARLY";
    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "salesFacadeData";
    private final String SUNDAY = "Sunday";  
    private final int WEEKTODATE   = 0;
    private final int MONTHTODATE  = 1;
    private final int YEARTODATE   = 2;
    private final int MONTHLY      = 3;
    
    public HashMap<String, Object> totalSales(String merchantCode, Date startDateDate, Date endDateDate) {
        Invoice entity = new Invoice();
        HashMap<String,Object> result = new HashMap<>();
        
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        List<Invoice> resultList = em.createNamedQuery("Invoice.findByMerchantIdWithDateRange", Invoice.class)
                .setParameter("merchantId", merchantId)
                .setParameter("startDate", startDateDate)
                .setParameter("endDate", endDateDate)
                .getResultList();
        
        // check if empty
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        result.put("status", "SUCCESS");
        result.put("totalTransaction", resultList.size());
        result.put("invoices", resultList);
        
        return result;
    }

    public HashMap<String, Object> itemQuery(String merchantCode, Date startDateDate, Date endDateDate) {
        HashMap<String, Object> result = new HashMap<>();
        // get merchantId from merchantCode
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // get invoices of merchant from date to date
        List<Invoice> resultList = em.createNamedQuery("Invoice.findByMerchantIdWithDateRange", Invoice.class)
                .setParameter("merchantId", merchantId)
                .setParameter("startDate", startDateDate)
                .setParameter("endDate", endDateDate)
                .getResultList();
        
        // check if empty
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        List<Long> invoiceIds = new ArrayList<>();
        // get all invoice ids
        for(Invoice i : resultList){
            invoiceIds.add(Long.parseLong(i.getId()));
        }
        
        // get invoiceitems from invoices
        List<InvoiceItems> itemList = em.createNamedQuery("InvoiceItems.findByInvoiceId",InvoiceItems.class)
                .setParameter("invoiceIds", invoiceIds)
                .getResultList();
        
        result.put("status", "SUCCESS");
        result.put("items", itemList);
        
        return result;
    }
    
    public HashMap<String, Object> periodToDate(String merchantCode, String period) {
        // period values: "WEEKLY", "MONTHLY", "YEARLY"
        
        HashMap<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // setup start and end date
        Date startDate = getStartDate(period);
        Date endDate = new Date();

        HashMap<String,String> queries = getQuery(period);
        // what to return exactly
        //staus: success/other status, count: numOfData, totalTrx: totalTrxValue, transactions: arrayOfTransaction [{date:'2015-01-01', value:'25000'},{date:'2015-01-02', value:'35000'},{date:'2015-01-03', value:'20000'}]
        // query with truncdate and groupby here
        // select date(datetime) as day, sum(amount) from invoice where merchantid = ? and status = ? group by day 
        
        List<PeriodExt> resultListSales = em.createQuery(queries.get("salesQuery"), PeriodExt.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        
        List<PeriodExt> resultListTopup = em.createQuery(queries.get("topupQuery"), PeriodExt.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

//        if(resultListSales.isEmpty()){
//            result.put("status", "NOTFOUND");
//            
//            return result;
//        }
        
        result.put("status", "SUCCESS");
        result.put("countSales", resultListSales.size());
        result.put("resultListSales", resultListSales);
        result.put("countTopup", resultListTopup);
        result.put("resultListTopup", resultListTopup);
        
        return result;
    }
    
    public HashMap<String, Object> periodYearToDate(String merchantCode, String period) {  
        HashMap<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // setup start and end date
        Date startDate = getStartDate(period);
        Date endDate = new Date();
        
        Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
            calendar.set(Calendar.DATE, 1);
        Date lastDate = calendar.getTime();  
            
        endDate.before(lastDate);

        HashMap<String,String> queries = getQuery(period);
        List<PeriodExt> resultListSales = em.createQuery(queries.get("salesQuery"), PeriodExt.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
     //temp   
        List<PeriodExt> resultListTopup = em.createQuery(queries.get("topupQuery"), PeriodExt.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
      
        result.put("status", "SUCCESS");
        result.put("resultListSales", resultListSales);
        
        //temp
        result.put("resultListTopup", resultListTopup);
        
        return result;
    }
    
    public HashMap<String, Object> topupHistory(String merchantCode) {
        HashMap<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String queryTopupHistory = "SELECT NEW id.hardana.ejb.merchantweb.extension.TransactionMerchantTopupHolder"
                + "(t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                + "t.totalAmount, t.status, t.dateTime, tm.type, o.userName, c.pan, c.cardHolderName, "
                + "p.account, p.firstName, p.lastName) FROM TransactionMerchantTopup tm "
                + "JOIN TransactionTbl t ON tm.transactionId = t.id "
                + "JOIN Operator o ON tm.operatorId = o.id "
                + "LEFT JOIN Card c ON tm.topupDestination = c.id "
                + "LEFT JOIN PersonalInfo p ON tm.topupDestination = p.id "
                + "WHERE tm.merchantId = :merchantId AND t.status = :status ";
        
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        

        // what to return exactly
        
        List<TransactionMerchantTopupHolder> resultList = em.createQuery(queryTopupHistory, TransactionMerchantTopupHolder.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .getResultList();
        
        // check if empty
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        result.put("status", "SUCCESS");
        result.put("topupHistoryList", resultList);
        
        return result;
    }
    
    public HashMap<String, Object> transactionHistory(String merchantCode) {
        HashMap<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String queryTransactionHistory = "SELECT NEW id.hardana.ejb.merchantweb.extension.InvoiceHolder(i.id, "
                + "i.number, i.amount, i.dateTime, "
                + "i.tableNumber, i.status, o.userName) FROM Invoice i JOIN Operator "
                + "o ON i.operatorId = o.id WHERE i.merchantId = :merchantId "
                + " AND i.status = :status";
        
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        List<InvoiceHolder> resultList = em.createQuery(queryTransactionHistory, InvoiceHolder.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", InvoiceStatusEnum.PAID)
                .getResultList();
                
        
                
                
        // check if empty
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        result.put("status", "SUCCESS");
        result.put("transHistory", resultList);
        
        return result;
    }
    
    public HashMap<String, Object> transactionByPaymentMethod(String merchantCode) {
        HashMap<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String queryTransactionByPaymentMethod = null;
    
        
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
//        List<HelperClass> resultList = em.createQuery(queryTransactionByPaymentMethod, HelperClass.class)
                
                
        // check if empty
//        if(resultList.isEmpty()){
//            result.put("status", "NOTFOUND");
//            return result;
//        }       
        
        result.put("status", "SUCCESS");
//        result.put("transByPaymentMethod", resultList);
        
        return result;
    }
    
    public HashMap<String, Object> revenueByCategory(String merchantCode) {
        HashMap<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String queryRevenueByCategory = null;
    
        // get merchant id from merchant code
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
//        List<HelperClass> resultList = em.createQuery(queryrevenueByCategory, HelperClass.class)
        
// check if empty
//        if(resultList.isEmpty()){
//            result.put("status", "NOTFOUND");
//            return result;
//        }       
        
        result.put("status", "SUCCESS");
//        result.put("revenueByCategory", resultList);
        
        return result;
    }

    
    ///////// helper
    private Long getMerchantIdFromMerchantCode(String merchantCode){
    Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
            .setParameter("code", merchantCode)
            .getSingleResult();

        return Long.valueOf(entityMerchantId.getId());

    }

    private Date getStartDate(String period) {
        Date retDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        if(WEEKLYPERIOD.equalsIgnoreCase(period)){
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
                calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
            }
            retDate = calendar.getTime();
        
        } else if (MONTHLYPERIOD.equalsIgnoreCase(period)){
            
//            while (calendar.get(Calendar.DATE) > 1) {
//                calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of month.
//            } // or just directly set 1
            calendar.set(Calendar.DATE, 1);
            retDate = calendar.getTime();
            
        } else if (YEARLYPERIOD.equalsIgnoreCase(period) ) {
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DATE, 1);
            retDate = calendar.getTime();           
        } else {
            retDate = calendar.getTime();          
        }
        
        return retDate;
    }

    private HashMap<String,String> getQuery(String period){
        HashMap<String,String> queries = new HashMap<>();
         
        String queryWeekly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt( "
                        + " FUNCTION ('DATE', i.dateTime), SUM(i.amount)) FROM Invoice i "
                        + " WHERE i.merchantId = :merchantId "
                        + " AND i.status = :status "
                        + " AND i.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY FUNCTION('DATE', i.dateTime) "
                        + " ORDER BY FUNCTION('DATE', i.dateTime) ASC ";
        
        String queryMonthly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt("
                        + " FUNCTION ('DATE', i.dateTime), sum(i.amount)) FROM Invoice i "
                        + " WHERE i.merchantId = :merchantId "
                        + " AND i.status = :status "
                        + " AND i.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY FUNCTION ('DATE', i.dateTime) "
                        + " ORDER BY FUNCTION ('DATE', i.dateTime) ASC ";
        
        String queryYearly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt( "
                        + " EXTRACT(MONTH FROM i.dateTime), SUM(i.amount)) FROM Invoice i"
                        + " WHERE i.merchantId = :merchantId "
                        + " AND i.status = :status "
                        + " AND i.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY EXTRACT(MONTH FROM i.dateTime) "
                        + " ORDER BY EXTRACT(MONTH FROM i.dateTime) ASC ";
        // topup
        // native: 
//        select date(t.datetime), sum(t.amount), sum(t.totalamount) from transactiontbl t 
//                join transactionmerchanttopup tmt on t.id = tmt.transactionid 
//                left join transactiontype tt on tt.id = t.type 
//                left join transactionmerchanttopuptype tmtp on tmtp.id=tmt.type 
//                where tt.type='MERCHANTTOPUP' group by date(t.datetime);
        String topupWeekly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt("
                        + " FUNCTION('DATE', t.dateTime), sum(t.amount), sum(t.totalAmount)) FROM transactiontbl t "
                        + " JOIN transactionmerchanttopup tmt on t.id = tmt.transactionId "
                        + " LEFT JOIN transactiontype tt on tt.id = t.type "
                        + " LEFT JOIN transactionmerchanttopuptype tmtp on tmtp.id=tmt.type "
                        + " WHERE tt.type='MERCHANTTOPUP' "
                        + " AND tmt.merchantId = :merchantId "
                        + " AND t.status = :status "
                        + " AND t.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY FUNCTION('DATE', t.dateTime) "
                        + " ORDER BY FUNCTION('DATE', t.dateTime) ASC ";
        String topupMonthly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt("
                        + " FUNCTION('DATE', t.dateTime), sum(t.amount), sum(t.totalAmount)) FROM transactiontbl t "
                        + " JOIN transactionmerchanttopup tmt on t.id = tmt.transactionId "
                        + " LEFT JOIN transactiontype tt on tt.id = t.type "
                        + " LEFT JOIN transactionmerchanttopuptype tmtp on tmtp.id=tmt.type "
                        + " WHERE tt.type='MERCHANTTOPUP' "
                        + " AND tmt.merchantId = :merchantId "
                        + " AND t.status = :status "
                        + " AND t.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY FUNCTION('DATE', t.dateTime) "
                        + " ORDER BY FUNCTION('DATE', t.dateTime) ASC ";
        String topupYearly = "SELECT NEW id.hardana.ejb.merchantweb.extension.PeriodExt("
                        + " EXTRACT(MONTH FROM t.dateTime), SUM(t.amount), SUM(t.totalAmount)) FROM transactiontbl t "
                        + " JOIN transactionmerchanttopup tmt on t.id = tmt.transactionId "
                        + " LEFT JOIN transactiontype tt on tt.id = t.type "
                        + " LEFT JOIN transactionmerchanttopuptype tmtp on tmtp.id=tmt.type "
                        + " WHERE tt.type='MERCHANTTOPUP' "
                        + " AND tmt.merchantId = :merchantId "
                        + " AND t.status = :status "
                        + " AND t.dateTime BETWEEN :startDate "
                        + " AND :endDate "
                        + " GROUP BY EXTRACT(MONTH FROM t.dateTime) "
                        + " ORDER BY EXTRACT(MONTH FROM t.dateTime) ASC ";
            
        if(WEEKLYPERIOD.equalsIgnoreCase(period)){
            queries.put("salesQuery", queryWeekly);
            queries.put("topupQuery", topupWeekly);
            return queries;
        } else if (MONTHLYPERIOD.equalsIgnoreCase(period)){
            queries.put("salesQuery", queryMonthly);
            queries.put("topupQuery", topupMonthly);
            return queries;
        } else if (YEARLYPERIOD.equalsIgnoreCase(period)){
            queries.put("salesQuery", queryYearly);
            queries.put("topupQuery", topupYearly);
            return queries;
        } else {
            queries.put("salesQuery", "");
            queries.put("topupQuery", "");
            return queries;
        }
        
    }
    
    private String getMerchantDashboardQuerySalesByPeriod(){          
        String query = "SELECT"
                + "(SUM(i.amount)) FROM  Invoice i "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode "
                + "AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
               ;
      
       return query;
    }
    
    private String getMerchantDashboardQueryTopupByPeriod(){          
         String query = "SELECT "
                 + "SUM(t.amount) FROM TransactionMerchantTopup tm "
                 + "JOIN TransactionTbl t ON tm.transactionId = t.id "
                 + "JOIN Merchant m ON m.id = tm.merchantId "
                 + "WHERE m.code = :merchantId "
                 + "AND t.status = :transactionStatus "
                 + "AND t.dateTime BETWEEN :startDate AND :endDate"
               ;
       return query;
    }

    public JSONObject yearToDateMerchantWebSales(String merchantCode) {
        Calendar now = Calendar.getInstance();       
        int currentMonth = (now.get(Calendar.MONTH) + 1);
        System.out.println("currentMonth : " + currentMonth);       
 
        return merchantWebYearToDate(merchantCode, currentMonth);
    }
    
    public JSONObject monthToDateMerchantWebSales(String merchantCode) {
        Calendar now = Calendar.getInstance();       
        int currentDate = (now.get(Calendar.DATE)); 
        System.out.println("currentDate : " + currentDate);
        
        return merchantWebMonthToDate(merchantCode, currentDate);
    }
    
    public JSONObject todayMerchantWebSales(String merchantCode) {
        return merchantWebToday(merchantCode);
    }
    
    public JSONObject monthlyMerchantWebSales(String merchantCode) {
        Calendar now = Calendar.getInstance();       
        int currentDate = (now.get(Calendar.DATE)); 
        System.out.println("currentDate : " + currentDate);
        return merchantWebMonthly(merchantCode, currentDate);
    }
    
    public JSONObject weekToDateMerchantWebSales(String merchantCode) {
        Calendar now = Calendar.getInstance();       
        int today = (now.get(Calendar.DAY_OF_WEEK) - 1); 
        System.out.println("today : " + today + " ==> (1 to 7 : Monday to Sunday)");       
        if(today == 0)
            today = 7;
        
        return merchantWebWeekToDate(merchantCode, today);
    }
    
    private JSONObject merchantWebToday(String merchantCode) {
        JSONObject response = new JSONObject();     
        MerchantWebHolderSalesToday salesFacadeDataToday = new MerchantWebHolderSalesToday();
        
        List<SalesTodayHolder> listOfToday = ListTotalSalesByHour(merchantCode);  
        salesFacadeDataToday.setTodayList(listOfToday);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(salesFacadeDataToday)));
        return response;
    }   
    
    private JSONObject merchantWebMonthly(String merchantCode, int currentDate) {
        JSONObject response = new JSONObject();     
        MerchantWebHolderSalesMonthly salesFacadeDataMonthly = new MerchantWebHolderSalesMonthly();
        
        List<SalesMonthlyByDateHolder> listOfMonthly = ListTotalSalesByDate(merchantCode, currentDate, MONTHLY);  
        salesFacadeDataMonthly.setMonthlyList(listOfMonthly);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(salesFacadeDataMonthly)));
        return response;
    }   

    private JSONObject merchantWebWeekToDate(String merchantCode, int today) {
        JSONObject response = new JSONObject();     
        MerchantWebHolderSalesWeekToDate salesFacadeDataWeekToDate = new MerchantWebHolderSalesWeekToDate();
        
        List<SalesWeekToDateHolder> listOfWeekToDate = ListTotalSalesByNameOfDay(merchantCode, today);  
        salesFacadeDataWeekToDate.setWeekToDateList(listOfWeekToDate);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(salesFacadeDataWeekToDate)));
        return response;
    }   
    
    private JSONObject merchantWebMonthToDate(String merchantCode, int currentDate) {
        JSONObject response = new JSONObject();     
        MerchantWebHolderSalesMonthToDate salesFacadeDataMonthToDate = new MerchantWebHolderSalesMonthToDate();
        
        List<SalesMonthlyByDateHolder> listOfMonthToDate = ListTotalSalesByDate(merchantCode, currentDate, MONTHTODATE);  
        salesFacadeDataMonthToDate.setMonthToDateList(listOfMonthToDate);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(salesFacadeDataMonthToDate)));
        return response;
    }  
    
    private JSONObject merchantWebYearToDate(String merchantCode, int currentMonth) {
        JSONObject response = new JSONObject();     
        MerchantWebHolderSalesYearToDate salesFacadeDataYearToDate = new MerchantWebHolderSalesYearToDate();
        List<SalesYearToDateHolder> listOfYearToDate = ListTotalSalesByMonth(merchantCode, currentMonth);  
        salesFacadeDataYearToDate.setYearToDateList(listOfYearToDate);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(salesFacadeDataYearToDate)));
        return response;
    }   
    
    private List<SalesMonthlyByDateHolder> ListTotalSalesByDate(String merchantCode, int currentDate, int periodeType)
    {    
        List<SalesMonthlyByDateHolder> listOfTotalSalesByDate = new ArrayList<>();   
        if (periodeType == MONTHTODATE) {
            for (int i = 0; i < currentDate; i++)
            {
                SalesMonthlyByDateHolder objOfMonthToDate = new SalesMonthlyByDateHolder();
                objOfMonthToDate.setDate(i + 1);

                BigDecimal totalSales = TotalSalesByMonth(merchantCode, getStartDate(MONTHTODATE, i+1), getEndDate(MONTHTODATE, i+1));
                objOfMonthToDate.setTotalSale(totalSales);    

                BigDecimal totalTopup = TotalTopupByMonth(merchantCode, getStartDate(MONTHTODATE, i+1),  getEndDate(MONTHTODATE, i+1));
                objOfMonthToDate.setTotalTopup(totalTopup);                

                listOfTotalSalesByDate.add(objOfMonthToDate);
            }
        } else if (periodeType == MONTHLY) {
            Calendar myCalendar = new GregorianCalendar();
            myCalendar.getTime();
            myCalendar.add(Calendar.MONTH, -1);
            Date startDate = myCalendar.getTime();
            System.out.println("startDate : " + startDate.toString());
            
            int daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
            System.out.println("daysInMonth : " + daysInMonth);
            
            for (int i = 0; i < daysInMonth; i++)
            {
                SalesMonthlyByDateHolder objOfMonthly = new SalesMonthlyByDateHolder();
                String getStartDate = getStartDate(MONTHLY, i+1).toString().substring(8, 10);                
                objOfMonthly.setDate(Integer.valueOf(getStartDate));

                BigDecimal totalSales = TotalSalesByMonth(merchantCode, getStartDate(MONTHLY, i+1), getEndDate(MONTHLY, i+1));
                objOfMonthly.setTotalSale(totalSales);    
                
                BigDecimal totalTopup = TotalTopupByMonth(merchantCode,   getStartDate(MONTHLY, i+1),  getEndDate(MONTHLY, i+1));
                objOfMonthly.setTotalTopup(totalTopup);                

                listOfTotalSalesByDate.add(objOfMonthly);
            }
        
        }
        return listOfTotalSalesByDate;
    }
    
    private List<SalesTodayHolder> ListTotalSalesByHour(String merchantCode)
    {
        List<SalesTodayHolder> listOfTodaySales = new ArrayList<>();   

        for (int i = 0; i < 24; i++)
        {      
            SalesTodayHolder objOfToday = new SalesTodayHolder();  
            String startHour = getStartHour(i).toString().substring(11, 13);;
            objOfToday.setHour(startHour);
 
            BigDecimal totalSales = TotalSalesByNameOfDay(merchantCode, getStartHour(i), getEndHour(i));
            objOfToday.setTotalSale(totalSales);    
            
            BigDecimal totalTopup = TotalTopupByMonth(merchantCode, getStartHour(i), getEndHour(i));
            objOfToday.setTotalTopup(totalTopup);                

            listOfTodaySales.add(objOfToday);
        }    
        return listOfTodaySales;
    }
     
    private List<SalesWeekToDateHolder> ListTotalSalesByNameOfDay(String merchantCode, int today)
    {
        //String[] dayArray = new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};   
        String[] dayArray = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; 
        List<SalesWeekToDateHolder> listOfWeekToDateSales = new ArrayList<>();   
       
        for (int i = 0; i < today; i++)
        {      
            SalesWeekToDateHolder objOfWeekToDate = new SalesWeekToDateHolder();  
            objOfWeekToDate.setNameOfDay(dayArray[i]);
             
            BigDecimal totalSales = TotalSalesByNameOfDay(merchantCode, getStartDate(WEEKTODATE, i), getEndDate(WEEKTODATE, i));
            objOfWeekToDate.setTotalSale(totalSales);    
            
            BigDecimal totalTopup = TotalTopupByMonth(merchantCode, getStartDate(WEEKTODATE, i), getEndDate(WEEKTODATE, i));
            objOfWeekToDate.setTotalTopup(totalTopup);                

            listOfWeekToDateSales.add(objOfWeekToDate);
        }    
        return listOfWeekToDateSales;
    }
    
    private List<SalesYearToDateHolder> ListTotalSalesByMonth(String merchantCode, int currentMonth)
    {
        String[] monthArray = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};       
        List<SalesYearToDateHolder> listOfYearToDateSales = new ArrayList<>();   
       
        for (int i = 0; i < currentMonth; i++)
        {
            SalesYearToDateHolder objOfYearToDate = new SalesYearToDateHolder();
            objOfYearToDate.setMonth(monthArray[i]);

            BigDecimal totalSales = TotalSalesByMonth(merchantCode, getStartDate(YEARTODATE, i), getEndDate(YEARTODATE, i)); 
            objOfYearToDate.setTotalSale(totalSales);    
            
            BigDecimal totalTopup = TotalTopupByMonth(merchantCode, getStartDate(YEARTODATE, i),  getEndDate(YEARTODATE, i));
            objOfYearToDate.setTotalTopup(totalTopup);                

            listOfYearToDateSales.add(objOfYearToDate);
        }
        return listOfYearToDateSales;
    }
    
    private BigDecimal TotalSalesByMonth(String merchantCode, Date startDate, Date endDate)
    {          
        BigDecimal totalSale = em.createQuery(getMerchantDashboardQuerySalesByPeriod(), BigDecimal.class)
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
             .getSingleResult();
       
       if(totalSale == null )
            totalSale = BigDecimal.ZERO;
       return totalSale;
    }
    
    private BigDecimal TotalTopupByMonth(String merchantId, Date startDate, Date endDate)
    {          
        BigDecimal totalTopup = em.createQuery(getMerchantDashboardQueryTopupByPeriod(), BigDecimal.class)
            .setParameter("merchantId", merchantId)
            .setParameter("transactionStatus", ResponseStatusEnum.SUCCESS)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
             .getSingleResult();
       
       if(totalTopup == null )
            totalTopup = BigDecimal.ZERO;
       return totalTopup;
    }
    
    private BigDecimal TotalSalesByNameOfDay(String merchantCode, Date startDate, Date endDate)
    {          
        BigDecimal totalSale = em.createQuery(getMerchantDashboardQuerySalesByPeriod(), BigDecimal.class)
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
             .getSingleResult();
       
       if(totalSale == null )
            totalSale = BigDecimal.ZERO;
       return totalSale;
    }
    
    private Date getStartHour(int hour)
    {
        Calendar date = new GregorianCalendar();
        Date startHourResult;
        
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        if(hour > 0)
            date.add(Calendar.HOUR, hour); 
        startHourResult = date.getTime();           
        
        return startHourResult;
    }
    
    private Date getEndHour(int hour)
    {
        Calendar date = new GregorianCalendar();
        Date startHourResult;
        
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.HOUR, hour); 

        startHourResult = date.getTime();           
        
        return startHourResult;
    }
    
    /*
    periodType :   0 --> Week to Date  : periodRange = 1 to 7 (Monday to Sunday)
                   1 --> Month To Date : periodRange = 1 to end date of the month
                   2 --> Year To Date  : periodRange = 0 to 11 (January to December) 
                   3 --> Monthly       : periodRange = one month from today
    */
    private Date getStartDate(int periodType, int periodRange)
    {
        Calendar date = new GregorianCalendar();
        Date startDateResult;
        
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        
        if (periodType == 0){
            String dayNames[] = new DateFormatSymbols().getWeekdays();
            String today = dayNames[date.get(Calendar.DAY_OF_WEEK)];
            if(SUNDAY.equalsIgnoreCase(today) && periodRange < 6)
                date.add(Calendar.DATE, -7);
            
            if(periodRange == 0)
                date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            else 
            {
                int nextDay = periodRange + 2;
                date.set(Calendar.DAY_OF_WEEK, nextDay);
            } 
        } else if (periodType == 1){
            date.set(Calendar.DATE, periodRange);
        } else if(periodType == 2){        
            date.set(Calendar.DATE, 1);      
            date.set(Calendar.MONTH, periodRange);  
        } else if (periodType == 3){
            date.add(Calendar.MONTH, -1);
            if(periodRange > 0)
                date.add(Calendar.DATE, periodRange);
        }
        startDateResult = date.getTime();           
        
        return startDateResult;
    }
    
    /*
      periodType : 0 --> Week to Date  : periodRange = 1 to 7 (Monday to Sunday)
                   1 --> Month To Date : periodRange = 1 to end date of the month
                   2 --> Year To Date  : periodRange = 0 to 11 (January to December)  
                   3 --> Monthly       : periodRange = one month from today
    */
    private Date getEndDate(int periodType, int periodRange)
    {
        Calendar date = new GregorianCalendar();
        Calendar now = Calendar.getInstance();  
        Date endDateResult;  
        int nextDay;
        
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        
        if(periodType == 0) {
            String dayNames[] = new DateFormatSymbols().getWeekdays();
            String today = dayNames[date.get(Calendar.DAY_OF_WEEK)];
            if(SUNDAY.equalsIgnoreCase(today) && periodRange < 6)
                date.add(Calendar.DATE, -7);
            
            if(periodRange == 0)
                date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            else 
            {
                nextDay = periodRange + 2;
                date.set(Calendar.DAY_OF_WEEK, nextDay);
            } 
        } else if( periodType == 1){        
            date.set(Calendar.DATE, periodRange);
        } else if(periodType == 2){
            int currentYear = (now.get(Calendar.YEAR));
            int modulusYear = currentYear % 4;

            if (periodRange == 1){
                if(modulusYear == 0)
                    date.set(Calendar.DATE, 29); 
                date.set(Calendar.DATE, 28);       
            }

            date.set(Calendar.MONTH, periodRange);  
            if (periodRange == 0 || periodRange == 2 || periodRange == 4 || periodRange == 6 || periodRange == 7 || periodRange == 9 || periodRange == 11 ) {
                 date.set(Calendar.DATE, 31); 
            } else if (periodRange == 3 || periodRange == 5 || periodRange == 8 || periodRange == 10){
                 date.set(Calendar.DATE, 30);
            }
        } else if( periodType == 3){        
            date.add(Calendar.MONTH, -1);
            if(periodRange > 0)
                date.add(Calendar.DATE, periodRange);
        }
        
        endDateResult = date.getTime();  
        return endDateResult;
    }
}
