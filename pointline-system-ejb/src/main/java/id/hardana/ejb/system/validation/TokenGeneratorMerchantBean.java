/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
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

public class TokenGeneratorMerchantBean {

    private final String STATUS_KEY = "status";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int tokenExpiredMerchant = 3; // Minutes
    private final int sessionExpiredMerchantWeb = 30; // Minutes

    public String requestNewTokenMPOS(String applicationKey, String merchantCode,
            String userName, String sessionId, String oldToken) {
        return requestNewToken(applicationKey, merchantCode, userName, sessionId, oldToken, true);
    }

    public String requestNewTokenWebMerchant(String applicationKey, String merchantCode,
            String userName, String sessionId, String oldToken) {
        return requestNewToken(applicationKey, merchantCode, userName, sessionId, oldToken, false);
    }

    private String requestNewToken(String applicationKey, String merchantCode,
            String userName, String sessionId, String oldToken, boolean isMPOS) {
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

        List<Merchant> merchantsSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        Merchant merchantSearch = merchantsSearch.get(0);

        Long merchantId = Long.parseLong(merchantSearch.getId());

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
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
            return response.toString();
        }
        Login login = logins.get(0);
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
        c.add(Calendar.MINUTE, tokenExpiredMerchant);
        Date newGeneratedTokenExpiredDate = c.getTime();

        if (!isMPOS) {
            Date newSessionExpiredDate;
            c.setTime(nowDate);
            c.add(Calendar.MINUTE, sessionExpiredMerchantWeb);
            newSessionExpiredDate = c.getTime();
            login.setSessionExpired(newSessionExpiredDate);
            em.merge(login);
        }

        if (oldToken.equals(newTokenFromDB)) {
            login.setTokenNew(newGeneratedToken);
            login.setTokenNewExpired(newGeneratedTokenExpiredDate);
            login.setTokenOld(newTokenFromDB);
            login.setTokenOldExpired(newTokenExpiredFromDB);
            em.merge(login);
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newGeneratedToken);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredMerchant * 60));
            return response.toString();
        } else if (oldToken.equals(oldTokeFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newTokenFromDB);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredMerchant * 60));
            return response.toString();
        } else {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response.toString();
        }
    }

}
