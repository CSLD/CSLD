package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.AbstractListEventPanel;
import cz.larpovadatabaze.calendar.component.panel.FilterEventsSidePanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.GeographicalFilter;
import cz.larpovadatabaze.calendar.service.SortableEventProvider;
import cz.larpovadatabaze.components.common.FilterablePage;
import cz.larpovadatabaze.components.common.tabs.TabsComponentPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterEvent;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class ListEventsPage extends CsldBasePage implements FilterablePage {
    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private transient Environment environment;

    private enum TabContentType {LIST, MAP, CALENDAR}
    private ListEventsPage.TabNumberModel tabNumberModel;
    private WebMarkupContainer tabContent;
    private ArrayList<ListEventsPage.TabContentType> tabContentType;

    private WebMarkupContainer requiredLabelsWrapper;
    private FilterEventsSidePanel sidePanel;
    private final IModel<FilterEvent> filterModel;

    private AbstractListEventPanel eventsList;

    /**
     * Model for selected tab number
     */
    private class TabNumberModel implements IModel<Integer> {
        private Integer value;

        private TabNumberModel(int initialValue) {
            this.value = initialValue;
        }

        @Override
        public Integer getObject() {
            return value;
        }

        @Override
        public void setObject(Integer newValue) {
            if (!value.equals(newValue)) {
                // Value changed
                this.value = newValue;

                // Replace tab panel
                addOrReplaceTabContentPanel();

                // Redraw

                Optional<AjaxRequestTarget> optionalArt = RequestCycle.get().find(AjaxRequestTarget.class);
                optionalArt.ifPresent(ajaxRequestTarget -> ajaxRequestTarget.add(tabContent));
            }
        }

        @Override
        public void detach() {
            // Nothing to do
        }
    }


    public ListEventsPage() {
        if (!Boolean.parseBoolean(environment.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }

        URL pathToShape = GeographicalFilter.class.getResource("CZE_adm1.shp");

        FilterEvent defaultFilter = new FilterEvent(new GeographicalFilter(pathToShape));
        defaultFilter.setFrom(Calendar.getInstance().getTime());
        filterModel = new Model(defaultFilter);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addTabComponent();

        // Tab content
        tabContent = new WebMarkupContainer("tabContent");
        tabContent.setOutputMarkupId(true);
        add(tabContent);
        addOrReplaceTabContentPanel();

        // Required labels
        requiredLabelsWrapper = new WebMarkupContainer("requiredLabelsWrapper");
        requiredLabelsWrapper.setOutputMarkupId(true);
        add(requiredLabelsWrapper);
        requiredLabelsWrapper.add(new ListView<Label>("requiredLabels", filterModel.getObject().getRequiredLabels()) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                final Label labelObj = item.getModelObject();

                // Add wrapping link
                AjaxLink link = new AjaxLink<Label>("requiredOne", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        // Set just this link to model
                        FilterEvent filter = filterModel.getObject();
                        filter.getRequiredLabels().clear();
                        filter.getRequiredLabels().add(labelObj);

                        // Refresh
                        filterChanged(false, true, false);
                    }
                };
                link.add(new org.apache.wicket.markup.html.basic.Label("text", WordUtils.capitalize(labelObj.getName())));
                item.add(link);

                // Add remove link (we must do it by behavior because it is link within link)
                WebMarkupContainer removeLink = new WebMarkupContainer("removeLink");
                link.add(removeLink);
                removeLink.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        FilterEvent filter = filterModel.getObject();
                        filter.getRequiredLabels().remove(labelObj);

                        filterChanged(false, true, false);
                    }
                });
            }
        });

        sidePanel = new FilterEventsSidePanel("filterEvents", filterModel);
        sidePanel.setOutputMarkupId(true);
        add(sidePanel);
    }

    public void filterChanged(boolean sort, boolean requiredLabelsChanged, boolean otherLabelsChanged) {
        // Re-render what is needed
        Optional<AjaxRequestTarget> optionalArt = getRequestCycle().find(AjaxRequestTarget.class);
        if(!optionalArt.isPresent()) {
            return;
        }

        AjaxRequestTarget art = optionalArt.get();

        art.add(eventsList);

        if (requiredLabelsChanged) {
            art.add(requiredLabelsWrapper);
            art.add(sidePanel.getRequiredLabelsWrapper());
        }

        if (otherLabelsChanged) {
            art.add(sidePanel);
        }
    }

    private void addOrReplaceTabContentPanel() {
        Fragment fragment;

        if (tabContentType.get(tabNumberModel.getObject()) == TabContentType.LIST) {// Create list representing the
            fragment = new Fragment("tabContentPanel", "events", this);

            eventsList = new AbstractListEventPanel<Event>("eventsPanel") {
                @Override
                protected SortableDataProvider<Event, String> getDataProvider() {
                    return new SortableEventProvider(sessionFactory, filterModel);
                }
            };
            eventsList.setOutputMarkupId(true);

            fragment.add(eventsList);
        } else {
            throw new IllegalStateException("Invalid tab content type");
        }

        tabContent.addOrReplace(fragment);
    }

    protected void addTabComponent() {
        tabNumberModel = new ListEventsPage.TabNumberModel(0);
        List<IModel> models = new ArrayList<>();
        tabContentType = new ArrayList<>();

        // List
        models.add(Model.of(getString("events")));
        tabContentType.add(ListEventsPage.TabContentType.LIST);

        add(new TabsComponentPanel("tabs", tabNumberModel, models.toArray(new IModel[models.size()])));
    }
}
