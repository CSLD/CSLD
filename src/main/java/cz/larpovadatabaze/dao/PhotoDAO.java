package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Photo;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 6.9.13
 * Time: 23:00
 */
@Repository
public class PhotoDAO extends GenericHibernateDAO<Photo, Integer> {
}
