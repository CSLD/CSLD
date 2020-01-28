package cz.larpovadatabaze;

import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.services.masqueradeStubs.InMemoryEvents;
import cz.larpovadatabaze.common.services.FilterService;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.common.services.builders.InMemoryMasqueradeBuilder;
import cz.larpovadatabaze.common.services.smtp.SmtpMailService;
import cz.larpovadatabaze.common.services.wicket.FilterServiceReflection;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.games.services.masqueradeStubs.*;
import cz.larpovadatabaze.search.services.TokenSearch;
import cz.larpovadatabaze.search.services.masqueradeStubs.InMemoryTokenSearch;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import cz.larpovadatabaze.users.services.masqueradeStubs.InMemoryCsldUsers;
import cz.larpovadatabaze.users.services.masqueradeStubs.InMemoryEmailAuthenticationTokens;
import cz.larpovadatabaze.users.services.masqueradeStubs.InMemoryGroups;
import cz.larpovadatabaze.users.services.wicket.WicketUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class RootTestWicketContext {
    @Autowired
    Environment env;

    @Bean
    public CsldUsers csldUsers() {
        return new InMemoryCsldUsers();
    }

    @Bean
    public Comments comments() {
        return new InMemoryComments();
    }

    @Bean
    public EmailAuthentications emailAuthentications() {
        return new InMemoryEmailAuthenticationTokens();
    }

    @Bean
    public Images images() {
        return new InMemoryFileImages();
    }

    @Bean
    public Photos photos() {
        return new InMemoryFilePhotos();
    }

    @Bean
    public Games games() {
        return new InMemoryGames();
    }

    @Bean
    public CsldGroups groups() {
        return new InMemoryGroups();
    }

    @Bean
    public Labels labels() {
        return new InMemoryLabels();
    }

    @Bean
    public Ratings ratings() {
        return new InMemoryRatings();
    }

    @Bean
    public Upvotes upvotes() {
        return new InMemoryUpvotes();
    }

    @Bean
    public Videos videos() {
        return new InMemoryVideos();
    }

    @Bean
    public Events events() {
        return new InMemoryEvents();
    }

    @Bean
    public AppUsers appUsers() {
        return new WicketUsers();
    }

    @Bean
    public TokenSearch tokenSearch() {
        return new InMemoryTokenSearch();
    }

    @Bean
    public SimilarGames similarGames() {
        return new InMemorySimilarGames();
    }

    @Bean
    public FilteredGames filteredGames() {
        return new InMemoryFilteredGames();
    }

    @Bean
    public AuthoredGames authoredGames() {
        return new InMemoryAuthoredGames();
    }

    @Bean
    public FilterService filterService() {
        return new FilterServiceReflection();
    }

    @Bean
    public GamesWithState gamesWithState() {
        return new InMemoryGamesWithState();
    }

    @Bean(initMethod = "build")
    public InMemoryMasqueradeBuilder builder() {
        return new InMemoryMasqueradeBuilder(comments(), csldUsers(), games(), similarGames(),
                groups(), labels(), ratings(), upvotes());
    }

    @Bean
    public Csld csld() {
        return new Csld(tokenSearch(), csldUsers(), null, env, events());
    }

    // Start of email settings
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(env.getProperty("mail.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.port")));
        mailSender.setUsername(env.getProperty("mail.username"));
        mailSender.setPassword(env.getProperty("mail.password"));
        mailSender.setJavaMailProperties(javaMailProperties());

        return mailSender;
    }

    private Properties javaMailProperties() {
        Properties mailProperties = new Properties();

        mailProperties.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        mailProperties.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));

        return mailProperties;
    }

    @Bean
    public MailService mailService() {
        return new SmtpMailService(
                mailSender(),
                appUsers(),
                env.getProperty("mail.from")
        );
    }
    // End of email settings
}
