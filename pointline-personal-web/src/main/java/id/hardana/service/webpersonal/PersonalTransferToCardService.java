/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.ejb.webpersonal.transaction.TransferToCardViaMerchantBean;
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
@Path("transferpersonaltocard")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class PersonalTransferToCardService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private TransferToCardViaMerchantBean TransferToCardViaMerchantBean;
    private final String STATUS_KEY = "status";

    public PersonalTransferToCardService() {
    }

    @POST
    @Path("transfer")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String transfer(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan,
            @FormParam("personalHashedPin") String personalHashedPin,
            @FormParam("amount") String amount,
            @FormParam("clientDateTime") String clientDateTime,
            @FormParam("clientTransRefnum") String clientTransRefnum) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return TransferToCardViaMerchantBean.transferToCard(account, pan, personalHashedPin, amount,
                    clientDateTime, clientTransRefnum).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("canceltransfer")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String cancelTransfer(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("pan") String pan,
            @FormParam("personalHashedPin") String personalHashedPin,
            @FormParam("referenceNumber") String referenceNumber,
            @FormParam("clientDateTime") String clientDateTime) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return TransferToCardViaMerchantBean.cancelTransferToCard(account, pan, personalHashedPin,
                    referenceNumber, clientDateTime).toString();
        }
        return responseValidate.toString();
    }
}
