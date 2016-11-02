/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;

/**
 *
 * @author Trisna
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "MailSystemQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MailSystemMDB implements MessageListener {

    @Resource(name = "mail/emoMail")
    private Session mailEmo;
    private final String mailFrom = "No Reply <noreply@emo.co.id>";
    private final String mailFromGroup = "Registration of Group Merchant<noreply@emo.co.id>";

    public MailSystemMDB() {
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = null;
        try {
            objectMessage = (ObjectMessage) message;
            MailSystemObject mailObject = (MailSystemObject) objectMessage.getObject();
            String to = mailObject.getTo();
            MailSystemType mailType = mailObject.getMailType();
            String subject = mailType.getSubject();
            String text = "";
            String from = "";
            if (mailType.equals(MailSystemType.MERCHANT_REGISTRATION)) {
                String userName = mailObject.getUserName();
                String merchantCode = mailObject.getMerchantCode();
                String merchantName = mailObject.getMerchantName();
                text = merchantRegEmail(userName, merchantName, merchantCode);
                from = mailFrom;
            } else if (mailType.equals(MailSystemType.PERSONAL_FORGET_PASSWORD)) {
                String firstName = mailObject.getFirstName();
                String lastName = mailObject.getLastName();
                String account = mailObject.getAccount();
                String newPassword = mailObject.getNewPassword();
                text = forgetPasswordPersonalEmail(firstName, lastName, account, newPassword);
                from = mailFrom;
            } else if (mailType.equals(MailSystemType.OP_MERCHANT_FORGET_PASSWORD)) {
                String merchantName = mailObject.getMerchantName();
                String userName = mailObject.getUserName();
                String newPassword = mailObject.getNewPassword();
                text = forgetPasswordMerchantOperatorEmail(merchantName, userName, newPassword);
                from = mailFrom;
            } else if (mailType.equals(MailSystemType.GROUP_MERCHANT_REGISTRATION)){
                text = groupCodeEmailText(mailObject.getGmUserName(), mailObject.getGmName(), mailObject.getGmCode());
                from = mailFromGroup;
            }
            sendMail(to, subject, text, from);
        } catch (NamingException | MessagingException | JMSException ex) {
            Logger.getLogger(MailSystemMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String merchantRegEmail(String userName, String merchantName, String merchantCode) {
        String emailText = "Dear Mr/Mrs. " + userName + ",\n\nThank you for registering your merchant at the emo.co.id."
                + "\n\nYour Username is : " + userName
                + "\nYour Merchant Name is : " + merchantName
                + "\nYour Merchant Code is : " + merchantCode
                + "\n\nIf you are still having problems signing up please "
                + "contact a member of our support staff at support@emo.co.id\n\n\nAll the best,\nTeam EMO";
        return emailText;
    }

    private String forgetPasswordPersonalEmail(String firstName, String lastName,
            String account, String newPassword) {
        String emailText = "Dear Mr/Mrs. " + firstName + " " + lastName
                + ",\n\nYour EMO Password has been reset. "
                + "\n\nAccount : " + account
                + "\nNew Password : " + newPassword
                + "\n\nPlese change your password immediately."
                + "\nIf you are still having problems signing up please "
                + "contact a member of our support staff at support@emo.co.id\n\n\nAll the best,\nTeam EMO";
        return emailText;
    }

    private String forgetPasswordMerchantOperatorEmail(String merchantName, String userName,
            String newPassword) {
        String emailText = "Dear Mr/Mrs. Merchant Owner " + merchantName
                + ",\n\nYour Operator Password has been reset. "
                + "\n\nOperator Username : " + userName
                + "\nNew Password : " + newPassword
                + "\n\nPlese change your operator password immediately."
                + "\nIf you are still having problems signing up please "
                + "contact a member of our support staff at support@emo.co.id\n\n\nAll the best,\nTeam EMO";
        return emailText;
    }

    private void sendMail(String email, String subject, String body, String from) throws NamingException, MessagingException {
        MimeMessage message = new MimeMessage(mailEmo);
        message.setSubject(subject);
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setText(body);
        message.setFrom(new InternetAddress(from));
        Transport.send(message);
    }
    
    private String groupCodeEmailText(String gmUserName, String gmName, String gmCode) {
        String emailText = "Dear " + gmUserName + ",\n\nThank you for registering your Group Merchant at the emo.co.id."
                + "\n\nYour Group Merchant Username is : " + gmUserName
                + "\nYour Group Name is : " + gmName
                + "\nYour Group Code is : " + gmCode
                + "\n\nIf you are still having problems signing up please "
                + "contact a member of our support staff at support@emo.co.id\n\n\nAll the best,\nTeam Emo";
        return emailText;
    }
}
