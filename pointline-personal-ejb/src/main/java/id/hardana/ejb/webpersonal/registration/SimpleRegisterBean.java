/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.registration;

import id.hardana.ejb.system.tools.EmailFormatValidator;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.profile.personal.PersonalVirtualAccount;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.Date;
import java.util.List;
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

public class SimpleRegisterBean {

    private final String STATUS_KEY = "status";
    private final String ACT_CODE_KEY = "actCode";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject registerVIP(String account, String firstName, String lastName, String email) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        if (firstName.length() > 30 || lastName.length() > 30) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        boolean isValidEmail = EmailFormatValidator.validate(email);
        if (!isValidEmail) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        em.refresh(personalInfo);

        Long personalInfoId = Long.parseLong(personalInfo.getId());
        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        PersonalStatusEnum status = personal.getStatusId();
        if (status.equals(PersonalStatusEnum.ACTIVE) || status.equals(PersonalStatusEnum.PENDINGUPGRADE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_ALREDY_ACTIVE.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ALREADY_REGISTERED.getResponseStatus());
            return response;
        }

        String activationCode = generateActivationCode();

        try {
            personalInfo.setLastUpdated(nowDate);
            personalInfo.setFirstName(firstName);
            personalInfo.setLastName(lastName);
            personalInfo.setEmail(email);
            em.merge(personalInfo);

            personal.setLastupdated(nowDate);
            personal.setStatusId(PersonalStatusEnum.INACTIVE);
            personal.setActivationCode(activationCode);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        generateAndPersistVANumber(personalInfoId, formattedAccount);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(ACT_CODE_KEY, activationCode);
        return response;
    }

    public JSONObject register(String account, String firstName, String lastName, String email) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        if (firstName.length() > 30 || lastName.length() > 30) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        boolean isValidEmail = EmailFormatValidator.validate(email);
        if (!isValidEmail) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        em.refresh(personalInfo);

        Long personalInfoId = Long.parseLong(personalInfo.getId());
        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        PersonalStatusEnum status = personal.getStatusId();
        if (status.equals(PersonalStatusEnum.ACTIVE) || status.equals(PersonalStatusEnum.PENDINGUPGRADE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_ALREDY_ACTIVE.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ALREADY_REGISTERED.getResponseStatus());
            return response;
        }

        String activationCode = generateActivationCode();

        try {
            personalInfo.setLastUpdated(nowDate);
            personalInfo.setFirstName(firstName);
            personalInfo.setLastName(lastName);
            personalInfo.setEmail(email);
            em.merge(personalInfo);

            personal.setLastupdated(nowDate);
            personal.setStatusId(PersonalStatusEnum.INACTIVE);
            personal.setActivationCode(activationCode);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        generateAndPersistVANumber(personalInfoId, formattedAccount);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(ACT_CODE_KEY, activationCode);
        return response;
    }

    private void generateAndPersistVANumber(Long personalInfoId, String account) {
        List<PersonalVirtualAccount> personalVASearch = em.createQuery("SELECT p FROM PersonalVirtualAccount p WHERE p.personalInfoId = :personalInfoId", PersonalVirtualAccount.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalVASearch.isEmpty()) {
            Boolean vaExist = true;
            String last5DigitAccount = account.substring(Math.max(account.length() - 5, 0));
            Long last4DigitsPersonalInfoId = personalInfoId % 10000;
            String vaNumber = null;
            while (vaExist) {
                vaNumber = String.format("%04d", last4DigitsPersonalInfoId) + last5DigitAccount;
                List<PersonalVirtualAccount> vaNumberSearch = em.createQuery("SELECT p FROM PersonalVirtualAccount p WHERE p.accountNumber = :accountNumber", PersonalVirtualAccount.class)
                        .setParameter("accountNumber", vaNumber)
                        .getResultList();
                if (vaNumberSearch.isEmpty()) {
                    vaExist = false;
                }
                last4DigitsPersonalInfoId++;
                if (last4DigitsPersonalInfoId > 9999) {
                    last4DigitsPersonalInfoId = (long) 0001;
                }
            }
            PersonalVirtualAccount pvaData = new PersonalVirtualAccount();
            pvaData.setAccountNumber(vaNumber);
            pvaData.setPersonalInfoId(personalInfoId);
            em.persist(pvaData);
        }
    }

    private String generateActivationCode() {
        String ALPHA_NUM = "0123456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

}
