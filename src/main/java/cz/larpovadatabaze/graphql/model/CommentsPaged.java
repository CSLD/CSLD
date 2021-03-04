package cz.larpovadatabaze.graphql.model;

import cz.larpovadatabaze.common.entities.Comment;

import java.util.List;

public class CommentsPaged {
    private final List<Comment> comments;
    private final int totalAmount;

    public CommentsPaged(List<Comment> comments, int totalAmount) {
        this.comments = comments;
        this.totalAmount = totalAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
