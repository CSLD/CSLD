package cz.larpovadatabaze.dao.builder;

import cz.larpovadatabaze.entities.Game;
import org.hibernate.criterion.DetachedCriteria;

public class GameBuilder implements IBuilder {
    DetachedCriteria baseCriteria;

    public GameBuilder() {
        this.baseCriteria = DetachedCriteria.forClass(Game.class, "game");
    }

    public DetachedCriteria build(){
        return baseCriteria;
    }                                      }
