package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.FilterablePage;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: Michal Kara Date: 29.3.15 Time: 17:46
 */
public class FilterByLabelsPanel extends FormComponentPanel<List<Label>> {
    @SpringBean
    Labels labels;

    private final List<Label> possibleLabels;

    private final boolean forRequiredLabels;

    public FilterByLabelsPanel(String id, List<Label> possibleLabels, boolean forRequiredLabels) {
        super(id);
        this.possibleLabels = possibleLabels;
        this.forRequiredLabels = forRequiredLabels;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ListView<Label>("labels", possibleLabels) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                final Label ourLabel = item.getModelObject();
                CheckBox cb = new CheckBox("checkbox", new IModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return FilterByLabelsPanel.this.getModelObject().contains(ourLabel);
                    }

                    @Override
                    public void setObject(Boolean object) {
                        List<Label> labels = FilterByLabelsPanel.this.getModelObject();
                        if (Boolean.TRUE.equals(object)) {
                            // Add
                            if (!labels.contains(ourLabel)) {
                                labels.add(ourLabel);
                            }
                        }
                        else {
                            // Remove
                            labels.remove(ourLabel);
                        }
                    }

                    @Override
                    public void detach() {
                        // Nothing to do
                    }
                });

                item.add(cb);

                cb.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        // Refresh
                        ((FilterablePage)getPage()).filterChanged(false, forRequiredLabels, !forRequiredLabels);
                    }
                });

                item.add(new org.apache.wicket.markup.html.basic.Label("label", WordUtils.capitalize(ourLabel.getName())));
            }
        });

        WebMarkupContainer showAllWrapper = new WebMarkupContainer("showAllWrapper");
        showAllWrapper.setVisible(false); // TODO - show when we have too many labels - TODo
        add(showAllWrapper);
    }

}
