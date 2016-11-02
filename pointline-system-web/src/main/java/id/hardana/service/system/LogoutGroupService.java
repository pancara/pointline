/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.logout.GroupLogoutBean;
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
 * @author Arya
 */
@Stateless
@Path("logoutgroup")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class LogoutGroupService {

    @EJB
    private GroupLogoutBean groupLogoutBean;

    public LogoutGroupService() {
    }

    @POST
    @Path("groupmerchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String logoutGroupMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("groupCode") String groupCode,
            @FormParam("groupUserName") String groupUserName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {
        
        return groupLogoutBean.logout(applicationKey, groupCode, groupUserName, sessionId, token);
    }

}
