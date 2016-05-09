package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

abstract public class AbstractListEventPanel<T> extends AbstractCsldPanel<T> {
    private SortableDataProvider<Event, String> sdp;

    public AbstractListEventPanel(String id) {
        super(id);
    }

    protected abstract SortableDataProvider<Event, String> getDataProvider();

    @Override
    protected void onInitialize() {
        super.onInitialize();

        sdp = getDataProvider();
        final DataView<Event> propertyList = new DataView<Event>("listEvents", sdp) {
            @Override
            protected void populateItem(Item<Event> item) {
                Event event = item.getModelObject();

                item.add(new BookmarkableLinkWithLabel("name",
                        DetailOfEventPage.class,
                        Model.of(event.getName()),
                        Model.of(DetailOfEventPage.pageParameters(event)))
                );
                item.add(new Label("loc", Model.of(event.getLoc())));
                item.add(new Label("date", Model.of(event.getDate())));
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }
}
