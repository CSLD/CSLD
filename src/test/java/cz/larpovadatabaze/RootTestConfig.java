package cz.larpovadatabaze;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.impl.LocalFiles;
import cz.larpovadatabaze.utils.MailClient;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
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
@ComponentScan(basePackages = "cz.larpovadatabaze")
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class RootTestConfig {}
