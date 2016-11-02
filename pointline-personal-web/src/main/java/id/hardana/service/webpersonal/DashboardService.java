/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.ejb.webpersonal.dashboard.TransactionHistoryCardBean;
import id.hardana.ejb.webpersonal.dashboard.TransactionHistoryPersonalBean;
import id.hardana.ejb.webpersonal.dashboard.TransactionStatisticBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.webpersonal.interceptor.NullCheckInterceptor;
import id.hardana.service.webpersonal.log.LoggingInterceptor;
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
@Path("dashboard")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class DashboardService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private TransactionHistoryPersonalBean transactionHistoryPersonalBean;
    @EJB
    private TransactionHistoryCardBean transactionHistoryCardBean;
    @EJB
    private TransactionStatisticBean transactionStatisticBean;
    private final String STATUS_KEY = "status";

    public DashboardService() {
    }

    @POST
    @Path("historycard")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transactionHistoryCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionHistoryCardBean.allCardHistoryByDate(account, limit, page, null, null).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("historycardbydate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transactionHistoryCardByDate(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionHistoryCardBean.allCardHistoryByDate(account, limit, page, startDate, endDate).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("historypersonal")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transactionHistoryPersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionHistoryPersonalBean.allPersonalHistoryByDate(account, limit, page, null, null).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("historypersonalbydate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transactionHistoryPersonalByDate(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionHistoryPersonalBean.allPersonalHistoryByDate(account, limit, page, startDate, endDate).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("statistic/daily")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String dailyStatistic(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionStatisticBean.dailyStatistic(account).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("statistic/monthly")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthlyStatistic(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionStatisticBean.monthlyStatistic(account).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("statistic/weektodate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String weekToDateStatistic(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionStatisticBean.weekToDateStatistic(account).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("statistic/monthtodate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String monthToDateStatistic(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionStatisticBean.monthToDateStatistic(account).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("statistic/yeartodate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String yearToDateStatistic(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return transactionStatisticBean.yearToDateStatistic(account).toString();
        }
        return responseValidate.toString();
    }

}
