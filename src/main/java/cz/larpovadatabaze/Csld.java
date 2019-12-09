package cz.larpovadatabaze;

import cz.larpovadatabaze.calendar.component.page.CreateOrUpdateEventPage;
import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.component.page.ListEventsPage;
import cz.larpovadatabaze.calendar.service.ICalProducerResource;
import cz.larpovadatabaze.calendar.service.LarpCzImport;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.page.TestDatabase;
import cz.larpovadatabaze.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.components.page.admin.AdministrationPage;
import cz.larpovadatabaze.components.page.admin.ManageLabelsPage;
import cz.larpovadatabaze.components.page.admin.ManageUserRightsPage;
import cz.larpovadatabaze.components.page.author.CreateOrUpdateAuthorPage;
import cz.larpovadatabaze.components.page.error.Error404Page;
import cz.larpovadatabaze.components.page.error.Error500Page;
import cz.larpovadatabaze.components.page.game.*;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.components.page.group.GroupDetail;
import cz.larpovadatabaze.components.page.search.SearchResultsPage;
import cz.larpovadatabaze.components.page.user.*;
import cz.larpovadatabaze.converters.*;
import cz.larpovadatabaze.donations.components.DonationPage;
import cz.larpovadatabaze.donations.service.BankAccount;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.LabelService;
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
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.RequestLoggerSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.converter.CalendarConverter;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 */
@Component(value = "wicketApplication")
public class Csld extends AuthenticatedWebApplication implements ApplicationContextAware
{
    @Autowired
    private CsldUserService csldUserService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Environment env;


    private static final String DEFAULT_ENCODING = "UTF-8";
    private static ApplicationContext ctx;

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
     * Constructor
     */
	public Csld()
	{
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
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            final Logger logger = Logger.getLogger(Csld.class);

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

        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                super.onBeginRequest(cycle);
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

        // Load information about donations and setup timer. Respect the configuration to ignore this in the context
        // of the testing.
        if(env.getProperty("csld.integrate_bank", Boolean.class)) {
            new BankAccount(sessionFactory).start();
        }

        if(env.getProperty("csld.integrate_calendar", Boolean.class)) {
            new Thread(() -> new LarpCzImport(sessionFactory).importEvents()).start();
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

    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();

        locator.set(CsldUser.class, new CsldUserConverter(csldUserService));
        locator.set(Game.class, new GameConverter(gameService));
        locator.set(CsldGroup.class, new GroupConverter(groupService));
        locator.set(Label.class, new LabelConverter(labelService));
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

        mountPage(context + "/home", HomePage.class);
        mountPage(context + "/game-was-deleted", GameWasDeleted.class);

        mountPage(context + "/error404", Error404Page.class);
        mountPage(context + "/error500", Error500Page.class);

        if(isDevelopmentMode()) {
            mountPage(context + "/testDatabase", TestDatabase.class);
        }

        ResourceReference icalReference = new ResourceReference("icalReference") {
            ICalProducerResource resource = new ICalProducerResource(sessionFactory, csldUserService);
            @Override
            public IResource getResource() {
                return resource;
            }
        };
        mountResource(context + "/ical", icalReference);
    }

    private void mountResources(String context) {
        mountResource(context + "/user-icon", csldUserService.getIconReference());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
        Session session = super.newSession(request, response);
        session.setLocale(Locale.forLanguageTag("cs"));
        return session;
    }
}
