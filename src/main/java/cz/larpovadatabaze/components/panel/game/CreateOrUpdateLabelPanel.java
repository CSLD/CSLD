package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It is used for creating and updating Labels for games.
 */
public class CreateOrUpdateLabelPanel extends Panel {
    @SpringBean
    private LabelService labelService;

    public CreateOrUpdateLabelPanel(String id){
        this(id,Label.getEmptyLabel());
    }

    public CreateOrUpdateLabelPanel(String id, Label label) {
        super(id);

        Form<Label> createOrUpdateLabel = new Form<Label>("createOrUpdateLabel", new CompoundPropertyModel<Label>(label)){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();
                if(!hasError()){
                    Label label = getModelObject();
                    saveOrUpdateLabel(label);
                }
            }
        };

        createOrUpdateLabel.add(new TextField<String>("name"));
        createOrUpdateLabel.add(new TextArea<String>("description"));
        createOrUpdateLabel.add(new Button("submit"));

        add(createOrUpdateLabel);
    }

    private void saveOrUpdateLabel(Label label) {
        CsldUser loggedUser = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        label.setAddedBy(loggedUser);
        label.setAddedById(loggedUser.getId());
        labelService.saveOrUpdate(label);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
