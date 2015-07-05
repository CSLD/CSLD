package cz.larpovadatabaze.components.panel.group;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.ManageGroupPage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.UserUtils;

/**
 * This panel contains link to the page allowing to manage members of the group.
 * It is shown only to logged person, which has rights toward the group.
 */
public class AddAuthorsToGroupPanel extends Panel {
    @SpringBean
    ImageService imageService;

    public AddAuthorsToGroupPanel(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", ((CsldGroup) getDefaultModelObject()).getId());

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("addAuthorsLink", ManageGroupPage.class, params);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(UserUtils.isAdminOfGroup(((CsldGroup)getDefaultModelObject())));
    }
}
