package cz.larpovadatabaze;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.common.services.smtp.SmtpMailService;
import cz.larpovadatabaze.common.services.wicket.LocalFiles;
import cz.larpovadatabaze.users.services.AppUsers;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration of Spring and its dependencies.
 */
@Configuration
@ComponentScan(basePackages = {
        "cz.larpovadatabaze.administration.services",
        "cz.larpovadatabaze.calendar.service",
        "cz.larpovadatabaze.common.services",
        "cz.larpovadatabaze.games.services",
        "cz.larpovadatabaze.search.services",
        "cz.larpovadatabaze.users.services",

        "cz.larpovadatabaze.common.dao"})
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class RootTestWithDbConfig {
    @Autowired
    private Environment env;

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .dataSource(dataSource())
                .locations("classpath:db/")
                .load();
    }

    // Data store specification
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClass(env.getProperty("jdbc.driver"));
        if (env.getProperty("JDBC_DATABASE_URL") != null) {
            dataSource.setJdbcUrl(env.getProperty("JDBC_DATABASE_URL"));
            dataSource.setUser(env.getProperty("JDBC_DATABASE_USERNAME"));
            dataSource.setPassword(env.getProperty("JDBC_DATABASE_PASSWORD"));
        } else {
            dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
            dataSource.setUser(env.getProperty("jdbc.user"));
            dataSource.setPassword(env.getProperty("jdbc.password"));
        }

        return dataSource;
    }

    @Bean
    @DependsOn("flyway")
    public LocalSessionFactoryBean localSessionFactoryBean() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();

        factory.setDataSource(dataSource());
        factory.setPackagesToScan(env.getProperty("jpa.packages_to_scan"));
        factory.setHibernateProperties(hibernateProperties());

        return factory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("autocommit", "true");
        properties.put("hibernate.globally_quoted_identifiers", "true");
        properties.put("hibernate.cache.provider_class", env.getProperty("hibernate.cache.provider_class"));
        properties.put("javax.persistence.sharedCache.mode", env.getProperty("javax.persistence.sharedCache.mode"));
        properties.put("hibernate.cache.default_cache_concurrency_strategy", env.getProperty("hibernate.cache.default_cache_concurrency_strategy"));
        properties.put("hibernate.default_schema", env.getProperty("hibernate.default_schema"));

        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
    // End of data store specification


    @Bean
    public AppUsers appUsers() {
        return Mockito.mock(AppUsers.class);
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

    @Bean
    public FileService fileService() {
        return new LocalFiles(env.getProperty("csld.data.dir"));
    }
}
