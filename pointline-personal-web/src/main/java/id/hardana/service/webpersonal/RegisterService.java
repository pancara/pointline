/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.webpersonal.activation.PersonalActivatorBean;
import id.hardana.ejb.webpersonal.registration.FullRegisterBean;
import id.hardana.ejb.webpersonal.registration.SimpleRegisterBean;
import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.webpersonal.interceptor.NullCheckInterceptor;
import id.hardana.service.webpersonal.log.LoggingInterceptor;
import id.hardana.service.webpersonal.messaging.Mail;
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
@Path("register")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class RegisterService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private SimpleRegisterBean simpleRegisterBean;
    @EJB
    private PersonalActivatorBean personalActivatorBean;
    @EJB
    private FullRegisterBean fullRegisterBean;
    private final String STATUS_KEY = "status";
    private final String ACT_CODE_KEY = "actCode";

    public RegisterService() {
    }

    private String activationCodeEmailText(String account, String firstName, String activationCode) {
        String emailText = "Dear " + firstName + ",\n\nThank you for registering at the emo.co.id. Before we can activate "
                + "your account, one last step must be taken to complete your registration.\n\nPlease note - you must complete "
                + "this last step to activate your account.\n\nYour Username is : " + account
                + "\nYour Activation Code is : " + activationCode + "\n\nIf you are still having problems signing up please "
                + "contact a member of our support staff at support@emo.co.id\n\n\nAll the best,\nTeam EMO";
        return emailText;
    }

    @POST
    @Path("simple")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String simpleRegister(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("email") String email) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            JSONObject responseSimpleRegister = simpleRegisterBean.register(account, firstName, lastName, email);
            if (responseSimpleRegister.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
                String activationCode = responseSimpleRegister.getString(ACT_CODE_KEY);
                Mail.sendMail(email, "Activation Code", activationCodeEmailText(account, firstName, activationCode));
//                responseSimpleRegister.remove(ACT_CODE_KEY);
            }
            return responseSimpleRegister.toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("full")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String fullRegister(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("gender") String gender,
            @FormParam("address") String address,
            @FormParam("zipCode") String zipCode,
            @FormParam("cityId") String cityId,
            @FormParam("placeOfBirth") String placeOfBirth,
            @FormParam("dateOfBirth") String dateOfBirth,
            @FormParam("identityType") String identityType,
            @FormParam("identityNumber") String identityNumber,
            @FormParam("secretQuestionId") String secretQuestionId,
            @FormParam("secretAnswer") String secretAnswer,
            @FormParam("bankId") String bankId,
            @FormParam("bankBranch") String bankBranch,
            @FormParam("bankAccHolderName") String bankAccHolderName,
            @FormParam("bankAccNumber") String bankAccNumber) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return fullRegisterBean.mandatoryRegister(account, gender, address, zipCode, cityId, placeOfBirth,
                    dateOfBirth, identityType, identityNumber, secretQuestionId, secretAnswer,
                    bankId, bankBranch, bankAccHolderName, bankAccNumber);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("optional")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String optionalRegister(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("npwp") String npwp) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return fullRegisterBean.optionalRegister(account, npwp);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("activate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String activatePersonal(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("activationCode") String activationCode) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return personalActivatorBean.activate(account, activationCode).toString();
        }
        return responseValidate.toString();
    }
}
