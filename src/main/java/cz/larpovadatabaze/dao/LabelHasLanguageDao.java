package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.LabelHasLanguages;
import org.springframework.stereotype.Repository;

/**
 * Created by jbalhar on 1/9/2015.
 */
@Repository
public class LabelHasLanguageDao extends GenericHibernateDAO<LabelHasLanguages, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<LabelHasLanguages>(LabelHasLanguages.class);
    }
}
