package cz.larpovadatabaze.components.common.tabs;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michal Kara
 * Date: 27.12.13
 * Time: 14:25
 */
public class TabsComponentPanel extends Panel {
    private static final String ACTIVE_CLASS = "active";

    private final IModel<String> labelModels[];

    private WebMarkupContainer wrapper;

    /**
     * @return Wrapper, for refreshing this component
     */
    public WebMarkupContainer getWrapper() {
        return wrapper;
    }

    /**
     * @param id Component id
     * @param model Model of currently selected tab index (0-based)
     * @param labelModels Models for labels
     */
    public TabsComponentPanel(String id, IModel<Integer> model, IModel<String> labelModels[]) {
        super(id, model);
        this.labelModels = labelModels;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Prepare list of indices
        List<Integer> list = new ArrayList<Integer>();
        for(int i=0; i<labelModels.length; i++) list.add(i);

        wrapper = new WebMarkupContainer("wrapper");
        wrapper.setOutputMarkupId(true);
        add(wrapper);

        // Create tabs view
        wrapper.add(new ListView<Integer>("tabs", list) {

            @Override
            protected void populateItem(final ListItem<Integer> item) {
                final int idx = item.getModelObject();

                // Add appender to set the active css class
                item.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return (item.getModelObject().equals(TabsComponentPanel.this.getDefaultModelObject())) ? ACTIVE_CLASS : "";
                    }
                }, " "));

                // Label
                AjaxLink link = new AjaxLink<Integer>("link", Model.of(idx)) {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        // Set new item
                        ((IModel<Integer>) TabsComponentPanel.this.getDefaultModel()).setObject(getModelObject());

                        // Redraw tabs
                        ajaxRequestTarget.add(wrapper);
                    }
                };
                item.add(link);

                link.add(new Label("label", labelModels[idx]));
            }
        });
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        // Detach label models
        for(IModel labelModel : labelModels) labelModel.detach();
    }
}
