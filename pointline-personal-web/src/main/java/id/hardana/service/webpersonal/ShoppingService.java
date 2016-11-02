/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.ejb.webpersonal.holder.JSwitchTransactionInfoHolder;
import id.hardana.ejb.webpersonal.jswitch.JSwitchProductBean;
import id.hardana.ejb.webpersonal.jswitch.JSwitchTransactionBean;
import id.hardana.ejb.webpersonal.util.ChartTableItem;
import id.hardana.ejb.webpersonal.util.PaymentInfo;

import id.hardana.entity.jswitch.JSwitchInquiry;
import id.hardana.entity.jswitch.JSwitchInquiryStatusEnum;
import id.hardana.entity.jswitch.JSwitchPayment;
import id.hardana.entity.jswitch.JSwitchPaymentStatusEnum;
import id.hardana.entity.jswitch.JSwitchProduct;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.jswitch.exception.JSwitchEjbException;
import id.hardana.jswitch.exception.JSwitchErrorCode;
import id.hardana.jswitch.exception.JSwitchRequestException;
import id.hardana.service.webpersonal.log.LoggingInterceptor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Trisna
 */
@Stateless
@Path("shopping")
@Interceptors({LoggingInterceptor.class})
public class ShoppingService {

    @EJB
    private PersonalValidationBean personalValidationBean;

    @EJB
    private JSwitchProductBean jswitchProductBean;

    @EJB
    private JSwitchTransactionBean jswitchTransactionBean;

    private final String STATUS_KEY = "status";
    private final String ERROR_CODE_KEY = "errorCode";
    private final String ERROR_MESSAGE_KEY = "errorMessage";
    private final String INQUIRY_ID_KEY = "inquiryId";
    private final String INQUIRY_CHART_TABLE_KEY = "chartTable";
    private final String PAYMENT_ID_KEY = "paymentId";

    public ShoppingService() {
    }

    @POST
    @Path("list/products")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String listProducts(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);

        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return responseValidate.toString();
        }

        JSONObject json = new JSONObject();
        json.put(STATUS_KEY, "SUCCESS");

        List<JSwitchProduct> products = jswitchProductBean.getActiveProducts();
        JSONArray jsonProducts = new JSONArray();
        for (JSwitchProduct p : products) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("name", p.getName());
            obj.put("code", p.getCode());

            jsonProducts.put(obj);
        }
        json.put("products", jsonProducts);
        return json.toString();
    }

    @POST
    @Path("inquiry")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String inquiry(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String personalAccount,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("destinationNumber") String destinationNumber,
            @FormParam("productId") Long productId) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                personalAccount, sessionId, token);

        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return responseValidate.toString();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            JSwitchInquiry inquiry = jswitchTransactionBean.inquiry(personalAccount, destinationNumber, productId);
            if (JSwitchInquiryStatusEnum.INQUIRY_SUCCESS.getId() == inquiry.getStatus().getId()) {
                jsonObject.put(STATUS_KEY, "SUCCESS");
                jsonObject.put(INQUIRY_ID_KEY, inquiry.getId());

                List<ChartTableItem> items = jswitchTransactionBean.getMarkupItemPrice(inquiry.getId());
                JSONArray arrItem = new JSONArray();
                for (ChartTableItem item : items) {
                    JSONObject obj = new JSONObject();
                    obj.put("denomination", item.getDenomination());
                    obj.put("price", item.getPrice());
                    arrItem.put(obj);
                }
                jsonObject.put(INQUIRY_CHART_TABLE_KEY, items);

            } else {
                jsonObject.put(STATUS_KEY, "FAIL");
                jsonObject.put(ERROR_MESSAGE_KEY, "Inquiry fail.");
            }
        } catch (JSwitchEjbException ex) {
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_CODE_KEY, ex.getErrorCode().name());
            jsonObject.put(ERROR_MESSAGE_KEY, ex.getMessage());
        } catch (JSwitchRequestException ex) {
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_CODE_KEY, JSwitchErrorCode.REQUEST_EXT_SERVICE_FAIL.name());
        } catch (NamingException ex) {
            ex.printStackTrace();
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_MESSAGE_KEY, "System error.");
        }
        return jsonObject.toString();
    }

    @POST
    @Path("pay")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String pay(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String personalAccount,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("inquiryId") Long inquiryId,
            @FormParam("denomination") BigDecimal denomination) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                personalAccount, sessionId, token);

        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return responseValidate.toString();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            JSwitchPayment payment = jswitchTransactionBean.pay(personalAccount, inquiryId, denomination);
            if (JSwitchPaymentStatusEnum.SUCCESS.equals(payment.getStatus())) {
                jsonObject.put(STATUS_KEY, "SUCCESS");
                jsonObject.put(PAYMENT_ID_KEY, payment.getId());
                
                JSwitchTransactionInfoHolder holder  = jswitchTransactionBean.populateTransactionInfo(payment);
                
                jsonObject.put("referenceNumber", holder.getReferenfeNumber());
                jsonObject.put("dateTime", holder.getDateTime());
                jsonObject.put("itemName", holder.getItemName());
                jsonObject.put("totalAmount", holder.getTotalAmount());
                
                JSONArray jsonPaymentInfo = new JSONArray();
                for (PaymentInfo info : holder.getPaymentInfo()) {
                        JSONArray jsonItem = new JSONArray();
                        jsonItem.put(info.getField());
                        jsonItem.put(info.getValue());
                        
                        jsonPaymentInfo.put(jsonItem);
                }
                
                jsonObject.put("paymentInfo", jsonPaymentInfo);
                
            } else {
                jsonObject.put(STATUS_KEY, "FAIL");
                jsonObject.put(ERROR_MESSAGE_KEY, "Payment fail.");
            }

        } catch (JSwitchEjbException ex) {
            ex.printStackTrace();
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_CODE_KEY, ex.getErrorCode().name());
            jsonObject.put(ERROR_MESSAGE_KEY, ex.getMessage());
        } catch (JSwitchRequestException ex) {
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_CODE_KEY, JSwitchErrorCode.REQUEST_EXT_SERVICE_FAIL.name());

        } catch (NamingException ex) {
            ex.printStackTrace();
            jsonObject.put(STATUS_KEY, "FAIL");
            jsonObject.put(ERROR_MESSAGE_KEY, "System error.");
        }

        return jsonObject.toString();
    }

    
    private void populateTransactionInfo(JSwitchPayment payment) {
        payment.getTransactionId();
    }
}
