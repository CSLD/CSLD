package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.services.CommentService;
import org.apache.wicket.Session;
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
public class SortableCommentProvider extends SortableDataProvider<Comment, String> {
    @Autowired
    private CommentService commentService;

    @Override
    public Iterator<? extends Comment> iterator(long first, long count) {
        return commentService.getLastComments(first, count, Session.get().getLocale()).iterator();
    }

    @Override
    public long size() {
        return commentService.getAmountOfComments(Session.get().getLocale());
    }

    @Override
    public IModel<Comment> model(Comment object) {
        return new Model<Comment>(object);
    }
}
