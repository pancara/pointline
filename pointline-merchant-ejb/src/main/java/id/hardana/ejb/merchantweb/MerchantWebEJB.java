/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import id.hardana.entity.sys.config.Config;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hanafi
 */
@Stateless
@LocalBean
public class MerchantWebEJB {

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }
    
    public String testMethod(String name) {
        return name;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public void sillyMethod(String configkeytext, String configvaluetext, Date dateUpdate) {

        Config entity = new Config();
        
        entity.setConfigKey(configkeytext);
        entity.setConfigValue(configvaluetext);
        entity.setLastModified(dateUpdate);
        
        em.persist(entity);
    }
    
    
    
}
