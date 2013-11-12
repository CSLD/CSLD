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
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It shows all Labels and allows any amount of them to be chosen.
 * It is possible to get chosen labels from this panel.
 */
public class ChooseLabelsPanel extends FormComponentPanel<ArrayList<Label>> {
    @SpringBean
    LabelService labelService;
    private List<LabelButton> buttons;

    public ChooseLabelsPanel(String id){
        super(id, null);
        buttons = new ArrayList<LabelButton>();

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();

        add(new SimpleListViewer("requiredLabels", labelService.getAuthorizedRequired(logged)));
        add(new SimpleListViewer("otherLabels", labelService.getAuthorizedOptional(logged)));
    }

    public void reload(AjaxRequestTarget target) {
        buttons = new ArrayList<LabelButton>();
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();

        ((ListView)get("requiredLabels")).setList(labelService.getAuthorizedRequired(logged));
        ((ListView)get("otherLabels")).setList(labelService.getAuthorizedOptional(logged));

        target.add(ChooseLabelsPanel.this);
    }

    @Override
    protected void convertInput() {
        ArrayList<Label> selected = new ArrayList<Label>();
        for(LabelButton button: buttons){
            if(button.getLabelModel().getObject().isSelected()){
                selected.add(button.getLabelModel().getObject());
            }
        }
        setConvertedInput(selected);
    }

    private class SimpleListViewer extends ListView<Label> {
        public SimpleListViewer(String id, List<? extends Label> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<Label> item) {
            Label actualLabel = item.getModelObject();
            if(!(ChooseLabelsPanel.this.getModelObject() instanceof ArrayList)){
                if(ChooseLabelsPanel.this.getModelObject() != null) {
                    ChooseLabelsPanel.this.setModelObject(new ArrayList<Label>(ChooseLabelsPanel.this.getModelObject()));
                } else {
                    ChooseLabelsPanel.this.setModelObject(new ArrayList<Label>());
                }
            }
            if(ChooseLabelsPanel.this.getModelObject() != null &&
                    ChooseLabelsPanel.this.getModelObject().contains(actualLabel)){
                actualLabel.setSelected(true);
            }

            LabelButton button = new LabelButton("name", Model.of(actualLabel.getName()), Model.of(actualLabel));
            boolean notFound = true;
            for(LabelButton buttonTmp: buttons){
                if(buttonTmp.getLabelModel().getObject().equals(actualLabel)){
                    notFound = false;
                }
            }
            if(notFound){
                buttons.add(button);
            }
            item.add(button);
        }
    }
}
