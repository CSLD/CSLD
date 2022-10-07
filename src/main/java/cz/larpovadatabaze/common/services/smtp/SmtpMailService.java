package cz.larpovadatabaze.common.services.smtp;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.models.SelectedUser;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

public class SmtpMailService implements MailService {
    private static Logger logger = LogManager.getLogger();;

    private MailSender mailSender;
    private AppUsers appUsers;
    private String emailToSendFrom;

    public SmtpMailService(MailSender mailSender, AppUsers appUsers, String emailToSendFrom) {
        this.mailSender = mailSender;
        this.appUsers = appUsers;
        this.emailToSendFrom = emailToSendFrom;
    }

    @Override
    public void sendInfoToInterestedUsers(List<SelectedUser> recipients,
                                          String nameOfTheGame,
                                          String contentTemplate,
                                          String subjectTemplate) {
        String[] recipientsAddresses = recipients.stream()
                .map(SelectedUser::getEmail)
                .toArray(String[]::new);
        String subject = String.format(subjectTemplate, nameOfTheGame);
        String content = String.format(contentTemplate, nameOfTheGame);

        sendEmailMessage(
                prepareEmailToMultiple(recipientsAddresses, subject, content)
        );
    }

    @Override
    public void sendInfoToAuthors(Game currentGame,
                                  String contentTemplate,
                                  String subjectTemplate) {
        Person loggedPerson = appUsers.getLoggedUser().getPerson();

        String[] recipientsAddresses = recipientAddreses(currentGame.getAuthors());
        String subject = String.format(subjectTemplate, loggedPerson.getName(), currentGame.getName());
        String content = String.format(contentTemplate, loggedPerson.getEmail(), currentGame.getName());

        sendEmailMessage(
                prepareEmailToMultiple(recipientsAddresses, subject, content)
        );
    }

    @Override
    public void sendInfoAboutNewEventToAllInterested(List<CsldUser> interested, String subject, String content) {
        String[] recipientsAddresses = recipientAddreses(interested);

        sendEmailMessage(
                prepareEmailToMultiple(recipientsAddresses, subject, content)
        );
    }

    @Override
    public void sendForgottenPassword(CsldUser recipient, String subject, String content) {
        sendEmailMessage(
                prepareEmailToOne(recipient.getPerson().getEmail(), subject, content)
        );
    }

    private String[] recipientAddreses(List<CsldUser> recipients) {
        return recipients.stream()
                .map(CsldUser::getPerson)
                .map(Person::getEmail)
                .toArray(String[]::new);
    }

    private SimpleMailMessage prepareEmailToOne(String recipientsEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailToSendFrom);
        message.setTo(recipientsEmail);
        message.setText(content);
        message.setSubject(subject);
        return message;
    }

    private SimpleMailMessage prepareEmailToMultiple(String[] emailsOfRecipients, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailToSendFrom);
        message.setTo(emailToSendFrom);
        message.setBcc(emailsOfRecipients);
        message.setText(content);
        message.setSubject(subject);

        return message;
    }

    private void sendEmailMessage(SimpleMailMessage toSend) {
        logger.debug("Sending message:\n" + toSend.getText() + "\n");
        try {
            mailSender.send(toSend);
        } catch (MailException e) {
            logger.error(e.getMessage());
        }
    }
}
