package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Labels;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Labels sotred in the SQL Store and related services.
 */
@Repository
@Transactional
public class SqlLabels extends CRUD<Label, Integer> implements Labels {
    public SqlLabels(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Label.class)));
    }

    public List<Label> getRequired() {
        return getByRequired(true);
    }

    public List<Label> getOptional() {
        return getByRequired(false);
    }

    private List<Label> getByRequired(boolean required) {
        return crudRepository.findByCriteria(
                Restrictions.eq("required", required)
        );
    }

    @Override
    public List<Label> getAuthorizedRequired(CsldUser authorizedTo) {
        return filterAuthorizedLabels(true, authorizedTo);
    }

    @Override
    public List<Label> getAuthorizedOptional(CsldUser authorizedTo) {
        return filterAuthorizedLabels(false, authorizedTo);
    }

    private List<Label> filterAuthorizedLabels(boolean required, CsldUser authorizedTo) {
        return crudRepository.findByCriteria(Restrictions.and(
                Restrictions.eq("required", required),
                Restrictions.or(
                        Restrictions.eq("authorized", true),
                        Restrictions.eq("addedBy.id", authorizedTo.getId())
                )
        ));
    }
}
