package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.graphql.model.CommentsPaged;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import cz.larpovadatabaze.common.models.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameCommentsPagedFetcherFactory {
    private Comments comments;

    @Autowired
    public GameCommentsPagedFetcherFactory(Comments comments) {
        this.comments = comments;
    }

    public DataFetcher<CommentsPaged> createCommentsPagedFetcher() {
        return new DataFetcher<CommentsPaged>() {
            public CommentsPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Game game = dataFetchingEnvironment.getSource();
                int offset = dataFetchingEnvironment.getArgument("offset");
                int limit = dataFetchingEnvironment.getArgument("limit");

                int numComments = comments.amountOfCommentsVisibleForCurrentUserAndGame(game);
                List<Comment> filteredComments = comments.visibleForCurrentUserOrderedByUpvotes(game, new Page(offset, limit));

                return new CommentsPaged(filteredComments, numComments);
            }
        };
    }
}
