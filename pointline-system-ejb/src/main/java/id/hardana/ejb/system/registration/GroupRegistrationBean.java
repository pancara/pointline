   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.registration;

import id.hardana.ejb.system.login.GroupLoginBean;
import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.mdb.MailSystemObject;
import id.hardana.ejb.system.mdb.MailSystemType;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.system.tools.EmailFormatValidator;
import id.hardana.ejb.system.validation.ChannelAPIValidationBean;
import id.hardana.entity.profile.enums.GroupMerchantStatusEnum;
import id.hardana.entity.profile.enums.GroupOperatorStatusEnum;
import id.hardana.entity.profile.group.GroupMerchant;
import id.hardana.entity.profile.group.GroupOperator;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.GroupResponseStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class GroupRegistrationBean {

    private final String STATUS_KEY = "status";
    private final String CHANNELID_KEY = "channelId";
    private final String GROUP_CODE_KEY = "groupCode";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private ChannelAPIValidationBean channelValidationBean;
    @EJB
    private GroupLoginBean groupLoginBean;
    @Resource(mappedName = "MailSystemQueue")
    private Queue mailSystemQueue;
    @Inject
    @JMSConnectionFactory("ConnectionFactory")
    private JMSContext context;

    public JSONObject registerGroup(String applicationKey, String hashApplicationSecret,
            String groupName, String groupUserName, String hashPassword, String dateFromClient, String email, String apiVersion) {

        EnumSet<ApplicationTypeEnum> applicationType = EnumSet.of(ApplicationTypeEnum.GROUP_MERCHANT);
        return registerGroup(applicationKey, hashApplicationSecret, groupName, groupUserName,
                hashPassword, applicationType, dateFromClient, email, apiVersion);
    }

    private JSONObject registerGroup(String applicationKey, String hashApplicationSecret,
            String groupName, String groupUserName, String hashPassword,
            EnumSet<ApplicationTypeEnum> applicationType,
            String dateFromClient, String email, String apiVersion) {

        JSONObject responseGroupLogin = new JSONObject();
        String groupCodeGenerate = null;
        try {

            JSONObject response = new JSONObject();
            boolean isValidEmail = EmailFormatValidator.validate(email);
            if (!isValidEmail) {
                response.put(STATUS_KEY, GroupResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
                return response;
            }

            //JSONObject responseValidateChannel = channelValidationBean.validateChannelGroup(applicationKey, 
            // hashApplicationSecret, applicationType, true, dateFromClient); //for testing purpose
            JSONObject responseValidateChannel = channelValidationBean.validateChannel(applicationKey, hashApplicationSecret, applicationType, true, dateFromClient, apiVersion);
            if (!responseValidateChannel.getString(STATUS_KEY).equals(GroupResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
                return responseValidateChannel;
            }

            String channelIdString = responseValidateChannel.getString(CHANNELID_KEY);
            Long channelId = Long.parseLong(channelIdString);

            boolean groupIsEmpty = false;
            List<GroupMerchant> groups;

            while (!groupIsEmpty) {
                groupCodeGenerate = CodeGenerator.generateGroupCode();
                groups = em.createNamedQuery("GroupMerchant.findByCode", GroupMerchant.class)
                        .setParameter("code", groupCodeGenerate)
                        .getResultList();
                groupIsEmpty = groups.isEmpty();
            }
            groupCodeGenerate = CodeGenerator.generateGroupCode();
            GroupMerchant newGroup = new GroupMerchant();
            newGroup.setContactEmail(email);
            newGroup.setCode(groupCodeGenerate);
            newGroup.setGroupName(groupName);
            newGroup.setStatus(GroupMerchantStatusEnum.ACTIVE);
            em.persist(newGroup);
            em.flush();

            Long newGroupId = Long.parseLong(newGroup.getId());

            GroupOperator groupOperatorOwner = new GroupOperator();
            groupOperatorOwner.setGroupUserName(groupUserName);
            groupOperatorOwner.setPassword(hashPassword);
            groupOperatorOwner.setGroupId(newGroupId);
            groupOperatorOwner.setIsOwner(true);
            groupOperatorOwner.setStatus(GroupOperatorStatusEnum.ACTIVE);
            groupOperatorOwner.setIsDeleted(false);
            groupOperatorOwner.setLastUpdated(new Date());
            em.persist(groupOperatorOwner);
            em.flush();

            Long groupOperatorOwnerId = Long.parseLong(groupOperatorOwner.getId());
            responseGroupLogin = groupLoginBean.autoLogin(newGroupId, groupOperatorOwnerId, channelId);
            responseGroupLogin.put(STATUS_KEY, GroupResponseStatusEnum.SUCCESS.getResponseStatus());
            responseGroupLogin.put(GROUP_CODE_KEY, groupCodeGenerate);
        } catch (NumberFormatException | JSONException e) {
            responseGroupLogin.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
        }
        MailSystemObject mailObject = new MailSystemObject();
        mailObject.setMailType(MailSystemType.GROUP_MERCHANT_REGISTRATION);
        mailObject.setGmCode(groupCodeGenerate); 
        mailObject.setGmName(groupName); 
        mailObject.setGmUserName(groupUserName); 
        mailObject.setTo(email);
        
        try {
            sendJMSMessageToMailSystemQueue(mailObject);
        } catch (JMSException ex) {
            Logger.getLogger(MerchantRegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseGroupLogin;
    }
    
     private void sendJMSMessageToMailSystemQueue(MailSystemObject mailObject) throws JMSException {
        ObjectMessage objectMessage = context.createObjectMessage();
        objectMessage.setObject(mailObject);
        context.createProducer().send(mailSystemQueue, objectMessage);
    }
}
