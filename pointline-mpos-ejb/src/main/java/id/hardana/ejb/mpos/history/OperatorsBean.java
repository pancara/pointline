/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.OperatorLevel;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Arya
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class OperatorsBean  {
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final String STATUS_KEY = "status";
    private final String SUCCESS = "SUCCESS";
    private final String NOT_FOUND = "NOT FOUND";
    protected EntityManager getEntityManager() {
        return em;
    }

    public OperatorsBean() {
    }
    
    public HashMap<String,Object> getOperator(String merchantCode) {

        HashMap<String,Object> result = new HashMap<>();
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        List<Operator> resultList =  em.createNamedQuery("Operator.findByMerchantId", Operator.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if(resultList.isEmpty()){
            result.put(STATUS_KEY, NOT_FOUND);
            return result;
        }

        for(Operator o : resultList){
            if(Boolean.valueOf(o.getIsOwner())){
                o.setOperatorLevelName("owner");
            } else{
                OperatorLevel entityOpLevel = em.createNamedQuery("OperatorLevel.findById", OperatorLevel.class)
                    .setParameter("id", Long.valueOf(o.getOperatorLevelId()))
                    .getSingleResult();
                o.setOperatorLevelName(entityOpLevel.getName());
            }
        }
        result.put("operators", resultList);
        result.put(STATUS_KEY, SUCCESS);
        
        return result;
    }
    
    private Long getMerchantIdFromMerchantCode(String merchantCode){
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        
        return Long.valueOf(entityMerchantId.getId());
    }
}
