/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.history.PrintSaleBean;
import id.hardana.ejb.mpos.history.OperatorsBean;
import id.hardana.ejb.mpos.history.PrintItemsBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.mpos.interceptor.NullCheckInterceptor;
import id.hardana.service.mpos.log.LoggingInterceptor;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Arya
 */
@Stateless
@Path("printsale")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})
public class PrintSaleService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    private final String STATUS_KEY = "status";
    @EJB
    private PrintSaleBean printSaleBean;
    @EJB
    private PrintItemsBean printItemsBean;
    @EJB
    private OperatorsBean operatorsBean;
    public PrintSaleService() {
    }
    
    @POST
    @Path("getoperator")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(NullCheckInterceptor.class)
    public String getOperator(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("sessionId") String sessionId,
                              @FormParam("token") String token){
        
        JSONObject status = operatorMerchantValidationBean.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, sessionId, token);
        if(!("VALID".equals(status.getString(STATUS_KEY)))){
          return status.toString();
        }
        HashMap<String,Object> retVal = operatorsBean.getOperator(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("sale")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String saleHistory(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("operatorId") String operatorId,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate,
            @FormParam("startTime") String startTime, 
            @FormParam("endTime") String endTime) 
    {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return printSaleBean.printSale(merchantCode, outletId, operatorId, startDate, endDate, startTime, endTime).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("items")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String itemSaleHistory(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("operatorId") String operatorId,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate,
            @FormParam("startTime") String startTime, 
            @FormParam("endTime") String endTime) 
    {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return printItemsBean.printItems(merchantCode, outletId, operatorId, startDate, endDate, startTime, endTime).toString();
        }
        return responseValidate.toString();
    }

}
