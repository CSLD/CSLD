package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * It consists of link to the page for editing actual group.
 * It is shown only for the logged users, who have rights to manage the group.
 */
public class EditGroupPanel extends Panel {
    public EditGroupPanel(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", ((CsldGroup)getDefaultModelObject()).getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editGroup", CreateOrUpdateGroupPage.class, params);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(UserUtils.isAdminOfGroup(((CsldGroup)getDefaultModelObject())));
    }
}
