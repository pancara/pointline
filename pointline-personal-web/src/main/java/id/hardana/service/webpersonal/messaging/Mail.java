package id.hardana.service.webpersonal.messaging;

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
            String text) {
        final String username = "noreply@emo.co.id";
        final String password = "teukeudahdiwales";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.emo.co.id");
        props.put("mail.smtp.port", "25");
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
            message.setText(text);
            message.setSubject(subject);
            message.setFrom(new InternetAddress("No Reply <noreply@emo.co.id>"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            System.out.println("Email Not Sent to: " + to + ", subject: " + subject + ", message: " + text);
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static boolean sendMailOld(String to, String subject,
            String text) {

        String userName = "admin@arkalogic.net";
        String passWord = "a19/KaR9|";
        String host = "smtp.gmail.com";
        String port = "465";
        String fallback = "false";
        String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        boolean debug = false;
        String starttls = "true";
        String auth = "true";

        Properties props = new Properties();
        props.put("mail.smtp.user", userName);
        //props.put("mail.smtp.host", host);
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", "465");
        props.put("mail.smtps.ssl.trust", host);

        //if (!"".equals(port))
        //props.put("mail.smtp.port", port);
        if (!"".equals(starttls)) {
            props.put("mail.smtp.starttls.enable", starttls);
        }
        props.put("mail.smtp.auth", auth);
        if (debug) {
            props.put("mail.smtp.debug", "true");
        } else {
            props.put("mail.smtp.debug", "false");
        }
        if (!"".equals(port)) {
            props.put("mail.smtp.socketFactory.port", port);
        }
        if (!"".equals(socketFactoryClass)) {
            props.put("mail.smtp.socketFactory.class", socketFactoryClass);
        }
        if (!"".equals(fallback)) {
            props.put("mail.smtp.socketFactory.fallback", fallback);
        }

        try {
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            MimeMessage msg = new MimeMessage(session);
            msg.setText(text);
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(
                    "Administrator <admin@arkalogic.net>"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // for(int i=0;i<cc.length;i++){
            // msg.addRecipient(Message.RecipientType.CC, new
            // InternetAddress(cc[i]));
            // }
            // for(int i=0;i<bcc.length;i++){
            // msg.addRecipient(Message.RecipientType.BCC, new
            // InternetAddress(bcc[i]));
            // }
            msg.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(host, userName, passWord);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            return true;
        } catch (Exception mex) {
            return false;
        }
    }
}
