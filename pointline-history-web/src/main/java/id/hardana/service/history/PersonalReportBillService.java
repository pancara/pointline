/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.history;

import id.hardana.ejb.history.bill.PersonalReportBillBean;
import id.hardana.ejb.system.validation.PersonalValidationBean;
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
@Path("bill/personal")
@Interceptors(LoggingInterceptor.class)

public class PersonalReportBillService {

    private final String STATUS_KEY = "status";
    @EJB
    private PersonalReportBillBean personalReportBillBean;
    @EJB
    private PersonalValidationBean personalValidationBean;

    public PersonalReportBillService() {
    }

    @POST
    @Path("report")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getReport(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
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
            @FormParam("info3") String info3) {
        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalReportBillBean.reportBill(account, limit, page, startCreatedDate,
                    endCreatedDate, startDueDate, endDueDate, billName, billNumber, status,
                    payer, info1, info2, info3).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("transactioninfo")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String getTransactionInfo(
            @FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("billNumber") String billNumber,
            @FormParam("referenceNumber") String referenceNumber) {
        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalReportBillBean.getTransactionBill(account, billNumber, referenceNumber).toString();
        }
        return responseValidate.toString();
    }

}
