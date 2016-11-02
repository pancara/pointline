/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.payment.MultiPaymentBean;
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
@Path("multipay")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class MultiPayService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private MultiPaymentBean multiPaymentBean;

    private final String STATUS_KEY = "status";

    public MultiPayService() {
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String multiPayment(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("invoiceAmount") String invoiceAmount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount")String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("payments") String payments,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return multiPaymentBean.multiPay(merchantCode, userName, outletId, invoiceAmount,
                    tableNumber, items, transactionDiscount, pricings, payments, customerName, customerEmail,
                    customerPhone, clientInvoiceId, clientDateTime, uid).toString();
        }
        return responseValidate.toString();
    }

}
