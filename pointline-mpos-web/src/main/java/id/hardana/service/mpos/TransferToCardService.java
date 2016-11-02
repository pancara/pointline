/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.transfercard.TransferCardBean;
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
@Path("transfertocard")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class TransferToCardService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private TransferCardBean transferCardBean;

    private final String STATUS_KEY = "status";

    public TransferToCardService() {
    }

    @POST
    @Path("inquiry")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String inquiry(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("cardId") String cardId) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transferCardBean.inquiryTransferCard(merchantCode, userName, outletId, cardId).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("transfer")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transfer(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("cardId") String cardId,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("cardFinalBalance") String cardFinalBalance,
            @FormParam("amount") String amount) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transferCardBean.transferCard(merchantCode, userName, outletId, cardId, 
                    referenceNumber, cardFinalBalance, amount).toString();
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
            @FormParam("cardId") String cardId,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("cardFinalBalance") String cardFinalBalance,
            @FormParam("amount") String amount) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transferCardBean.reversalTransferCard(merchantCode, userName, outletId, cardId, 
                    referenceNumber, cardFinalBalance, amount).toString();
        }
        return responseValidate.toString();
    }

}
