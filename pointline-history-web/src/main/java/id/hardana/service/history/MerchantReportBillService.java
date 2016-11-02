/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.history;

import id.hardana.ejb.history.bill.MerchantReportBillBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.history.interceptor.LoggingInterceptor;
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
@Path("bill/merchant")
@Interceptors(LoggingInterceptor.class)

public class MerchantReportBillService {

    private final String STATUS_KEY = "status";
    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private MerchantReportBillBean merchantReportBillBean;

    public MerchantReportBillService() {
    }

    @POST
    @Path("summary")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getSummary(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("startCreatedDate") String startCreatedDate,
            @FormParam("endCreatedDate") String endCreatedDate,
            @FormParam("startDueDate") String startDueDate,
            @FormParam("endDueDate") String endDueDate,
            @FormParam("billName") String billName,
            @FormParam("billNumber") String billNumber,
            @FormParam("status") String status,
            @FormParam("payer") String payer,
            @FormParam("info1") String info1,
            @FormParam("info2") String info2,
            @FormParam("info3") String info3,
            @FormParam("payerName") String payerName) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantReportBillBean.summaryReportBill(merchantCode, startCreatedDate, endCreatedDate,
                    startDueDate, endDueDate, billName, billNumber, status, payer, info1, info2, info3, payerName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("report")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getReport(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startCreatedDate") String startCreatedDate,
            @FormParam("endCreatedDate") String endCreatedDate,
            @FormParam("startDueDate") String startDueDate,
            @FormParam("endDueDate") String endDueDate,
            @FormParam("billName") String billName,
            @FormParam("billNumber") String billNumber,
            @FormParam("status") String status,
            @FormParam("payer") String payer,
            @FormParam("info1") String info1,
            @FormParam("info2") String info2,
            @FormParam("info3") String info3,
            @FormParam("payerName") String payerName) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantReportBillBean.reportBill(merchantCode, limit, page, startCreatedDate,
                    endCreatedDate, startDueDate, endDueDate, billName, billNumber, status,
                    payer, info1, info2, info3, payerName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("invoiceinfo")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getInvoiceInfo(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("billNumber") String billNumber,
            @FormParam("invoiceNumber") String invoiceNumber) {
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return merchantReportBillBean.getInvoiceFromBill(merchantCode, billNumber, invoiceNumber).toString();
        }
        return responseValidate.toString();
    }

}
