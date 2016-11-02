/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.login.GroupLoginBean;
import id.hardana.service.system.interceptor.NullCheckInterceptor;
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
 * @author Arya
 */
@Stateless
@Path("logingroup")
@Interceptors({LoggingInterceptor.class, NullCheckLoginInterceptor.class})

public class LoginGroupService {

    @EJB
    private GroupLoginBean groupLoginBean;

    public LoginGroupService() {
    }

    @POST
    @Path("groupmerchantweb/groupmerchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String loginGroup(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("groupCode") String groupCode,
            @FormParam("groupUserName") String groupUserName,
            @FormParam("hashPassword") String hashPassword,
            @FormParam("logTime") String logTime,
            @FormParam("apiVersion") String apiVersion) {
        
        return groupLoginBean.loginGroup(applicationKey, hashApplicationSecret, groupCode, groupUserName, hashPassword, logTime, apiVersion);
    }

}
