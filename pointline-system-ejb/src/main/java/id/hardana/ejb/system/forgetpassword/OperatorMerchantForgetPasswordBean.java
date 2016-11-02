/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.forgetpassword;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.mdb.MailSystemObject;
import id.hardana.ejb.system.mdb.MailSystemType;
import id.hardana.ejb.system.registration.MerchantRegistrationBean;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class OperatorMerchantForgetPasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource(mappedName = "MailSystemQueue")
    private Queue mailSystemQueue;
    @Inject
    @JMSConnectionFactory("ConnectionFactory")
    private JMSContext context;
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    public JSONObject forgetPassword(String applicationKey, String hashApplicationSecret,
            String merchantCode, String userName, String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.MPOS,
                ApplicationTypeEnum.WEBMERCHANT);
        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel;
        }

        List<Merchant> merchantsSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchantSearch = merchantsSearch.get(0);
        Long merchantId = Long.parseLong(merchantSearch.getId());
        String contactEmail = merchantSearch.getContactEmail();
        String ownerEmail = merchantSearch.getOwnerEmail();
        String email = contactEmail + "," + ownerEmail;
        String merchantName = merchantSearch.getBrandName();

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId",
                Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        Operator operatorSearch = operatorsSearch.get(0);
        em.refresh(operatorSearch);

        String newPassword = CodeGenerator.generatePassword();
        String hashNewPassword;
        try {
            hashNewPassword = SHA.SHA256Hash(userName + SHA.SHA256Hash(newPassword));
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        operatorSearch.setPassword(hashNewPassword);
        operatorSearch.setLastUpdated(nowDate);
        em.merge(operatorSearch);

        MailSystemObject mailObject = new MailSystemObject();
        mailObject.setMailType(MailSystemType.OP_MERCHANT_FORGET_PASSWORD);
        mailObject.setMerchantName(merchantName);
        mailObject.setUserName(userName);
        mailObject.setNewPassword(newPassword);
        mailObject.setTo(email);

        try {
            sendJMSMessageToMailSystemQueue(mailObject);
        } catch (JMSException ex) {
            Logger.getLogger(MerchantRegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private void sendJMSMessageToMailSystemQueue(MailSystemObject mailObject) throws JMSException {
        ObjectMessage objectMessage = context.createObjectMessage();
        objectMessage.setObject(mailObject);
        context.createProducer().send(mailSystemQueue, objectMessage);
    }

}
