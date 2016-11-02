/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.payment.MultiPayInvoiceBean;
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
@Path("multipayinvoice")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class MultiPayInvoiceService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private MultiPayInvoiceBean multiPayInvoiceBean;

    private final String STATUS_KEY = "status";

    public MultiPayInvoiceService() {
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String multiPaymentInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("invoiceAmount") String invoiceAmount,
            @FormParam("payments") String payments,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber,
            @FormParam("clientDateTime") String clientDateTime) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return multiPayInvoiceBean.multiPayInvoice(merchantCode, userName, outletId, 
                    invoiceAmount, payments, customerName, customerEmail, customerPhone, 
                    clientInvoiceId, invoiceNumber, clientDateTime).toString();
        }
        return responseValidate.toString();
    }

}
