package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Comments;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Fetchers for comments
 */
@Component
public class CommentFetcherFactory {
    private final Comments comments;

    @Autowired
    public CommentFetcherFactory(Comments comments) {
        this.comments = comments;
    }

    public DataFetcher<Collection<Comment>> createLastAddedCommentsFetcher() {
        return dataFetchingEnvironment -> {
            int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
            int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);

            return comments.getLastComments(offset, limit);
        };
    }
}
