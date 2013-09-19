package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.GroupHasMember;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.GenericModel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.RepeatableInput;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It contains form necessary for adding author to group.
 */
public abstract class AddAuthorToGroupPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    public AddAuthorToGroupPanel(String id) {
        super(id);

        GroupHasMember memberOfGroup = new GroupHasMember();
        if(memberOfGroup.getUser() == null){
            memberOfGroup.setUser(CsldUser.getEmptyUser());
        }

        final ValidatableForm<GroupHasMember> createMember = new ValidatableForm<GroupHasMember>("addAuthorToGroup",
                new CompoundPropertyModel<GroupHasMember>(memberOfGroup));

        createMember.add(addFeedbackPanel(new DateTextField("start","dd.mm.yyyy").setRequired(true), createMember, "startFeedback"));
        createMember.add(addFeedbackPanel(new DateTextField("end","dd.mm.yyyy"), createMember, "endFeedback"));

        final IModel<CsldUser> actualUser = new GenericModel<CsldUser>(memberOfGroup.getUser());
        RepeatableInput<CsldUser> user = new RepeatableInput<CsldUser>("name", actualUser, CsldUser.class, csldUserService);
        createMember.add(addFeedbackPanel(user, createMember, "nameFeedback"));

        createMember.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if(createMember.isValid()){
                    ((GroupHasMember) form.getModelObject()).setUser(actualUser.getObject());
                    onCsldAction(target, form);
                }
            }
        });

        add(createMember);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String nameOfFeedbackPanel){
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(addFeedbackTo);
        final FeedbackPanel feedbackPanel = new FeedbackPanel(nameOfFeedbackPanel, filter);
        feedbackPanel.setOutputMarkupId(true);
        addingFeedbackTo.add(feedbackPanel);
        addFeedbackTo.add(new AjaxFeedbackUpdatingBehavior("blur", feedbackPanel));
        return addFeedbackTo;
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
