/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal;

import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.ejb.webpersonal.profile.EditProfileBean;
import id.hardana.ejb.webpersonal.profile.PINManagementBean;
import id.hardana.ejb.webpersonal.profile.ViewProfileBean;
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
@Path("profile")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class ProfileService {

    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private ViewProfileBean viewProfileBean;
    @EJB
    private EditProfileBean editProfileBean;
    @EJB
    private PINManagementBean pinManagementBean;
    private final String STATUS_KEY = "status";

    public ProfileService() {
    }

    @POST
    @Path("view")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewProfile(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return viewProfileBean.viewProfile(account).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("edit")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editProfile(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("email") String email,
            @FormParam("gender") String gender,
            @FormParam("address") String address,
            @FormParam("zipCode") String zipCode,
            @FormParam("cityId") String cityId,
            @FormParam("placeOfBirth") String placeOfBirth,
            @FormParam("dateOfBirth") String dateOfBirth,
            @FormParam("identityType") String identityType,
            @FormParam("identityNumber") String identityNumber,
            @FormParam("npwp") String npwp,
            @FormParam("bankId") String bankId,
            @FormParam("bankBranch") String bankBranch,
            @FormParam("bankAccHolderName") String bankAccHolderName,
            @FormParam("bankAccNumber") String bankAccNumber) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return editProfileBean.edit(account, firstName, lastName, email, gender, address,
                    zipCode, cityId, placeOfBirth, dateOfBirth, identityType, identityNumber,
                    npwp, bankId, bankBranch, bankAccHolderName, bankAccNumber);
        }
        return responseValidate.toString();
    }

    @POST
    @Path("addResetPin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String addResetPin(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashedPin") String hashedPin,
            @FormParam("hashedActivationCode") String hashedActivationCode,
            @FormParam("clientDateTime") String clientDateTime) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return pinManagementBean.addOrResetPIN(account, hashedPin, hashedActivationCode,
                    clientDateTime).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("editPin")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editPin(@FormParam("applicationKey") String applicationKey,
            @FormParam("account") String account,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("hashedOldPin") String hashedOldPin,
            @FormParam("hashedNewPin") String hashedNewPin,
            @FormParam("clientDateTime") String clientDateTime) {

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return pinManagementBean.editPIN(account, hashedOldPin, hashedNewPin, clientDateTime).toString();
        }
        return responseValidate.toString();
    }

}
