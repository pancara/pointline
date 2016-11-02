/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.history;

import id.hardana.ejb.history.autodebet.AutoDebetReportBean;
import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.history.interceptor.LoggingInterceptor;
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
@Path("autodebet")
@Interceptors(LoggingInterceptor.class)

public class AutoDebetReportService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private AutoDebetReportBean autoDebetReportBean;
    private final String STATUS_KEY = "status";

    public AutoDebetReportService() {
    }

    @POST
    @Path("report")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getReport(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startCreatedDate") String startCreatedDate,
            @FormParam("endCreatedDate") String endCreatedDate,
            @FormParam("number") String number,
            @FormParam("status") String status,
            @FormParam("type") String type,
            @FormParam("destinationPan") String destinationPan,
            @FormParam("destinationAccount") String destinationAccount) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return autoDebetReportBean.reportAutoDebet(account, limit, page, startCreatedDate, 
                    endCreatedDate, number, status, type, destinationPan, destinationAccount).toString();                   
        }
        return responseValidate.toString();
    }

}
