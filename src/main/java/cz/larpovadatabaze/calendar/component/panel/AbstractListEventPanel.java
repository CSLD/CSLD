package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.EventModel;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class AbstractListEventPanel<T> extends AbstractCsldPanel<T> {
    @SpringBean
    private Events events;

    public AbstractListEventPanel(String id) {
        super(id);
    }

    protected abstract SortableDataProvider<Event, String> getDataProvider();

    private class SqlEventModel extends EventModel {
        public SqlEventModel(Integer id) {
            super(id);
        }

        @Override
        public Events getEvents() {
            return events;
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SortableDataProvider<Event, String> sdp = getDataProvider();
        final DataView<Event> propertyList = new DataView<>("listEvents", sdp) {
            @Override
            protected void populateItem(Item<Event> item) {
                Event event = item.getModelObject();

                item.add(new EventNameAndLabelsPanel("nameAndLabels", new SqlEventModel(event.getId())));
                item.add(new Label("loc", Model.of(event.getLoc())));
                item.add(new Label("date", event.getDate()));
                item.add(new ExternalLink("web", Model.of(event.getWeb()), Model.of(event.getWeb())).setVisible(StringUtils.isNotBlank(event.getWeb())));
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }
}
