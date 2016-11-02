/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.registration.GroupRegistrationBean;
import id.hardana.service.system.interceptor.LoggingInterceptor;
import id.hardana.service.system.interceptor.NullCheckLoginInterceptor;
import javax.interceptor.Interceptors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
@Path("registergroup")
@Interceptors({LoggingInterceptor.class, NullCheckLoginInterceptor.class})

public class RegisterGroupService {

    @EJB
    private GroupRegistrationBean groupRegistrationBean;
    public RegisterGroupService() {
    }

    
    
    @POST  
    @Path("groupmerchantweb/groupmerchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String registerGroup(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret, 
            @FormParam("groupName") String groupName,
            @FormParam("groupUserName") String groupUserName,
            @FormParam("hashPassword") String hashPassword,   
            @FormParam("regTime") String regTime,
            @FormParam("email") String email,
            @FormParam("apiVersion") String apiVersion){
        
        return groupRegistrationBean.registerGroup(applicationKey, hashApplicationSecret, groupName, groupUserName, hashPassword, regTime, email, apiVersion).toString();
    }

}
