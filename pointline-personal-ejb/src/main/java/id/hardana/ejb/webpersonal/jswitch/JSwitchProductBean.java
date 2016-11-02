package id.hardana.ejb.webpersonal.jswitch;

import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.jswitch.JSwitchProduct;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author pancara
 */

@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})
public class JSwitchProductBean {
    
    @PersistenceContext(unitName= "JPAPU")
    private EntityManager em;
    
    
    public List<JSwitchProduct> getActiveProducts() {
        String ql = "SELECT p FROM JSwitchProduct p WHERE (p.enabled = true) AND (p.listedInJSwitch = true) ORDER BY p.name";
        return em.createQuery(ql, JSwitchProduct.class)
                .getResultList();
    }
    
}
