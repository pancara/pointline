/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.profile.admin.Administrator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.LoginAdmin;
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

public class TokenGeneratorAdminBean {

    private final String STATUS_KEY = "status";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int tokenExpiredAdmin = 3; // Minutes
    private final int sessionExpiredAdmin = 30; // Minutes

    public String requestNewToken(String applicationKey, String userName,
            String sessionId, String oldToken) {
        JSONObject response = new JSONObject();

        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();
        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response.toString();
        }
        ChannelAPI channelAPI = channelAPIs.get(0);

        Long channelId = Long.parseLong(channelAPI.getId());

       List<Administrator> adminsSearch = em.createNamedQuery("Personal.findByUserName", Administrator.class)
                .setParameter("userName", userName)
                .getResultList();

        if (adminsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
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
            return response.toString();
        }
        LoginAdmin login = logins.get(0);
        em.refresh(login);

        String sessionIdFromDB = login.getSessionId();
        if (!sessionId.equals(sessionIdFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_SESSION.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        SimpleDateFormat dateformatSessionAndTokenExpired = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String sessionExpiredDateString = login.getSessionExpired();
        Date sessionExpiredDate;
        try {
            sessionExpiredDate = dateformatSessionAndTokenExpired.parse(sessionExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response.toString();
        }
        if (sessionExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.SESSION_EXPIRED.getResponseStatus());
            return response.toString();
        }

        String newTokenFromDB = login.getTokenNew();
        String oldTokeFromDB = login.getTokenOld();
        String newTokenExpiredFromDBString = login.getTokenNewExpired();
        Date newTokenExpiredFromDB;
        try {
            newTokenExpiredFromDB = dateformatSessionAndTokenExpired.parse(newTokenExpiredFromDBString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response.toString();
        }

        String newGeneratedToken = CodeGenerator.generateToken();
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredAdmin);
        Date newGeneratedTokenExpiredDate = c.getTime();

        Date newSessionExpiredDate;
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredAdmin);
        newSessionExpiredDate = c.getTime();

        if (oldToken.equals(newTokenFromDB)) {
            login.setTokenNew(newGeneratedToken);
            login.setTokenNewExpired(newGeneratedTokenExpiredDate);
            login.setTokenOld(newTokenFromDB);
            login.setTokenOldExpired(newTokenExpiredFromDB);
            login.setSessionExpired(newSessionExpiredDate);
            em.merge(login);
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newGeneratedToken);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredAdmin * 60));
            return response.toString();
        } else if (oldToken.equals(oldTokeFromDB)) {
            login.setSessionExpired(newSessionExpiredDate);
            em.merge(login);
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newTokenFromDB);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredAdmin * 60));
            return response.toString();
        } else {
            login.setSessionExpired(newSessionExpiredDate);
            em.merge(login);
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response.toString();
        }
    }

}
