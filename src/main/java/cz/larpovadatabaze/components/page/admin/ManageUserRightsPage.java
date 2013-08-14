package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.models.ClassContentModel;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
@AuthorizeInstantiation({"Admin"})
public class ManageUserRightsPage extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public ManageUserRightsPage(){
        Form manageUsers = new Form("manageUser");

        final List<CsldUser> users = csldUserService.getAll();
        ListView<CsldUser> userView = new ListView<CsldUser>("users", users) {
            private String selected;

            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                final CsldUser user = item.getModelObject();
                String personName = String.format("%s %s", user.getPerson().getNickname(), user.getPerson().getName());
                Label name = new Label("name", Model.of(personName));
                item.add(name);


                final UserRole role = new UserRole();
                role.selected = CsldRoles.getNameByRole(user.getRole());
                Select<String> roles = new Select<String>("role", new PropertyModel<String>(role, "selected"));
                roles.add(new SelectOption<String>("user", new StringResourceModel("admin.user", this, null)));
                roles.add(new SelectOption<String>("editor", new StringResourceModel("admin.editor", this, null)));
                roles.add(new SelectOption<String>("admin", new StringResourceModel("admin.admin", this, null)));
                roles.setOutputMarkupId(true);
                roles.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        user.setRole(CsldRoles.getRoleByName(role.selected));
                        csldUserService.saveOrUpdate(user);
                    }
                });
                item.add(roles);
            }
        };
        manageUsers.add(userView);

        add(manageUsers);
    }

    private class UserRole {
        String selected;
    }
}
