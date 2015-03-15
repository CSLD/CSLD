package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Language;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.Locale;

/**
 * Created by jbalhar on 12/21/2014.
 */
public class LanguageDao extends GenericHibernateDAO<Language, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Language>(Language.class);
    }

    public Language findByLocale(Locale language) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession()).
                add(Restrictions.eq("language", language));
        return (Language) crit.uniqueResult();
    }
}
