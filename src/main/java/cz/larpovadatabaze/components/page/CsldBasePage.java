package cz.larpovadatabaze.components.page;

import com.googlecode.wicket.jquery.core.resource.JQueryUIResourceReference;
import cz.larpovadatabaze.components.common.i18n.LocalePicker;
import cz.larpovadatabaze.components.page.about.AboutDatabase;
import cz.larpovadatabaze.components.page.author.ListAuthor;
import cz.larpovadatabaze.components.page.game.ListGame;
import cz.larpovadatabaze.components.page.group.ListGroup;
import cz.larpovadatabaze.components.page.user.ListUser;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.AdminPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.AttributeModifier;
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

        add(new LoginBoxPanel("login"));
        add(new LoggedBoxPanel("loggedInfo"));
        add(new AdminPanel("adminPanel"));

        add(new BookmarkablePageLink<CsldBasePage>("list-game", ListGame.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-authors", ListAuthor.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-users", ListUser.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-groups", ListGroup.class));
        add(new BookmarkablePageLink<CsldBasePage>("about", AboutDatabase.class));

        add(new LocalePicker("languagePicker"));
        add(new SearchBoxPanel("searchBox"));
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        //Protection against ClickJacking, prevents the page from being rendered in an iframe element
        response.setHeader("X-Frame-Options","deny");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings()
                .getJQueryReference()));
        response.render(JavaScriptHeaderItem.forReference(TinyMCESettings.javaScriptReference()));

        response.render(JavaScriptHeaderItem.forReference(JQueryUIResourceReference.get()));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"js/jquery.slides.min.js")));

        response.render(CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/nivo-slider.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/style.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/smoothness/jquery-ui-1.8.24.custom.css")));
        super.renderHead(response);
    }
}
