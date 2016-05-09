package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.AbstractListEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.SortableEventProvider;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;

public class ListEventsPage extends CsldBasePage {
    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private Environment environment;

    public ListEventsPage(){
        if(!Boolean.parseBoolean(environment.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }
    }

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
