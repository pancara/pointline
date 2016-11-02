/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.login;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.admin.Administrator;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.LoginAdmin;
import id.hardana.entity.sys.log.LoginAdminAPIVersion;
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

public class AdminLoginBean {

    private final String STATUS_KEY = "status";
    private final String SESSIONID_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    private final String CHANNELID_KEY = "channelId";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredAdmin = 30; // Minutes
    private final int tokenExpiredAdmin = 3; // Minutes
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    private JSONObject autoLogin(Long userId, Long channelId, Integer apiVersion) {
        kickOldLogin(userId, channelId);

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredAdmin);
        Date sessionExpiredDate = c.getTime();

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredAdmin);
        Date tokenExpiredDate = c.getTime();

        String sessionId = CodeGenerator.generateSessionId();
        String token = CodeGenerator.generateToken();

        LoginAdmin loginEntity = new LoginAdmin();
        loginEntity.setChannelId(channelId);
        loginEntity.setUserId(userId);
        loginEntity.setLoginTime(nowDate);
        loginEntity.setStatus(LoginStatusEnum.LOGIN);
        loginEntity.setSessionId(sessionId);
        loginEntity.setSessionExpired(sessionExpiredDate);
        loginEntity.setTokenNew(token);
        loginEntity.setTokenNewExpired(tokenExpiredDate);
        em.persist(loginEntity);
        em.flush();

        LoginAdminAPIVersion loginAdminAPIVersion = new LoginAdminAPIVersion();
        loginAdminAPIVersion.setLoginAdminId(Long.parseLong(loginEntity.getId()));
        loginAdminAPIVersion.setApiVersion(apiVersion);
        em.persist(loginAdminAPIVersion);

        JSONObject response = new JSONObject();
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SESSIONID_KEY, sessionId);
        response.put(TOKEN_KEY, token);
        response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredAdmin * 60));

        return response;
    }

    public String login(String applicationKey, String hashApplicationSecret,
            String userName, String hashPassword, String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, EnumSet.of(ApplicationTypeEnum.WEBADMIN),
                true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel.toString();
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Integer apiVersionInt = responseValidateChannel.getInt(API_VERSION_KEY);
        Long channelId = Long.parseLong(channelIdString);

        List<Administrator> adminsSearch = em.createNamedQuery("Personal.findByUserName", Administrator.class)
                .setParameter("userName", userName)
                .getResultList();

        if (adminsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }
        Administrator adminSearch = adminsSearch.get(0);
        em.refresh(adminSearch);
        Long adminId = Long.parseLong(adminSearch.getId());

        Date nowDate = new Date();
        String passwordHashFromDB = adminSearch.getPassword1();
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

        JSONObject autoLoginResponse = autoLogin(adminId, channelId, apiVersionInt);

        String passwordExpiredDateString = adminSearch.getPasswordExpired();
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
            return autoLoginResponse.toString();
        }

        return autoLoginResponse.toString();
    }

    private void kickOldLogin(Long userId, Long channelId) {
        List<LoginAdmin> loginList = em.createNamedQuery("LoginAdmin.findActiveUserByChannel", LoginAdmin.class)
                .setParameter("channelId", channelId)
                .setParameter("userId", userId)
                .setParameter("status", LoginStatusEnum.LOGIN)
                .getResultList();

        if (!loginList.isEmpty()) {
            for (LoginAdmin login : loginList) {
                login.setStatus(LoginStatusEnum.EXPIRED);
                login.setLogoutTime(new Date());
                em.merge(login);
            }
        }
    }
}
