package cz.larpovadatabaze.components.page;

import com.googlecode.wicket.jquery.core.resource.JQueryUIResourceReference;
import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.page.ListEventsPage;
import cz.larpovadatabaze.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.components.panel.home.AdvertisementPanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.AdminPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.env.Environment;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import java.util.Locale;

/**
 * Base page from which all other pages are derived.
 */
public abstract class CsldBasePage extends WebPage {
    @SpringBean
    private Environment env;

    public CsldBasePage() {
    }

    /**
     * @return Model for HTML page title
     */
    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("larpDatabaseTitle", null);
    }

    protected IModel<String> getPreviewImageUrlModel() {
        return Model.of(urlFor(new PackageResourceReference(CsldBasePage.class, "img/logo50.png"), null).toString());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if(!CsldAuthenticatedWebSession.get().isSignedIn()){
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();
            String[] data = strategy.load();
            if(data != null && data.length > 1){
                // TODO: Verify setting language on login.
                CsldAuthenticatedWebSession.get().signIn(data[0], data[1]);
                CsldUser user = UserUtils.getLoggedUser();
                if(user != null && user.getDefaultLang() != null) {
                    CsldAuthenticatedWebSession.get().setLocale(Locale.forLanguageTag(user.getDefaultLang()));
                }
            }
        }

        add(new Label("pageTitle", getPageTitleModel()));

        String previewImageURL = getRequestCycle().getUrlRenderer().renderFullUrl(Url.parse(getPreviewImageUrlModel().getObject()));

        WebMarkupContainer previewImageTag1 = new WebMarkupContainer("previewImageTag1");
        previewImageTag1.add(new AttributeModifier("href", previewImageURL));
        add(previewImageTag1);

        WebMarkupContainer previewImageTag2 = new WebMarkupContainer("previewImageTag2");
        previewImageTag2.add(new AttributeModifier("content", previewImageURL));
        add(previewImageTag2);

        add(new BookmarkablePageLink<CsldBasePage>("list-game", ListGamePage.class));
        // Hide when calendar isn't allowed.
        add(new BookmarkablePageLink<CsldBasePage>("list-events", ListEventsPage.class){
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(Boolean.parseBoolean(env.getProperty(Toggles.CALENDAR)) ||
                        CsldAuthenticatedWebSession.get().isAtLeastEditor());
            }
        });
        add(new BookmarkablePageLink<CsldBasePage>("about", AboutDatabasePage.class));
        add(new AdminPanel("adminPanel"));

        // Add user panel or login links
        if (CsldAuthenticatedWebSession.get().isSignedIn()) {
            add(new LoggedBoxPanel("user"));
        }
        else {
            add(new LoginBoxPanel("user"));
        }

        add(new SearchBoxPanel("searchBox"));

        add(provideAdvertisementsPanel("advertisements"));
    }

    /**
     * @param id Desired component id
     *
     * @return Advertisement component. Usually it is games slider, but it can be anything else.
     */
    protected Component provideAdvertisementsPanel(String id) {
        return new AdvertisementPanel(id);
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        //Protection against ClickJacking, prevents the page from being rendered in an iframe element
        response.setHeader("X-Frame-Options", "deny");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings()
                .getJQueryReference()));
        response.render(JavaScriptHeaderItem.forReference(TinyMCESettings.javaScriptReference()));

        response.render(JavaScriptHeaderItem.forReference(JQueryUIResourceReference.get()));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"js/jquery.slides.min.js"))); // XXX - is this still needed?

//        response.render(CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/nivo-slider.css")));

        response.render(CssHeaderItem.forReference(CsldCssResourceReference.get()));

        super.renderHead(response);
    }
}
