/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.changepassword;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
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

public class GroupOperatorChangePasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String changePassword(String groupCode, String groupUsername,
            String hashOldPassword, String hashNewPassword) {
        JSONObject response = new JSONObject();

        List<GroupMerchant> groupSearchList = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                .setParameter("code", groupCode)
                .getResultList();
        if (groupSearchList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_GROUP_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        GroupMerchant groupSearch = groupSearchList.get(0);
        Long groupId = Long.parseLong(groupSearch.getId());

        List<GroupOperator> groupOperatorList = em.createNamedQuery("GroupOperator.findByUserNameAndGroupId", GroupOperator.class)
                .setParameter("groupUserName", groupUsername)
                .setParameter("groupId", groupId)
                .getResultList();
        if (groupOperatorList.isEmpty()) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
        }
        GroupOperator groupOperator = groupOperatorList.get(0);
        em.refresh(groupOperator);

        String hashOldPasswordFromDB = groupOperator.getPassword();
        if (!hashOldPassword.equals(hashOldPasswordFromDB)) {
            response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_PASSWORD.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        groupOperator.setPassword(hashNewPassword);
        groupOperator.setLastUpdated(nowDate);
        em.merge(groupOperator);

        response.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }

}
