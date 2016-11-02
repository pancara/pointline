/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.registration;

import id.hardana.ejb.system.login.PersonalLoginBean;
import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.enums.PersonalTypeEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PersonalRegistrationBean {

    private final String STATUS_KEY = "status";
    private final String CHANNELID_KEY = "channelId";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private ChannelAPIValidationBean channelValidationBean;
    @EJB
    private PersonalLoginBean personalLoginBean;
    private final int passwordExpirationDays = 90; // Days

    public String registerPersonalFromWebPersonal(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.WEBPERSONAL);
        return registerPersonal(applicationKey, hashApplicationSecret, account, hashPassword,
                applicationType, dateFromClient, apiVersion);
    }

    public String registerPersonalFromPersonalApplication(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.PERSONAL);
        return registerPersonal(applicationKey, hashApplicationSecret, account, hashPassword,
                applicationType, dateFromClient, apiVersion);
    }

    private String registerPersonal(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, EnumSet<ApplicationTypeEnum> applicationType,
            String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response.toString();
        }

        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel.toString();
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Integer apiVersionInt = responseValidateChannel.getInt(API_VERSION_KEY);
        Long channelId = Long.parseLong(channelIdString);

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (!personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ALREADY_REGISTERED.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        PersonalInfo newPersonalInfo = new PersonalInfo();
        newPersonalInfo.setAccount(formattedAccount);
        newPersonalInfo.setRegDate(nowDate);
        newPersonalInfo.setLastUpdated(nowDate);
        em.persist(newPersonalInfo);
        em.flush();

        Long newPersonalInfoId = Long.parseLong(newPersonalInfo.getId());

        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.DAY_OF_YEAR, passwordExpirationDays);
        Date passwordExpiredDate = c.getTime();

        Personal newPersonal = new Personal();
        newPersonal.setPersonalInfoId(newPersonalInfoId);
        newPersonal.setPassword1(hashPassword);
        newPersonal.setPasswordExpired(passwordExpiredDate);
        newPersonal.setBalance(BigDecimal.ZERO);
        newPersonal.setStatusId(PersonalStatusEnum.INCOMPLETE);
        newPersonal.setTypeId(PersonalTypeEnum.UNREGISTER);
        newPersonal.setLastupdated(nowDate);
        newPersonal.setIsDeleted(false);
        em.persist(newPersonal);

        JSONObject responsePersonalLogin = personalLoginBean.autoLogin(newPersonalInfoId, channelId,
                apiVersionInt);
        return responsePersonalLogin.toString();
    }

    private String registerVIP(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, EnumSet<ApplicationTypeEnum> applicationType,
            String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response.toString();
        }

        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel.toString();
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Integer apiVersionInt = responseValidateChannel.getInt(API_VERSION_KEY);
        Long channelId = Long.parseLong(channelIdString);

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (!personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ALREADY_REGISTERED.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        PersonalInfo newPersonalInfo = new PersonalInfo();
        newPersonalInfo.setAccount(formattedAccount);
        newPersonalInfo.setRegDate(nowDate);
        newPersonalInfo.setLastUpdated(nowDate);
        em.persist(newPersonalInfo);
        em.flush();

        Long newPersonalInfoId = Long.parseLong(newPersonalInfo.getId());

        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.DAY_OF_YEAR, passwordExpirationDays);
        Date passwordExpiredDate = c.getTime();

        Personal newPersonal = new Personal();
        newPersonal.setPersonalInfoId(newPersonalInfoId);
        newPersonal.setPassword1(hashPassword);
        newPersonal.setPasswordExpired(passwordExpiredDate);
        newPersonal.setBalance(BigDecimal.ZERO);
        newPersonal.setStatusId(PersonalStatusEnum.INCOMPLETE);
        newPersonal.setTypeId(PersonalTypeEnum.UNREGISTER);
        newPersonal.setLastupdated(nowDate);
        newPersonal.setIsDeleted(false);
        em.persist(newPersonal);

        JSONObject responsePersonalLogin = personalLoginBean.autoLogin(newPersonalInfoId, channelId,
                apiVersionInt);
        return responsePersonalLogin.toString();
    }
}
