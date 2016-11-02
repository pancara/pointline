/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginGroupStatusEnum;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
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

public class GroupOperatorValidationBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int sessionExpiredGroup = 30; // Minutes

    public JSONObject validateGroupOperator(String applicationKey, String groupCode,
            String groupUserName, String sessionId, String token) {
        return validateGO(applicationKey, groupCode, groupUserName, sessionId, token);
    }

    private JSONObject validateGO(String applicationKey, String groupCode, String groupUserName, String sessionId, String token) {
       
        JSONObject response = new JSONObject();
        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();
        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response;
        }
        ChannelAPI channelAPI = channelAPIs.get(0);
        Long channelId = Long.parseLong(channelAPI.getId());
        List<GroupMerchant> groupMerchantList = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                .setParameter("code", groupCode)
                .getResultList();
        if (groupMerchantList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_GROUP_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        GroupMerchant groupMerchantSearch = groupMerchantList.get(0);
        Long groupId = Long.parseLong(groupMerchantSearch.getId());
        List<GroupOperator> goSearchList = em.createNamedQuery("GroupOperator.findByUserNameAndGroupId", GroupOperator.class)
                .setParameter("groupUserName", groupUserName)
                .setParameter("groupId", groupId)
                .getResultList();
        if (goSearchList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        GroupOperator groupOperator = goSearchList.get(0);
        Long groupOperatorId = Long.parseLong(groupOperator.getId());
        List<LoginGroup> loginGroupList = em.createNamedQuery("LoginGroup.findActiveUserGroupByChannel", LoginGroup.class)
                .setParameter("channelId", channelId)
                .setParameter("groupId", groupId)
                .setParameter("userId", groupOperatorId)
                .setParameter("status", LoginGroupStatusEnum.LOGIN)
                .getResultList();
        if (loginGroupList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.EMPTY_LOGIN_SESSION.getResponseStatus());
            return response;
        }
        LoginGroup loginGroup = loginGroupList.get(0);
        em.refresh(loginGroup);

        String sessionIdFromDB = loginGroup.getSessionId();
        if (!sessionId.equals(sessionIdFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_SESSION.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        SimpleDateFormat dateformatSessionAndTokenExpired = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String sessionExpiredDateString = loginGroup.getSessionExpired();
        Date sessionExpiredDate;
        try {
            sessionExpiredDate = dateformatSessionAndTokenExpired.parse(sessionExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response;
        }
        if (sessionExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.SESSION_EXPIRED.getResponseStatus());
            return response;
        }

        String tokenFromDB = loginGroup.getTokenNew();
        if (!token.equals(tokenFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response;
        }

        String tokenExpiredDateString = loginGroup.getTokenNewExpired();
        Date tokenExpiredDate;
        try {
            tokenExpiredDate = dateformatSessionAndTokenExpired.parse(tokenExpiredDateString);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response;
        }
        if (tokenExpiredDate.before(nowDate)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.TOKEN_EXPIRED.getResponseStatus());
            return response;
        }

        Date newSessionExpiredDate;
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.MINUTE, sessionExpiredGroup);
        newSessionExpiredDate = c.getTime();
        loginGroup.setSessionExpired(newSessionExpiredDate);
        em.merge(loginGroup);

        response.put(STATUS_KEY, GroupResponseStatusEnum.VALID.getResponseStatus());
        return response;
    }

}
