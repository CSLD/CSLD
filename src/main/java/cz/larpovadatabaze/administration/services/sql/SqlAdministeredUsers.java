package cz.larpovadatabaze.administration.services.sql;

import cz.larpovadatabaze.administration.model.UserRatesOwnGameDto;
import cz.larpovadatabaze.administration.services.AdministeredUsers;
import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Rating;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SqlAdministeredUsers implements AdministeredUsers {
    private GenericHibernateDAO<Rating, Integer> ratings;

    public SqlAdministeredUsers(SessionFactory sessionFactory) {
        ratings = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Rating.class));
    }

    @Override
    public List<UserRatesOwnGameDto> getUsersWhoRatesOwnGames() {
        List<Rating> ratedByAuthors = ratings.findByCriteria(
                Restrictions.eq("by_author", true)
        );

        return ratedByAuthors.stream()
                .map(rating -> new UserRatesOwnGameDto(rating.getGame(), rating.getUser()))
                .collect(Collectors.toList());
    }
}
