/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import id.hardana.ejb.system.changepassword.GroupOperatorChangePasswordBean;
import id.hardana.ejb.system.validation.GroupOperatorValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Arya
 */
@Stateless
@Path("changepasswordgroup")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class ChangePasswordGroupService {

    @EJB
    private GroupOperatorValidationBean groupOperatorValidationBean;
    @EJB
    private GroupOperatorChangePasswordBean groupOperatorChangePasswordBean;
    private final String STATUS_KEY = "status";

    public ChangePasswordGroupService() {
    }

    @POST
    @Path("groupmerchantweb/groupmerchant")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String changePasswordGroupOperator(@FormParam("applicationKey") String applicationKey,
            @FormParam("groupCode") String groupCode,
            @FormParam("groupUserName") String groupUserName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashOldPassword") String hashOldPassword,
            @FormParam("hashNewPassword") String hashNewPassword) {
        
        JSONObject responseValidate = groupOperatorValidationBean.validateGroupOperator(applicationKey, groupCode, groupUserName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return groupOperatorChangePasswordBean.changePassword(groupCode, groupUserName, hashOldPassword, hashNewPassword);
        }
        return responseValidate.toString();
    }
}
