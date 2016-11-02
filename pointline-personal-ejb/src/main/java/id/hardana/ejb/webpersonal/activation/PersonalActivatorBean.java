/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.activation;

import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.webpersonal.registration.BNIRegisterObject;
import id.hardana.ejb.webpersonal.registration.SimpleRegisterBean;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PersonalActivatorBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource(mappedName = "BNIRegisterQueue")
    private Queue bniQueue;
    @Inject
    @JMSConnectionFactory("ConnectionFactory")
    private JMSContext jmsContext;
    @Resource
    private EJBContext context;

    public JSONObject activate(String account, String activationCode) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        String activationCodeFromDB = personal.getActivationCode();
        PersonalStatusEnum status = personal.getStatusId();
        if (status.equals(PersonalStatusEnum.ACTIVE) || status.equals(PersonalStatusEnum.PENDINGUPGRADE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_ALREDY_ACTIVE.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INCOMPLETE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INCOMPLETE_REGISTRATION.getResponseStatus());
            return response;
        }

        if (activationCode.equals(activationCodeFromDB)) {

            try {
                personal.setStatusId(PersonalStatusEnum.ACTIVE);
                personal.setLastupdated(nowDate);
                em.merge(personal);
            } catch (OptimisticLockException e) {
                context.setRollbackOnly();
                response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
                return response;
            }

            BNIRegisterObject bniObject = new BNIRegisterObject();
            bniObject.setPersonalInfoId(personalInfoId);
            sendJMSMessageToBNIQueue(bniObject);

            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            return response;
        } else {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_ACTIVATION_CODE.getResponseStatus());
            return response;
        }
    }

    private void sendJMSMessageToBNIQueue(BNIRegisterObject bniObject) {
        try {
            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            objectMessage.setObject(bniObject);
            jmsContext.createProducer().send(bniQueue, objectMessage);
        } catch (JMSException ex) {
            Logger.getLogger(SimpleRegisterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
