package cz.larpovadatabaze.components.page;

import com.googlecode.wicket.jquery.core.resource.JQueryUIResourceReference;

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

import cz.larpovadatabaze.components.common.i18n.LocalePicker;
import cz.larpovadatabaze.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.components.page.author.ListAuthor;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.components.page.group.ListGroup;
import cz.larpovadatabaze.components.page.user.ListUser;
import cz.larpovadatabaze.components.panel.home.AdvertisementPanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.AdminPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * Base page from which all other pages are derived.
 */
public abstract class CsldBasePage extends WebPage {
    private LocaleProvider locales = new CodeLocaleProvider();

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
                CsldAuthenticatedWebSession.get().signIn(data[0], data[1]);
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
        add(new BookmarkablePageLink<CsldBasePage>("list-authors", ListAuthor.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-users", ListUser.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-groups", ListGroup.class));
        add(new AdminPanel("adminPanel"));


        add(new BookmarkablePageLink<CsldBasePage>("about", AboutDatabasePage.class));

        // Add user panel or login links
        if (CsldAuthenticatedWebSession.get().isSignedIn()) {
            add(new LoggedBoxPanel("user"));
        }
        else {
            add(new LoginBoxPanel("user"));
        }

        add(new LocalePicker("localePicker"));

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
