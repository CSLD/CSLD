package cz.larpovadatabaze;

import cz.larpovadatabaze.administration.rest.GCalSyncProducer;
import cz.larpovadatabaze.administration.rest.StatisticsProducer;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import cz.larpovadatabaze.calendar.service.ICalProducerResource;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.components.page.error.Error500Page;
import cz.larpovadatabaze.games.rest.GameProducer;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.graphql.GraphQLResource;
import cz.larpovadatabaze.search.services.TokenSearch;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.log4j.Logger;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.RequestLoggerSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
@Component(value = "wicketApplication")
public class Csld extends AuthenticatedWebApplication implements ApplicationContextAware {
    private final TokenSearch tokenSearch;
    private final CsldUsers csldUsers;
    private final Environment env;
    private final Events events;
    private final Statistics statistics;
    private final Games games;
    private final GraphQLResource graphqlResource;
    private final GoogleCalendarEvents googleCalendarEvents;

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static ApplicationContext ctx;

    @Autowired
    public Csld(TokenSearch tokenSearch, CsldUsers csldUsers,
                Environment env, Events events, Statistics statistics,
                Games games, GraphQLResource graphqlResource, GoogleCalendarEvents googleCalendarEvents) {
        this.tokenSearch = tokenSearch;
        this.csldUsers = csldUsers;
        this.env = env;
        this.events = events;
        this.statistics = statistics;
        this.games = games;
        this.graphqlResource = graphqlResource;
        this.googleCalendarEvents = googleCalendarEvents;
    }

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}

    @Override
	protected void init() {
		super.init();

        // Exception reporting
        getRequestCycleListeners().add(new IRequestCycleListener() {
            final Logger logger = Logger.getLogger(IRequestCycleListener.class);

            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                logger.error("Caught exception:", ex);

                return null;
            }
        });

        RequestLoggerSettings enableRequestLogger = new RequestLoggerSettings();
        enableRequestLogger.setRequestLoggerEnabled(true);
        setRequestLoggerSettings(enableRequestLogger);

        getApplicationSettings().setInternalErrorPage(Error500Page.class);

        getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        getMarkupSettings().setDefaultMarkupEncoding(DEFAULT_ENCODING);
        getMarkupSettings().setStripWicketTags(true);
        if (!isDevelopmentMode()) {
            // Strip comments in deployment mode
            getMarkupSettings().setStripComments(true);
        }
        getRequestCycleSettings().setResponseRequestEncoding(DEFAULT_ENCODING);

        IPackageResourceGuard packageResourceGuard = getResourceSettings().getPackageResourceGuard();
        if(packageResourceGuard instanceof SecurePackageResourceGuard){
            SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;
            guard.addPattern("+*.JPG");
            guard.addPattern("+*.JPEG");
            guard.addPattern("+*.PNG");
            guard.addPattern("+*.GIF");
            guard.addPattern("+*.ttf");
        }

        getRequestCycleListeners().add(new IRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                CsldAuthenticatedWebSession session = CsldAuthenticatedWebSession.get();
                if (session.isClearRequested()) {
                    session.clear();
                    session.setClearRequested(false);
                }
            }
        });

        String context = env.getProperty("csld.root_path");
        mountPages(context);

        mountResources(context);

        if (isDevelopmentMode()) {
            // Turn on containers names when debugging
            getDebugSettings().setOutputMarkupContainerClassName(true);
        }
	}

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return CsldAuthenticatedWebSession.class;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return null;
    }

    private void mountPages(String context) {
        ResourceReference icalReference = new ResourceReference("icalReference") {
            ICalProducerResource resource = new ICalProducerResource(events, csldUsers);

            @Override
            public IResource getResource() {
                return resource;
            }
        };

        ResourceReference statsReference = new ResourceReference("statsReference") {
            StatisticsProducer resource = new StatisticsProducer(statistics);

            @Override
            public IResource getResource() {
                return resource;
            }
        };

        ResourceReference gameReference = new ResourceReference("gameReference") {
            GameProducer resource = new GameProducer(games);

            @Override
            public IResource getResource() {
                return resource;
            }
        };

        ResourceReference gCalSyncReference = new ResourceReference("gCalSync") {
            GCalSyncProducer resource = new GCalSyncProducer(googleCalendarEvents);

            @Override
            public IResource getResource() {
                return resource;
            }
        };

        mountResource(context + "/ical", icalReference);
        mountResource(context + "/rest/stats", statsReference);
        mountResource(context + "/rest/game", gameReference);
        mountResource(context + "/cal-sync", gCalSyncReference);
    }

    private void mountResources(String context) {
        mountResource(context + "/user-icon", csldUsers.getIconReference());
        mountResource(context + "/game-image", games.getIconReference());

        if (graphqlResource != null) {
            // GraphQL needs web application, it must be set later to overcome circular dependency
            graphqlResource.setWebApplication(this);
            ResourceReference graphqlReference = new ResourceReference("graphqlReference") {
                @Override
                public IResource getResource() {
                    return Csld.this.graphqlResource;
                }
            };
            mountResource(context + "/graphql", graphqlReference);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    protected boolean isDevelopmentMode() {
        return RuntimeConfigurationType.DEVELOPMENT.equals(this.getConfigurationType());
    }

    @Override
    public Session newSession(Request request, Response response) {
        // The csldUsers aren't created as serializable proxy.
        Session session = new CsldAuthenticatedWebSession(request);
        session.setLocale(Locale.forLanguageTag("cs"));
        return session;
    }
}
