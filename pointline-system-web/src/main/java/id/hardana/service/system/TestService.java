/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.system.interceptor.NullCheckInterceptor;
import id.hardana.ejb.system.validation.AdminValidationBean;
import id.hardana.service.system.interceptor.LoggingInterceptor;
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
@Path("test")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class TestService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private AdminValidationBean adminValidationBean;
    private final String STATUS_KEY = "status";

    public TestService() {
    }

    @POST
    @Path("mpos")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String testFromMPOS(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("data") String data) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            responseValidate.put("data", data);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("merchantweb")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String testFromWebMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("data") String data) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMerchantWeb(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            responseValidate.put("data", data);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("personalweb")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String testFromWebPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("data") String data) {
        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            responseValidate.put("data", data);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("personalapp")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String testFromPersonalApp(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("data") String data) {
        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            responseValidate.put("data", data);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("admin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String testFromAdmin(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("data") String data) {
        JSONObject responseValidate = adminValidationBean.validateAdmin(applicationKey,
                userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            responseValidate.put("data", data);
        }
        return responseValidate.toString();
    }

}
