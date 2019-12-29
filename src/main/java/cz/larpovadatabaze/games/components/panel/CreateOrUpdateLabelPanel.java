package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.api.ValidatableForm;
import cz.larpovadatabaze.common.components.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It is used for creating and updating Labels for games.
 */
public abstract class CreateOrUpdateLabelPanel extends Panel {
    @SpringBean
    Labels labels;

    public CreateOrUpdateLabelPanel(String id){
        this(id,Label.getEmptyLabel());
    }

    public CreateOrUpdateLabelPanel(String id, Label label) {
        super(id);
        if(label == null){
            label = Label.getEmptyLabel();
        }

        final ValidatableForm<Label> createOrUpdateLabel =
                new ValidatableForm<Label>("createOrUpdateLabel", new CompoundPropertyModel<>(label)){};

        TextField<String> name = new TextField<>("name");
        name.setRequired(true);
        createOrUpdateLabel.add(name);
        createOrUpdateLabel.add(new CsldFeedbackMessageLabel("nameFeedback", name, null));

        createOrUpdateLabel.add(new TextArea<String>("description"));

        createOrUpdateLabel.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);

                if(createOrUpdateLabel.isValid()){
                    Label label = createOrUpdateLabel.getModelObject();
                    if(saveOrUpdateLabel(label)){
                        onCsldAction(target, label);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                target.add(CreateOrUpdateLabelPanel.this);
            }
        });

        add(createOrUpdateLabel);
    }

    private boolean saveOrUpdateLabel(Label label) {
        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        label.setAddedBy(loggedUser);
        label.setRequired(false);
        return labels.saveOrUpdate(label);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target, Object object){}
}
