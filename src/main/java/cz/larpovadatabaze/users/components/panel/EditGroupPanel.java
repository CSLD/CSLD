package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.users.components.page.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It consists of link to the page for editing actual group.
 * It is shown only for the logged users, who have rights to manage the group.
 */
public class EditGroupPanel extends Panel {
    @SpringBean
    private AppUsers appUsers;

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
        setVisibilityAllowed(appUsers.isAdminOfGroup());
    }
}