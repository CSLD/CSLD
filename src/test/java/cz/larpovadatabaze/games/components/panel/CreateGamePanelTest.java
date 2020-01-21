package cz.larpovadatabaze.games.components.panel;

import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.common.api.ValidatableForm;
import cz.larpovadatabaze.common.components.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CreateGamePanelTest extends WithWicket {
    @Autowired
    CsldUsers users;
    @Autowired
    Games games;

    @Test
    public void runAsUser() {
        TestUtils.logUser(users.getById(3));

        tester.startComponentInPage(new CreateOrUpdateGamePanel("id", Model.of(games.getById(1))) {
        });

        tester.assertComponent("id:addGame", ValidatableForm.class);
        tester.assertComponent("id:addGame:feedback", FeedbackPanel.class);
        tester.assertComponent("id:addGame:name", RequiredTextField.class);
        tester.assertComponent("id:addGame:nameFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:labels", ChooseLabelsPanel.class);
        tester.assertComponent("id:addGame:descriptionWrapper:description", WysiwygEditor.class);
        tester.assertComponent("id:addGame:descriptionWrapper:descriptionFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:year", TextField.class);
        tester.assertComponent("id:addGame:yearFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:players", TextField.class);
        tester.assertComponent("id:addGame:playersFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:womenRole", TextField.class);
        tester.assertComponent("id:addGame:womenRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:menRole", TextField.class);
        tester.assertComponent("id:addGame:menRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:bothRole", TextField.class);
        tester.assertComponent("id:addGame:bothRoleFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:hours", TextField.class);
        tester.assertComponent("id:addGame:hoursFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:days", TextField.class);
        tester.assertComponent("id:addGame:daysFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:web", TextField.class);
        tester.assertComponent("id:addGame:webFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:photoAuthor", TextField.class);
        tester.assertComponent("id:addGame:photoAuthorFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:galleryURL", TextField.class);
        tester.assertComponent("id:addGame:galleryURLFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:video", TextField.class);
        tester.assertComponent("id:addGame:videoFeedback", CsldFeedbackMessageLabel.class);
        tester.assertComponent("id:addGame:coverImage", UploadCoverImagePanel.class);
        tester.assertComponent("id:addGame:ratingsDisabled", CheckBox.class);
        tester.assertComponent("id:addGame:commentsDisabled", CheckBox.class);

        tester.assertComponent("id:createAuthor", ModalWindow.class);
        tester.assertComponent("id:createGroup", ModalWindow.class);
        tester.assertComponent("id:createLabel", ModalWindow.class);
    }
}
