package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It contains information about the author, who created most games.
 */
public class MostActiveCreators extends AbstractCsldPanel<CsldUser> {
    @SpringBean
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    private class UserModel extends LoadableDetachableModel<CsldUser> {

        @Override
        protected CsldUser load() {
            CsldUser res = csldUserService.getWithMostAuthored();
            if (res == null) res = CsldUser.getEmptyUser();
            return res;
        }
    }

    public MostActiveCreators(String id) {
        super(id);
        setDefaultModel(new UserModel());
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser author = getModelObject();
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
        final UserIcon moderatorImage = new UserIcon("mostActiveCommenterImage", getModel());
        moderatorLink.add(moderatorImage);
        add(moderatorLink);

        final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                new BookmarkablePageLink<CsldBasePage>("mostActiveCommenterContent", UserDetail.class, params);
        final Label moderatorNick = new Label("mostActiveCommenterNick", author.getPerson().getNickNameView());
        final Label moderatorName = new Label("mostActiveCommenterName", author.getPerson().getName());
        moderatorLinkContent.add(moderatorNick);
        moderatorLinkContent.add(moderatorName);
        add(moderatorLinkContent);

        Label amountCreated = new Label("amountCommented", new StringResourceModel("amountOfCreated", this, new Model<CsldUser>(author)));
        add(amountCreated);
    }
}
