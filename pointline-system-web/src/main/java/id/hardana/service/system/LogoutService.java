/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.logout.AdminLogoutBean;
import id.hardana.ejb.system.logout.MerchantLogoutBean;
import id.hardana.ejb.system.logout.PersonalLogoutBean;
import id.hardana.service.system.interceptor.NullCheckInterceptor;
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
@Path("logout")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class LogoutService {

    @EJB
    private MerchantLogoutBean merchantLogoutBean;
    @EJB
    private PersonalLogoutBean personalLogoutBean;
    @EJB
    private AdminLogoutBean adminLogoutBean;

    public LogoutService() {
    }

    @POST
    @Path("merchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String logoutMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {
        return merchantLogoutBean.logout(applicationKey, merchantCode, userName, sessionId, token);
    }

    @POST
    @Path("personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String logoutPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {
        return personalLogoutBean.logout(applicationKey, account, sessionId, token);
    }

    @POST
    @Path("admin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String logoutAdmin(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {
        return adminLogoutBean.logout(applicationKey, userName, sessionId, token);
    }

}
