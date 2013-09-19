package cz.larpovadatabaze.utils;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailClient {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    public void sendMail(String content, String email) {
            SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
            message.setTo(email);
            message.setText(content);
            System.out.println("Sending message:\n" + message.getText() + "\n");
            try{
                this.mailSender.send(message);
            }
            catch(MailException e) {
                System.err.println(e.getMessage());
            }
    }
}