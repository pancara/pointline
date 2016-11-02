/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.MerchantDashboardBean;
import id.hardana.ejb.merchantweb.interceptor.InterceptNull;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
 * @author Trisna
 */
@Stateless
@Path("merchantdashboard")
@Interceptors(LoggingInterceptor.class)

public class MerchantDashboard {

    @EJB
    OperatorMerchantValidationBean omvb;
    @EJB
    private MerchantDashboardBean merchantDashboardBean;

    private final String STATUS_KEY = "status";

    public MerchantDashboard() {
    }

    @POST
    @Path("daily")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String dailyDashboard(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDashboardBean.dailyMerchantDashboard(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("monthly")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthlyDashboard(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDashboardBean.monthlyMerchantDashboard(merchantCode).toString();
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("weektodate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String weekToDateDashboard(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDashboardBean.weekToDateMerchantDashboard(merchantCode).toString();
        }
        return responseValidate.toString();
    }   
    
    @POST
    @Path("monthtodate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthToDateDashboard(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDashboardBean.monthToDateMerchantDashboard(merchantCode).toString();
        }
        return responseValidate.toString();
    }   
    
    @POST
    @Path("yeartodate") 
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String yearToDateDashboard(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDashboardBean.yearToDateMerchantDashboard(merchantCode).toString();
        }
        return responseValidate.toString();
    }  
 
}
