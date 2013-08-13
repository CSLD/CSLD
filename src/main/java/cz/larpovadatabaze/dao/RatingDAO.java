package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Rating;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class RatingDAO extends GenericHibernateDAO<Rating, Integer> {
}
