package cz.larpovadatabaze.calendar.service.events;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.models.FilterEvent;
import cz.larpovadatabaze.services.sql.CRUD;
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
    @Autowired
    public SqlEvents(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory,
                new GenericBuilder<>(Event.class)));
    }

    @Override
    public boolean saveOrUpdate(Event entity) {
        entity.sanitize();
        return super.saveOrUpdate(entity);
    }

    @Override
    public List<Event> forWantedGames(CsldUser user, List<Event> events) {
        Collection<Event> all = events;
        List<Event> filtered = new ArrayList<>();
        Collection<Game> wantsToPlay = new ArrayList<>();
        for (UserPlayedGame stateOfGame : user.getPlayedGames()) {
            if (stateOfGame.getStateEnum() == UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY) {
                wantsToPlay.add(stateOfGame.getGame());
            }
        }

        for (Event event : all) {
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

            boolean containsAllLabels = labels.containsAll(filterEvent.getRequiredLabels()) && labels.containsAll(filterEvent.getOtherLabels());
            boolean isInGivenTimeFrame = true;
            boolean endsBeforeLimit = true;
            boolean startsBeforeLimit = true;
            boolean startsAfterLimit = true;
            boolean isInRegion = true;

            // There is some time based filter specified.
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

            if (filterEvent.getRegion() != null && filterEvent.getFilter() != null && event.getLocation() != null) {
                if (!filterEvent.getFilter().isGeometryInArea(filterEvent.getRegion(), event.getLocation())) {
                    isInRegion = false;
                }
            }

            if (containsAllLabels && isInGivenTimeFrame && isInRegion) {
                filtered.add(event);
            }
        }

        filtered.sort((o1, o2) -> {
            if (o1.getFrom() == null || o2.getFrom().before(o1.getFrom())) {
                return 1;
            } else if (o2.getFrom() == null || o1.getFrom().before(o2.getFrom())) {
                return -1;
            } else {
                return 0;
            }
        });

        if (filterEvent.getLimit() != null && filtered.size() > filterEvent.getLimit()) {
            filtered = new ArrayList<>(filtered).subList(0, filterEvent.getLimit());
        }

        return filtered;
    }

    @Override
    public List<Event> geographicallyFiltered(BoundingBox limitation) {
        Collection<Event> loaded = getAll();
        return loaded
                .stream()
                .filter(event -> limitation.isInArea(event.getLocation()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> byName(String name) {
        return crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("name", name))
                .list();
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
