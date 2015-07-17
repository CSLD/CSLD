package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.LabelHasLanguages;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocaleNames;

/**
 * It is used for creating and updating Labels for games.
 */
public abstract class CreateOrUpdateLabelPanel extends Panel {
    @SpringBean
    LabelService labelService;
    LanguageSolver sessionLanguageSolver = new SessionLanguageSolver();

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
        List<String> availableLanguages = new ArrayList<>(availableLocaleNames());
        final DropDownChoice<String> changeLocale =
                new DropDownChoice<>("lang", availableLanguages);
        createOrUpdateLabel.add(changeLocale);

        createOrUpdateLabel.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createOrUpdateLabel.isValid()){
                    Label label = createOrUpdateLabel.getModelObject();
                    processLanguage(label);
                    if(saveOrUpdateLabel(label)){
                        onCsldAction(target, form);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);

                target.add(form);
            }
        });

        add(createOrUpdateLabel);
    }

    private void processLanguage(Label label){
        String toBeSaved;
        if(label.getLang() == null) {
            label.setLabelHasLanguages(new ArrayList<>());
            toBeSaved = sessionLanguageSolver.getTextLangForUser().get(0);
        } else {
            toBeSaved = label.getLang();
        }

        if(label.getLabelHasLanguages().isEmpty()) {
            LabelHasLanguages firstLanguage = new LabelHasLanguages();
            firstLanguage.setLabel(label);
            firstLanguage.setLanguage(toBeSaved);
            firstLanguage.setName(label.getName());
            firstLanguage.setDescription(label.getDescription());
            label.getLabelHasLanguages().add(firstLanguage);
        } else {
            // Find existing locale
            List<LabelHasLanguages> actualLanguages = label.getLabelHasLanguages();
            for(LabelHasLanguages language: actualLanguages) {
                if(language.getLanguage().equals(toBeSaved)){
                    language.setName(label.getName());
                    language.setDescription(label.getDescription());
                }
            }
        }
    }

    private boolean saveOrUpdateLabel(Label label) {
        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        label.setAddedBy(loggedUser);
        label.setRequired(false);
        return labelService.saveOrUpdate(label);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
