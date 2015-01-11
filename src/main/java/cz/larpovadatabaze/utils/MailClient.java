package cz.larpovadatabaze.utils;

import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;


public class MailClient {
    private static Logger logger = Logger.getLogger(MailClient.class);
    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    public void sendMail(String content, String email) {
        sendEmail(content, email, templateMessage.getSubject());
    }

    public void sendMail(String content, String email, String subject) {
        sendEmail(content, email, subject);
    }

    private void sendEmail(String content, String email, String subject) {
        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        message.setTo(email);
        message.setText(content);
        message.setSubject(subject);
        logger.debug("Sending message:\n" + message.getText() + "\n");
        try {
            this.mailSender.send(message);
        } catch (MailException e) {
            System.err.println(e.getMessage());
        }
    }
}