/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.ejb.webpersonal.card.PersonalToCardBean;
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
@Path("card")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class CardService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private PersonalToCardBean personalToCardBean;
    private final String STATUS_KEY = "status";

    public CardService() {
    }

    @POST
    @Path("add")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String addCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan,
            @FormParam("pin") String pin,
            @FormParam("cardHolderName") String cardHolderName) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalToCardBean.addCard(account, pan, pin, cardHolderName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("delete")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String deleteCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalToCardBean.deleteCard(account, pan).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("block")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String blockCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalToCardBean.blockCard(account, pan).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("unblock")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String unblockCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan,
            @FormParam("pin") String pin) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalToCardBean.unblockCard(account, pan, pin).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("list")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String listCard(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalToCardBean.listCard(account).toString();
        }
        return responseValidate.toString();
    }

}
