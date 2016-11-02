/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.payment.AdminAdjustmentCardPaymentBean;
import id.hardana.ejb.mpos.topupcard.AdminReversalTopupBean;
import id.hardana.ejb.system.validation.AdminValidationBean;
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
@Path("admintransaction")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class AdminTransactionService {

    @EJB
    private AdminValidationBean adminValidationBean;
    @EJB
    private AdminAdjustmentCardPaymentBean adminAdjustmentCardPaymentBean;
    @EJB
    private AdminReversalTopupBean adminReversalTopupBean;

    private final String STATUS_KEY = "status";

    public AdminTransactionService() {
    }

    @POST
    @Path("paymentadjustment")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String paymentAdjustment(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("pan") String pan,
            @FormParam("cardFinalBalance") String cardFinalBalance) {
        JSONObject responseValidate = adminValidationBean.validateAdmin(applicationKey, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return adminAdjustmentCardPaymentBean.pay(merchantCode, outletId, amount, pan,
                    cardFinalBalance).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("reversaltopup")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String reversalTopup(@FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("cardFinalBalance") String cardFinalBalance) {
        JSONObject responseValidate = adminValidationBean.validateAdmin(applicationKey, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return adminReversalTopupBean.reversalTopup(referenceNumber, cardFinalBalance).toString();
        }
        return responseValidate.toString();
    }

}
