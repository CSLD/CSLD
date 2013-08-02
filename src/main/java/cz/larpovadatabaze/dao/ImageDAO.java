package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Image;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 19:53
 */
@Repository
public class ImageDAO extends GenericHibernateDAO<Image, Integer> {
}
