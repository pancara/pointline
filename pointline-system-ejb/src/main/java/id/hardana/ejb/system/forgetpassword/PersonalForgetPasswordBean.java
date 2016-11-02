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
import id.hardana.ejb.system.tools.EmailFormatValidator;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
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

public class PersonalForgetPasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int passwordExpirationDays = 90; // Days
    @Resource(mappedName = "MailSystemQueue")
    private Queue mailSystemQueue;
    @Inject
    @JMSConnectionFactory("ConnectionFactory")
    private JMSContext context;
    @EJB
    private ChannelAPIValidationBean channelValidationBean;

    public JSONObject forgetPassword(String applicationKey, String hashApplicationSecret,
            String account, String email, String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.PERSONAL,
                ApplicationTypeEnum.WEBPERSONAL);
        JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey,
                hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
        if (!responseValidateChannel.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
            return responseValidateChannel;
        }

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        boolean isValidEmail = EmailFormatValidator.validate(email);
        if (!isValidEmail) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfosSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfosSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        PersonalInfo personalInfoSearch = personalInfosSearch.get(0);

        Long personalInfoId = Long.parseLong(personalInfoSearch.getId());
        String firstName = personalInfoSearch.getFirstName();
        String lastName = personalInfoSearch.getLastName();
        String emailFromDB = personalInfoSearch.getEmail();
        if (!emailFromDB.equals(email)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response;
        }

        List<Personal> personalsSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        Personal personalSearch = personalsSearch.get(0);
        em.refresh(personalSearch);

        String hashOldPasswordFromDB = personalSearch.getPassword1();
        String hashOldPassword2FromDB = personalSearch.getPassword2();

        String newPassword = CodeGenerator.generatePassword();
        String hashNewPassword;
        try {
            hashNewPassword = SHA.SHA256Hash(formattedAccount + SHA.SHA256Hash(newPassword));
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.DAY_OF_YEAR, passwordExpirationDays);
        Date passwordExpiredDate = c.getTime();
        personalSearch.setPassword1(hashNewPassword);
        personalSearch.setPassword2(hashOldPasswordFromDB);
        personalSearch.setPassword3(hashOldPassword2FromDB);
        personalSearch.setPasswordExpired(passwordExpiredDate);
        personalSearch.setLastupdated(nowDate);
        em.merge(personalSearch);

        MailSystemObject mailObject = new MailSystemObject();
        mailObject.setMailType(MailSystemType.PERSONAL_FORGET_PASSWORD);
        mailObject.setFirstName(firstName);
        mailObject.setLastName(lastName);
        mailObject.setAccount(account);
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
