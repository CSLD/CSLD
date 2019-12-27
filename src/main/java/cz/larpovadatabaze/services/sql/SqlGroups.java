package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.CsldGroups;
import cz.larpovadatabaze.services.Images;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Groups represented in the SQL Data Store.
 */
@Repository
@Transactional
public class SqlGroups extends CRUD<CsldGroup, Integer> implements CsldGroups {
    private Images images;

    @Autowired
    public SqlGroups(SessionFactory sessionFactory, Images images) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(CsldGroup.class)));
        this.images = images;
    }

    private ResourceReference iconResourceReference;

    @Override
    public List<CsldGroup> getFirstChoices(String startsWith, int maxChoices) {
        Criteria firstChoices = crudRepository.getExecutableCriteria()
                .setMaxResults(maxChoices)
                .add(Restrictions.ilike("name", "%" + startsWith + "%"));

        return firstChoices.list();
    }

    @Override
    public List<CsldGroup> getByAutoCompletable(String groupName) {
        Criteria uniqueGroup = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("name", groupName));

        return uniqueGroup.list();
    }

    @Override
    public ResourceReference getIconReference() {
        synchronized(this) {
            if (iconResourceReference == null) {
                iconResourceReference = images.createImageTypeResourceReference(crudRepository);
            }
        }

        return iconResourceReference;
    }
}
