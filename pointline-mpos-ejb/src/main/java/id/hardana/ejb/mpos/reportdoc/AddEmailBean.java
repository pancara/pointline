/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.reportdoc;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.customeremail.CustomerEmail;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class AddEmailBean {
    private final String STATUS_KEY = "status";
    private final String SEND_EMAIL_KEY = "sendEmailStatus";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject addEmailCashPayment(boolean emailResponse, String customerName, String email, String dateTime) {       
        JSONObject response = new JSONObject();
        
        CustomerEmail entity = new CustomerEmail();       
        entity.setEmail(email);
        entity.setDateTime(dateTime);
        entity.setName(customerName);
        em.persist(entity);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SEND_EMAIL_KEY, emailResponse);
        return response;
    }
}
