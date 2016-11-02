/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.registration.MerchantRegistrationBean;
import id.hardana.ejb.system.registration.PersonalRegistrationBean;
import id.hardana.service.system.interceptor.LoggingInterceptor;
import id.hardana.service.system.interceptor.NullCheckLoginInterceptor;
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
@Path("register")
@Interceptors({LoggingInterceptor.class, NullCheckLoginInterceptor.class})

public class RegisterService {

    @EJB
    private MerchantRegistrationBean merchantRegistrationBean;
    @EJB
    private PersonalRegistrationBean personalRegistrationBean;

    public RegisterService() {
    }

    @POST
    @Path("mpos/merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String registerMerchantFromMPOS(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("merchantName") String merchantName,
            @FormParam("userName") String userName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("regTime") String regTime,
            @FormParam("email") String email,
            @FormParam("apiVersion") String apiVersion) {
        return merchantRegistrationBean.registerMerchantFromMPOS(applicationKey, hashApplicationSecret,
                merchantName, userName, hashPassword, regTime, email, apiVersion).toString();
    }

    @POST
    @Path("merchantweb/merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String registerMerchantFromWebMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("merchantName") String merchantName,
            @FormParam("userName") String userName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("regTime") String regTime,
            @FormParam("email") String email,
            @FormParam("apiVersion") String apiVersion) {
        return merchantRegistrationBean.registerMerchantFromWebMerchant(applicationKey, hashApplicationSecret,
                merchantName, userName, hashPassword, regTime, email, apiVersion).toString();
    }

    @POST
    @Path("personalweb/personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String registerPersonalFromWebPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("account") String account,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("regTime") String regTime,
            @FormParam("apiVersion") String apiVersion) {
        return personalRegistrationBean.registerPersonalFromWebPersonal(applicationKey,
                hashApplicationSecret, account, hashPassword, regTime, apiVersion);
    }

    @POST
    @Path("personalapp/personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String registerPersonalFromPersonalApplication(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("account") String account,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("regTime") String regTime,
            @FormParam("apiVersion") String apiVersion) {
        return personalRegistrationBean.registerPersonalFromPersonalApplication(applicationKey,
                hashApplicationSecret, account, hashPassword, regTime, apiVersion);
    }
}
