package id.hardana.service.mpos.messaging;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

    public synchronized static boolean sendMail(String to, String subject,
            String text) 
            {
        final String username = "noreply@emo.co.id";
        final String password = "teukeudahdiwales";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.emo.co.id");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.localhost", "mail.emo.co.id");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setContent(text, "text/html; charset=utf-8");
            message.setSubject(subject);
            message.setFrom(new InternetAddress("Digital Receipt <noreply@emo.co.id>"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

}
