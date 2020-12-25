package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.graphql.fetchers.AdminMutationFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.AdminQueryFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.AdminSectionCheckedFetcher;
import cz.larpovadatabaze.graphql.fetchers.CalendarFetcher;
import cz.larpovadatabaze.graphql.fetchers.CommentAsTextFetcher;
import cz.larpovadatabaze.graphql.fetchers.CommentFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.ConfigFetcher;
import cz.larpovadatabaze.graphql.fetchers.DonationsFetcher;
import cz.larpovadatabaze.graphql.fetchers.EventFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameCommentsPagedFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameMutationFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameRatingStatsFetcher;
import cz.larpovadatabaze.graphql.fetchers.GameRatingsProtectedFetcher;
import cz.larpovadatabaze.graphql.fetchers.GameSearchFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.GameWantsToPlayFetcher;
import cz.larpovadatabaze.graphql.fetchers.GroupFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.LabelFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.RatingFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.UserFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.UserMutationFetcherFactory;
import cz.larpovadatabaze.graphql.fetchers.UserRoleFetcher;
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
    private final GameFetcherFactory gameFetcherFactory;
    private final GameSearchFetcherFactory gameSearchFetcherFactory;
    private final CommentFetcherFactory commentFetcherFactory;
    private final EventFetcherFactory eventFetcherFactory;
    private final CalendarFetcher calendarFetcher;
    private final GameCommentsPagedFetcherFactory gameCommentsPagedFetcherFactory;
    private final UserMutationFetcherFactory userMutationFetcherFactory;
    private final GameMutationFetcherFactory gameMutationFetcherFactory;
    private final UserFetcherFactory userFetcherFactory;
    private final AdminSectionCheckedFetcher adminSectionCheckedFetcher;
    private final LabelFetcherFactory labelFetcherFactory;
    private final AdminQueryFetcherFactory adminQueryFetcherFactory;
    private final AdminMutationFetcherFactory adminMutationFetcherFactory;
    private final DonationsFetcher donationsFetcher;
    private final ConfigFetcher configFetcher;
    private final RatingFetcherFactory ratingFetcherFactory;
    private final GameRatingStatsFetcher ratingStatsFetcher;
    private final GroupFetcherFactory groupFetcherFactory;

    @Autowired
    public GraphQLTypeConfigurator(GameFetcherFactory gameFetcherFactory, CommentFetcherFactory commentFetcherFactory, EventFetcherFactory eventFetcherFactory, CalendarFetcher calendarFetcher, GameSearchFetcherFactory gameSearchFetcherFactory, GameCommentsPagedFetcherFactory gameCommentsPagedFetcherFactory, UserMutationFetcherFactory userMutationFetcherFactory, GameMutationFetcherFactory gameMutationFetcherFactory, UserFetcherFactory userFetcherFactory, AdminSectionCheckedFetcher adminSectionCheckedFetcher, LabelFetcherFactory labelFetcherFactory, AdminQueryFetcherFactory adminQueryFetcherFactory, AdminMutationFetcherFactory adminMutationFetcherFactory, DonationsFetcher donationsFetcher, ConfigFetcher configFetcher, RatingFetcherFactory ratingFetcherFactory, GameRatingStatsFetcher ratingStatsFetcher, GroupFetcherFactory groupFetcherFactory) {
        this.gameFetcherFactory = gameFetcherFactory;
        this.commentFetcherFactory = commentFetcherFactory;
        this.eventFetcherFactory = eventFetcherFactory;
        this.calendarFetcher = calendarFetcher;
        this.gameSearchFetcherFactory = gameSearchFetcherFactory;
        this.gameCommentsPagedFetcherFactory = gameCommentsPagedFetcherFactory;
        this.userMutationFetcherFactory = userMutationFetcherFactory;
        this.gameMutationFetcherFactory = gameMutationFetcherFactory;
        this.userFetcherFactory = userFetcherFactory;
        this.adminSectionCheckedFetcher = adminSectionCheckedFetcher;
        this.labelFetcherFactory = labelFetcherFactory;
        this.adminQueryFetcherFactory = adminQueryFetcherFactory;
        this.adminMutationFetcherFactory = adminMutationFetcherFactory;
        this.donationsFetcher = donationsFetcher;
        this.configFetcher = configFetcher;
        this.ratingFetcherFactory = ratingFetcherFactory;
        this.ratingStatsFetcher = ratingStatsFetcher;
        this.groupFetcherFactory = groupFetcherFactory;
    }

    public RuntimeWiring configureTypes() {
        return newRuntimeWiring()
                // Query
                .type("Query", builder -> builder
                        .dataFetcher("homepage", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("loggedInUser", userFetcherFactory.createLoggedInUserFetcher())
                        .dataFetcher("gameById", gameFetcherFactory.createGameByIdFetcher())
                        .dataFetcher("groupById", groupFetcherFactory.createGroupByIdFetcher())
                        .dataFetcher("groupsByQuery", groupFetcherFactory.createGroupsByQueryFetcher())
                        .dataFetcher("userById", userFetcherFactory.createUserByIdFetcher())
                        .dataFetcher("userByEmail", userFetcherFactory.createUserByEmailFetcher())
                        .dataFetcher("usersByQuery", userFetcherFactory.createUsersByQueryFetcher())
                        .dataFetcher("eventById", eventFetcherFactory.createEventByIdFetcher())
                        .dataFetcher("games", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("admin", adminSectionCheckedFetcher)
                        .dataFetcher("authorizedRequiredLabels", labelFetcherFactory.createAuthorizedRequiredLabelsFetcher())
                        .dataFetcher("authorizedOptionalLabels", labelFetcherFactory.createAuthorizedOptionalLabelsFetcher())
                        .dataFetcher("donations", donationsFetcher)
                        .dataFetcher("config", configFetcher)
                )
                // Homepage
                .type("HomepageQuery", builder -> builder
                        .dataFetcher("lastAddedGames", gameFetcherFactory.createLastAddedGamesFetcher())
                        .dataFetcher("mostPopularGames", gameFetcherFactory.createMostPopularGamesFetcher())
                        .dataFetcher("lastComments", commentFetcherFactory.createLastAddedCommentsFetcher())
                        .dataFetcher("nextEvents", eventFetcherFactory.createNextEventsFetcher())
                )
                .type("GamesQuery", builder -> builder
                        .dataFetcher("byQuery", gameSearchFetcherFactory.createByQueryFetcher())
                        .dataFetcher("recentAndMostPlayed", gameSearchFetcherFactory.createRecentAndMostPlayedFetcher())
                        .dataFetcher("mostPlayed", gameSearchFetcherFactory.createMostPlayedFetcher())
                        .dataFetcher("recent", gameSearchFetcherFactory.createRecentFetcher())
                        .dataFetcher("best", gameSearchFetcherFactory.createBestFetcher())
                        .dataFetcher("mostCommented", gameSearchFetcherFactory.createMostCommentedFetcher())
                )
                .type("Event", builder -> builder
                        .dataFetcher("from", calendarFetcher)
                        .dataFetcher("to", calendarFetcher)
                )
                .type("Game", builder -> builder
                        .dataFetcher("ratingStats", ratingStatsFetcher)
                        .dataFetcher("wantsToPlay", new GameWantsToPlayFetcher())
                        .dataFetcher("similarGames", gameFetcherFactory.createGameSimilarGamesFetcher())
                        .dataFetcher("gamesOfAuthors", gameFetcherFactory.createGameGamesOfAuthorsFetcher())
                        .dataFetcher("commentsPaged", gameCommentsPagedFetcherFactory.createCommentsPagedFetcher())
                        .dataFetcher("currentUsersComment", commentFetcherFactory.getCurrentUsersGameComment())
                        .dataFetcher("currentUsersRating", ratingFetcherFactory.createUsersGameRatingFetcher())
                        .dataFetcher("ratings", new GameRatingsProtectedFetcher())
                )
                .type("Comment", builder -> builder.dataFetcher("commentAsText", new CommentAsTextFetcher()))
                .type("Rating", builder -> builder.dataFetcher("user", userFetcherFactory.createRatingUserProtectedChecker()))
                .type("User", builder -> builder
                        .dataFetcher("role", new UserRoleFetcher())
                        .dataFetcher("commentsPaged", commentFetcherFactory.createUserCommentsPagedFetcher())
                        .dataFetcher("ratings", ratingFetcherFactory.createUserRatingsFetcher())
                        .dataFetcher("playedGames", gameFetcherFactory.createUserPlayedGamesFetcher())
                        .dataFetcher("wantedGames", gameFetcherFactory.createUserWantedGamesFetcher())
                        .dataFetcher("authoredGames", gameFetcherFactory.createUserAuthoredGamesFetcher())
                        .dataFetcher("name", userFetcherFactory.createNameFetcher())
                        .dataFetcher("email", userFetcherFactory.createUserEmailProtectedChecker())
                        .dataFetcher("description", userFetcherFactory.createDescriptionFetcher())
                        .dataFetcher("nickname", userFetcherFactory.createNicknameFetcher())
                        .dataFetcher("birthDate", userFetcherFactory.createBirthDateFetcher())
                        .dataFetcher("city", userFetcherFactory.createCityFetcher())
                )
                .type("AdminQuery", builder -> builder
                        .dataFetcher("allLabels", labelFetcherFactory.createAllLabelsFetcher())
                        .dataFetcher("allUsers", adminQueryFetcherFactory.createAllUsersFetcher())
                        .dataFetcher("ratingStats", adminQueryFetcherFactory.createRatingStatsFetcher())
                        .dataFetcher("commentStats", adminQueryFetcherFactory.createCommentStatsFetcher())
                        .dataFetcher("selfRated", adminQueryFetcherFactory.createSelfRatedFetcher())
                )

                /**
                 * Mutation
                 */
                .type("Mutation", builder -> builder
                        .dataFetcher("user", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("game", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("group", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("event", new StaticDataFetcher(Collections.emptyMap()))
                        .dataFetcher("admin", adminSectionCheckedFetcher)
                )
                .type("UserMutation", builder -> builder
                        .dataFetcher("logIn", userMutationFetcherFactory.createLogInMutationFetcher())
                        .dataFetcher("logOut", userMutationFetcherFactory.createLogOutMutationFetcher())
                        .dataFetcher("createUser", userMutationFetcherFactory.createCreateUserMutationFetcher())
                        .dataFetcher("updateLoggedInUser", userMutationFetcherFactory.createUpdateLoggedInUserMutationFetcher())
                        .dataFetcher("updateLoggedInUserPassword", userMutationFetcherFactory.createUpdateLoggedInUserPasswordMutationFetcher())
                        .dataFetcher("startRecoverPassword", userMutationFetcherFactory.createStartRecoverPasswordMutationFetcher())
                        .dataFetcher("finishRecoverPassword", userMutationFetcherFactory.createFinishRecoverPasswordMutationFetcher())
                )
                .type("GameMutation", builder -> builder
                        .dataFetcher("createGame", gameMutationFetcherFactory.createCreateGameFetcher())
                        .dataFetcher("updateGame", gameMutationFetcherFactory.createUpdateGameFetcher())
                        .dataFetcher("deleteGame", gameMutationFetcherFactory.createDeleteGameFetcher())
                        .dataFetcher("rateGame", gameMutationFetcherFactory.createRateGameFetcher())
                        .dataFetcher("deleteGameRating", gameMutationFetcherFactory.createDeleteGameRatingFetcher())
                        .dataFetcher("setGamePlayedState", gameMutationFetcherFactory.createSetGamePlayedStateFetcher())
                        .dataFetcher("createOrUpdateComment", gameMutationFetcherFactory.createCreateOrUpdateComment())
                        .dataFetcher("setCommentVisible", gameMutationFetcherFactory.createSetCommentVisibleFetcher())
                        .dataFetcher("setCommentLiked", gameMutationFetcherFactory.createSetCommentLikedFetcher())
                )
                .type("EventMutation", builder -> builder
                        .dataFetcher("createEvent", eventFetcherFactory.createCreateEventFetcher())
                        .dataFetcher("updateEvent", eventFetcherFactory.createUpdateEventFetcher())
                        .dataFetcher("updateEvent", eventFetcherFactory.createDeleteEventFetcher())
                )
                .type("AdminMutation", builder -> builder
                        .dataFetcher("updateLabel", adminMutationFetcherFactory.createUpdateLabelFetcher())
                        .dataFetcher("setLabelRequired", adminMutationFetcherFactory.createSetLabelRequiredFetcher())
                        .dataFetcher("setLabelAuthorized", adminMutationFetcherFactory.createSetLabelAuthorizedFetcher())
                        .dataFetcher("deleteLabel", adminMutationFetcherFactory.createDeleteLabelFetcher())
                        .dataFetcher("setUserRole", adminMutationFetcherFactory.createSetUserRoleFetcher())
                        .dataFetcher("deleteUser", adminMutationFetcherFactory.createDeleteUserFetcher())
                )
                .type("GroupMutation", builder -> builder
                        .dataFetcher("createGroup", groupFetcherFactory.createCreateGroupFetcher())
                        .dataFetcher("updateGroup", groupFetcherFactory.createUpdateGroupFetcher())
                )

                // Finish
                .build();

    }
}
