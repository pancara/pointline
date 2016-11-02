/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PersonalValidatorBean {

    private final String STATUS_KEY = "status";
    private final String PERSONAL_INFO_KEY = "personalInfo";
    private final String PERSONAL_KEY = "personal";
    private final String FIRST_NAME_KEY = "firstName";
    private final String LAST_NAME_KEY = "lastName";
    private final String ACCOUNT_KEY = "account";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public HashMap<String, Object> getAccountInfo(String account) {
        HashMap response = new HashMap();

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

        PersonalStatusEnum status = personal.getStatusId();
        HashMap validatePersonalStatusResponse = validatePersonalStatus(status);
        if (!validatePersonalStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validatePersonalStatusResponse;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(ACCOUNT_KEY, formattedAccount);
        response.put(FIRST_NAME_KEY, personalInfo.getFirstName());
        response.put(LAST_NAME_KEY, personalInfo.getLastName());
        return response;
    }

    public HashMap<String, Object> validateAccount(String account) {
        HashMap response = new HashMap();

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

        PersonalStatusEnum status = personal.getStatusId();
        HashMap validatePersonalStatusResponse = validatePersonalStatus(status);
        if (!validatePersonalStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validatePersonalStatusResponse;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_KEY, personalInfo);
        response.put(PERSONAL_KEY, personal);
        return response;
    }

    public HashMap<String, Object> validateAccountById(Long personalInfoId) {
        HashMap response = new HashMap();

         List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findById", PersonalInfo.class)
                .setParameter("id", personalInfoId)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        personalInfoId = Long.parseLong(personalInfo.getId());

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        PersonalStatusEnum status = personal.getStatusId();
        HashMap validatePersonalStatusResponse = validatePersonalStatus(status);
        if (!validatePersonalStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validatePersonalStatusResponse;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_KEY, personalInfo);
        response.put(PERSONAL_KEY, personal);
        return response;
    }

    public HashMap<String, Object> validateAccountAndPIN(String account, String hashedPIN,
            String clientDateTime) {

        HashMap response = validateAccount(account);
        Personal personal = (Personal) response.get(PERSONAL_KEY);
        if (!response.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return response;
        }

        String pinFromDB = personal.getPin();
        if (pinFromDB == null) {
            response.remove(PERSONAL_INFO_KEY);
            response.remove(PERSONAL_KEY);
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }
        String hashedPinFromDB = null;
        try {
            hashedPinFromDB = SHA.SHA256Hash(pinFromDB + clientDateTime);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PersonalValidatorBean.class.getName()).log(Level.SEVERE, null, ex);
            response.remove(PERSONAL_INFO_KEY);
            response.remove(PERSONAL_KEY);
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!hashedPIN.equals(hashedPinFromDB)) {
            response.remove(PERSONAL_INFO_KEY);
            response.remove(PERSONAL_KEY);
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }

        return response;
    }

    private HashMap<String, Object> validatePersonalStatus(PersonalStatusEnum status) {
        HashMap response = new HashMap();

        if (status.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INCOMPLETE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_INACTIVE.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_INACTIVE.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

}
