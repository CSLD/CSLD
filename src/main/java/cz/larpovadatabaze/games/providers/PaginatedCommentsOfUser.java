package cz.larpovadatabaze.games.providers;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.models.Page;
import cz.larpovadatabaze.games.services.Comments;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 16.10.13
 * Time: 22:55
 */
@Component
public abstract class PaginatedCommentsOfUser extends SortableDataProvider<Comment, String> {
    private Comments comments;

    @Autowired
    public PaginatedCommentsOfUser(Comments comments) {
        this.comments = comments;
    }

    abstract public CsldUser getUser();

    @Override
    public Iterator<? extends Comment> iterator(long first, long count) {
        return comments.visibleForCurrentUserOrderedByRecent(getUser(), new Page((int) first, (int) count)).iterator();
    }

    @Override
    public long size() {
        return comments.amountOfCommentsVisibleForCurrentUserAndUser(getUser());
    }

    @Override
    public IModel<Comment> model(Comment object) {
        return new Model<>(object);
    }
}
