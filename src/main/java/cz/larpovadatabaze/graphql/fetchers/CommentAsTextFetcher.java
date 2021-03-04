package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Comment;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.jsoup.Jsoup;

public class CommentAsTextFetcher implements DataFetcher<String> {
    private static final int MAX_CHARS_IN_COMMENT = 350;

    @Override
    public String get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Comment comment = dataFetchingEnvironment.getSource();

        String commentAsText = Jsoup.parse(comment.getComment()).text();
        if (commentAsText.length() > MAX_CHARS_IN_COMMENT) {
            commentAsText = commentAsText.substring(0, MAX_CHARS_IN_COMMENT);
        }

        return commentAsText;
    }
}
