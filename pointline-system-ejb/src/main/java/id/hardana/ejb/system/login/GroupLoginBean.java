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
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.enums.GroupMerchantStatusEnum;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.profile.enums.GroupOperatorStatusEnum;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.LoginGroupStatusEnum;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
import id.hardana.entity.sys.log.LoginGroup;
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
 * @author Arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class GroupLoginBean {

    private final String STATUS_KEY = "status";
    private final String SESSIONID_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    private final String CHANNELID_KEY = "channelId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredGroup = 30; // Minutes
    private final int tokenExpiredGroup = 3; // Minutes
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    public JSONObject autoLogin(Long groupId, Long userId, Long channelId) {
        kickOldLogin(groupId, userId, channelId);

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();

        Date sessionExpiredDate;
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredGroup);
        sessionExpiredDate = c.getTime();

        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredGroup);
        Date tokenExpiredDate = c.getTime();

        String sessionId = CodeGenerator.generateSessionId();
        String token = CodeGenerator.generateToken();

        LoginGroup loginGroupEntity = new LoginGroup();
        loginGroupEntity.setChannelId(channelId);
        loginGroupEntity .setGroupId(groupId);
        loginGroupEntity.setUserId(userId);
        loginGroupEntity.setLoginGroupTime(nowDate);
        loginGroupEntity.setStatus(LoginGroupStatusEnum.LOGIN);
        loginGroupEntity.setSessionId(sessionId);
        loginGroupEntity.setSessionExpired(sessionExpiredDate);
        loginGroupEntity.setTokenNew(token);
        loginGroupEntity.setTokenNewExpired(tokenExpiredDate);
        em.persist(loginGroupEntity);

        JSONObject response = new JSONObject();
        response.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SESSIONID_KEY, sessionId);
        response.put(TOKEN_KEY, token);
        response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredGroup * 60));

        return response;
    }

    public String loginGroup(String applicationKey, String hashApplicationSecret,
            String groupMerchantCode, String groupUsername, String hashPassword, String dateFromClient, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.GROUP_MERCHANT);
        return login(applicationKey, hashApplicationSecret, groupMerchantCode, groupUsername,
                hashPassword, applicationType, dateFromClient, apiVersion);
    }

    private String login(String applicationKey, String hashApplicationSecret,
            String groupMerchantCode, String groupUsername, String hashPassword,
            EnumSet<ApplicationTypeEnum> applicationType, String dateFromClient, String apiVersion) {
        
        JSONObject response = new JSONObject();
        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(GroupResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel.toString();
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Long channelId = Long.parseLong(channelIdString);

        List<GroupMerchant> groupMerchantList = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                .setParameter("code", groupMerchantCode)
                .getResultList();
        if (groupMerchantList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_GROUP_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        GroupMerchant groupSearch = groupMerchantList.get(0);
        em.refresh(groupSearch);
        Long groupId = Long.parseLong(groupSearch.getId());
        List<GroupOperator> goSearchList = em.createNamedQuery("GroupOperator.findByUserNameAndGroupId", GroupOperator.class)
                .setParameter("groupUserName", groupUsername)
                .setParameter("groupId", groupId)
                .getResultList();
        if (goSearchList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
        }
        GroupOperator groupOperator = goSearchList.get(0);
        em.refresh(groupOperator);

        String passwordHashFromDB = groupOperator.getPassword();
        String passwordHashFromDBHashed;
        try {
            passwordHashFromDBHashed = SHA.SHA256Hash(passwordHashFromDB + dateFromClient);
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response.toString();
        }
        if (!passwordHashFromDBHashed.equals(hashPassword)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_PASSWORD.getResponseStatus());
            return response.toString();
        }

        GroupMerchantStatusEnum statusGroup = groupSearch.getStatus();
        if (statusGroup.equals(GroupMerchantStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.GROUP_MERCHANT_INACTIVE.getResponseStatus());
            return response.toString();
        }

        Boolean isDeleted = Boolean.valueOf(groupOperator.isDeleted());
        if (isDeleted) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.USER_IS_DELETED.getResponseStatus());
            return response.toString();
        }

        GroupOperatorStatusEnum statusOperator = groupOperator.getStatus();
        if (statusOperator.equals(GroupOperatorStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.USER_INACTIVE.getResponseStatus());
            return response.toString();
        }

        Long groupOperatorId = Long.parseLong(groupOperator.getId());
        JSONObject autoLoginResponse = autoLogin(groupId, groupOperatorId, channelId);
        return autoLoginResponse.toString();
    }

    private void kickOldLogin(Long groupId, Long userId, Long channelId) {
        List<LoginGroup> loginList = em.createNamedQuery("LoginGroup.findActiveUserGroupByChannel", LoginGroup.class)
                .setParameter("channelId", channelId)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .setParameter("status", LoginGroupStatusEnum.LOGIN)
                .getResultList();

        if (!loginList.isEmpty()) {
            for (LoginGroup login : loginList) {
                login.setStatus(LoginGroupStatusEnum.EXPIRED);
                login.setLogoutTime(new Date());
                em.merge(login);
            }
        }
    }
}
