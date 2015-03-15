package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.GameHasLanguages;

/**
 * Created by jbalhar on 1/8/2015.
 */
public class GameHasLanguageDao extends GenericHibernateDAO<GameHasLanguages, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<GameHasLanguages>(GameHasLanguages.class);
    }
}
