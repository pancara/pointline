/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.validation.TokenGeneratorMerchantBean;
import id.hardana.ejb.system.validation.TokenGeneratorPersonalBean;
import id.hardana.service.system.interceptor.NullCheckInterceptor;
import id.hardana.ejb.system.validation.TokenGeneratorAdminBean;
import id.hardana.ejb.system.validation.TokenGeneratorGroupMerchantBean;
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
@Path("newtoken")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class TokenService {

    @EJB
    private TokenGeneratorMerchantBean tokenGeneratorMerchantBean;
    @EJB
    private TokenGeneratorPersonalBean tokenGeneratorPersonalBean;
    @EJB
    private TokenGeneratorAdminBean tokenGeneratorAdminBean;
    @EJB
    private TokenGeneratorGroupMerchantBean tokenGeneratorGroupMerchantBean;

    public TokenService() {
    }

    @POST
    @Path("mpos")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String requestNewTokenFromMPOS(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("oldToken") String oldToken) {
        return tokenGeneratorMerchantBean.requestNewTokenMPOS(applicationKey,
                merchantCode, userName, sessionId, oldToken);
    }

    @POST
    @Path("merchantweb")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String requestNewTokenFromWebMerchant(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("oldToken") String oldToken) {
        return tokenGeneratorMerchantBean.requestNewTokenWebMerchant(applicationKey,
                merchantCode, userName, sessionId, oldToken);
    }

    @POST
    @Path("personal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String requestNewTokenPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("oldToken") String oldToken) {
        return tokenGeneratorPersonalBean.requestNewToken(applicationKey,
                account, sessionId, oldToken);
    }

    @POST
    @Path("admin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String requestNewTokenAdmin(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("oldToken") String oldToken) {
        return tokenGeneratorAdminBean.requestNewToken(applicationKey, userName, sessionId, oldToken);
    }
    
    @POST
    @Path("groupmerchantweb")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String requestNewTokenFromGroupWeb(@FormParam("applicationKey") String applicationKey,
            @FormParam("groupCode") String groupCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("oldToken") String oldToken) {
        return tokenGeneratorGroupMerchantBean.requestNewTokenWebGroupMerchant(applicationKey,
                groupCode, userName, sessionId, oldToken);
    }

}
