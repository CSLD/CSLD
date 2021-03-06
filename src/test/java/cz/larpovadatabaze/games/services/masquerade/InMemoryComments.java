package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.models.Page;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Comments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryComments extends InMemoryCrud<Comment, Integer> implements Comments {
    @Override
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        List<Comment> found = inMemory.stream()
                .filter(comment -> comment.getUser().getId() == userId &&
                        comment.getGame().getId() == gameId)
                .collect(Collectors.toList());
        if (found.size() > 0) {
            return found.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int getAmountOfComments() {
        return inMemory.size();
    }

    @Override
    public Collection<Comment> getLastComments(int amount) {
        int startsFrom = inMemory.size() - amount;
        if (startsFrom < 0) {
            startsFrom = 0;
        }
        int ends = startsFrom + amount;
        if (ends > inMemory.size()) {
            ends = inMemory.size();
        }
        return inMemory.subList(startsFrom, ends);
    }

    @Override
    public Collection<Comment> getLastComments(int first, int count) {
        if (first < 0) {
            throw new RuntimeException("First comment must be above 0");
        }
        int end = first + count;
        if (end > inMemory.size()) {
            end = inMemory.size();
        }
        return inMemory.subList(first, end);
    }

    @Override
    public void hideComment(Comment comment) {
        comment.setHidden(false);
    }

    @Override
    public void unHideComment(Comment comment) {
        comment.setHidden(true);
    }

    @Override
    public List<Comment> visibleForCurrentUserOrderedByUpvotes(Game game, Page page) {
        return new ArrayList<>();
    }

    @Override
    public int amountOfCommentsVisibleForCurrentUserAndGame(Game game) {
        return 0;
    }

    @Override
    public List<Comment> visibleForCurrentUserOrderedByRecent(CsldUser user, Page page) {
        return new ArrayList<>();
    }

    @Override
    public int amountOfCommentsVisibleForCurrentUserAndUser(CsldUser user) {
        return 0;
    }

    @Override
    public void removeForUser(CsldUser toRemove) {

    }
}
