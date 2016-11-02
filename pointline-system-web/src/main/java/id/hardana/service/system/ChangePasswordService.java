/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.changepassword.AdminChangePasswordBean;
import id.hardana.ejb.system.changepassword.OperatorMerchantChangePasswordBean;
import id.hardana.ejb.system.changepassword.PersonalChangePasswordBean;
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
@Path("changepassword")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class ChangePasswordService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private AdminValidationBean adminValidationBean;
    @EJB
    private OperatorMerchantChangePasswordBean operatorMerchantChangePasswordBean;
    @EJB
    private PersonalChangePasswordBean personalChangePasswordBean;
    @EJB
    private AdminChangePasswordBean adminChangePasswordBean;
    private final String STATUS_KEY = "status";

    public ChangePasswordService() {
    }

    @POST
    @Path("merchant/mpos")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String changePasswordOperatorMerchantMPOS(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashOldPassword") String hashOldPassword,
            @FormParam("hashNewPassword") String hashNewPassword) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return operatorMerchantChangePasswordBean.changePassword(merchantCode, userName,
                    hashOldPassword, hashNewPassword);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("merchant/webmerchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String changePasswordOperatorMerchantWeb(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashOldPassword") String hashOldPassword,
            @FormParam("hashNewPassword") String hashNewPassword) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMerchantWeb(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return operatorMerchantChangePasswordBean.changePassword(merchantCode, userName,
                    hashOldPassword, hashNewPassword);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String changePasswordPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashOldPassword") String hashOldPassword,
            @FormParam("hashNewPassword") String hashNewPassword) {
        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalChangePasswordBean.changePassword(account, hashOldPassword, hashNewPassword);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("admin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String changePasswordAdmin(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashOldPassword") String hashOldPassword,
            @FormParam("hashNewPassword") String hashNewPassword) {
        JSONObject responseValidate = adminValidationBean.validateAdmin(applicationKey,
                userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return adminChangePasswordBean.changePassword(userName, hashOldPassword, hashNewPassword);
        }
        return responseValidate.toString();
    }

}
