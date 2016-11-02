/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.invoice.SendInvoiceBean;
import id.hardana.ejb.mpos.invoice.UpdateInvoiceBean;
import id.hardana.ejb.mpos.invoice.VoidInvoiceBean;
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
@Path("invoice")
@Interceptors({LoggingInterceptor.class})
public class InvoiceService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private SendInvoiceBean sendInvoiceBean;
    @EJB
    private UpdateInvoiceBean updateInvoiceBean;
    @EJB
    private VoidInvoiceBean voidInvoiceBean;

    private final String STATUS_KEY = "status";

    public InvoiceService() {
    }

    @POST
    @Path("send")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String sendInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount")String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        
        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return responseValidate.toString();
        }
        
        JSONObject jsonObject = sendInvoiceBean.sendInvoice(merchantCode, userName, outletId, amount,
                tableNumber, items, transactionDiscount, pricings, 
                clientInvoiceId, clientDateTime, uid);
        return jsonObject.toString();
    }

    @POST
    @Path("update")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String updateInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount") String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return updateInvoiceBean.updateInvoice(merchantCode, userName, outletId, amount, tableNumber,
                    items, transactionDiscount, pricings, clientInvoiceId, invoiceNumber).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("void")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String voidInvoice(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("invoiceNumber") String invoiceNumber) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return voidInvoiceBean.voidInvoice(merchantCode, userName, outletId,
                    invoiceNumber, clientInvoiceId).toString();
        }
        return responseValidate.toString();
    }

}
