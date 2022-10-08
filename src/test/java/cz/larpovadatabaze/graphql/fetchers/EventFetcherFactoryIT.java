package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import cz.larpovadatabaze.calendar.services.masqueradeStubs.InMemoryEvents;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.games.services.masquerade.InMemoryGames;
import cz.larpovadatabaze.games.services.masquerade.InMemoryLabels;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class EventFetcherFactoryIT {
    private static GoogleCalendarEvents mockGoogleCalendarEvents;

    @BeforeAll
    public static void beforeClass() throws Exception {
        mockGoogleCalendarEvents = mock(GoogleCalendarEvents.class);
    }

    private Event createEvent() {
        return new Event(1, "Test", new GregorianCalendar(), new GregorianCalendar(), 5, "Praha", "Test event", "https://www.centrum.cz", "test");
    }

    private Map<String, Object> createInput() {
        Map<String, Object> input = new HashMap<>();

        input.put("id", "1");
        input.put("name", "newName");
        input.put("fromDate", "2010-08-17T14:45:00");
        input.put("toDate", "2010-09-01T00:00:00");
        input.put("amountOfPlayers", 123);
        input.put("web", "https://larpovadatabaze.cz");
        input.put("loc", "Sever");
        input.put("description", "Popis");
        input.put("games", Arrays.asList(new String[] { "2" }));
        input.put("labels", Arrays.asList(new String[] { "3" }));
        input.put("newLabels", Collections.emptyList());
        input.put("latitude", 50.123);
        input.put("longitude", 15.932);

        return input;
    }

    @Test
    public void updateEventAsEditor() throws Exception {
        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(true);

        Event event = createEvent();
        event.setId(1);
        Events events = new InMemoryEvents();
        events.saveOrUpdate(event);

        Games games = new InMemoryGames();
        Game game = new Game();
        game.setId(2);
        games.saveOrUpdate(game);

        Labels labels = new InMemoryLabels();
        Label label = new Label();
        label.setId(3);
        labels.saveOrUpdate(label);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", createInput());

        EventFetcherFactory factory = new EventFetcherFactory(events, games, labels, mockAppUsers, mockGoogleCalendarEvents);
        DataFetcher<Event> dataFetcher = factory.createUpdateEventFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        Event updatedEvent = dataFetcher.get(dataFetchingEnvironment);
        assertThat(updatedEvent, notNullValue());

        assertThat(updatedEvent.getName(), equalTo("newName"));
        assertThat(FetcherUtils.formatDateTime((GregorianCalendar)updatedEvent.getFrom()), equalTo("2010-08-17T14:45:00"));
        assertThat(FetcherUtils.formatDateTime((GregorianCalendar)updatedEvent.getTo()), equalTo("2010-09-01T00:00:00"));
        assertThat(updatedEvent.getAmountOfPlayers(), equalTo(123));
        assertThat(updatedEvent.getWeb(), equalTo("https://larpovadatabaze.cz"));
        assertThat(updatedEvent.getLoc(), equalTo("Sever"));
        assertThat(updatedEvent.getDescription(), equalTo("Popis"));
        List<Game> updatedGames = updatedEvent.getGames();
        assertThat(updatedGames.size(), equalTo(1));
        assertThat(updatedGames.get(0).getId(), equalTo(2));
        List<Label> updatedLabels = updatedEvent.getLabels();
        assertThat(updatedLabels.size(), equalTo(1));
        assertThat(updatedLabels.get(0).getId(), equalTo(3));
        assertThat(updatedEvent.getLocation().getLatitude(), equalTo(50.123));
        assertThat(updatedEvent.getLocation().getLongitude(), equalTo(15.932));
    }

    @Test
    public void updateEventAsCreator() throws Exception {
        CsldUser creator = new CsldUser();
        creator.setId(123);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(creator);
        when(mockAppUsers.getLoggedUserId()).thenReturn(creator.getId());

        Event event = createEvent();
        event.setId(1);
        event.setAddedBy(creator);
        Events events = new InMemoryEvents();
        events.saveOrUpdate(event);

        Games games = new InMemoryGames();
        Labels labels = new InMemoryLabels();

        Map<String, Object> arguments = new HashMap<>();
        Map<String, Object> input = createInput();
        input.put("games", Collections.emptyList());
        input.put("labels", Collections.emptyList());
        arguments.put("input", input);

        EventFetcherFactory factory = new EventFetcherFactory(events, games, labels, mockAppUsers, mockGoogleCalendarEvents);
        DataFetcher<Event> dataFetcher = factory.createUpdateEventFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        Event updatedEvent = dataFetcher.get(dataFetchingEnvironment);
        assertThat(updatedEvent, notNullValue());
        assertThat(updatedEvent.getId(), equalTo(1));
    }

    @Test
    public void updateEventAsOtherUser() throws Exception {
        CsldUser creator = new CsldUser();
        creator.setId(123);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(creator);
        when(mockAppUsers.getLoggedUserId()).thenReturn(creator.getId());

        Event event = createEvent();
        event.setId(1);
        Events events = new InMemoryEvents();
        events.saveOrUpdate(event);

        Games games = new InMemoryGames();
        Labels labels = new InMemoryLabels();

        Map<String, Object> arguments = new HashMap<>();
        Map<String, Object> input = createInput();
        input.put("games", Collections.emptyList());
        input.put("labels", Collections.emptyList());
        arguments.put("input", input);

        EventFetcherFactory factory = new EventFetcherFactory(events, games, labels, mockAppUsers, mockGoogleCalendarEvents);
        DataFetcher<Event> dataFetcher = factory.createUpdateEventFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        try {
            dataFetcher.get(dataFetchingEnvironment);
            // Should throw
            assertThat(1, equalTo(2));
        }
        catch(Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.ACCESS_DENIED));
        }
    }
}
