/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.payment;

import id.hardana.ejb.system.validation.AdminValidationBean;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.merchantweb.payment.PaymentReversalBean;
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
@Path("paymentreversal")
@Interceptors(LoggingInterceptor.class)

public class PaymentReversal {

    @EJB
    OperatorMerchantValidationBean omvb;
    @EJB
    private AdminValidationBean adminValidationBean;
    @EJB
    private PaymentReversalBean paymentReversalBean;
    private final String STATUS_KEY = "status";

    public PaymentReversal() {
    }
    
    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String paymentReversal(@FormParam("applicationKey") String applicationKey,
            @FormParam("hashApplicationSecret") String hashApplicationSecret,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("invoiceNumber") String invoiceNumber
            ) {
        
        JSONObject responseValidate = adminValidationBean.validateAdmin(applicationKey, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            
            return paymentReversalBean.paymentReversal(invoiceNumber).toString();
        }
        return responseValidate.toString();
    }

}
