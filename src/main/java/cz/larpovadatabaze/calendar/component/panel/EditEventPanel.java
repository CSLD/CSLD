package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.component.page.CreateOrUpdateEventPage;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * It adds link for updating of event.
 */
public class EditEventPanel extends Panel {
    private IModel<Event> model;

    public EditEventPanel(String id, IModel<Event> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", model.getObject().getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editEvent", CreateOrUpdateEventPage.class, params);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                if(model.getObject() == null || model.getObject().getAddedBy() == null) {
                    isVisible = false;
                } else if(!model.getObject().getAddedBy().equals(logged)){
                    isVisible = false;
                }
            }
        }

        setVisibilityAllowed(isVisible);
    }
}
