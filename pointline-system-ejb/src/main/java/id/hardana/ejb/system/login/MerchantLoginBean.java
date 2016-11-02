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
import id.hardana.entity.profile.enums.MerchantStatusEnum;
import id.hardana.entity.profile.enums.OperatorStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.LoginStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
import id.hardana.entity.sys.log.LoginAPIVersion;
import java.security.NoSuchAlgorithmException;
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

public class MerchantLoginBean {

    private final String STATUS_KEY = "status";
    private final String SESSIONID_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    private final String CHANNELID_KEY = "channelId";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredMerchantMPOS = 15; // Hours
    private final int sessionExpiredMerchantWeb = 30; // Minutes
    private final int tokenExpiredMerchant = 3; // Minutes
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    public JSONObject autoLogin(Long merchantId, Long userId, Long channelId,
            boolean isMPOS, Integer apiVersion) {
        kickOldLogin(merchantId, userId, channelId);

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();

        Date sessionExpiredDate;
        if (isMPOS) {
            c.setTime(nowDate);
            c.add(Calendar.HOUR_OF_DAY, sessionExpiredMerchantMPOS);
            sessionExpiredDate = c.getTime();
        } else {
            c.setTime(nowDate);
            c.add(Calendar.MINUTE, sessionExpiredMerchantWeb);
            sessionExpiredDate = c.getTime();
        }

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredMerchant);
        Date tokenExpiredDate = c.getTime();

        String sessionId = CodeGenerator.generateSessionId();
        String token = CodeGenerator.generateToken();

        Login loginEntity = new Login();
        loginEntity.setChannelId(channelId);
        loginEntity.setMerchantId(merchantId);
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

        JSONObject response = new JSONObject();
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SESSIONID_KEY, sessionId);
        response.put(TOKEN_KEY, token);
        response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredMerchant * 60));

        return response;
    }

    public String loginFromMPOS(String applicationKey, String hashApplicationSecret,
            String merchantCode, String userName, String hashPassword,
            String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.MPOS);
        return login(applicationKey, hashApplicationSecret, merchantCode, userName,
                hashPassword, applicationType, true, dateFromClient, apiVersion);
    }

    public String loginFromWebMerchant(String applicationKey, String hashApplicationSecret,
            String merchantCode, String userName, String hashPassword,
            String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.WEBMERCHANT);
        return login(applicationKey, hashApplicationSecret, merchantCode, userName,
                hashPassword, applicationType, false, dateFromClient, apiVersion);
    }

    private String login(String applicationKey, String hashApplicationSecret,
            String merchantCode, String userName, String hashPassword,
            EnumSet<ApplicationTypeEnum> applicationType, boolean isMPOS,
            String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel.toString();
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Integer apiVersionInt = responseValidateChannel.getInt(API_VERSION_KEY);
        Long channelId = Long.parseLong(channelIdString);

        List<Merchant> merchantsSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        Merchant merchantSearch = merchantsSearch.get(0);
        em.refresh(merchantSearch);
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
        em.refresh(operatorSearch);

        String passwordHashFromDB = operatorSearch.getPassword();
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

        MerchantStatusEnum statusMerchant = merchantSearch.getStatus();
        if (statusMerchant.equals(MerchantStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.MERCHANT_INACTIVE.getResponseStatus());
            return response.toString();
        }

        Boolean isDeleted = Boolean.valueOf(operatorSearch.getIsDeleted());
        if (isDeleted) {
            response.put(STATUS_KEY, ResponseStatusEnum.USER_IS_DELETED.getResponseStatus());
            return response.toString();
        }

        OperatorStatusEnum statusOperator = operatorSearch.getStatus();
        if (statusOperator.equals(OperatorStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.USER_INACTIVE.getResponseStatus());
            return response.toString();
        }

        Long operatorId = Long.parseLong(operatorSearch.getId());

        JSONObject autoLoginResponse = autoLogin(merchantId, operatorId, channelId, isMPOS, apiVersionInt);
        return autoLoginResponse.toString();
    }
    
    private void kickOldLogin(Long merchantId, Long userId, Long channelId) {
        List<Login> loginList = em.createNamedQuery("Login.findActiveUserMerchantByChannel", Login.class)
                .setParameter("channelId", channelId)
                .setParameter("merchantId", merchantId)
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
