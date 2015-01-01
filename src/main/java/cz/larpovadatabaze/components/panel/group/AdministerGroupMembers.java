package cz.larpovadatabaze.components.panel.group;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.GenericFactory;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.GenericValidator;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IFactory;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.RepeatableInputPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidator;

import java.util.List;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.GroupHasMember;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GroupHasMemberService;
import cz.larpovadatabaze.services.GroupService;

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

    private final IModel<CsldGroup> model;
    private ManageGroupAuthorsPanel manageGroupAuthorsPanel;
    private RepeatableInputPanel<CsldUser> authors;

    public AdministerGroupMembers(String id, IModel<CsldGroup> model) {
        super(id, model);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm administerGroup = new ValidatableForm("administerGroup");

        addAdministratorsToForm(administerGroup, model.getObject());
        // Copy list on model creation so it persists between requests
        IModel<List<GroupHasMember>> listModel = (IModel<List<GroupHasMember>>)(Object)Model.ofList(model.getObject().getMembers()); // Casting via Object needed to work around compiler bug
        manageGroupAuthorsPanel = new ManageGroupAuthorsPanel("authors", listModel);
        manageGroupAuthorsPanel.setOutputMarkupId(true);
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
        // Get group
        CsldGroup group = model.getObject();

        // Copy members from panel model
        groupHasMemberService.removeAllMembersOfGroup(group);
        for(GroupHasMember memberOfGroup: manageGroupAuthorsPanel.getGroupMembers()){
            memberOfGroup.setGroup(group);
            memberOfGroup.setUser(memberOfGroup.getUser());
            groupHasMemberService.saveOrUpdate(memberOfGroup);
        }

        // Copy administrators from panel model
        group.setAdministrators((List<CsldUser>)authors.getDefaultModelObject());

        // Save
        groupService.saveOrUpdate(group);
    }

    private void addAdministratorsToForm(Form administerGroup, CsldGroup group) {
        IFactory<CsldUser> userIFactory = new GenericFactory<CsldUser>(CsldUser.class);
        IValidator<CsldUser> userIValidator = new GenericValidator<CsldUser>(csldUserService);

        // Copy list on model creation so it persists between requests
        IModel<List<CsldUser>> listModel = (IModel<List<CsldUser>>)(Object)Model.ofList(model.getObject().getAdministrators()); // Casting via Object needed to work around compiler bug
        authors = new RepeatableInputPanel<CsldUser>("administrators", userIFactory,
                userIValidator, listModel.getObject(), csldUserService);
        authors.setDefaultModel(listModel);
        administerGroup.add(authors);
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
