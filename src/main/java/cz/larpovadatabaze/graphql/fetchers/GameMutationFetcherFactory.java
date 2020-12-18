package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.entities.Video;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.games.services.Upvotes;
import cz.larpovadatabaze.games.services.Videos;
import cz.larpovadatabaze.graphql.GraphQLUploadedFile;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cz.larpovadatabaze.common.entities.Rating.STATE_CODE_NONE;
import static cz.larpovadatabaze.common.entities.Rating.STATE_CODE_PLAYED;
import static cz.larpovadatabaze.common.entities.Rating.STATE_CODE_WANT_TO_PLAY;

@Component
public class GameMutationFetcherFactory {
    private final Games games;
    private final Videos videos;
    private final Labels labels;
    private final CsldUsers users;
    private final CsldGroups groups;
    private final AppUsers appUsers;
    private final Ratings ratings;
    private final Comments comments;
    private final Upvotes upvotes;

    @Autowired
    public GameMutationFetcherFactory(Games games, Videos videos, Labels labels, CsldUsers users, CsldGroups groups, AppUsers appUsers, Ratings ratings, Comments comments, Upvotes upvotes) {
        this.games = games;
        this.videos = videos;
        this.labels = labels;
        this.users = users;
        this.groups = groups;
        this.appUsers = appUsers;
        this.ratings = ratings;
        this.comments = comments;
        this.upvotes = upvotes;
    }

    /**
     * Find user in the list (by email)
     *
     * @param userList User list
     * @param email    Email to find
     * @return user or null
     */
    private CsldUser findUser(List<CsldUser> userList, String email) {
        for (CsldUser user : userList) {
            if (user.getPerson().getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Find group in the list (by name)
     *
     * @param groupList Group list
     * @param name      Name to find
     * @return Group or null
     */
    private CsldGroup findGroup(List<CsldGroup> groupList, String name) {
        for (CsldGroup label : groupList) {
            if (label.getName().equalsIgnoreCase(name)) {
                return label;
            }
        }

        return null;
    }

    /**
     * Get authors as CsldUser objects by ids & new label specifications
     *
     * @param authorIds  List of author ids
     * @param newAuthors List of author specifications. Tries to find author by name and when it does not exist, creates it.
     * @return Author objects
     */
    private List<CsldUser> getAuthors(List<String> authorIds, List<Map<String, Object>> newAuthors) {
        List<CsldUser> res = new ArrayList<>();

        // Fetch existing users
        for (String id : authorIds) {
            CsldUser author = users.getById(Integer.parseInt(id));
            if (author == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Author with id " + id + " does not exist", "input.authors");
            }
            res.add(author);
        }

        // Create new users (if necessary)
        List<CsldUser> createdUsers = new ArrayList<>();
        for (Map<String, Object> newAuthor : newAuthors) {
            String email = (String) newAuthor.get("email");
            String name = (String) newAuthor.get("name");
            String nickname = (String) newAuthor.get("nickname");

            // Try to find user by email
            CsldUser author = users.getByEmail(email);
            if (author == null) {
                author = findUser(createdUsers, email);
                if (author == null) {
                    // We need to create new author
                    author = new CsldUser();
                    Person person = new Person();
                    person.setEmail(email);
                    person.setName(name);
                    person.setNickname(nickname);
                    author.setPerson(person);

                    users.saveOrUpdateNewAuthor(author);
                    createdUsers.add(author);
                }
            }

            res.add(author);
        }

        return res;
    }

    /**
     * Get group object by ids & new group specifications
     *
     * @param groupIds  List of group ids
     * @param newGroups List of group specifications. Tries to find group by name and when it does not exist, creates it.
     * @return Group objects
     */
    private List<CsldGroup> getGroups(List<String> groupIds, List<Map<String, Object>> newGroups) {
        List<CsldGroup> res = new ArrayList<>();

        // Fetch existing groups
        for (String id : groupIds) {
            CsldGroup group = groups.getById(Integer.parseInt(id));
            if (group == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Group with id " + id + " does not exist", "input.groups");
            }
            res.add(group);
        }

        // Create new labels (if necessary)
        List<CsldGroup> allGroups = groups.getAll();
        List<CsldGroup> createdGroups = new ArrayList<>();
        for (Map<String, Object> newGroup : newGroups) {
            String name = (String) newGroup.get("name");

            // Try to find existing label by name
            CsldGroup group = findGroup(allGroups, name);
            if (group == null) {
                group = findGroup(createdGroups, name);
                if (group == null) {
                    // We need to create new label
                    group = new CsldGroup();
                    group.setName(name);

                    groups.saveOrUpdate(group);

                    createdGroups.add(group);
                }
            }
            res.add(group);
        }

        return res;
    }

    /**
     * Check that current user can rate / comment game
     *
     * @param game Game to check
     * @throws Exception when access is denied or game does not exist
     */
    private void checkGameUserAccess(Game game) {
        if (game == null) {
            throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Game does not exist", "input.id");
        }

        if (!appUsers.isSignedIn()) {
            throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Must be logged in");
        }
    }

    /**
     * Check that current user can modify game
     *
     * @param game Game to check
     * @throws Exception when access is denied or game does not exist
     */
    private void checkGameEditAccess(Game game) {
        if (game == null) {
            throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Game does not exist", "input.id");
        }

        if (!appUsers.isSignedIn()) {
            throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Must be logged in");
        }

        if (!appUsers.isAtLeastEditor()) {
            Integer loggedInUserId = appUsers.getLoggedUserId();

            // Check whether logged user created the game
            if (!loggedInUserId.equals(game.getAddedBy().getId())) {
                // Check whether logged user is author of the game
                if (game.getAuthors().stream().noneMatch(csldUser -> loggedInUserId.equals(csldUser.getId()))) {
                    throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Not editor / author / creator");
                }
            }
        }
    }

    /**
     * Get rating of the game by current user. Create new record when user have not yet rated the game.
     *
     * @param game Game to get rating for
     * @return User rating
     */
    private Rating getUserGameRating(Game game) {
        Rating rating = ratings.getUserRatingOfGame(appUsers.getLoggedUserId(), game.getId());
        if (rating == null) {
            // We need to create new rating
            rating = new Rating();
            rating.setGame(game);
            rating.setUser(appUsers.getLoggedUser());
        }

        return rating;
    }


    /**
     * Apply values from input to the game
     *
     * @param game  Game to affect
     * @param input Input object from GraphQL
     * @return Game object
     */
    private Game applyInputValues(Game game, Map<String, Object> input) {
        game.setName((String) input.get("name"));
        game.setDescription((String) input.get("description"));
        game.setYear((Integer) input.get("year"));
        game.setPlayers((Integer) input.get("players"));
        game.setMenRole((Integer) input.get("menRole"));
        game.setWomenRole((Integer) input.get("womenRole"));
        game.setBothRole((Integer) input.get("bothRole"));
        game.setHours((Integer) input.get("hours"));
        game.setDays((Integer) input.get("days"));
        // TODO - cover photo - TODO
        game.setWeb((String) input.get("web"));
        game.setPhotoAuthor((String) input.get("photoAuthor"));
        game.setGalleryURL((String) input.get("galleryURL"));

        String videoURL = (String) input.get("video");
        if (videoURL != null) {
            Video video = new Video();
            video.setPath(videoURL);
            video.setType(0);

            videos.saveOrUpdate(video);

            game.setVideo(video);
        }

        game.setRatingsDisabled(Boolean.TRUE.equals(input.get("ratingsDisabled")));
        game.setCommentsDisabled(Boolean.TRUE.equals(input.get("commentsDisabled")));

        game.setLabels(FetcherUtils.getLabels(labels, (List<String>) input.get("labels"), (List<Map<String, Object>>) input.get("newLabels")));
        game.setAuthors(getAuthors((List<String>) input.get("authors"), (List<Map<String, Object>>) input.get("newAuthors")));
        game.setGroupAuthor(getGroups((List<String>) input.get("groupAuthors"), (List<Map<String, Object>>) input.get("newGroupAuthors")));

        if (game.getLabels().stream().noneMatch(label -> label.getRequired())) {
            throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Must provide at least one required label");
        }

        return game;
    }

    public DataFetcher<Game> createCreateGameFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            // TODO - check recaptcha - TODO

            Game game = applyInputValues(new Game(), input);
            if (appUsers.isSignedIn()) {
                game.setAddedBy(appUsers.getLoggedUser());
            }

            // Image
            GraphQLUploadedFile coverPhoto = null;
            Map<String, String> profilePictureMap = (Map<String, String>)input.get("coverPhoto");
            if (profilePictureMap != null) {
                coverPhoto = new GraphQLUploadedFile(profilePictureMap.get("fileName"), profilePictureMap.get("contents"));
            }

            games.saveOrUpdate(game, coverPhoto);

            return game;
        };
    }

    public DataFetcher<Game> createUpdateGameFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            Game game = games.getById(Integer.parseInt((String) input.get("id")));

            // Check rights
            checkGameEditAccess(game);

            // Apply values and modify
            game = applyInputValues(game, input);

            // Image
            GraphQLUploadedFile coverPhoto = null;
            Map<String, String> profilePictureMap = (Map<String, String>)input.get("coverPhoto");
            if (profilePictureMap != null) {
                coverPhoto = new GraphQLUploadedFile(profilePictureMap.get("fileName"), profilePictureMap.get("contents"));
            }

            games.saveOrUpdate(game, coverPhoto);

            return game;
        };
    }

    public DataFetcher<Game> createDeleteGameFetcher() {
        return dataFetchingEnvironment -> {
            Game game = games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("id")));

            // Check rights
            checkGameEditAccess(game);

            // Apply values and modify
            game.setDeleted(true);
            games.saveOrUpdate(game);

            return game;
        };
    }

    public DataFetcher<Game> createRateGameFetcher() {
        return dataFetchingEnvironment -> {
            Game game = games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("gameId")));

            checkGameUserAccess(game);

            Integer newRating = dataFetchingEnvironment.getArgument("rating");
            if (newRating != null) {
                // Check rating
                if ((newRating < 1) || (newRating > 10)) {
                    throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Rating must be between 1 and 10", "rating");
                }
            }

            Rating rating = getUserGameRating(game);
            rating.setRating(newRating);

            if (newRating != null) {
                // User rated game so he played it
                rating.setStateEnum(Rating.GameState.PLAYED);
            }

            ratings.saveOrUpdate(rating);

            return game;
        };
    }

    public DataFetcher<Game> createDeleteGameRatingFetcher() {
        return dataFetchingEnvironment -> {
            if (!appUsers.isAtLeastEditor()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Access denied");
            }

            Game game = games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("gameId")));

            checkGameUserAccess(game);

            Integer userId = Integer.parseInt(dataFetchingEnvironment.getArgument("userId"));

            Rating rating = ratings.getUserRatingOfGame(game.getId(), userId);
            if (rating != null) {
                ratings.remove(rating);
            }

            return game;
        };
    }

    public DataFetcher<Game> createSetGamePlayedStateFetcher() {
        return dataFetchingEnvironment -> {
            Game game = games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("gameId")));

            checkGameUserAccess(game);

            Integer newState = dataFetchingEnvironment.getArgument("state");
            if ((newState != STATE_CODE_NONE) && (newState != STATE_CODE_WANT_TO_PLAY) && (newState != STATE_CODE_PLAYED)) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Invalid state value", "state");
            }

            Rating rating = getUserGameRating(game);
            rating.setState(newState);
            if (newState != STATE_CODE_PLAYED) {
                // Clear rating when state is other than played
                rating.setRating(null);
            }
            ratings.saveOrUpdate(rating);

            return game;
        };
    }

    public DataFetcher<Game> createCreateOrUpdateComment() {
        return dataFetchingEnvironment -> {
            Game game = games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("gameId")));

            checkGameUserAccess(game);

            String commentText = dataFetchingEnvironment.getArgument("comment");

            Comment comment = comments.getCommentOnGameFromUser(appUsers.getLoggedUserId(), game.getId());
            if (comment == null) {
                comment = new Comment();
                comment.setGame(game);
                comment.setUser(appUsers.getLoggedUser());
            }

            comment.setComment(commentText);

            comments.saveOrUpdate(comment);

            return game;
        };
    }

    public DataFetcher<Game> createSetCommentVisibleFetcher() {
        return dataFetchingEnvironment -> {
            // Check rights
            if (!appUsers.isAtLeastEditor()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Editor needed");
            }

            Comment comment = comments.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("commentId")));
            if (comment == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Comment not found", "commentId");
            }

            if (Boolean.TRUE.equals(dataFetchingEnvironment.getArgument("visible"))) {
                comments.unHideComment(comment);
            } else {
                comments.hideComment(comment);
            }

            return comment.getGame();
        };
    }

    public DataFetcher<Game> createSetCommentLikedFetcher() {
        return dataFetchingEnvironment -> {
            // Check rights
            if (!appUsers.isSignedIn()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Must be signed in");
            }

            Comment comment = comments.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("commentId")));
            if (comment == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Comment not found", "commentId");
            }

            if (Boolean.TRUE.equals(dataFetchingEnvironment.getArgument("liked"))) {
                upvotes.upvote(appUsers.getLoggedUser(), comment);
            } else {
                upvotes.downvote(appUsers.getLoggedUser(), comment);
            }

            return comment.getGame();
        };
    }
}