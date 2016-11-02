/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.logout;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.admin.Administrator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.LoginAdmin;
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

public class AdminLogoutBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String logout(String applicationKey, String userName,
            String sessionId, String token) {
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

        String tokenFromDB = login.getTokenNew();
        if (!token.equals(tokenFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        login.setLogoutTime(nowDate);
        login.setStatus(LoginStatusEnum.LOGOUT);
        em.merge(login);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }
}
