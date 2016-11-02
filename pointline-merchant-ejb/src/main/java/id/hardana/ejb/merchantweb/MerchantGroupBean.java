/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.merchantweb.extension.GroupMerchantHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.merchantweb.tools.GeneralQueries;
import id.hardana.entity.profile.enums.GroupMerchantToMerchantStatusEnum;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupMerchantToMerchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONArray;

/**
 *
 * @author Arya
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MerchantGroupBean  {
    @EJB
    private GeneralQueries generalQueries;
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final String STATUS_KEY = "status";
    private final String SUCCESS = "SUCCESS";
    private final String PENDING_GROUP = "pendingGroupList";
    private final String GROUP_LIST = "groupList";
    private final String EMPTY_GROUP = "EMPTY_GROUP_LIST";
    private final String EMPTY_PENDING_GROUP ="EMPTY_PENDING_GROUP_LIST";

    protected EntityManager getEntityManager() {
        return em;
    }

    public MerchantGroupBean() {
    }
    
    public HashMap<String,Object> getGroups(String merchantCode, GroupMerchantToMerchantStatusEnum status) {
        HashMap<String,Object> reponse = new HashMap<>();
        
        String queryGetGroupId = "SELECT gmm FROM GroupMerchantToMerchant gmm WHERE gmm.merchantCode = :merchantCode AND gmm.status = :status";
        List<GroupMerchantToMerchant> gmmList = em.createQuery(queryGetGroupId, GroupMerchantToMerchant.class)
                    .setParameter("merchantCode", merchantCode)
                    .setParameter(STATUS_KEY, status)
                    .getResultList();
        
        if (!gmmList.isEmpty()) {
            for (GroupMerchantToMerchant gmm : gmmList) {
                em.refresh(gmm);
            }
            
            List<String> strGroupIdList = new ArrayList<>();
            JSONArray groupIdArray = new JSONArray(strGroupIdList);
            Type listType = new TypeToken<List<Long>>() {}.getType();
            List<Long> groupIdList = new Gson().fromJson(groupIdArray.toString(), listType);

            for (GroupMerchantToMerchant gmm : gmmList) {
                groupIdList.add(Long.parseLong(gmm.getGroupId()));
            }

            String queryGetGroupMerchant = "SELECT gm.id, gm.groupName, gm.contactEmail FROM GroupMerchant gm ";
            if (!groupIdList.isEmpty()) {
                queryGetGroupMerchant += " WHERE (gm.id IN :groupIdList)";
            }

            Query query  = em.createQuery(queryGetGroupMerchant, GroupMerchant.class);
            if (!groupIdList.isEmpty()) {
                query.setParameter("groupIdList", groupIdList);         
            }
            
            List<GroupMerchantHolder> result = new LinkedList<>();
            for (Object row : query.getResultList()) {
                Object[] values = (Object[]) row;

                Long groupId = (Long) values[0];
                String groupName = (String) values[1];
                String email = (String) values[2];
                
                result.add(new GroupMerchantHolder(groupId, groupName, email));
            }

            if(status == GroupMerchantToMerchantStatusEnum.ACCEPTED) {
                reponse.put(GROUP_LIST, result);
            } else {
                reponse.put(PENDING_GROUP, result);
            } 
            reponse.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        } else {
            if(status == GroupMerchantToMerchantStatusEnum.ACCEPTED) {
                reponse.put(STATUS_KEY, EMPTY_GROUP);
            } else {
                reponse.put(STATUS_KEY, EMPTY_PENDING_GROUP);
            }
        }
        return reponse;
    }
    
    public String updateStatusFromMerchant(String groupId, String merchantCode, int status, Date dateResponse) {
        Long merchantId = generalQueries.getMerchantIdFromMerchantCode(merchantCode);
        GroupMerchantToMerchant groupMerchant = new GroupMerchantToMerchant();
        
        switch (status) {
            case 3:
                groupMerchant = em.createNamedQuery("GroupMerchantToMerchant.findByGroupIdAndMerchantId", GroupMerchantToMerchant.class)
                   .setParameter("groupId", groupId)
                   .setParameter("merchantId", merchantId)
                   .getSingleResult();
                groupMerchant.setStatus(GroupMerchantToMerchantStatusEnum.ACCEPTED);
                groupMerchant.setLinkDateResponse(dateResponse);
                groupMerchant.setDateUpdated(new Date());
                em.merge(groupMerchant);
                em.flush();
                break;
            case 4:
                groupMerchant = em.createNamedQuery("GroupMerchantToMerchant.findByGroupIdAndMerchantId", GroupMerchantToMerchant.class)
                   .setParameter("groupId", groupId)
                   .setParameter("merchantId", merchantId)
                   .getSingleResult();
                groupMerchant.setStatus(GroupMerchantToMerchantStatusEnum.DENIED);
                groupMerchant.setLinkDateResponse(dateResponse);
                groupMerchant.setDateUpdated(new Date());
                em.merge(groupMerchant);
                em.flush();
                break;
            case 6:
                groupMerchant = em.createNamedQuery("GroupMerchantToMerchant.findByGroupIdAndMerchantId", GroupMerchantToMerchant.class)
                   .setParameter("groupId", groupId)
                   .setParameter("merchantId", merchantId)
                   .getSingleResult();
                groupMerchant.setStatus(GroupMerchantToMerchantStatusEnum.DELETED_BY_GROUP);
                groupMerchant.setLinkDateResponse(dateResponse);
                groupMerchant.setDateUpdated(new Date());
                em.merge(groupMerchant);
                em.flush();
                break;
        }
        
        return SUCCESS;
    }
    
}
