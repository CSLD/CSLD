package cz.larpovadatabaze.calendar.service.sql;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.GamesWithState;
import org.apache.wicket.model.IModel;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Database as a persistent storage.
 */
@Repository
@Transactional
public class SqlEvents extends CRUD<Event, Integer> implements Events {
    private GamesWithState games;

    @Autowired
    public SqlEvents(SessionFactory sessionFactory, GamesWithState games) {
        super(new GenericHibernateDAO<>(sessionFactory,
                new GenericBuilder<>(Event.class)));
        this.games = games;
    }

    @Override
    public boolean saveOrUpdate(Event entity) {
        entity.sanitize();
        return super.saveOrUpdate(entity);
    }

    @Override
    public List<Event> forWantedGames(CsldUser user, List<Event> events) {
        List<Event> filtered = new ArrayList<>();
        Collection<Game> wantsToPlay = games.getWantedByUser(user);

        for (Event event : events) {
            for (Game game : event.getGames()) {
                if (wantsToPlay.contains(game)) {
                    filtered.add(event);
                    break;
                }
            }
        }

        return filtered;
    }

    @Override
    public List<Event> inTheTimeFrame(Calendar from, Calendar to) {
        List<Event> all = getAll();
        return all.
                stream().
                filter(event -> isInTimeFrame(event, from, to))
                .collect(Collectors.toList());
    }

    private boolean isInGivenTimeFrame(Event event, FilterEvent filterEvent) {
        boolean endsBeforeLimit = true;
        boolean startsBeforeLimit = true;
        boolean startsAfterLimit = true;
        boolean isInGivenTimeFrame = true;
        if ((filterEvent.getFrom() != null) || (filterEvent.getTo() != null)) {
            if (filterEvent.getTo() != null) {
                endsBeforeLimit = event.getTo().getTime().before(filterEvent.getTo());
                startsBeforeLimit = event.getFrom().getTime().before(filterEvent.getTo());
            }
            if (filterEvent.getFrom() != null) {
                startsAfterLimit = event.getFrom().getTime().after(filterEvent.getFrom());
            }

            isInGivenTimeFrame = endsBeforeLimit && startsAfterLimit && startsBeforeLimit;
        }
        return isInGivenTimeFrame;
    }

    private void sortByTime(List<Event> filtered) {
        filtered.sort((o1, o2) -> {
            if (o1.getFrom() == null || o2.getFrom().before(o1.getFrom())) {
                return 1;
            } else if (o2.getFrom() == null || o1.getFrom().before(o2.getFrom())) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    private List<Event> limit(List<Event> filtered, FilterEvent filterEvent) {
        if (filterEvent.getLimit() != null && filtered.size() > filterEvent.getLimit()) {
            return new ArrayList<>(filtered).subList(0, filterEvent.getLimit());
        }
        return filtered;
    }

    @Override
    public List<Event> filtered(IModel<FilterEvent> filterCriteria) {
        Collection<Event> events = getAll();
        List<Event> filtered = new ArrayList<>();

        FilterEvent filterEvent = filterCriteria.getObject();
        for (Event event : events) {
            if (event.isDeleted()) {
                continue;
            }

            List<Label> labels = event.getLabels();

            boolean containsAllLabels = labels.containsAll(filterEvent.getRequiredLabels()) &&
                    labels.containsAll(filterEvent.getOtherLabels());
            boolean isInGivenTimeFrame = isInGivenTimeFrame(event, filterEvent);

            if (containsAllLabels && isInGivenTimeFrame) {
                filtered.add(event);
            }
        }

        sortByTime(filtered);
        return limit(filtered, filterEvent);
    }

    @Override
    public List<Event> byName(String name) {
        return crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("name", name))
                .list();
    }

    @Override
    public void removeAddedBy(CsldUser toRemove) {
        // Find all added by user
        List<Event> eventsAddedBy = crudRepository.findByCriteria(
                Restrictions.eq("addedBy", toRemove)
        );
        // Remove user, save
        eventsAddedBy.forEach(event -> {
            event.setAddedBy(null);
            saveOrUpdate(event);
        });
    }

    // Time related limitations.
    private boolean isInTimeFrame(Event event, Calendar from, Calendar to) {
        return fullyInTimeFrame(event, from, to) ||
                partiallyInTimeFrame(event, from, to) ||
                enclosingTheTimeFrame(event, from, to);
    }

    private boolean enclosingTheTimeFrame(Event event, Calendar from, Calendar to) {
        return event.getFrom().before(from) && event.getTo().after(to);
    }

    private boolean partiallyInTimeFrame(Event event, Calendar from, Calendar to) {
        return startingBeforeEndingIn(event, from, to) ||
                startingInEndingAfter(event, from, to);
    }

    private boolean startingBeforeEndingIn(Event event, Calendar from, Calendar to) {
        return event.getFrom().before(from) && event.getTo().before(to) && event.getTo().after(from);
    }

    private boolean startingInEndingAfter(Event event, Calendar from, Calendar to) {
        return event.getFrom().after(from) && event.getFrom().before(to) && event.getTo().after(to);
    }

    private boolean fullyInTimeFrame(Event event, Calendar from, Calendar to) {
        return event.getFrom().after(from) && event.getTo().before(to);
    }
}
