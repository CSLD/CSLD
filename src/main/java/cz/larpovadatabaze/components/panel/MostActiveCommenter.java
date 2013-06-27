package cz.larpovadatabaze.components.panel;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.detail.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 18:33
 */
public class MostActiveCommenter extends Panel {
    @SpringBean
    private CsldUserService csldUserService;
    private CsldUser commenter;

    public MostActiveCommenter(String id) {
        super(id);

        commenter = csldUserService.getWithMostComments();
        if(commenter == null){
            commenter = new CsldUser();
            commenter.setId(-1);
            cz.larpovadatabaze.entities.Image image = new cz.larpovadatabaze.entities.Image();
            image.setPath("");
            commenter.setImage(image);
            commenter.setPerson(new Person());
            setVisible(false);
        }

        PageParameters params = new PageParameters();
        params.add("id", commenter.getId());
        final BookmarkablePageLink<CsldBasePage> mostActiveCommenterHead =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterHead", UserDetail.class, params);
        add(mostActiveCommenterHead);

        final BookmarkablePageLink<CsldBasePage> moderatorLink =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenter", UserDetail.class, params);
        final Image moderatorImage = new Image("mostActiveCommenterImage",
                new ContextRelativeResource(commenter.getImage().getPath()));
        moderatorLink.add(moderatorImage);
        add(moderatorLink);

        final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterContent", UserDetail.class, params);
        final Label moderatorNick = new Label("mostActiveCommenterNick", commenter.getPerson().getNickname());
        final Label moderatorName = new Label("mostActiveCommenterName", commenter.getPerson().getName());
        moderatorLinkContent.add(moderatorNick);
        moderatorLinkContent.add(moderatorName);
        add(moderatorLinkContent);

        List<Comment> comments = (commenter.getCommented() != null) ?
                commenter.getCommented() : new ArrayList<Comment>();
        Label amountCommented = new Label("ammountCommented", comments.size());
        add(amountCommented);
    }
}
