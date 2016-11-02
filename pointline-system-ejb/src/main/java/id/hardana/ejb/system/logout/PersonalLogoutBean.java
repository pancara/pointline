/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.logout;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
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

public class PersonalLogoutBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String logout(String applicationKey, String account,
            String sessionId, String token) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response.toString();
        }

        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();
        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response.toString();
        }
        ChannelAPI channelAPI = channelAPIs.get(0);

        Long channelId = Long.parseLong(channelAPI.getId());

        List<PersonalInfo> personalInfosSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();

        if (personalInfosSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }
        PersonalInfo personalInfoSearch = personalInfosSearch.get(0);
        Long personalInfoId = Long.parseLong(personalInfoSearch.getId());

        List<Login> logins = em.createNamedQuery("Login.findActiveUserByChannel", Login.class)
                .setParameter("channelId", channelId)
                .setParameter("userId", personalInfoId)
                .setParameter("status", LoginStatusEnum.LOGIN)
                .getResultList();
        if (logins.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_LOGIN_SESSION.getResponseStatus());
            return response.toString();
        }
        Login login = logins.get(0);
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
