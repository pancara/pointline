/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
import id.hardana.entity.sys.enums.LoginGroupStatusEnum;
import id.hardana.entity.sys.log.LoginGroup;
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
 * @author Arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class TokenGeneratorGroupMerchantBean {

    private final String STATUS_KEY = "status";
    private final String TOKEN_KEY = "token";
    private final String TOKENEXPIRED_KEY = "tokenExpired";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int tokenExpiredGroupMerchant = 3; // Minutes 
    private final int sessionExpiredGroupMerchantWeb = 30; // Minutes

    public String requestNewTokenWebGroupMerchant(String applicationKey, String merchantCode,
            String userName, String sessionId, String oldToken) {
        return requestNewToken(applicationKey, merchantCode, userName, sessionId, oldToken, false);
    }

    private String requestNewToken(String applicationKey, String groupMerchantCode,
            String userName, String sessionId, String oldToken, boolean isMPOS) {
        JSONObject response = new JSONObject();

        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();
        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response.toString();
        }
        ChannelAPI channelAPI = channelAPIs.get(0);

        Long channelId = Long.parseLong(channelAPI.getId());

        List<GroupMerchant> groupMerchantsSearch = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                .setParameter("code", groupMerchantCode)
                .getResultList();
        if (groupMerchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_GROUP_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        GroupMerchant groupMerchantSearch = groupMerchantsSearch.get(0);

        Long groupMerchantId = Long.parseLong(groupMerchantSearch.getId());

        List<GroupOperator> groupOperatorsSearchList = em.createNamedQuery("GroupOperator.findByUserNameAndGroupId", GroupOperator.class)
                .setParameter("groupUserName", userName)
                .setParameter("groupId", groupMerchantId)
                .getResultList();
        if (groupOperatorsSearchList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
        }
        GroupOperator groupOperatorSearch = groupOperatorsSearchList.get(0);

        Long groupOperatorId = Long.parseLong(groupOperatorSearch.getId());
        List<LoginGroup> loginGroupList = em.createNamedQuery("LoginGroup.findActiveUserGroupByChannel", LoginGroup.class)
                .setParameter("channelId", channelId)
                .setParameter("groupId", groupMerchantId)
                .setParameter("userId", groupOperatorId)
                .setParameter("status", LoginGroupStatusEnum.LOGIN)
                .getResultList();
        if (loginGroupList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.EMPTY_LOGIN_SESSION.getResponseStatus());
            return response.toString();
        }
        LoginGroup loginGroup = loginGroupList.get(0);
        em.refresh(loginGroup);

        String sessionIdFromDB = loginGroup.getSessionId();
        if (!sessionId.equals(sessionIdFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_SESSION.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        SimpleDateFormat dateformatSessionAndTokenExpired = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String sessionExpiredDateString = loginGroup.getSessionExpired();
        Date sessionExpiredDate;
        try {
            sessionExpiredDate = dateformatSessionAndTokenExpired.parse(sessionExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response.toString();
        }
        if (sessionExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.SESSION_EXPIRED.getResponseStatus());
            return response.toString();
        }

        String newTokenFromDB = loginGroup.getTokenNew();
        String oldTokeFromDB = loginGroup.getTokenOld();
        String newTokenExpiredFromDBString = loginGroup.getTokenNewExpired();
        Date newTokenExpiredFromDB;
        try {
            newTokenExpiredFromDB = dateformatSessionAndTokenExpired.parse(newTokenExpiredFromDBString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response.toString();
        }

        String newGeneratedToken = CodeGenerator.generateToken();
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, tokenExpiredGroupMerchant);
        Date newGeneratedTokenExpiredDate = c.getTime();

        if (!isMPOS) {
            Date newSessionExpiredDate;
            c.setTime(nowDate);
            c.add(Calendar.MINUTE, sessionExpiredGroupMerchantWeb);
            newSessionExpiredDate = c.getTime();
            loginGroup.setSessionExpired(newSessionExpiredDate);
            em.merge(loginGroup);
        }

        if (oldToken.equals(newTokenFromDB)) {
            loginGroup.setTokenNew(newGeneratedToken);
            loginGroup.setTokenNewExpired(newGeneratedTokenExpiredDate);
            loginGroup.setTokenOld(newTokenFromDB);
            loginGroup.setTokenOldExpired(newTokenExpiredFromDB);
            em.merge(loginGroup);
            response.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newGeneratedToken);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredGroupMerchant * 60));
            return response.toString();
        } else if (oldToken.equals(oldTokeFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(TOKEN_KEY, newTokenFromDB);
            response.put(TOKENEXPIRED_KEY, String.valueOf(tokenExpiredGroupMerchant * 60));
            return response.toString();
        } else {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response.toString();
        }
    }

}
