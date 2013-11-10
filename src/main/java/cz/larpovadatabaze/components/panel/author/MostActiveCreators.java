package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It contains information about the author, who created most games.
 */
public class MostActiveCreators extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    public MostActiveCreators(String id) {
        super(id);

        CsldUser author = csldUserService.getWithMostAuthored();
        if(author == null){
            author = CsldUser.getEmptyUser();
            setVisible(false);
        }

        PageParameters params = new PageParameters();
        params.add("id", author.getId());
        final BookmarkablePageLink<CsldBasePage> mostActiveCommenterHead =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterHead", UserDetail.class, params);
        add(mostActiveCommenterHead);

        final BookmarkablePageLink<CsldBasePage> moderatorLink =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenter", UserDetail.class, params);
        final Image moderatorImage = new Image("mostActiveCommenterImage",
                new PackageResourceReference(Csld.class, author.getImage().getPath()));
        moderatorLink.add(moderatorImage);
        add(moderatorLink);

        final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterContent", UserDetail.class, params);
        final Label moderatorNick = new Label("mostActiveCommenterNick", author.getPerson().getNickname());
        final Label moderatorName = new Label("mostActiveCommenterName", author.getPerson().getName());
        moderatorLinkContent.add(moderatorNick);
        moderatorLinkContent.add(moderatorName);
        add(moderatorLinkContent);

        Label amountCreated = new Label("amountCommented", new StringResourceModel("amountOfCreated", this, new Model<CsldUser>(author)));
        add(amountCreated);
    }
}
