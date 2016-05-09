package cz.larpovadatabaze;

import cz.larpovadatabaze.calendar.component.page.CreateNewEventPage;
import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.component.page.ListEventsPage;
import cz.larpovadatabaze.calendar.component.panel.DetailedEventPanel;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.page.TestDatabase;
import cz.larpovadatabaze.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.components.page.admin.AdministrationPage;
import cz.larpovadatabaze.components.page.admin.ManageLabelsPage;
import cz.larpovadatabaze.components.page.admin.ManageUserRightsPage;
import cz.larpovadatabaze.components.page.author.CreateOrUpdateAuthorPage;
import cz.larpovadatabaze.components.page.calendar.CalendarWithMapPage;
import cz.larpovadatabaze.components.page.error.Error404Page;
import cz.larpovadatabaze.components.page.error.Error500Page;
import cz.larpovadatabaze.components.page.game.*;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.components.page.group.GroupDetail;
import cz.larpovadatabaze.components.page.search.SearchResultsPage;
import cz.larpovadatabaze.components.page.user.*;
import cz.larpovadatabaze.converters.*;
import cz.larpovadatabaze.dao.UserHasLanguagesDao;
import cz.larpovadatabaze.entities.*;
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
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.settings.IRequestLoggerSettings;
import org.apache.wicket.settings.def.RequestLoggerSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.converter.CalendarConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocale;

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
    private UserHasLanguagesDao userHasLanguages;

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
            if (requestHandler instanceof ListenerInterfaceRequestHandler ||
                    requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
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

        IRequestLoggerSettings enableRequestLogger = new RequestLoggerSettings();
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

        mountPages();

        mountResources();

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

    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();

        locator.set(CsldUser.class, new CsldUserConverter(csldUserService));
        locator.set(Game.class, new GameConverter(gameService));
        locator.set(CsldGroup.class, new GroupConverter(groupService));
        locator.set(Label.class, new LabelConverter(labelService));
        locator.set(UserHasLanguages.class, new UserHasLanguageConverter(userHasLanguages));

        return locator;

    }

    private void mountPages() {
        mountPage("/sign-out", SignOutPage.class);
        mountPage("/sign-in", CsldSignInPage.class);
        mountPage("/register", CreateUserPage.class);
        mountPage("/edit-user", UpdateUserPage.class);

        mountPage("/add-group", CreateOrUpdateGroupPage.class);
        mountPage("/add-game", CreateOrUpdateGamePage.class);
        mountPage("/add-author", CreateOrUpdateAuthorPage.class);

        mountPage("/zebricky", ListGamePage.class);

        mountPage("/detail-game", GameDetailOld.class);
        mount(new MountedMapperWithoutPageComponentInfo("/larp/${name}/${id}", GameDetail.class));
        mount(new MountedMapperWithoutPageComponentInfo("/larp/${name}/${lang}/${id}", GameDetail.class));
        mountPage("/detail-author", UserDetailPage.class);
        mountPage("/detail-user", UserDetailPage.class);
        mountPage("/detail-group", GroupDetail.class);

        mountPage("/kalendar", CalendarWithMapPage.class);

        mountPage("/calendar", ListEventsPage.class);
        mountPage("/add-event", CreateNewEventPage.class);
        mount(new MountedMapperWithoutPageComponentInfo("/event/${name}/${id}", DetailOfEventPage.class));

        mountPage("/search", SearchResultsPage.class);

        mountPage("/oDatabazi", AboutDatabasePage.class);
        mountPage("/reset", ResetPassword.class);
        mountPage("/forgot-password", ForgotPassword.class);

        mountPage("/admin", AdministrationPage.class);
        mountPage("/admin/manage-labels", ManageLabelsPage.class);
        mountPage("/admin/manage-users", ManageUserRightsPage.class);

        mountPage("/home", HomePage.class);
        mountPage("/game-was-deleted", GameWasDeleted.class);

        mountPage("/error404", Error404Page.class);
        mountPage("/error500", Error500Page.class);

        if(isDevelopmentMode()) {
            mountPage("/testDatabase", TestDatabase.class);
        }
    }

    private void mountResources() {
        mountResource("/user-icon", csldUserService.getIconReference());
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
        List<Locale> available = availableLocale();
        if(!available.contains(session.getLocale())){
            session.setLocale(Locale.ENGLISH);
        }
        return session;
    }
}
