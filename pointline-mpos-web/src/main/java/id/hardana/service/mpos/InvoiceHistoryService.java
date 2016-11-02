/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.history.InvoiceHistoryBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.mpos.interceptor.NullCheckInterceptor;
import id.hardana.service.mpos.log.LoggingInterceptor;
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
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class InvoiceHistoryService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private InvoiceHistoryBean invoiceHistoryBean;

    private final String STATUS_KEY = "status";

    public InvoiceHistoryService() {
    }

    @POST
    @Path("new")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String newInvoiceHistory(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("limit") String limit) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return invoiceHistoryBean.invoiceHistoryNow(merchantCode, outletId, limit).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("before")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String beforeInvoiceHistory(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("limit") String limit,
            @FormParam("dateTime") String dateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return invoiceHistoryBean.invoiceHistoryBefore(merchantCode, outletId, limit, dateTime).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("after")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String afterInvoiceHistory(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("limit") String limit,
            @FormParam("dateTime") String dateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return invoiceHistoryBean.invoiceHistoryAfter(merchantCode, outletId, limit, dateTime).toString();
        }
        return responseValidate.toString();
    }

}
