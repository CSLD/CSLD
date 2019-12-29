package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.games.components.panel.ListGamesWithAnnotations;
import cz.larpovadatabaze.users.components.page.GroupDetail;
import cz.larpovadatabaze.users.components.panel.AddGroupPanel;
import cz.larpovadatabaze.users.components.panel.EditGroupPanel;
import cz.larpovadatabaze.users.components.panel.GroupDetailPanel;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
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
