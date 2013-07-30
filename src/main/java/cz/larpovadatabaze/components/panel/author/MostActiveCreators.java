package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
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
 * Date: 30.4.13
 * Time: 12:45
 */
public class MostActiveCreators extends Panel {
    @SpringBean
    private CsldUserService csldUserService;
    private CsldUser commenter;

    public MostActiveCreators(String id) {
        super(id);

        commenter = csldUserService.getWithMostAuthored();
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
        params.add("userId", commenter.getId());
        final BookmarkablePageLink<CsldBasePage> mostActiveCommenterHead =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterHead", AuthorDetail.class, params);
        add(mostActiveCommenterHead);

        final BookmarkablePageLink<CsldBasePage> moderatorLink =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenter", AuthorDetail.class, params);
        final Image moderatorImage = new Image("mostActiveCommenterImage",
                new ContextRelativeResource(commenter.getImage().getPath()));
        moderatorLink.add(moderatorImage);
        add(moderatorLink);

        final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterContent", AuthorDetail.class, params);
        final Label moderatorNick = new Label("mostActiveCommenterNick", commenter.getPerson().getNickname());
        final Label moderatorName = new Label("mostActiveCommenterName", commenter.getPerson().getName());
        moderatorLinkContent.add(moderatorNick);
        moderatorLinkContent.add(moderatorName);
        add(moderatorLinkContent);

        List<Game> authorOf = (commenter.getAuthorOf() != null) ?
                commenter.getAuthorOf() : new ArrayList<Game>();
        Label amountCommented = new Label("ammountCommented", authorOf.size());
        add(amountCommented);
    }
}
