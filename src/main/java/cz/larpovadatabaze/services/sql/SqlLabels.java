package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.services.Labels;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        return crudRepository
                .getExecutableCriteria()
                .add(Restrictions.eq("required", required))
                .list();
    }

    @Override
    public List<Label> getAuthorizedRequired(CsldUser authorizedTo) {
        return filterAuthorizedLabels(getRequired(), authorizedTo);
    }

    @Override
    public List<Label> getAuthorizedOptional(CsldUser authorizedTo) {
        return filterAuthorizedLabels(getOptional(), authorizedTo);
    }

    private List<Label> filterAuthorizedLabels(List<Label> labelsToFilter, CsldUser authorizedTo) {
        List<Label> labels = new ArrayList<>();
        for (Label label : labelsToFilter) {
            if (label.getAuthorized() != null && !label.getAuthorized()) {
                if (!label.getAddedBy().equals(authorizedTo)) {
                    continue;
                }
            }

            labels.add(label);
        }
        return labels;
    }

    @Override
    public List<Label> getByAutoCompletable(String labelName) {
        return crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("name", labelName))
                .list();
    }
}
