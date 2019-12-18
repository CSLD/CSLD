package cz.larpovadatabaze;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.impl.LocalFiles;
import cz.larpovadatabaze.services.s3.S3Bucket;
import cz.larpovadatabaze.services.s3.S3Files;
import cz.larpovadatabaze.utils.MailClient;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration of Spring and its dependencies.
 */
@Configuration
@ComponentScan(basePackages = "cz.larpovadatabaze")
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class RootConfig {
    @Autowired
    private Environment env;

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        if(env.getProperty("csld.run_flyway", Boolean.class)) {
            return Flyway.configure()
                    .baselineOnMigrate(true)
                    .dataSource(dataSource())
                    .locations("classpath:db/")
                    .load();
        } else {
            return null;
        }
    }

    // Data store specification
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClass(env.getProperty("jdbc.driver"));
        if(env.getProperty("JDBC_DATABASE_URL") != null) {
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

    @Bean @DependsOn("flyway")
    public LocalSessionFactoryBean sessionFactory() {
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
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(env.getProperty("mail.from"));
        message.setSubject(env.getProperty("mail.subject"));

        return message;
    }

    @Bean
    public MailClient mailService() {
        MailClient mail = new MailClient();

        mail.setTemplateMessage(templateMessage());
        mail.setMailSender(mailSender());

        return mail;
    }
    // End of email settings

    public S3Client client() {
        return S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }

    @Bean
    public FileService fileService() {
        String typeOfFileService = env.getProperty("csld.data.source");
        if (typeOfFileService == null) {
            throw new RuntimeException("It is required to specify type of service. Property csld.data_source is missing.");
        }

        if (typeOfFileService.equals("local")) {
            return new LocalFiles(env.getProperty("csld.data.dir"));
        } else if (typeOfFileService.equals("s3")) {
            return new S3Files(
                    new S3Bucket(
                            client(),
                            env.getProperty("csld.data.s3.bucketName")
                    )
            );
        } else {
            throw new RuntimeException("Ilegal type of service. Only s3 and files are supported.");
        }
    }
}
