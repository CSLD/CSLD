package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.calendar.component.panel.AbstractListEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.SortableEventProvider;
import cz.larpovadatabaze.components.page.CsldBasePage;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;

public class ListEventsPage extends CsldBasePage {
    @SpringBean
    private SessionFactory sessionFactory;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new AbstractListEventPanel<Event>("events") {
            @Override
            protected SortableDataProvider<Event, String> getDataProvider() {
                return new SortableEventProvider(sessionFactory);
            }
        });
    }
}
