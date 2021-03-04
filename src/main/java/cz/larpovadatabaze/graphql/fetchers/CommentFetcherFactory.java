package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.graphql.model.CommentsPaged;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetchers for comments
 */
@Component
public class CommentFetcherFactory {
    private final Comments comments;
    private final AppUsers appUsers;

    @Autowired
    public CommentFetcherFactory(Comments comments, AppUsers appUsers) {
        this.comments = comments;
        this.appUsers = appUsers;
    }

    public DataFetcher<Collection<Comment>> createLastAddedCommentsFetcher() {
        return dataFetchingEnvironment -> {
            int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
            int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);

            return comments.getLastComments(offset, limit);
        };
    }

    public DataFetcher<Comment> getCurrentUsersGameComment() {
        return dataFetchingEnvironment -> {
            if (!appUsers.isSignedIn()) {
                return null;
            }

            Game game = dataFetchingEnvironment.getSource();
            return comments.getCommentOnGameFromUser(appUsers.getLoggedUserId(), game.getId());
        };
    }

    public DataFetcher<CommentsPaged> createUserCommentsPagedFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
            int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);

            List<Comment> userComments = user.getCommented().stream().sorted(((o1, o2) -> -o1.getAdded().compareTo(o2.getAdded()))).collect(Collectors.toList());
            List<Comment> filteredComments = userComments.subList(offset, Math.min(offset+limit, userComments.size()));

            return new CommentsPaged(filteredComments, userComments.size());
        };
    }
}
