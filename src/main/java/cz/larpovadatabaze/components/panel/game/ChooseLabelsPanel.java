package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.ClassContentModel;
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
    private LabelService labelService;
    private List<Label> allLabels;

    public ChooseLabelsPanel(String id, final List<Label> chosen){
        super(id);

        allLabels = labelService.getAll();
        ListView<Label> labelsChooser = new ListView<Label>("labels", allLabels) {
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
        };
        add(labelsChooser);
    }

    public ChooseLabelsPanel(String id) {
        this(id, new ArrayList<Label>());
    }

    public List<Label> getSelected() {
        List<Label> selected = new ArrayList<Label>();
        for(Label label: allLabels) {
            if(label.isSelected()) {
                selected.add(label);
            }
        }
        return selected;
    }
}
