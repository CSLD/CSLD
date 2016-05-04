package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

abstract public class CreateEventPanel extends AbstractCsldPanel<Event> {
    @SpringBean
    private SessionFactory sessionFactory;

    public CreateEventPanel(String id, IModel<Event> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<Event> createEvent = new ValidatableForm<>("addEvent", new CompoundPropertyModel<>(getModel()));
        createEvent.setOutputMarkupId(true);

        RequiredTextField name = new RequiredTextField<String>("name");
        createEvent.add(name);
        createEvent.add(new CsldFeedbackMessageLabel("nameFeedback", name, "form.game.nameHint"));

        RequiredTextField date = new RequiredTextField<String>("date");
        createEvent.add(date);
        createEvent.add(new CsldFeedbackMessageLabel("dateFeedback", date, "form.event.dateHint"));

        RequiredTextField location = new RequiredTextField<String>("loc");
        createEvent.add(location);
        createEvent.add(new CsldFeedbackMessageLabel("locFeedback", location, "form.event.locationHint"));

        WebMarkupContainer descriptionWrapper = new WebMarkupContainer("descriptionWrapper");
        createEvent.add(descriptionWrapper);
        TextArea description = (TextArea) new TextArea<String>("description").setRequired(true);
        description.add(new CSLDTinyMceBehavior());
        descriptionWrapper.add(description);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", description, descriptionWrapper, "form.game.descriptionHint"));

        add(createEvent);

        createEvent.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                Event event = (Event) form.getModelObject();
                new DatabaseEvents(sessionFactory.getCurrentSession()).store(event);

                onCsldAction(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if(!createEvent.isValid()){
                    target.add(getParent());
                }
            }
        }.add(new TinyMceAjaxSubmitModifier()));
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
