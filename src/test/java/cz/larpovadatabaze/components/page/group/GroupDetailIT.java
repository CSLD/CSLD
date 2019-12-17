package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import org.junit.Test;

/**
 *
 */
public class GroupDetailIT extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        startPage();

        tester.assertComponent("groupDetail", GroupDetailPanel.class);
        tester.assertComponent("annotatedGamesOfAuthor", ListGamesWithAnnotations.class);
    }

    @Test
    public void runAsUser() {
        TestUtils.logUser(masqueradeBuilder.getUser());
        startPage();

        tester.assertComponent("addGroup", AddGroupPanel.class);
    }

    @Test
    public void runAsEditor() {
        TestUtils.logUser(masqueradeBuilder.getEditor());
        startPage();

        tester.assertComponent("editGroup", EditGroupPanel.class);
    }

    private void startPage() {
        tester.startPage(GroupDetail.class, GroupDetail.paramsForGroup(masqueradeBuilder.getNosferatu()));
        tester.assertRenderedPage(GroupDetail.class);
    }
}
