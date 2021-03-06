/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.MerchantReportDashboardBean;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
@Path("merchantreportdashboard")
@Interceptors(LoggingInterceptor.class)

public class MerchantReportDashboard {

    @EJB
    OperatorMerchantValidationBean omvb;
    @EJB
    private MerchantReportDashboardBean merchantReportDashboardBean;

    private final String STATUS_KEY = "status";

    public MerchantReportDashboard() {
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String merchantReportDashboard(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startDate") String startDateString,
            @FormParam("endDate") String endDateString,
            @FormParam("outletId") String outletId,
            @FormParam("operatorId") String operatorId) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantReportDashboardBean.merchantReportDashboard(merchantCode, limit, page, startDateString,
                    endDateString, outletId, operatorId).toString();
        }
        return responseValidate.toString();
    }

}
