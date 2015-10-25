package cz.larpovadatabaze.components.page.game;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.page.user.CsldSignInPage;
import cz.larpovadatabaze.components.panel.UploadCoverImagePanel;
import cz.larpovadatabaze.components.panel.game.ChooseLabelsPanel;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.components.panel.game.RatingsDisabledPanel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Test;

import java.awt.*;

/**
 *
 */
public class CreateGameTest extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CsldSignInPage.class);
    }

    @Test
    public void runAsUser() {
        TestUtils.logUser(masqueradeBuilder.getUser());

        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CreateOrUpdateGamePage.class);

        tester.assertComponent("createOrUpdateGame", CreateOrUpdateGamePanel.class);
    }
}
