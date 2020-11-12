package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.graphql.fetchers.CalendarFetcher;
import cz.larpovadatabaze.graphql.fetchers.CommentAsTextFetcher;
import cz.larpovadatabaze.graphql.fetchers.CommentFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.EventFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameCommentsPagedFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameRatingStatsFetcher;
import cz.larpovadatabaze.graphql.fetchers.GameSearchFetcher;
import cz.larpovadatabaze.graphql.fetchers.GameWantsToPlayFetcher;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

/**
 * Configures types & fetchers for graphql
 */
@Component
public class GraphQLTypeConfigurator {
    private GameFetcherFactory gameFetcherFactory;
    private GameSearchFetcher gameSearchFetcher;
    private CommentFetcherFactory commentFetcherFactory;
    private EventFetcherFactory eventFetcherFactory;
    private CalendarFetcher calendarFetcher;
    private GameCommentsPagedFetcherFactory gameCommentsPagedFetcherFactory;

    @Autowired
    public GraphQLTypeConfigurator(GameFetcherFactory gameFetcherFactory, CommentFetcherFactory commentFetcherFactory, EventFetcherFactory eventFetcherFactory, CalendarFetcher calendarFetcher, GameSearchFetcher gameSearchFetcher, GameCommentsPagedFetcherFactory gameCommentsPagedFetcherFactory) {
        this.gameFetcherFactory = gameFetcherFactory;
        this.commentFetcherFactory = commentFetcherFactory;
        this.eventFetcherFactory = eventFetcherFactory;
        this.calendarFetcher = calendarFetcher;
        this.gameSearchFetcher = gameSearchFetcher;
        this.gameCommentsPagedFetcherFactory = gameCommentsPagedFetcherFactory;
    }

    public RuntimeWiring configureTypes() {
        return newRuntimeWiring()
                // Query
                .type("Query", builder -> builder
                        .dataFetcher("homepage", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("gameById", gameFetcherFactory.createGameByIdFetcher())
                        .dataFetcher("gamesBySearchTerm", gameSearchFetcher)
                )
                // Homepage
                .type("Homepage", builder -> builder
                        .dataFetcher("lastAddedGames", gameFetcherFactory.createLastAddedGamesFetcher())
                        .dataFetcher("mostPopularGames", gameFetcherFactory.createMostPopularGamesFetcher())
                        .dataFetcher("lastComments", commentFetcherFactory.createLastAddedCommentsFetcher())
                        .dataFetcher("nextEvents", eventFetcherFactory.createNextEventsFetcher())
                )
                .type("Event", builder -> builder
                        .dataFetcher("from", this.calendarFetcher)
                        .dataFetcher("to", this.calendarFetcher)
                )
                .type("Game", builder -> builder
                        .dataFetcher("ratingStats", new GameRatingStatsFetcher())
                        .dataFetcher("wantsToPlay", new GameWantsToPlayFetcher())
                        .dataFetcher("similarGames", gameFetcherFactory.createGameSimilarGamesFetcher())
                        .dataFetcher("gamesOfAuthors", gameFetcherFactory.createGameGamesOfAuthorsFetcher())
                        .dataFetcher("commentsPaged", gameCommentsPagedFetcherFactory.createCommentsPagedFetcher())
                )
                .type("Comment", builder -> builder.dataFetcher("commentAsText", new CommentAsTextFetcher()))
                // Finish
                .build();

    }
}
