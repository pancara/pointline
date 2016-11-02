/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.topupcard.InquiryTopupBean;
import id.hardana.ejb.mpos.topupcard.ReversalTopupBean;
import id.hardana.ejb.mpos.topupcard.TopupBean;
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
@Path("topupcard")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class TopupCardService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private InquiryTopupBean inquiryTopupBean;
    @EJB
    private TopupBean topupBean;
    @EJB
    private ReversalTopupBean reversalTopupBean;

    private final String STATUS_KEY = "status";

    public TopupCardService() {
    }

    @POST
    @Path("inquiry")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String inquiryTopup(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("cardId") String cardId,
            @FormParam("clientTransRefnum") String clientTransRefnum) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inquiryTopupBean.inquiryTopup(merchantCode, userName, outletId,
                    amount, cardId, clientTransRefnum).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("topup")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String topup(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("creditAmount") String creditAmount,
            @FormParam("cardFinalBalance") String cardFinalBalance) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return topupBean.topup(merchantCode, userName, outletId, referenceNumber,
                    creditAmount, cardFinalBalance).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("reversal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String reversal(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("reversalCreditAmount") String reversalCreditAmount,
            @FormParam("cardFinalBalance") String cardFinalBalance,
            @FormParam("clientDateTime") String clientDateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return reversalTopupBean.reversalTopup(merchantCode, userName, outletId, referenceNumber,
                    reversalCreditAmount, cardFinalBalance, clientDateTime).toString();
        }
        return responseValidate.toString();
    }

}
