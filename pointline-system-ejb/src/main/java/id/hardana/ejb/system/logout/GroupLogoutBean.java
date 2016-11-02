/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.logout;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.LoginGroupStatusEnum;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
import id.hardana.entity.sys.log.LoginGroup;
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

public class GroupLogoutBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String logout(String applicationKey, String groupCode,
            String groupUsername, String sessionId, String token) {
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
        List<GroupMerchant> groupMerchantList = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                .setParameter("code", groupCode)
                .getResultList();
        if (groupMerchantList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_GROUP_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        GroupMerchant groupMerchantSearch = groupMerchantList.get(0);
        Long groupId = Long.parseLong(groupMerchantSearch.getId());

        List<GroupOperator> goSearchList = em.createNamedQuery("GroupOperator.findByUserNameAndGroupId", GroupOperator.class)
                .setParameter("groupUserName", groupUsername)
                .setParameter("groupId", groupId)
                .getResultList();
        if (goSearchList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
        }
        GroupOperator groupOperatorSearch = goSearchList.get(0);
        Long groupOperatorId = Long.parseLong(groupOperatorSearch.getId());

        List<LoginGroup> loginGroupList = em.createNamedQuery("LoginGroup.findActiveUserGroupByChannel", LoginGroup.class)
                .setParameter("channelId", channelId)
                .setParameter("groupId", groupId)
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

        String tokenFromDB = loginGroup.getTokenNew();
        if (!token.equals(tokenFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_TOKEN.getResponseStatus());
            return response.toString();
        }
        
        Date nowDate = new Date();
        loginGroup.setLogoutTime(nowDate);
        loginGroup.setStatus(LoginGroupStatusEnum.LOGOUT);
                em.merge(loginGroup);

        response.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }
}
