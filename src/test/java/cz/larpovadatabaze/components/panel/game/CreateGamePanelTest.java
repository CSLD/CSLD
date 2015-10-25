package cz.larpovadatabaze.components.panel.game;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.panel.UploadCoverImagePanel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.junit.Test;

/**
 *
 */
public class CreateGamePanelTest extends AcceptanceTest {
    @Test
    public void runAsUser() {
        TestUtils.logUser(masqueradeBuilder.getUser());

        tester.startComponentInPage(new CreateOrUpdateGamePanel("id", Model.of(masqueradeBuilder.getFirstMasquerade())){});

        tester.assertComponent("addGame", ValidatableForm.class);
        tester.assertComponent("addGame:feedback", FeedbackPanel.class);
        tester.assertComponent("addGame:name", RequiredTextField.class);
        tester.assertComponent("addGame:nameFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:labels", ChooseLabelsPanel.class);
        tester.assertComponent("addGame:labelsFeedbackWrapper:labelsFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:descriptionWrapper:description", TextArea.class);
        tester.assertComponent("addGame:descriptionWrapper:descriptionFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:year", TextField.class);
        tester.assertComponent("addGame:yearFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:lang", DropDownChoice.class);
        tester.assertComponent("addGame:langFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:players", TextField.class);
        tester.assertComponent("addGame:playersFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:womenRole", TextField.class);
        tester.assertComponent("addGame:womenRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:menRole", TextField.class);
        tester.assertComponent("addGame:menRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:bothRole", TextField.class);
        tester.assertComponent("addGame:bothRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:hours", TextField.class);
        tester.assertComponent("addGame:hoursFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:days", TextField.class);
        tester.assertComponent("addGame:daysFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:web", TextField.class);
        tester.assertComponent("addGame:webFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:photoAuthor", TextField.class);
        tester.assertComponent("addGame:photoAuthorFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:galleryURL", TextField.class);
        tester.assertComponent("addGame:galleryURLFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:video", TextField.class);
        tester.assertComponent("addGame:videoFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("addGame:coverImage", UploadCoverImagePanel.class);
        tester.assertComponent("addGame:ratingsDisabled", CheckBox.class);
        tester.assertComponent("addGame:commentsDisabled", CheckBox.class);
        tester.assertComponent("addGame:submit", AjaxButton.class);

        tester.assertComponent("createAuthor", ModalWindow.class);
        tester.assertComponent("addGame:createLabel", AjaxButton.class);
        tester.assertComponent("createGroup", ModalWindow.class);
        tester.assertComponent("addGame:createGroup", AjaxButton.class);
        tester.assertComponent("createLabel", ModalWindow.class);
        tester.assertComponent("addGame:createAuthorBtn", AjaxButton.class);
    }
}
