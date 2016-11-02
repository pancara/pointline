/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.view.ItemsBean;
import id.hardana.ejb.mpos.view.MerchantDataBean;
import id.hardana.ejb.mpos.view.OutletBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.mpos.interceptor.NullCheckInterceptor;
import id.hardana.service.mpos.log.LoggingInterceptor;
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
@Path("view")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class ViewService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private ItemsBean itemsBean;
    @EJB
    private MerchantDataBean merchantDataBean;
    @EJB
    private OutletBean outletBean;

    private final String STATUS_KEY = "status";

    public ViewService() {
    }

    @POST
    @Path("items")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getItems(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("lastUpdated") String lastUpdated) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return itemsBean.getAllItems(merchantCode, lastUpdated).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("merchantdata")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getMerchantData(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("latitude") String latitude,
            @FormParam("longitude") String longitude) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantDataBean.getMerchantData(merchantCode, latitude, longitude).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("outlet")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getOutlet(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return outletBean.getAllOutlet(merchantCode).toString();
        }
        return responseValidate.toString();
    }

}
