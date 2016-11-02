/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.forgetpassword.OperatorMerchantForgetPasswordBean;
import id.hardana.ejb.system.forgetpassword.PersonalForgetPasswordBean;
import id.hardana.service.system.interceptor.NullCheckLoginInterceptor;
import id.hardana.service.system.interceptor.LoggingInterceptor;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * REST Web Service
 *
 * @author Trisna
 */
@Stateless
@Path("forgetpassword")
@Interceptors({LoggingInterceptor.class, NullCheckLoginInterceptor.class})

public class ForgetPasswordService {

    @EJB
    private PersonalForgetPasswordBean personalForgetPasswordBean;
    @EJB
    private OperatorMerchantForgetPasswordBean operatorMerchantForgetPasswordBean;

    public ForgetPasswordService() {
    }

    @POST
    @Path("merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String merchantForgetPassword(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        return operatorMerchantForgetPasswordBean.forgetPassword(applicationKey, hashApplicationSecret, 
                merchantCode, userName, logTime, apiVersion).toString();
    }

    @POST
    @Path("personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String personalForgetpassword(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("account") String account,
            @FormParam("email") String email,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        return personalForgetPasswordBean.forgetPassword(applicationKey, hashApplicationSecret, 
                account, email, logTime, apiVersion).toString();
    }

}
