package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;

/**
 * It shows all Labels and allows any amount of them to be chosen.
 * It is possible to get chosen labels from this panel.
 */
public class ChooseLabelsPanel extends FormComponentPanel<List<cz.larpovadatabaze.entities.Label>> {
    @SpringBean
    LabelService labelService;

    public ChooseLabelsPanel(String id, IModel<List<cz.larpovadatabaze.entities.Label>> model){
        super(id, model);

        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        add(new SimpleListViewer("requiredLabels", labelService.getAuthorizedRequired(logged)));
        add(new SimpleListViewer("otherLabels", labelService.getAuthorizedOptional(logged)));
    }

    public void reload(AjaxRequestTarget target) {
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        ((ListView)get("requiredLabels")).setList(labelService.getAuthorizedRequired(logged));
        ((ListView)get("otherLabels")).setList(labelService.getAuthorizedOptional(logged));

        target.add(ChooseLabelsPanel.this);
    }

    private class SimpleListViewer extends ListView<cz.larpovadatabaze.entities.Label> {
        public SimpleListViewer(String id, List<? extends cz.larpovadatabaze.entities.Label> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
            final cz.larpovadatabaze.entities.Label ourLabel = item.getModelObject();
            item.add(new CheckBox("checkbox", new IModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return ChooseLabelsPanel.this.getModelObject().contains(ourLabel);
                }

                @Override
                public void setObject(Boolean object) {
                    if (Boolean.TRUE.equals(object)) {
                        if (!ChooseLabelsPanel.this.getModelObject().contains(ourLabel)) {
                            ChooseLabelsPanel.this.getModelObject().add(ourLabel);
                        }
                        ourLabel.setSelected(true);
                    }
                    else {
                        ChooseLabelsPanel.this.getModelObject().remove(ourLabel);
                        ourLabel.setSelected(false);
                    }
                }

                @Override
                public void detach() {
                }
            }));

            item.add(new Label("name", ourLabel.getName()));

            /*
            Label tooltip = new Label("tooltip", actualLabel.getDescription());
            tooltip.setVisible(StringUtils.isNotBlank(actualLabel.getDescription()));
            item.add(tooltip);
            */
        }
    }
}
