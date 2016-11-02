/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.admin.Administrator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.LoginAdmin;
import id.hardana.entity.sys.log.LoginAdminAPIVersion;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

public class AdminValidationBean {

    private final String STATUS_KEY = "status";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredAdmin = 30; // Minutes

    public JSONObject validateAdmin(String applicationKey, String userName,
            String sessionId, String token) {
        JSONObject response = new JSONObject();

        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();
        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response;
        }
        ChannelAPI channelAPI = channelAPIs.get(0);

        Long channelId = Long.parseLong(channelAPI.getId());

        List<Administrator> adminsSearch = em.createNamedQuery("Personal.findByUserName", Administrator.class)
                .setParameter("userName", userName)
                .getResultList();

        if (adminsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        Administrator adminSearch = adminsSearch.get(0);
        Long adminId = Long.parseLong(adminSearch.getId());

        List<LoginAdmin> logins = em.createNamedQuery("LoginAdmin.findActiveUserByChannel", LoginAdmin.class)
                .setParameter("channelId", channelId)
                .setParameter("userId", adminId)
                .setParameter("status", LoginStatusEnum.LOGIN)
                .getResultList();
        if (logins.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_LOGIN_SESSION.getResponseStatus());
            return response;
        }
        LoginAdmin login = logins.get(0);
        em.refresh(login);

        Integer apiVersion = 0;
        List<LoginAdminAPIVersion> loginApiVersions = em.createNamedQuery("LoginAdminAPIVersion.findByLoginAdminId",
                LoginAdminAPIVersion.class)
                .setParameter("loginAdminId", Long.parseLong(login.getId()))
                .getResultList();
        if (!loginApiVersions.isEmpty()) {
            LoginAdminAPIVersion loginApiVersion = loginApiVersions.get(0);
            try {
                apiVersion = Integer.parseInt(loginApiVersion.getApiVersion());
            } catch (Exception e) {
            }
        }

        String sessionIdFromDB = login.getSessionId();
        if (!sessionId.equals(sessionIdFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_SESSION.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        SimpleDateFormat dateformatSessionAndTokenExpired = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String sessionExpiredDateString = login.getSessionExpired();
        Date sessionExpiredDate;
        try {
            sessionExpiredDate = dateformatSessionAndTokenExpired.parse(sessionExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response;
        }
        if (sessionExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.SESSION_EXPIRED.getResponseStatus());
            return response;
        }

        String tokenFromDB = login.getTokenNew();
        if (!token.equals(tokenFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response;
        }

        String tokenExpiredDateString = login.getTokenNewExpired();
        Date tokenExpiredDate;
        try {
            tokenExpiredDate = dateformatSessionAndTokenExpired.parse(tokenExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response;
        }
        if (tokenExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.TOKEN_EXPIRED.getResponseStatus());
            return response;
        }

        Date newSessionExpiredDate;
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredAdmin);
        newSessionExpiredDate = c.getTime();
        login.setSessionExpired(newSessionExpiredDate);
        em.merge(login);

        response.put(STATUS_KEY, ResponseStatusEnum.VALID.getResponseStatus());
        response.put(API_VERSION_KEY, apiVersion);
        return response;
    }

}
