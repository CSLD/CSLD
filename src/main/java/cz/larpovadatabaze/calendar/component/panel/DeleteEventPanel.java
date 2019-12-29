package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It contains logic for showing and hiding and allows deletion of an event.
 */
public class DeleteEventPanel extends Panel {
    private IModel<Event> model;
    private IModel<String> deletedEventLabelModel;

    @SpringBean
    private Events events;

    public DeleteEventPanel(String id, IModel<Event> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Event event = model.getObject();

        PageParameters params = new PageParameters();
        params.add("id", event.getId());

        deletedEventLabelModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return new StringResourceModel(event.isDeleted() ? "event.show" : "event.delete", DeleteEventPanel.this, null).getString();
            }
        };
        Label deleteGameLabel = new Label("deleteEventLabel", deletedEventLabelModel);
        deleteGameLabel.setOutputMarkupId(true);

        AjaxLink<CsldBasePage> deleteGame = new AjaxLink<CsldBasePage>("deleteEvent") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                event.setDeleted(!event.isDeleted());
                events.saveOrUpdate(event);
                deletedEventLabelModel.detach();
                ajaxRequestTarget.add(DeleteEventPanel.this);
            }
        };
        deleteGame.add(deleteGameLabel);
        deleteGame.setOutputMarkupId(true);

        add(deleteGame);
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isAtLeastEditor());
    }
}
