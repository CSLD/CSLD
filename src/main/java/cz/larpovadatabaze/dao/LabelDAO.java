package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 *
 */
@Repository
public class LabelDAO extends GenericHibernateDAO<Label, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Label>(Label.class);
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param labelName Expected format is {Name} Name is unique identifier of label.
     * @return It should return only single label or no label if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Label> getByAutoCompletable(String labelName) throws WrongParameterException {
        Criteria uniqueLabel = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession()).add(
                Restrictions.eq("name", labelName)
        );
        return uniqueLabel.list();
    }

    public List<Label> findAll(List<Locale> languages) {
        Criteria allLabels = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        addFilterByLanguage(allLabels, languages);
        return allLabels.list();
    }

    public List<Label> getRequired(List<Locale> languagesForUser) {
        Criteria requiredLabels = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
            requiredLabels.add(Restrictions.eq("required", true));
        addFilterByLanguage(requiredLabels, languagesForUser);
        return requiredLabels.list();
    }

    public List<Label> getOptional(List<Locale> languagesForUser) {
        Criteria requiredLabels = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        requiredLabels.add(Restrictions.eq("required", false));
        addFilterByLanguage(requiredLabels, languagesForUser);
        return requiredLabels.list();
    }

    public Label getByName(String name) {
        Criteria label = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        label
                .createCriteria("labelHasLanguages")
                .add(Restrictions.eq("name", name));

        return (Label) label.uniqueResult();
    }

    private void addFilterByLanguage(Criteria criteria, List<Locale> languages) {
        criteria
                .createCriteria("labelHasLanguages")
                .createCriteria("language")
                .add(Restrictions.in("language", new HashSet(languages)));
    }
}
