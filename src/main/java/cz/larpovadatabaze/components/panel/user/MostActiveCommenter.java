package cz.larpovadatabaze.components.panel.user;

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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows information about the user, who has most comments.
 */
public class MostActiveCommenter extends AbstractCsldPanel<CsldUser> {
    @SpringBean
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    private class UserModel extends LoadableDetachableModel<CsldUser> {
        @Override
        protected CsldUser load() {
            CsldUser commenter = csldUserService.getWithMostComments();
            if(commenter == null){
                commenter = CsldUser.getEmptyUser();
            }
            return commenter;
        }
    }

    public MostActiveCommenter(String id) {
        super(id);
        setDefaultModel(new UserModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", getModelObject().getId());
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
        final Label moderatorNick = new Label("mostActiveCommenterNick", getModelObject().getPerson().getNickNameView());
        final Label moderatorName = new Label("mostActiveCommenterName", getModelObject().getPerson().getName());
        moderatorLinkContent.add(moderatorNick);
        moderatorLinkContent.add(moderatorName);
        add(moderatorLinkContent);

        Label amountCommented = new Label("amountCommented", getModelObject().getAmountOfComments());
        add(amountCommented);
    }
}
