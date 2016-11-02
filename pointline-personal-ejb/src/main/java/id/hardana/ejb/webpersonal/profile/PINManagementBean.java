/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.profile;

import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PINManagementBean {

    private final String STATUS_KEY = "status";
    private final String PERSONAL_DATA_KEY = "personalData";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject addOrResetPIN(String account, String hashedPin, String hashedActivationCode,
            String clientDateTime) {
        JSONObject response = new JSONObject();

        JSONObject responseValidate = validateAndGetPersonalData(account);
        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseValidate;
        }

        Personal personal = (Personal) responseValidate.get(PERSONAL_DATA_KEY);

        String activationCodeFromDB = personal.getActivationCode();
        String hashActivationCodeFromDB = null;
        try {
            hashActivationCodeFromDB = SHA.SHA256Hash(account + activationCodeFromDB + clientDateTime);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PINManagementBean.class.getName()).log(Level.SEVERE, null, ex);
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!hashedActivationCode.equals(hashActivationCodeFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_ACTIVATION_CODE.getResponseStatus());
            return response;
        }

        try {
            personal.setPin(hashedPin);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject editPIN(String account, String hashedOldPin, String hashedNewPin,
            String clientDateTime) {
        JSONObject response = new JSONObject();

        JSONObject responseValidate = validateAndGetPersonalData(account);
        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseValidate;
        }

        Personal personal = (Personal) responseValidate.get(PERSONAL_DATA_KEY);

        String pinFromDB = personal.getPin();
        if (pinFromDB == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }
        String hashedPinFromDB = null;
        try {
            hashedPinFromDB = SHA.SHA256Hash(pinFromDB + clientDateTime);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PINManagementBean.class.getName()).log(Level.SEVERE, null, ex);
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!hashedOldPin.equals(hashedPinFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }

        try {
            personal.setPin(hashedNewPin);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private JSONObject validateAndGetPersonalData(String account) {
        JSONObject response = new JSONObject();
        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_DATA_KEY, personal);
        return response;
    }

}
