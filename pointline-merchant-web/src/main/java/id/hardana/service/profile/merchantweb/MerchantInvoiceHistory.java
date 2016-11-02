/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.InvoiceHistoryBean;
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
 * @author Trisna
 */
@Stateless
@Path("invoicehistory")
@Interceptors(LoggingInterceptor.class)

public class MerchantInvoiceHistory {

    @EJB
    OperatorMerchantValidationBean omvb;
    @EJB
    private InvoiceHistoryBean invoiceHistoryBean;

    private final String STATUS_KEY = "status";

    public MerchantInvoiceHistory() {
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String invoiceHistory(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startDate") String startDateString,
            @FormParam("endDate") String endDateString) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return invoiceHistoryBean.invoiceHistory(merchantCode, limit, page, startDateString, endDateString).toString();
        }
        return responseValidate.toString();
    }

}
