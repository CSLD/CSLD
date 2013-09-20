package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.ClassContentModel;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It shows all Labels and allows any amount of them to be chosen.
 * It is possible to get chosen labels from this panel.
 */
public class ChooseLabelsPanel extends Panel {
    @SpringBean
    LabelService labelService;

    private List<Label> requiredLabels;
    private List<Label> optionalLabels;
    private List<Label> chosen;

    private ListView<Label> labelsChooser;
    private ListView<Label> labelsChooserOptional;

    public ChooseLabelsPanel(String id, final List<Label> chosen){
        super(id);
        this.chosen = chosen;

        fillLabels();
        labelsChooser = new SimpleListViewer("requiredLabels", requiredLabels);
        add(labelsChooser);

        labelsChooserOptional = new SimpleListViewer("otherLabels", optionalLabels);
        add(labelsChooserOptional);
    }

    public ChooseLabelsPanel(String id) {
        this(id, new ArrayList<Label>());
    }

    private void fillLabels(){
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();

        List<Label> allLabels = labelService.getAll();
        requiredLabels = new ArrayList<Label>();
        optionalLabels = new ArrayList<Label>();
        for(Label label: allLabels){
            if(label.getAuthorized() != null && !label.getAuthorized()) {
                if(!label.getAddedBy().equals(logged)) {
                    continue;
                }
            }

            if(label.getRequired() != null && label.getRequired()){
                requiredLabels.add(label);
            } else {
                optionalLabels.add(label);
            }
        }
    }

    public List<Label> getSelected() {
        List<Label> selected = new ArrayList<Label>();
        for(Label label: requiredLabels) {
            if(label.isSelected()) {
                selected.add(label);
            }
        }
        for(Label label: optionalLabels) {
            if(label.isSelected()) {
                selected.add(label);
            }
        }
        return selected;
    }

    public void reload(AjaxRequestTarget target) {
        fillLabels();

        labelsChooser.setList(requiredLabels);
        labelsChooserOptional.setList(optionalLabels);

        target.add(ChooseLabelsPanel.this);
    }

    private class SimpleListViewer extends ListView<Label> {
        public SimpleListViewer(String id, List<? extends Label> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<Label> item) {
            final Label actualLabel = item.getModelObject();
            final Button nameOfLabel = new Button("name");
            final ClassContentModel classContent = new ClassContentModel();
            nameOfLabel.add(new AttributeAppender("value",actualLabel.getName()));
            nameOfLabel.add(new AttributeModifier("class", classContent));
            nameOfLabel.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    classContent.select();
                    actualLabel.select();
                    target.add(nameOfLabel);
                }
            });
            if(chosen.contains(actualLabel)){
                classContent.select();
                actualLabel.select();
            }
            nameOfLabel.setOutputMarkupId(true);
            item.add(nameOfLabel);
        }
    }
}
