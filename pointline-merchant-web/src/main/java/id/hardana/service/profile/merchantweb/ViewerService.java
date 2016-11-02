/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.viewer.ViewerBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.ejb.merchantweb.interceptor.InterceptNull;
import id.hardana.ejb.merchantweb.viewer.ViewerItemsBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.service.profile.log.LoggingInterceptor;
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
@Interceptors({LoggingInterceptor.class, InterceptNull.class})

public class ViewerService {

    @EJB
    private OperatorMerchantValidationBean omvb;

    @EJB
    private ViewerBean viewerBean;
    
    @EJB
    private ViewerItemsBean viewerItemsBean;

    private final String STATUS_KEY = "status";

    public ViewerService() {
    }

    @POST
    @Path("province")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewProvince(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewProvince();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("city")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewCity(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("provinceId") String provinceId) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewCity(provinceId);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("secretquestion")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewSecretQuestion(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewSecretQuestion();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("bank")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewBank(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewBank();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("idtype")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewIdType(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewIdentityType();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("lineofbusiness")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewLoB(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewLoB();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("allitems")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewAllItems(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerItemsBean.viewAllItems(merchantCode).toString();
        }
        return responseValidate.toString();
    }

}
