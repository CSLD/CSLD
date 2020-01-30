package cz.larpovadatabaze.search.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.TokenSearch;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SqlTokenSearch implements TokenSearch {
    private GenericHibernateDAO<Label, Integer> labels;
    private GenericHibernateDAO<Game, Integer> games;
    private GenericHibernateDAO<CsldUser, Integer> users;
    private GenericHibernateDAO<CsldGroup, Integer> groups;

    @Autowired
    public SqlTokenSearch(SessionFactory sessionFactory, AppUsers appUsers) {
        labels = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Label.class));
        games = new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers));
        users = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(CsldUser.class));
        groups = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(CsldGroup.class));
    }

    @Override
    public List<Label> findLabels(String token) {
        return labels.findByCriteria(
                Restrictions.ilike("name", "%" + token + "%")
        );
    }

    @Override
    public List<Game> findGames(String token) {
        return games.findByCriteria(
                Restrictions.eq("name", token)
        );
    }

    @Override
    public List<CsldUser> findUsers(String token) {
        String[] personData = token.split(", ");
        if (personData.length < 2) {
            throw new RuntimeException("The person data needs to be in format {name,email}");
        }
        String email = personData[1];
        return users.findByCriteria(
                Restrictions.eq("person.email", email)
        );
    }

    @Override
    public List<CsldGroup> findGroups(String token) {
        return groups.findByCriteria(
                Restrictions.eq("name", token)
        );
    }
}
