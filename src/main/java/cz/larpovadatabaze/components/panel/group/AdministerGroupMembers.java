package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.GroupHasMember;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GroupHasMemberService;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * It allows to modify authors and administrators of group.
 */
public abstract class AdministerGroupMembers extends Panel {
    @SpringBean
    GroupService groupService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GroupHasMemberService groupHasMemberService;

    private List<CsldUser> administratorsOfGroup;
    private List<GroupHasMember> membersOfGroup;
    private CsldGroup group;

    public AdministerGroupMembers(String id, CsldGroup group) {
        super(id);
        this.group = group;

        final ValidatableForm administerGroup = new ValidatableForm("administerGroup");

        addAdministratorsToForm(administerGroup, group);
        ManageGroupAuthorsPanel manageGroupAuthorsPanel = new ManageGroupAuthorsPanel("authors", group);
        manageGroupAuthorsPanel.setOutputMarkupId(true);
        membersOfGroup = manageGroupAuthorsPanel.getGroupMembers();
        administerGroup.add(manageGroupAuthorsPanel);

        administerGroup.add(new AjaxButton("submit", new StringResourceModel("form.addAuthor", this, null)) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if(administerGroup.isValid()) {
                    saveGroup();
                    onCsldAction(target, form);
                }
            }
        });
        add(administerGroup);
    }

    private void saveGroup() {
        group.setAdministrators(administratorsOfGroup);
        groupHasMemberService.removeAllMembersOfGroup(group);
        for(GroupHasMember memberOfGroup: membersOfGroup){
            memberOfGroup.setGroup(group);
            memberOfGroup.setUser(memberOfGroup.getUser());
            groupHasMemberService.saveOrUpdate(memberOfGroup);
        }
        groupService.saveOrUpdate(group);
    }

    private void addAdministratorsToForm(Form administerGroup, CsldGroup group) {
        IFactory<CsldUser> userIFactory = new GenericFactory<CsldUser>(CsldUser.class);
        IValidator<CsldUser> userIValidator = new GenericValidator<CsldUser>(csldUserService);

        RepeatableInputPanel<CsldUser> authors = new RepeatableInputPanel<CsldUser>("administrators", userIFactory,
                userIValidator, group.getAdministrators(), csldUserService);
        administerGroup.add(authors);
        administratorsOfGroup = authors.getModelObject();
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
