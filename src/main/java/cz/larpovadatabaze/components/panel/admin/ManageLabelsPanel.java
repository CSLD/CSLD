package cz.larpovadatabaze.components.panel.admin;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.ClassContentModel;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * This panel shows all labels and allows to change their status.
 * Label can be required or optional.
 * To be used by everyone, it needs to be approved.
 * It is also possible to delete Label.
 */
public class ManageLabelsPanel extends Panel {
    @SpringBean
    LabelService labelService;

    public ManageLabelsPanel(String id) {
        super(id);

        Form manageLabels = new Form("manageLabels");

        final List<Label> labels = labelService.getAll();
        ListView<Label> labelsView = new ListView<cz.larpovadatabaze.entities.Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                final cz.larpovadatabaze.entities.Label label = item.getModelObject();
                org.apache.wicket.markup.html.basic.Label name = new org.apache.wicket.markup.html.basic.Label("name", Model.of(label.getName()));
                item.add(name);

                org.apache.wicket.markup.html.basic.Label description = new org.apache.wicket.markup.html.basic.Label("description", Model.of(label.getDescription()));
                item.add(description);

                final ClassContentModel classRequireContent = new ClassContentModel();
                if(label.getRequired() != null && label.getRequired()){
                    classRequireContent.select();
                }
                AjaxButton require = new AjaxButton("require") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        if(label.getRequired() == null){
                            label.setRequired(false);
                        }
                        label.setRequired(!label.getRequired());
                        classRequireContent.select();
                        labelService.saveOrUpdate(label);
                    }
                };
                require.add(new AttributeModifier("class", classRequireContent));
                item.add(require);

                final ClassContentModel classAcceptContent = new ClassContentModel();
                if(label.getAuthorized() != null && label.getAuthorized()){
                    classAcceptContent.select();
                }
                AjaxButton accept = new AjaxButton("accept") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        if(label.getAuthorized() == null){
                            label.setAuthorized(false);
                        }
                        label.setAuthorized(!label.getAuthorized());
                        classAcceptContent.select();
                        labelService.saveOrUpdate(label);
                    }
                };
                accept.add(new AttributeModifier("class", classAcceptContent));
                item.add(accept);

                AjaxButton remove = new AjaxButton("remove") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        labelService.remove(label);
                        labels.remove(label);
                        target.add(ManageLabelsPanel.this);
                    }
                };
                item.add(remove);
            }
        };
        manageLabels.add(labelsView);

        add(manageLabels);
    }
}
