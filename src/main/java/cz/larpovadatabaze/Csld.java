package cz.larpovadatabaze;

import cz.larpovadatabaze.administration.components.page.*;
import cz.larpovadatabaze.administration.rest.StatisticsProducer;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.calendar.component.page.CreateOrUpdateEventPage;
import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.component.page.ListEventsPage;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.ICalProducerResource;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.components.page.TestDatabase;
import cz.larpovadatabaze.common.components.page.error.Error404Page;
import cz.larpovadatabaze.common.components.page.error.Error500Page;
import cz.larpovadatabaze.common.converters.EnglishDateConverter;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.donations.components.DonationPage;
import cz.larpovadatabaze.games.components.page.*;
import cz.larpovadatabaze.games.converters.GameConverter;
import cz.larpovadatabaze.games.converters.LabelConverter;
import cz.larpovadatabaze.games.rest.GameProducer;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.graphql.GraphQLResource;
import cz.larpovadatabaze.search.components.SearchResultsPage;
import cz.larpovadatabaze.search.services.TokenSearch;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.page.*;
import cz.larpovadatabaze.users.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.users.converters.CsldUserConverter;
import cz.larpovadatabaze.users.converters.GroupConverter;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.log4j.Logger;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerRequestHandler;
import org.apache.wicket.core.request.handler.ListenerRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.RequestLoggerSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.converter.CalendarConverter;
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

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static ApplicationContext ctx;

    @Autowired
    public Csld(TokenSearch tokenSearch, CsldUsers csldUsers,
                Environment env, Events events, Statistics statistics,
                Games games, GraphQLResource graphqlResource) {
        this.tokenSearch = tokenSearch;
        this.csldUsers = csldUsers;
        this.env = env;
        this.events = events;
        this.statistics = statistics;
        this.games = games;
        this.graphqlResource = graphqlResource;
    }

    public class MountedMapperWithoutPageComponentInfo extends MountedMapper {

        public MountedMapperWithoutPageComponentInfo(String mountPath, Class<? extends IRequestablePage> pageClass) {
            super(mountPath, pageClass, new PageParametersEncoder());
        }

        @Override
        protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
            // do nothing so that component info does not get rendered in url
        }

        @Override
        public Url mapHandler(IRequestHandler requestHandler)
        {
            if (requestHandler instanceof ListenerRequestHandler ||
                    requestHandler instanceof BookmarkableListenerRequestHandler) {
                return null;
            } else {
                return super.mapHandler(requestHandler);
            }
        }
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
        return CsldSignInPage.class;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();

        locator.set(CsldUser.class, new CsldUserConverter(tokenSearch));
        locator.set(Game.class, new GameConverter(tokenSearch));
        locator.set(CsldGroup.class, new GroupConverter(tokenSearch));
        locator.set(Label.class, new LabelConverter(tokenSearch));
        locator.set(GregorianCalendar.class, new CalendarConverter(new EnglishDateConverter()));

        return locator;

    }

    private void mountPages(String context) {
	    mountPage(context + "/sign-out", SignOutPage.class);
        mountPage(context + "/sign-in", CsldSignInPage.class);
        mountPage(context + "/register", CreateUserPage.class);
        mountPage(context + "/edit-user", UpdateUserPage.class);

        mountPage(context + "/add-group", CreateOrUpdateGroupPage.class);
        mountPage(context + "/add-game", CreateOrUpdateGamePage.class);
        mountPage(context + "/add-author", CreateOrUpdateAuthorPage.class);

        mountPage(context + "/zebricky", ListGamePage.class);

        mountPage(context + "/detail-game", GameDetailOld.class);
        mount(new MountedMapperWithoutPageComponentInfo(context + "/larp/${name}/${id}", GameDetail.class));
        mount(new MountedMapperWithoutPageComponentInfo(context + "/larp/${name}/cs/${id}", GameDetail.class));
        mountPage(context + "/detail-author", UserDetailPage.class);
        mountPage(context + "/detail-user", UserDetailPage.class);
        mountPage(context + "/detail-group", GroupDetail.class);

        mountPage(context + "/calendar", ListEventsPage.class);
        mountPage(context + "/add-event", CreateOrUpdateEventPage.class);
        mount(new MountedMapperWithoutPageComponentInfo(context + "/event/${name}/${id}", DetailOfEventPage.class));

        mountPage(context + "/search", SearchResultsPage.class);

        mountPage(context + "/donations", DonationPage.class);

        mountPage(context + "/oDatabazi", AboutDatabasePage.class);
        mountPage(context + "/reset", ResetPassword.class);
        mountPage(context + "/forgot-password", ForgotPassword.class);

        mountPage(context + "/admin", AdministrationPage.class);
        mountPage(context + "/admin/manage-labels", ManageLabelsPage.class);
        mountPage(context + "/admin/manage-users", ManageUserRightsPage.class);
        mountPage(context + "/admin/rating-stats", RatingStatistics.class);
        mountPage(context + "/admin/comment-stats", CommentStatistics.class);

        mountPage(context + "/home", HomePage.class);
        mountPage(context + "/game-was-deleted", GameWasDeleted.class);

        mountPage(context + "/error404", Error404Page.class);
        mountPage(context + "/error500", Error500Page.class);

        if (isDevelopmentMode()) {
            mountPage(context + "/testDatabase", TestDatabase.class);
        }

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

        mountResource(context + "/ical", icalReference);
        mountResource(context + "/rest/stats", statsReference);
        mountResource(context + "/rest/game", gameReference);
    }

    private void mountResources(String context) {
        mountResource(context + "/user-icon", csldUsers.getIconReference());
        mountResource(context + "/game-image", games.getIconReference());

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
