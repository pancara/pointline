/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
import id.hardana.entity.sys.log.LoginAPIVersion;
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

public class OperatorMerchantValidationBean {

    private final String STATUS_KEY = "status";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredMerchantWeb = 30; // Minutes

    public JSONObject validateOperatorMPOS(String applicationKey, String merchantCode,
            String userName, String sessionId, String token) {
        return validateOperator(applicationKey, merchantCode, userName, sessionId, token, true);
    }

    public JSONObject validateOperatorMerchantWeb(String applicationKey, String merchantCode,
            String userName, String sessionId, String token) {
        return validateOperator(applicationKey, merchantCode, userName, sessionId, token, false);
    }

    private JSONObject validateOperator(String applicationKey, String merchantCode,
            String userName, String sessionId, String token, boolean isMPOS) {
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

        List<Merchant> merchantsSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchantSearch = merchantsSearch.get(0);

        Long merchantId = Long.parseLong(merchantSearch.getId());

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        Operator operatorSearch = operatorsSearch.get(0);

        Long operatorId = Long.parseLong(operatorSearch.getId());

        List<Login> logins = em.createNamedQuery("Login.findActiveUserMerchantByChannel", Login.class)
                .setParameter("channelId", channelId)
                .setParameter("merchantId", merchantId)
                .setParameter("userId", operatorId)
                .setParameter("status", LoginStatusEnum.LOGIN)
                .getResultList();
        if (logins.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_LOGIN_SESSION.getResponseStatus());
            return response;
        }
        Login login = logins.get(0);
        em.refresh(login);

        Integer apiVersion = 0;
        List<LoginAPIVersion> loginApiVersions = em.createNamedQuery("LoginAPIVersion.findByLoginId", LoginAPIVersion.class)
                .setParameter("loginId", Long.parseLong(login.getId()))
                .getResultList();
        if (!loginApiVersions.isEmpty()) {
            LoginAPIVersion loginApiVersion = loginApiVersions.get(0);
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

        if (!isMPOS) {
            Date newSessionExpiredDate;
            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.MINUTE, sessionExpiredMerchantWeb);
            newSessionExpiredDate = c.getTime();
            login.setSessionExpired(newSessionExpiredDate);
            em.merge(login);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.VALID.getResponseStatus());
        response.put(API_VERSION_KEY, apiVersion);
        return response;
    }

}
