package cz.larpovadatabaze.users.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.CsldGroups;
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
    public ResourceReference getIconReference() {
        synchronized(this) {
            if (iconResourceReference == null) {
                iconResourceReference = images.createImageTypeResourceReference(crudRepository);
            }
        }

        return iconResourceReference;
    }
}
