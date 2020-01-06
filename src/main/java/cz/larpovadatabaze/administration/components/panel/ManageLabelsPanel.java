package cz.larpovadatabaze.administration.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.CRUDService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.List;

/**
 * This panel shows all labels and allows to change their status.
 * Label can be required or optional.
 * To be used by everyone, it needs to be approved.
 * It is also possible to delete Label.
 */
public class ManageLabelsPanel extends AbstractCsldPanel<List<Label>> {
    @SpringBean
    CRUDService<Label, Integer> labels;

    Form<List<Label>> manageLabels;
    HiddenField<String> editedLabelId;
    TextArea<String> newLabelDescription;
    PackageTextTemplate initJS;

    private class LabelsModel extends LoadableDetachableModel<List<Label>> {
        @Override
        protected List<Label> load() {
            return labels.getAll();
        }
    }

    public ManageLabelsPanel(String id) {
        super(id);
        setDefaultModel(new LabelsModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        manageLabels = new Form<>("manageLabels") {
            @Override
            protected void onSubmit() {
                if (StringUtils.isNotEmpty(editedLabelId.getModelObject())) {
                    Label label = labels.getById(Integer.parseInt(editedLabelId.getModelObject()));
                    label.setDescription(newLabelDescription.getModelObject());
                    labels.saveOrUpdate(label);
                }
            }
        };

        ListView<Label> labelsView = new ListView<cz.larpovadatabaze.common.entities.Label>("labels", getModel()) {
            @Override
            protected void populateItem(final ListItem<Label> item) {
                final cz.larpovadatabaze.common.entities.Label label = item.getModelObject();
                org.apache.wicket.markup.html.basic.Label name = new org.apache.wicket.markup.html.basic.Label("name", Model.of(label.getName()));
                item.add(name);

                org.apache.wicket.markup.html.basic.Label description = new org.apache.wicket.markup.html.basic.Label("description", Model.of(label.getDescription()));
                item.add(description);

                WebMarkupContainer editBtn = new WebMarkupContainer("editBtn");
                editBtn.add(new AttributeModifier("data-id", label.getId()));
                item.add(editBtn);

                final AjaxButton require = new AjaxButton("require") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        final cz.larpovadatabaze.common.entities.Label iLabel = labels.getById(label.getId());

                        if (iLabel.getRequired() == null) {
                            iLabel.setRequired(false);
                        }
                        iLabel.setRequired(!iLabel.getRequired());
                        labels.saveOrUpdate(iLabel);

                        target.add(this);
                    }
                };
                require.setOutputMarkupId(true);
                require.add(new AttributeAppender("class", (IModel<String>) () ->
                        Boolean.TRUE.equals(item.getModelObject().getRequired()) ? "selected" : "", " "));
                item.add(require);

                AjaxButton accept = new AjaxButton("accept") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        final cz.larpovadatabaze.common.entities.Label iLabel = labels.getById(label.getId());

                        if (iLabel.getAuthorized() == null) {
                            iLabel.setAuthorized(false);
                        }
                        iLabel.setAuthorized(!iLabel.getAuthorized());
                        labels.saveOrUpdate(iLabel);
                        target.add(this);
                    }
                };
                accept.add(new AttributeAppender("class", (IModel<String>) () ->
                        Boolean.TRUE.equals(item.getModelObject().getAuthorized()) ? "selected" : "", " "));
                accept.setOutputMarkupId(true);
                item.add(accept);

                AjaxButton remove = new AjaxButton("remove") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        final cz.larpovadatabaze.common.entities.Label iLabel = labels.getById(label.getId());
                        labels.remove(iLabel);
                        target.add(ManageLabelsPanel.this);
                    }
                };
                item.add(remove);
            }
        };
        manageLabels.add(labelsView);

        editedLabelId = new HiddenField<String>("editedLabelId", Model.of(""));
        editedLabelId.setMarkupId("editedLabelId");
        editedLabelId.setOutputMarkupId(true);
        manageLabels.add(editedLabelId);

        newLabelDescription = new TextArea<String>("newLabelDescription", Model.of(""));
        newLabelDescription.setMarkupId("newLabelDescription");
        newLabelDescription.setOutputMarkupId(true);
        manageLabels.add(newLabelDescription);

        add(manageLabels);

        initJS = new PackageTextTemplate(ManageLabelsPanel.class, "ManageLabelsPanel.js");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(OnDomReadyHeaderItem.forScript(initJS.getString()));
    }
}
