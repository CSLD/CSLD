package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.CreateEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.EventModel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;

public class CreateOrUpdateEventPage extends CsldBasePage {
    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private Environment env;

    private class SqlEventModel extends EventModel {
        public SqlEventModel(Integer id) {
            super(id);
        }

        @Override
        public Session getSession() {
            return sessionFactory.getCurrentSession();
        }
    }

    public CreateOrUpdateEventPage(PageParameters params) {
        if(!Boolean.parseBoolean(env.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }

        if(!params.isEmpty()) {
            setDefaultModel(new SqlEventModel(params.get("id").to(Integer.class)));
        } else {
            setDefaultModel(new SqlEventModel(null));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CreateEventPanel("createEvent", (EventModel)getDefaultModel()) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                CsldAuthenticatedWebSession session = CsldAuthenticatedWebSession.get();
                session.requestClear();

                throw new RestartResponseException(DetailOfEventPage.class, DetailOfEventPage.pageParameters((Event) object));
            }
        });
    }
}
