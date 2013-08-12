package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class EditGroupPanel extends Panel {
    public EditGroupPanel(String id, CsldGroup toEdit) {
        super(id);

        PageParameters params = new PageParameters();
        params.add("id", toEdit.getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editGroup", CreateOrUpdateGroupPage.class, params);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
