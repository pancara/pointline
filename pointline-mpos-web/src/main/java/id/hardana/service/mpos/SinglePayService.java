/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.payment.CardSinglePaymentBean;
import id.hardana.ejb.mpos.payment.OtherSinglePaymentBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.ejb.mpos.reportdoc.AddEmailBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.mpos.interceptor.NullCheckInterceptor;
import id.hardana.service.mpos.log.LoggingInterceptor;
import id.hardana.service.mpos.messaging.Mail;
import id.hardana.service.mpos.messaging.CashPaymentEmailContent;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONArray;
import org.json.JSONObject;
import id.hardana.ejb.mpos.view.OutletBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

/**
 * REST Web Service
 *
 * @author Trisna
 */
@Stateless
@Path("singlepay")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})
public class SinglePayService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private CardSinglePaymentBean cardSinglePaymentBean;
    @EJB
    private OtherSinglePaymentBean otherSinglePaymentBean;
    @EJB
    private OutletBean outletBean;    
    @EJB 
    private AddEmailBean addEmailBean;
    
    private final String STATUS_KEY             = "status";
    private final String EMAIL_SUBJECT          = "Digital Receipt"; 
    private final String EMAIL_SUCCESS          = "Email Success";
    private final String EMAIL_FAILED           = "Email Failed";   
    private final String CASH_PAY_SEND_EMAIL    ="cashPaySendEmail";
    private final String CASH_PAY               ="cashPay";
    static boolean sendEmailReturn = false;
    static String timeStamp="";
    String emailStatus="";
    
    public SinglePayService() {
    }

    @POST
    @Path("card")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cardPayment(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount")String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("cardId") String cardId,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("debetAmount") String debetAmount,
            @FormParam("cardFinalBalance") String cardFinalBalance,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid) {
        
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        
        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return responseValidate.toString();
        }
            
        JSONObject jsonObject = cardSinglePaymentBean.pay(merchantCode, userName, outletId, amount,
            transactionFee, totalAmount, tableNumber, items, transactionDiscount, pricings, 
            clientTransRefnum, cardId, customerName, customerEmail, customerPhone, debetAmount,
            cardFinalBalance, clientInvoiceId, clientDateTime, uid);

        return jsonObject.toString();
        
    }
      
    @POST
    @Path("cashpayemail")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cashPaySendEmail(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount")String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid) {
       
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
              
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            if (!sendEmailReturn)
            {
                return sendEmailProcess(merchantCode, outletId, items, transactionDiscount, 
                        customerEmail, customerName, userName,  tableNumber, clientDateTime, uid, clientInvoiceId, pricings, totalAmount, CASH_PAY_SEND_EMAIL);
            }
        }
        return responseValidate.toString();
    }
    
    @POST
    @Path("cash")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cashPayment(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount")String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid)
            {
       
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
              
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {             
            if (!sendEmailReturn && !emailStatus.equals(EMAIL_SUCCESS))
            {        
                System.out.println("cashPayment -timeStamp :"+timeStamp);
                if(!timeStamp.equalsIgnoreCase(clientDateTime))
                {
                    sendEmailProcess(merchantCode, outletId, items, transactionDiscount, customerEmail, customerName, userName, 
                        tableNumber, clientDateTime, uid, clientInvoiceId, pricings, totalAmount, CASH_PAY);
                }
                if(sendEmailReturn)
                    timeStamp = clientDateTime;
                sendEmailReturn = false;              
            }

            return otherSinglePaymentBean.cashPay(merchantCode, userName, outletId, amount,
                    transactionFee, totalAmount, tableNumber, items, transactionDiscount, pricings,
                    clientTransRefnum, customerName, customerEmail, customerPhone,
                    clientInvoiceId, clientDateTime, uid).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("othercard")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String otherCardPayment(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("amount") String amount,
            @FormParam("transactionFee") String transactionFee,
            @FormParam("totalAmount") String totalAmount,
            @FormParam("tableNumber") String tableNumber,
            @FormParam("items") String items,
            @FormParam("transactionDiscount") String transactionDiscount,
            @FormParam("pricings") String pricings,
            @FormParam("clientTransRefnum") String clientTransRefnum,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("customerPhone") String customerPhone,
            @FormParam("creditCardType") String creditCardType,
            @FormParam("creditCardHolderName") String creditCardHolderName,
            @FormParam("approvalCode") String approvalCode,
            @FormParam("clientInvoiceId") String clientInvoiceId,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("uid") String uid,
            @FormParam("debitCredit") String debitCredit) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
 
            return otherSinglePaymentBean.otherCardPay(merchantCode, userName, outletId,
                    amount, transactionFee, totalAmount, tableNumber, items, transactionDiscount, pricings,
                    clientTransRefnum, customerName, customerEmail, customerPhone,
                    creditCardType, creditCardHolderName, approvalCode,
                    clientInvoiceId, clientDateTime, uid, debitCredit).toString();
        }
        return responseValidate.toString();
    }
    
    private String sendEmailProcess(String merchantCode, String outletId, String items, String transactionDiscount,
            String customerEmail, String customerName,String userName, String tableNumber, 
                    String clientDateTime, String uid, String clientInvoiceId, String pricings, String totalAmount, String methodName)
    {
        JSONArray arr = new JSONArray(items);
        String outletAddress = "";
        String outletName = "";
        List listItemName = new ArrayList<>();
        List listQuantity = new ArrayList<>();
        List listPrice = new ArrayList<>();
        List listSubTotal = new ArrayList<>();
        BigDecimal totalItemsDiscount = BigDecimal.ZERO; 
        BigDecimal totalTransactionDiscount = BigDecimal.ZERO;
        String itemsDiscountName = "";
        String transactionDiscountName = "";
        String pricingAmount = "";
        String pricingName = "";
        List listPricingName = new ArrayList<>();
        List listPricingAmount = new ArrayList<>();
        
        if(pricings.equalsIgnoreCase("[]"))
        {    
            pricings="0";
            pricingAmount="0";
        }
        try{
            for (int i=0; i<arr.length(); i++){

                JSONObject getJSONObject = arr.getJSONObject(i);
                String itemNameOrigin = getJSONObject.getString("itemName");
                String qtyOrigin = getJSONObject.getString("itemQuantity");
                String priceOrigin = getJSONObject.getString("itemSalesPrice");
                String subTotalOrigin = getJSONObject.getString("itemSubTotal");

                listItemName.add(itemNameOrigin);               
                listQuantity.add(qtyOrigin);
                listPrice.add(priceOrigin);
                listSubTotal.add(subTotalOrigin);   
                
                if(!getJSONObject.isNull("itemDiscount"))
                {
                    JSONObject jsoItemDiscount = getJSONObject.getJSONObject("itemDiscount");
                    itemsDiscountName = jsoItemDiscount.getString("discountName");
                    BigDecimal discountAmount = new BigDecimal(jsoItemDiscount.getString("discountAmount"));
                    totalItemsDiscount = totalItemsDiscount.add(discountAmount);
                }
            }
            
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
        
        String merchantName = null;
        try{
            JSONObject obj = new JSONObject(outletBean.getOutlet(merchantCode, outletId).toString());
            JSONArray array = obj.getJSONArray("outlet");
            for(int i = 0 ; i < array.length() ; i++){
               outletAddress = array.getJSONObject(i).getString("address");
               outletName = array.getJSONObject(i).getString("name");
            }
            merchantName = obj.getString("merchant");

        }catch(JSONException e){
            e.printStackTrace(System.out);
        }
        
        if(!transactionDiscount.equalsIgnoreCase("null")) {
            JSONObject jsonObject = new JSONObject(transactionDiscount);
            transactionDiscountName = (String) jsonObject.get("discountName");
            totalTransactionDiscount = new BigDecimal(jsonObject.getString("discountAmount"));
        }

        if(!pricings.equalsIgnoreCase("0")) {
            JSONArray arrPricings = new JSONArray(pricings);
            for (int j=0; j<arrPricings.length(); j++){
                JSONObject getJSONObjectPricings = arrPricings.getJSONObject(j);
                pricingAmount = getJSONObjectPricings.getString("pricingAmount");
                listPricingAmount.add(pricingAmount);
                pricingAmount.substring(1);
                pricingName = getJSONObjectPricings.getString("name");
                listPricingName.add(pricingName);
             }
        }
        
        CashPaymentEmailContent emailContent = new CashPaymentEmailContent();
        String sTotalItemsDiscount = totalItemsDiscount.toString();
        String sTotalTransactionDiscount = totalTransactionDiscount.toString();
        
        if( sTotalItemsDiscount.equalsIgnoreCase("0") && sTotalTransactionDiscount.equalsIgnoreCase("0")) {
            sendEmailReturn = Mail.sendMail(customerEmail, EMAIL_SUBJECT, 
                                emailContent.setEmailCashPayment(customerName, userName, merchantName, outletName, tableNumber, outletAddress, clientDateTime, uid,
                                   clientInvoiceId, listItemName, listQuantity, listPrice, listSubTotal, listPricingName, listPricingAmount, totalAmount));
        }
        else {
             sendEmailReturn = Mail.sendMail(customerEmail, EMAIL_SUBJECT, 
                                emailContent.setEmailCashPaymentWithDiscount(customerName, userName, merchantName, outletName, tableNumber, outletAddress, clientDateTime, uid,
                                   clientInvoiceId, listItemName, listQuantity, listPrice, listSubTotal, "Total Items Discount", totalItemsDiscount.toString(), 
                                   "Total Transactions Discount", 
                                   totalTransactionDiscount.toString(), listPricingName, listPricingAmount, totalAmount));
        }
        
        if(methodName.equalsIgnoreCase(CASH_PAY_SEND_EMAIL)) {
            if(sendEmailReturn) {
                emailStatus = EMAIL_SUCCESS;
                sendEmailReturn = false;
            }
            else {
                System.out.println("cashPaySendEmail : Sending Email failed."); 
                emailStatus = EMAIL_FAILED;
            }
        }
        else if(methodName.equalsIgnoreCase(CASH_PAY)) {
             if(!sendEmailReturn) 
                System.out.println("cashPayment --> Sending Email failed.");  
        }
            
        listItemName.clear();
        listQuantity.clear();
        listPrice.clear();
        listSubTotal.clear();
        listPricingAmount.clear();
        listPricingName.clear();
        return addEmailBean.addEmailCashPayment(sendEmailReturn, customerName, customerEmail, clientDateTime).toString();
    }

}
