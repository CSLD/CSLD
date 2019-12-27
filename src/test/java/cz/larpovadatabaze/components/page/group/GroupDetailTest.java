package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.services.CsldGroups;
import cz.larpovadatabaze.services.CsldUsers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class GroupDetailTest extends WithWicket {
    @Autowired
    CsldUsers users;
    @Autowired
    CsldGroups groups;

    @Test
    public void runAsGuest() {
        startPage();

        tester.assertComponent("groupDetail", GroupDetailPanel.class);
        tester.assertComponent("annotatedGamesOfAuthor", ListGamesWithAnnotations.class);
    }

    @Test
    public void runAsUser() {
        TestUtils.logUser(users.getById(3));
        startPage();

        tester.assertComponent("addGroup", AddGroupPanel.class);
    }

    @Test
    public void runAsEditor() {
        TestUtils.logUser(users.getById(2));
        startPage();

        tester.assertComponent("editGroup", EditGroupPanel.class);
    }

    private void startPage() {
        tester.startPage(GroupDetail.class, GroupDetail.paramsForGroup(groups.getById(1)));
        tester.assertRenderedPage(GroupDetail.class);
    }
}
