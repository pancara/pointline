/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.webpersonal.viewer.ViewerBean;
import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.webpersonal.interceptor.NullCheckInterceptor;
import id.hardana.service.webpersonal.log.LoggingInterceptor;
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
@Path("view")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class ViewerService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private ViewerBean viewerBean;
    private final String STATUS_KEY = "status";

    public ViewerService() {
    }

    @POST
    @Path("province")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewProvince(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewProvince();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("city")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewCity(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("provinceId") String provinceId) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewCity(provinceId);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("secretquestion")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewSecretQuestion(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewSecretQuestion();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("bank")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewBank(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewBank();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("idtype")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewIdType(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewerBean.viewIdentityType();
        }
        return responseValidate.toString();
    }
}
