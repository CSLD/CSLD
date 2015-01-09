package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.LabelHasLanguages;

/**
 * Created by jbalhar on 1/9/2015.
 */
public class LabelHasLanguageDao extends GenericHibernateDAO<LabelHasLanguages, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<LabelHasLanguages>(LabelHasLanguages.class);
    }
}
