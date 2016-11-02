/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.tools;

//import id.hardana.ejb.webgroup.log.LoggingInterceptor;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.merchant.Merchant;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class GeneralQueries {
   
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    public Long getGroupIdFromGroupCode(String groupCode){
        GroupMerchant entityGroupId = (GroupMerchant) em.createNamedQuery("GroupMerchant.findByCode")
                .setParameter("code", groupCode)
                .getSingleResult();
        
        return Long.valueOf(entityGroupId.getId());
    }
    
    public Long getMerchantIdFromMerchantCode(String merchantCode){
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
            .setParameter("code", merchantCode)
            .getSingleResult();

        return Long.valueOf(entityMerchantId.getId());
    }
    
    public String getMerchantCodeFromMerchantId(String merchantId){
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findById")
            .setParameter("code", merchantId)
            .getSingleResult();

        return entityMerchantId.getCode();
    }
    public String getBrandNameFromMerchantCode(String merchantCode){
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
            .setParameter("code", merchantCode)
            .getSingleResult();

        return entityMerchantId.getBrandName();
    }
    
    public boolean isMerchantIdExist(String groupCode, String merchantCode) {
        Long groupId = getGroupIdFromGroupCode(groupCode);
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        List<Long> merchantIdList =  em.createNamedQuery("GroupMerchantToMerchant.findMerchantIdByGroupId", Long.class)
                .setParameter("groupId", groupId.toString())
                .getResultList();
        
        for (Long i : merchantIdList) {
            if(merchantIdList.contains(merchantId)){
                return true;
            }
        }
        return false;
    }
    
    public List<Long> getMerchantIdList(String groupCode){
        Long groupId = getGroupIdFromGroupCode(groupCode);
        List<Long> merchantIdList =  em.createNamedQuery("GroupMerchantToMerchant.findMerchantIdByGroupId", Long.class)
                .setParameter("groupId", groupId.toString())
                .getResultList();
        return merchantIdList;
    }
}
