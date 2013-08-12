package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.GroupHasMember;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.cfg.beanvalidation.GroupsPerOperation;

import java.util.List;

/**
 * This Panel shows list of users allowing you to add new ones into it and removing existing ones.
 * When adding new users to this list it also wants additional information like since and to which date.
 */
public class ManageGroupAuthorsPanel extends Panel {
    private List<GroupHasMember> groupMembers;

    public ManageGroupAuthorsPanel(String id, CsldGroup group) {
        super(id);

        groupMembers = group.getMembers();
        init();
    }

    private void init() {
        // Create List of members
        createListOfMembers();
        //Create addButton - onClick it shows modal window adding author.
        createAddButton();
    }

    private void createListOfMembers() {
        ListView<GroupHasMember> membersOfGroup = new ListView<GroupHasMember>("groupMembers", groupMembers) {
            @Override
            protected void populateItem(ListItem<GroupHasMember> item) {
                final GroupHasMember member = item.getModelObject();

                Label name = new Label("name", Model.of(member.getUser().getPerson().getName()));
                item.add(name);
                Label since = new Label("since", Model.of(member.getStart()));
                item.add(since);
                Label to = new Label("to", Model.of(member.getEnd()));
                item.add(to);

                Label remove = new Label("remove", new StringResourceModel("form.remove", this, null));
                remove.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        groupMembers.remove(member);
                        target.add(ManageGroupAuthorsPanel.this);
                    }
                });
                remove.setOutputMarkupId(true);
                item.add(remove);
            }
        };
        add(membersOfGroup);
    }

    private void createAddButton() {
        final ModalWindow addAuthorModal;
        add(addAuthorModal = new ModalWindow("addAuthor"));

        final AddAuthorToGroupPanel createAuthor = new AddAuthorToGroupPanel(addAuthorModal.getContentId()){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);
                addAuthorModal.close(target);

                GroupHasMember newMember = (GroupHasMember) form.getModelObject();
                groupMembers.add(newMember);

                target.add(ManageGroupAuthorsPanel.this);
            }
        };
        addAuthorModal.setContent(createAuthor);
        addAuthorModal.setTitle(new StringResourceModel("group.addAuthor", this, null));
        addAuthorModal.setCookieName("create-author");

        Label add = new Label("add", new StringResourceModel("form.addAuthor",this,null));
        add.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                addAuthorModal.show(target);
            }
        });
        add.setOutputMarkupId(true);
        add(add);
    }

    public List<GroupHasMember> getGroupMembers() {
        return groupMembers;
    }
}
