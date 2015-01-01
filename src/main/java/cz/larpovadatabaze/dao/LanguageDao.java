package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Language;

/**
 * Created by jbalhar on 12/21/2014.
 */
public class LanguageDao extends GenericHibernateDAO<Language, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Language>(Language.class);
    }
}
