/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.login.AdminLoginBean;
import id.hardana.ejb.system.login.MerchantLoginBean;
import id.hardana.ejb.system.login.PersonalLoginBean;
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
@Path("login")
@Interceptors({LoggingInterceptor.class, NullCheckLoginInterceptor.class})

public class LoginService {

    @EJB
    private MerchantLoginBean merchantLoginBean;
    @EJB
    private PersonalLoginBean personalLoginBean;
    @EJB
    private AdminLoginBean adminLoginBean;

    public LoginService() {
    }

    @POST
    @Path("mpos/merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginMerchantFromMPOS(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        return merchantLoginBean.loginFromMPOS(applicationKey, hashApplicationSecret,
                merchantCode, userName, hashPassword, logTime, apiVersion);
    }

    @POST
    @Path("merchantweb/merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginMerchantFromWebMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        return merchantLoginBean.loginFromWebMerchant(applicationKey, hashApplicationSecret,
                merchantCode, userName, hashPassword, logTime, apiVersion);
    }

    @POST
    @Path("personalweb/personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginPersonalFromWebPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("account") String account,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        return personalLoginBean.loginFromWebPersonal(applicationKey,
                hashApplicationSecret, account, hashPassword, logTime, apiVersion);
    }

    @POST
    @Path("personalapp/personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginPersonalFromPersonalApplication(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("account") String account,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime, 
            @FormParam("apiVersion") String apiVersion) {
        return personalLoginBean.loginFromPersonalApplication(applicationKey,
                hashApplicationSecret, account, hashPassword, logTime, apiVersion);
    }

    @POST
    @Path("admin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginAdmin(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("userName") String userName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime, 
            @FormParam("apiVersion") String apiVersion) {
        return adminLoginBean.login(applicationKey, hashApplicationSecret,
                userName, hashPassword, logTime, apiVersion);
    }

}
