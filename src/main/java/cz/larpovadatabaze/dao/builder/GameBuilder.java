package cz.larpovadatabaze.dao.builder;

import cz.larpovadatabaze.entities.Game;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 *
 */
public class GameBuilder {
    Criteria basicCriteria;

    public GameBuilder(Session session) {
        this.basicCriteria = session.createCriteria(Game.class, "game");
    }

    public Criteria build(){
        return basicCriteria;
    }                                      }
