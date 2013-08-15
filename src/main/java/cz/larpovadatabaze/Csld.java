package cz.larpovadatabaze;

import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.page.about.AboutDatabase;
import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.components.page.author.ListAuthor;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.game.ListGame;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.components.page.group.ListGroup;
import cz.larpovadatabaze.components.page.user.*;
import cz.larpovadatabaze.converters.*;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.*;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 */
@Component(value = "wicketApplication")
public class Csld extends AuthenticatedWebApplication
{
    @Autowired
    private PersonService personService;
    @Autowired
    private CsldUserService csldUserService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private LabelService labelService;

    private static final String DEFAULT_ENCODING = "UTF-8";

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
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        getMarkupSettings().setDefaultMarkupEncoding(DEFAULT_ENCODING);
        getMarkupSettings().setStripWicketTags(true);
        getRequestCycleSettings().setResponseRequestEncoding(DEFAULT_ENCODING);

        mountPages();
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

        locator.set(Person.class, new PersonConverter(personService));
        locator.set(CsldUser.class, new CsldUserConverter(csldUserService));
        locator.set(Game.class, new GameConverter(gameService));
        locator.set(CsldGroup.class, new GroupConverter(groupService));
        locator.set(Label.class, new LabelConverter(labelService));

        return locator;

    }

    private void mountPages() {
        mountPage("/sign-out", SignOut.class);
        mountPage("/sign-in", CsldSignInPage.class);
        mountPage("/register", CreateOrUpdateUserPage.class);

        mountPage("/add-group", CreateOrUpdateGroupPage.class);
        mountPage("/add-game", CreateOrUpdateGamePage.class);
        mountPage("/add-user", CreateOrUpdateUserPage.class);

        mountPage("/zebricky", ListGame.class);
        mountPage("/autori", ListAuthor.class);
        mountPage("/uzivatele", ListUser.class);
        mountPage("/skupiny", ListGroup.class);

        mountPage("/detail-game", GameDetail.class);
        mountPage("/detail-author", AuthorDetail.class);
        mountPage("/detail-user", UserDetail.class);

        mountPage("/oDatabazi", AboutDatabase.class);
        mountPage("/reset", ResetPassword.class);
        mountPage("/forgot-password", ForgotPassword.class);
    }

    public ApplicationContext getApplicationContext(){
        return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }

    public static String getBaseContext(){
        return "/files/upload/";
    }
}
