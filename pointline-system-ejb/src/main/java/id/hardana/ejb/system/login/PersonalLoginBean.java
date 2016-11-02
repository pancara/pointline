/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.login;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
import id.hardana.entity.sys.log.LoginAPIVersion;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class PersonalLoginBean {

    private final String STATUS_KEY = "status";
    private final String SESSIONID_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    private final String CHANNELID_KEY = "channelId";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredPersonal = 30; // Minutes
    private final int tokenExpiredPersonal = 3; // Minutes
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    public JSONObject autoLogin(Long userId, Long channelId, Integer apiVersion) {
        JSONObject response = new JSONObject();

        kickOldLogin(userId, channelId);

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredPersonal);
        Date sessionExpiredDate = c.getTime();

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredPersonal);
        Date tokenExpiredDate = c.getTime();

        String sessionId = CodeGenerator.generateSessionId();
        String token = CodeGenerator.generateToken();

        Login loginEntity = new Login();
        loginEntity.setChannelId(channelId);
        loginEntity.setMerchantId(null);
        loginEntity.setUserId(userId);
        loginEntity.setLoginTime(nowDate);
        loginEntity.setStatus(LoginStatusEnum.LOGIN);
        loginEntity.setSessionId(sessionId);
        loginEntity.setSessionExpired(sessionExpiredDate);
        loginEntity.setTokenNew(token);
        loginEntity.setTokenNewExpired(tokenExpiredDate);
        em.persist(loginEntity);
        em.flush();

        LoginAPIVersion loginAPIVersion = new LoginAPIVersion();
        loginAPIVersion.setLoginId(Long.parseLong(loginEntity.getId()));
        loginAPIVersion.setApiVersion(apiVersion);
        em.persist(loginAPIVersion);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SESSIONID_KEY, sessionId);
        response.put(TOKEN_KEY, token);
        response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredPersonal * 60));

        return response;
    }

    public String loginFromPersonalApplication(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.PERSONAL);
        return login(applicationKey, hashApplicationSecret, account, hashPassword, applicationType,
                dateFromClient, apiVersion);
    }

    public String loginFromWebPersonal(String applicationKey, String hashApplicationSecret,
            String account, String hashPassword, String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.WEBPERSONAL);
        return login(applicationKey, hashApplicationSecret, account, hashPassword, applicationType,
                dateFromClient, apiVersion);
    }

    private String login(String applicationKey, String hashApplicationSecret,
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

        List<PersonalInfo> personalInfosSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();

        if (personalInfosSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }
        PersonalInfo personalInfoSearch = personalInfosSearch.get(0);
        Long personalInfoId = Long.parseLong(personalInfoSearch.getId());

        List<Personal> personalsSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        Personal personalSearch = personalsSearch.get(0);
        em.refresh(personalSearch);

        Date nowDate = new Date();
        String passwordHashFromDB = personalSearch.getPassword1();
        String passwordHashFromDBHashed;
        try {
            passwordHashFromDBHashed = SHA.SHA256Hash(passwordHashFromDB + dateFromClient);
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response.toString();
        }
        if (!passwordHashFromDBHashed.equals(hashPassword)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PASSWORD.getResponseStatus());
            return response.toString();
        }

        Boolean isDeletedPersonal = Boolean.valueOf(personalSearch.getIsDeleted());
        if (isDeletedPersonal) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_IS_DELETED.getResponseStatus());
            return response.toString();
        }

        PersonalStatusEnum statusPersonal = personalSearch.getStatusId();
        if (statusPersonal.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response.toString();
        }

        JSONObject autoLoginResponse = autoLogin(personalInfoId, channelId, apiVersionInt);

        String passwordExpiredDateString = personalSearch.getPasswordExpired();
        SimpleDateFormat dateformatPasswordExpired = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date passwordExpiredDate;
        try {
            passwordExpiredDate = dateformatPasswordExpired.parse(passwordExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response.toString();
        }
        if (nowDate.after(passwordExpiredDate)) {
            autoLoginResponse.remove(STATUS_KEY);
            autoLoginResponse.put(STATUS_KEY, ResponseStatusEnum.PASSWORD_EXPIRED.getResponseStatus());
        }

        if (statusPersonal.equals(PersonalStatusEnum.INACTIVE)) {
            autoLoginResponse.remove(STATUS_KEY);
            autoLoginResponse.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_INACTIVE.getResponseStatus());
        } else if (statusPersonal.equals(PersonalStatusEnum.INCOMPLETE)) {
            autoLoginResponse.remove(STATUS_KEY);
            autoLoginResponse.put(STATUS_KEY, ResponseStatusEnum.INCOMPLETE_REGISTRATION.getResponseStatus());
        }

        return autoLoginResponse.toString();
    }

    private void kickOldLogin(Long userId, Long channelId) {
        List<Login> loginList = em.createNamedQuery("Login.findActiveUserByChannel", Login.class)
                .setParameter("channelId", channelId)
                .setParameter("userId", userId)
                .setParameter("status", LoginStatusEnum.LOGIN)
                .getResultList();

        if (!loginList.isEmpty()) {
            for (Login login : loginList) {
                login.setStatus(LoginStatusEnum.EXPIRED);
                login.setLogoutTime(new Date());
                em.merge(login);
            }
        }
    }
}
