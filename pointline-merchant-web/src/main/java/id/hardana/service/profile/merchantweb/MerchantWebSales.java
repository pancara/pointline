/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.MerchantSalesFacade;
import id.hardana.ejb.merchantweb.interceptor.InterceptNull;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.profile.log.LoggingInterceptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author hanafi
 */
@Stateless
@Path("/merchantwebsales")
@Interceptors(LoggingInterceptor.class)
public class MerchantWebSales {
    
    private String STATUSSUCCESS = "SUCCESS";
    private final String STATUS_KEY = "status";

    @Context
    private UriInfo context;
    

    /**
     * Creates a new instance of MerchantWebSales
     */
    public MerchantWebSales() {
    }

    @EJB
    MerchantSalesFacade merchantSalesFacade;
    
    @EJB
    OperatorMerchantValidationBean omvb;

    // class of what-the-fuck
    
    @POST
    @Path("/totalSalesDaily")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalSalesDaily(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        
        HashMap<String,String> retVal = null; // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/totalWeekToDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalWeekToDate(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // payload
        //staus: success/other status, count: numOfData, totalTrx: totalTrxValue, transactions: arrayOfTransaction [{date:'2015-01-01', value:'25000'},{date:'2015-01-02', value:'35000'},{date:'2015-01-03', value:'20000'}]
        // call ejb
        String period = "WEEKLY";
        HashMap<String,Object> retVal = merchantSalesFacade.periodToDate(merchantCode, period);

        return new JSONObject(retVal).toString();
    }    
    
    @POST
    @Path("/totalMonthToDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalMonthToDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        String period = "MONTHLY";
        HashMap<String,Object> retVal = merchantSalesFacade.periodToDate(merchantCode, period); 
        // retVal should be either <String,String> or <String,Object>
        retVal.put("status", STATUSSUCCESS);
        return new JSONObject(retVal).toString();
    }
    
    
    @POST
    @Path("/totalYearToDate")
    //@Path("/yeartodate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalYearToDate(@FormParam("merchantCode") String merchantCode,
    //public String YearToDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        
        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        String period = "YEARTODATE";
        HashMap<String,Object> retVal = merchantSalesFacade.periodYearToDate(merchantCode, period);
        retVal.put("status", STATUSSUCCESS);
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/TotalHourlyByDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalHourlyByDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,String> retVal = null; // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    @POST
    @Path("/totalDailyByWeekToDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalDailyByWeekToDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,String> retVal = null; // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    @POST
    @Path("/totalDailyByMonthToDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalDailyByMonthToDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,String> retVal = null; // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    @POST
    @Path("/totalMonthlyByYearToDate")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalMonthlyByYearToDate(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,String> retVal = null; // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    @POST
    @Path("/totalSales") // by outlet? of items?
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String totalSales(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("startDate") String startDate,
                             @FormParam("endDate") String endDate){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String,Object> retVal = new HashMap<>();
        
        Date startDateDate;
        Date endDateDat;
        try {
            startDateDate = sdf.parse(startDate);
            endDateDat = sdf.parse(endDate);
        } catch (ParseException ex) {
            Logger.getLogger(MerchantWeb.class.getName()).log(Level.SEVERE, null, ex);
            retVal.put("status", "WRONG DATE SENT");
            return new JSONObject(retVal).toString();
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(endDateDat);
        c.add(Calendar.DATE, 1);
        Date endDateDate = c.getTime();
        
        retVal = merchantSalesFacade.totalSales(merchantCode, startDateDate, endDateDate); // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    @POST
    @Path("/itemQuery") // itemSales?
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String itemQuery(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("startDate") String startDate,
                             @FormParam("endDate") String endDate){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String,Object> retVal = new HashMap<>();
        
        Date startDateDate;
        Date endDateDat;
        try {
            startDateDate = sdf.parse(startDate);
            endDateDat = sdf.parse(endDate);
        } catch (ParseException ex) {
            Logger.getLogger(MerchantWeb.class.getName()).log(Level.SEVERE, null, ex);
            retVal.put("status", "WRONG DATE SENT");
            return new JSONObject(retVal).toString();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(endDateDat);
        c.add(Calendar.DATE, 1);
        Date endDateDate = c.getTime();
        
        
        retVal = merchantSalesFacade.itemQuery(merchantCode, startDateDate, endDateDate); // call ejb here
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/topupHistory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String topupHistory(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantSalesFacade.topupHistory(merchantCode);
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    
    @POST
    @Path("/transactionHistory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String transactionHistory(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantSalesFacade.transactionHistory(merchantCode);
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/transactionByPaymentMethod")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String transactionByPaymentMethod(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantSalesFacade.transactionByPaymentMethod(merchantCode);
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/revenueByCategory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String revenueByCategory(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantSalesFacade.revenueByCategory(merchantCode);
        // retVal should be either <String,String> or <String,Object>
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("todaysales")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String toDayMerchantWebSales(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantSalesFacade.todayMerchantWebSales(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("monthlysales")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthlyMerchantWebSales(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantSalesFacade.monthlyMerchantWebSales(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("weektodatesales")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String weekToDateMerchantWebSales(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantSalesFacade.weekToDateMerchantWebSales(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("monthtodatesales")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthToDateWebMerchantSales(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantSalesFacade.monthToDateMerchantWebSales(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("yeartodatesales")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String yearToDateMerchantWebSales(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantSalesFacade.yearToDateMerchantWebSales(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
}
