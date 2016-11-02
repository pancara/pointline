/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.payment.CardSinglePayInvoiceBean;
import id.hardana.ejb.mpos.payment.OtherSinglePayInvoiceBean;
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
@Path("singlepayinvoice")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class SinglePayInvoiceService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private CardSinglePayInvoiceBean cardSinglePayInvoiceBean;
    @EJB
    private OtherSinglePayInvoiceBean otherSinglePayInvoiceBean;

    private final String STATUS_KEY = "status";

    public SinglePayInvoiceService() {
    }

    @POST
    @Path("card")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cardPaymentInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("cardId") String cardId,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("debetAmount") String debetAmount,
            @FormParam("cardFinalBalance") String cardFinalBalance,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber,
            @FormParam("clientDateTime") String clientDateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return cardSinglePayInvoiceBean.payInvoice(merchantCode, userName, outletId,
                    amount, transactionFee, totalAmount, clientTransRefnum, cardId,
                    customerName, customerEmail, customerPhone, debetAmount,
                    cardFinalBalance, clientInvoiceId, invoiceNumber, clientDateTime).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("cash")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cashPaymentInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber,
            @FormParam("clientDateTime") String clientDateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return otherSinglePayInvoiceBean.cashPay(merchantCode, userName, outletId, amount,
                    transactionFee, totalAmount, clientTransRefnum, customerName, customerEmail,
                    customerPhone, clientInvoiceId, invoiceNumber, clientDateTime).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("othercard")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String otherCardPaymentInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("creditCardType") String creditCardType,
            @FormParam("creditCardHolderName") String creditCardHolderName,
            @FormParam("approvalCode") String approvalCode,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("debitCredit") String debitCredit) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return otherSinglePayInvoiceBean.otherCardPay(merchantCode, userName, outletId, amount,
                    transactionFee, totalAmount, clientTransRefnum, customerName, customerEmail,
                    customerPhone, creditCardType, creditCardHolderName, approvalCode,
                    clientInvoiceId, invoiceNumber, clientDateTime, debitCredit).toString();
        }
        return responseValidate.toString();
    }

}
