package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.common.RoleBasedCheckbox;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.dto.SelectedUser;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.FilterService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * This Panel gets List of Users and simply creates list of them with links to their details.
 */
public class CheckBoxSelectionUsers extends Panel {
    private final IModel<List<SelectedUser>> model;
    @SpringBean
    private FilterService filterService;

    public CheckBoxSelectionUsers(String id, IModel<List<CsldUser>> pModel) {
        super(id);
        List<SelectedUser> users = new ArrayList<SelectedUser>();
        for(CsldUser user: pModel.getObject()){
            SelectedUser userDto = new SelectedUser();
            userDto.setUserName(user.getPerson().getName());
            userDto.setNickName(user.getPerson().getNickNameView());
            userDto.setEmail(user.getPerson().getEmail());
            userDto.setId(user.getId());
            users.add(userDto);
        }
        model = new IModel<List<SelectedUser>>() {
            private List<SelectedUser> users;

            @Override
            public List<SelectedUser> getObject() {
                return users;
            }

            @Override
            public void setObject(List<SelectedUser> selectedUsers) {
                users = selectedUsers;
            }

            @Override
            public void detach() {}
        };
        model.setObject(users);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ListView<SelectedUser> users  = new ListView<SelectedUser>("users", model) {
            @Override
            protected void populateItem(ListItem<SelectedUser> item) {
                final SelectedUser user = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", user.getId());

                final BookmarkablePageLink<CsldUser> authorName =
                        new BookmarkablePageLink<CsldUser>("user", UserDetail.class, params);

                authorName.add(new Label("userName", user.getUserName()));
                authorName.add(new Label("userNickname", user.getNickName()));

                List<CsldRoles> allowedRoles = new ArrayList<CsldRoles>();
                allowedRoles.add(CsldRoles.ADMIN);
                allowedRoles.add(CsldRoles.EDITOR);
                allowedRoles.add(CsldRoles.AUTHOR);
                item.add(new RoleBasedCheckbox("selectUser", new PropertyModel<Boolean>(user, "selected"), allowedRoles){
                    @Override
                    public CsldRoles getRoleOfActualUser() {
                        return CheckBoxSelectionUsers.this.getRoleOfActualUser();
                    }
                });
                item.add(authorName);
            }
        };

        add(users);
    }

    /**
     * To be overriden in most cases.
     */
    protected CsldRoles getRoleOfActualUser() {
        return CsldRoles.ANONYMOUS;
    }

    public List<SelectedUser> getSelectedUsers() {

        //noinspection unchecked
        return (List<SelectedUser>) filterService.filterByPropertyName(model.getObject(),"selected", Boolean.TRUE);
    }
}
