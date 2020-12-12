package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.games.services.Videos;
import cz.larpovadatabaze.games.services.masquerade.InMemoryComments;
import cz.larpovadatabaze.games.services.masquerade.InMemoryGames;
import cz.larpovadatabaze.games.services.masquerade.InMemoryLabels;
import cz.larpovadatabaze.games.services.masquerade.InMemoryRatings;
import cz.larpovadatabaze.games.services.masquerade.InMemoryUpvotes;
import cz.larpovadatabaze.games.services.masquerade.InMemoryVideos;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.masquerade.InMemoryCsldUsers;
import cz.larpovadatabaze.users.services.masquerade.InMemoryGroups;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameMutationFetcherFactoryIT {
    @Test
    public void createCreateGame() throws Exception {
        Map<String, Object> input = new HashMap<>();

        input.put("name", "Name");
        input.put("description", "Description");
        input.put("year", 1990);
        input.put("players", 20);
        input.put("menRole", 4);
        input.put("womenRole", 6);
        input.put("bothRole", 10);
        input.put("hours", 7);
        input.put("days", 8);
        input.put("web", "https://www.centrum.cz");
        input.put("photoAuthor", "Photographer");
        input.put("galleryURL", "https://photo.centrum.cz");
        input.put("video", "https://youtu.be");
        input.put("ratingsDisabled", true);
        input.put("commentsDisabled", true);

        input.put("labels", Arrays.asList(new String[] { "1" }));

        List<Map<String, Object>> newLabels = new ArrayList<>();

        Map<String, Object> newLabel2 = new HashMap<>();
        newLabel2.put("name", "existingLabel");
        newLabel2.put("description", "existingLabelDescription");
        newLabels.add(newLabel2);

        Map<String, Object> newLabel = new HashMap<>();
        newLabel.put("name", "newLabel");
        newLabel.put("description", "newLabelDescription");
        newLabels.add(newLabel);

        input.put("newLabels", newLabels);

        input.put("authors", Arrays.asList(new String[] { "2" }));

        List<Map<String, Object>> newAuthors = new ArrayList<>();
        Map<String, Object> newAuthor2 = new HashMap<>();
        newAuthor2.put("email", "petr@stary.cz");
        newAuthor2.put("name", "Petr Stary");
        newAuthor2.put("nickname", "Petan");
        newAuthors.add(newAuthor2);
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("email", "petr@novy.cz");
        newAuthor.put("name", "Petr Novy");
        newAuthor.put("nickname", "Peta");
        newAuthors.add(newAuthor);
        input.put("newAuthors", newAuthors);

        input.put("groupAuthors", Arrays.asList(new String[] { "3" }));

        List<Map<String, Object>> newGroups = new ArrayList<>();
        Map<String, Object> newGroup2 = new HashMap<>();
        newGroup2.put("name", "Stara");
        newGroups.add(newGroup2);

        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("name", "Nova");
        newGroups.add(newGroup);

        input.put("newGroupAuthors", newGroups);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", input);

        Games games = new InMemoryGames();
        Videos videos = new InMemoryVideos();
        Labels labels = new InMemoryLabels();

        Label label1 = new Label();
        label1.setId(1);
        label1.setName("OldLabel");
        labels.saveOrUpdate(label1);

        Label label2 = new Label();
        label2.setId(11);
        label2.setName("existingLabel");
        label2.setDescription("Existing");
        labels.saveOrUpdate(label2);

        CsldGroups groups = new InMemoryGroups();

        CsldGroup group1 = new CsldGroup();
        group1.setId(3);
        group1.setName("ex");
        groups.saveOrUpdate(group1);

        CsldGroup group2 = new CsldGroup();
        group2.setId(13);
        group2.setName("Stara");
        groups.saveOrUpdate(group2);

        CsldUsers users = new InMemoryCsldUsers();

        CsldUser user1 = new CsldUser();
        user1.setId(2);
        user1.setPerson(new Person());
        user1.getPerson().setEmail("existing@stary.cz");
        users.saveOrUpdate(user1);

        CsldUser user2 = new CsldUser();
        user2.setId(12);
        user2.setPerson(new Person());
        user2.getPerson().setEmail("petr@stary.cz");
        users.saveOrUpdate(user2);

        AppUsers mockAppUsers = mock(AppUsers.class);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, videos, labels, users, groups, mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createCreateGameFetcher();
        Game created = fetcher.get(dataFetchingEnvironment);

        assertThat(created.getName(), equalTo("Name"));
        assertThat(created.getDescription(), equalTo("Description"));
        assertThat(created.getYear(), equalTo(1990));
        assertThat(created.getPlayers(), equalTo(20));
        assertThat(created.getMenRole(), equalTo(4));
        assertThat(created.getWomenRole(), equalTo(6));
        assertThat(created.getBothRole(), equalTo(10));
        assertThat(created.getHours(), equalTo(7));
        assertThat(created.getDays(), equalTo(8));
        assertThat(created.getWeb(), equalTo("https://www.centrum.cz"));
        assertThat(created.getPhotoAuthor(), equalTo("Photographer"));
        assertThat(created.getGalleryURL(), equalTo("https://photo.centrum.cz"));
        assertThat(created.getVideo(), notNullValue());
        assertThat(created.getVideo().getPath(), equalTo("https://youtu.be"));
        assertThat(created.isRatingsDisabled(), equalTo(true));
        assertThat(created.isCommentsDisabled(), equalTo(true));

        List<CsldUser> authors = created.getAuthors();
        assertThat(authors.size(), equalTo(3));
        assertThat(authors.get(0).getId(), equalTo(2));
        assertThat(authors.get(1).getId(), equalTo(12));
        assertThat(authors.get(2).getPerson().getEmail(), equalTo("petr@novy.cz"));

        List<Label> nLabels = created.getLabels();
        assertThat(nLabels.size(), equalTo(3));
        assertThat(nLabels.get(0).getId(), equalTo(1));
        assertThat(nLabels.get(1).getId(), equalTo(11));
        assertThat(nLabels.get(2).getName(), equalTo("newLabel"));

        List<CsldGroup> nGroups = created.getGroupAuthor();
        assertThat(nGroups.size(), equalTo(3));
        assertThat(nGroups.get(0).getId(), equalTo(3));
        assertThat(nGroups.get(1).getId(), equalTo(13));
        assertThat(nGroups.get(2).getName(), equalTo("Nova"));
    }

    @Test
    public void deleteGameAsAdmin() throws Exception {
        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(true);

        InMemoryGames games = new InMemoryGames();
        Game game = new Game();
        game.setId(1);
        games.saveOrUpdate(game);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, new InMemoryVideos(), new InMemoryLabels(), new InMemoryCsldUsers(), new InMemoryGroups(), mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", "1");

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createDeleteGameFetcher();
        Game updated = fetcher.get(dataFetchingEnvironment);

        assertThat(updated.getId(), equalTo(1));
    }

    @Test
    public void deleteGameAsAuthor() throws Exception {
        CsldUser author = new CsldUser();
        author.setPerson(new Person());
        author.setId(2);

        CsldUser creator = new CsldUser();
        creator.setPerson(new Person());
        creator.setId(3);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(author);
        when(mockAppUsers.getLoggedUserId()).thenReturn(author.getId());

        InMemoryGames games = new InMemoryGames();
        Game game = new Game();
        game.setId(1);
        game.setAuthors(Arrays.asList(new CsldUser[]{ author }));
        game.setAddedBy(creator);
        games.saveOrUpdate(game);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, new InMemoryVideos(), new InMemoryLabels(), new InMemoryCsldUsers(), new InMemoryGroups(), mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", "1");

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createDeleteGameFetcher();
        Game updated = fetcher.get(dataFetchingEnvironment);

        assertThat(updated.getId(), equalTo(1));
    }

    @Test
    public void deleteGameAsCreator() throws Exception {
        CsldUser creator = new CsldUser();
        creator.setPerson(new Person());
        creator.setId(3);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(creator);
        when(mockAppUsers.getLoggedUserId()).thenReturn(creator.getId());

        InMemoryGames games = new InMemoryGames();
        Game game = new Game();
        game.setId(1);
        game.setAuthors(Collections.emptyList());
        game.setAddedBy(creator);
        games.saveOrUpdate(game);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, new InMemoryVideos(), new InMemoryLabels(), new InMemoryCsldUsers(), new InMemoryGroups(), mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", "1");

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createDeleteGameFetcher();
        Game updated = fetcher.get(dataFetchingEnvironment);

        assertThat(updated.getId(), equalTo(1));
    }

    @Test
    public void deleteGameAsOtherUser() {
        CsldUser author = new CsldUser();
        author.setPerson(new Person());
        author.setId(2);

        CsldUser creator = new CsldUser();
        creator.setPerson(new Person());
        creator.setId(3);

        CsldUser loggedIn = new CsldUser();
        loggedIn.setPerson(new Person());
        loggedIn.setId(4);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(true);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(loggedIn);
        when(mockAppUsers.getLoggedUserId()).thenReturn(loggedIn.getId());

        InMemoryGames games = new InMemoryGames();
        Game game = new Game();
        game.setId(1);
        game.setAuthors(Arrays.asList(new CsldUser[]{ author }));
        game.setAddedBy(creator);
        games.saveOrUpdate(game);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, new InMemoryVideos(), new InMemoryLabels(), new InMemoryCsldUsers(), new InMemoryGroups(), mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", "1");

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createDeleteGameFetcher();

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        }
        catch(Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.ACCESS_DENIED));
        }
    }

    @Test
    public void deleteGameAsAnonymous() {
        CsldUser author = new CsldUser();
        author.setPerson(new Person());
        author.setId(2);

        CsldUser creator = new CsldUser();
        creator.setPerson(new Person());
        creator.setId(3);

        AppUsers mockAppUsers = mock(AppUsers.class);
        when(mockAppUsers.isSignedIn()).thenReturn(false);
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);
        when(mockAppUsers.getLoggedUser()).thenReturn(null);
        when(mockAppUsers.getLoggedUserId()).thenReturn(null);

        InMemoryGames games = new InMemoryGames();
        Game game = new Game();
        game.setId(1);
        game.setAuthors(Arrays.asList(new CsldUser[]{ author }));
        game.setAddedBy(creator);
        games.saveOrUpdate(game);
        GameMutationFetcherFactory factory = new GameMutationFetcherFactory(games, new InMemoryVideos(), new InMemoryLabels(), new InMemoryCsldUsers(), new InMemoryGroups(), mockAppUsers, new InMemoryRatings(), new InMemoryComments(), new InMemoryUpvotes());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", "1");

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        DataFetcher<Game> fetcher = factory.createDeleteGameFetcher();

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        }
        catch(Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.ACCESS_DENIED));
        }
    }
}
