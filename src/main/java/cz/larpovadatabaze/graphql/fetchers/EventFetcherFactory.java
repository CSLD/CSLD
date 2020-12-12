package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import org.apache.wicket.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventFetcherFactory {
    private final Events events;
    private final Games games;
    private final Labels labels;
    private final AppUsers appUsers;

    @Autowired
    public EventFetcherFactory(Events events, Games games, Labels labels, AppUsers appUsers) {
        this.events = events;
        this.games = games;
        this.labels = labels;
        this.appUsers = appUsers;
    }

    private List<Game> getGames(List<String> gameIds) {
        return gameIds.stream().map(id -> {
            Game game = games.getById(Integer.parseInt(id));
            if (game == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Game id " + id + " not found", "games");
            }
            return game;
        }).collect(Collectors.toList());
    }

    private Event applyInputValues(Event event, Map<String, Object> input) {
        event.setName((String)input.get("name"));

        event.setFrom(FetcherUtils.parseDateTime((String)input.get("fromDate"), "input.fromDate"));
        event.setTo(FetcherUtils.parseDateTime((String)input.get("toDate"), "input.toDate"));

        event.setAmountOfPlayers((Integer) input.get("amountOfPlayers"));
        event.setWeb((String)input.get("web"));
        event.setLoc((String)input.get("loc"));
        event.setDescription((String)input.get("description"));

        event.setGames(getGames((List<String>)input.get("games")));
        event.setLabels(FetcherUtils.getLabels(labels, (List<String>) input.get("labels"), (List<Map<String, Object>>) input.get("newLabels")));

        Double latitude = (Double)input.get("latitude");
        Double longitude = (Double)input.get("longitude");

        if (latitude != null && longitude != null) {
            event.setLocation(new Location(latitude, longitude));
        }
        else {
            event.setLocation(null);
        }

        return event;
    }

    public DataFetcher<List<Event>> createNextEventsFetcher() {
        return dataFetchingEnvironment -> {
            FilterEvent filter = new FilterEvent();
            filter.setFrom(Calendar.getInstance().getTime());
            filter.setLimit(6);
            filter.setSorted(FilterEvent.Sort.TIME_MOST_RECENT);

            return events.filtered(Model.of(filter));
        };
    }

    public DataFetcher<Event> createCreateEventFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            // TODO - check recaptcha - TODO

            Event event = new Event();
            event.setAddedBy(appUsers.getLoggedUser());
            event = applyInputValues(event, input);

            events.saveOrUpdate(event);

            return event;
        };
    }

    public DataFetcher<Event> createUpdateEventFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            Event event = events.getById(Integer.parseInt((String)input.get("id")));
            if (event == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Event does not exist");
            }

            if (!appUsers.isAtLeastEditor()) {
                CsldUser addedBy = event.getAddedBy();
                if (addedBy == null || !addedBy.getId().equals(appUsers.getLoggedUserId())) {
                    throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Must be editor or creator");
                }
            }

            event = applyInputValues(event, input);

            events.saveOrUpdate(event);

            return event;
        };
    }

    public DataFetcher<Event> createDeleteEventFetcher() {
        return dataFetchingEnvironment -> {
            String id = dataFetchingEnvironment.getArgument("id");
            Event event = events.getById(Integer.parseInt(id));
            if (event == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Event does not exist");
            }

            event.setDeleted(true);
            events.saveOrUpdate(event);

            return event;
        };
    }
}
