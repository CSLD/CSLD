package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.EmailAuthentication;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class EmailAuthenticationDAO extends GenericHibernateDAO<EmailAuthentication, Integer>{
}
