package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows information about the user, who has most comments.
 */
public class MostActiveCommenter extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    public MostActiveCommenter(String id) {
        super(id);

        CsldUser commenter = csldUserService.getWithMostComments();
        if(commenter == null){
            commenter = CsldUser.getEmptyUser();
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

        Label amountCommented = new Label("amountCommented", commenter.getAmountOfComments());
        add(amountCommented);
    }
}
