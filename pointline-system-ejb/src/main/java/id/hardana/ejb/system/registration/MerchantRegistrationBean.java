 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.registration;

import id.hardana.ejb.system.login.MerchantLoginBean;
import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.mdb.MailSystemObject;
import id.hardana.ejb.system.mdb.MailSystemType;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.system.tools.EmailFormatValidator;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.enums.MerchantStatusEnum;
import id.hardana.entity.profile.enums.OperatorStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.math.BigDecimal;
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

public class MerchantRegistrationBean {

    @Resource(mappedName = "MailSystemQueue")
    private Queue mailSystemQueue;
    @Inject
    @JMSConnectionFactory("ConnectionFactory")
    private JMSContext context;

    private final String STATUS_KEY = "status";
    private final String CHANNELID_KEY = "channelId";
    private final String MERCHANTCODE_KEY = "merchantCode";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private ChannelAPIValidationBean channelValidationBean;
    @EJB
    private MerchantLoginBean merchantLoginBean;

    public JSONObject registerMerchantFromWebMerchant(String applicationKey, String hashApplicationSecret,
            String merchantName, String userName, String hashPassword,
            String dateFromClient, String email, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.WEBMERCHANT);
        return registerMerchant(applicationKey, hashApplicationSecret, merchantName, userName,
                hashPassword, applicationType, false, dateFromClient, email, apiVersion);
    }

    public JSONObject registerMerchantFromMPOS(String applicationKey, String hashApplicationSecret,
            String merchantName, String userName, String hashPassword,
            String dateFromClient, String email, String apiVersion) {
        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.MPOS);
        return registerMerchant(applicationKey, hashApplicationSecret, merchantName, userName,
                hashPassword, applicationType, true, dateFromClient, email, apiVersion);
    }

    private JSONObject registerMerchant(String applicationKey, String hashApplicationSecret,
            String merchantName, String userName, String hashPassword,
            EnumSet<ApplicationTypeEnum> applicationType, boolean isMPOS,
            String dateFromClient, String email, String apiVersion) {

        JSONObject response = new JSONObject();
        boolean isValidEmail = EmailFormatValidator.validate(email);
        if (!isValidEmail) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response;
        }

        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel;
        }

        String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
        Integer apiVersionInt = responseValidateChannel.getInt(API_VERSION_KEY);
        Long channelId = Long.parseLong(channelIdString);

        boolean merchantIsEmpty = false;
        String merchantCodeGenerate = null;
        List<Merchant> merchants;

        while (!merchantIsEmpty) {
            merchantCodeGenerate = CodeGenerator.generateMerchantCode();
            merchants = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                    .setParameter("code", merchantCodeGenerate)
                    .getResultList();
            merchantIsEmpty = merchants.isEmpty();
        }

        Merchant newMerchant = new Merchant();
        newMerchant.setContactEmail(email);
        newMerchant.setCode(merchantCodeGenerate);
        newMerchant.setBrandName(merchantName);
        newMerchant.setStatus(MerchantStatusEnum.ACTIVE);
        newMerchant.setMerchantFee(BigDecimal.ZERO);
        em.persist(newMerchant);
        em.flush();

        Long newMerchantId = Long.parseLong(newMerchant.getId());

        Operator operatorOwner = new Operator();
        operatorOwner.setUserName(userName);
        operatorOwner.setPassword(hashPassword);
        operatorOwner.setMerchantId(newMerchantId);
        operatorOwner.setIsOwner(true);
        operatorOwner.setStatus(OperatorStatusEnum.ACTIVE);
        operatorOwner.setIsDeleted(false);
        operatorOwner.setLastUpdated(new Date());
        em.persist(operatorOwner);
        em.flush();

        Long operatorOwnerId = Long.parseLong(operatorOwner.getId());

        JSONObject responseMerchantLogin = merchantLoginBean.autoLogin(newMerchantId, operatorOwnerId,
                channelId, isMPOS, apiVersionInt);
        responseMerchantLogin.put(MERCHANTCODE_KEY, merchantCodeGenerate);
        
        MailSystemObject mailObject = new MailSystemObject();
        mailObject.setMailType(MailSystemType.MERCHANT_REGISTRATION);
        mailObject.setMerchantCode(merchantCodeGenerate);
        mailObject.setMerchantName(merchantName);
        mailObject.setUserName(userName);
        mailObject.setTo(email);
        
        try {
            sendJMSMessageToMailSystemQueue(mailObject);
        } catch (JMSException ex) {
            Logger.getLogger(MerchantRegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseMerchantLogin;
    }

    private void sendJMSMessageToMailSystemQueue(MailSystemObject mailObject) throws JMSException {
        ObjectMessage objectMessage = context.createObjectMessage();
        objectMessage.setObject(mailObject);
        context.createProducer().send(mailSystemQueue, objectMessage);
    }

}
