package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.news.NewsListPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.panel.home.LarpCzCalendarPanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.RecentGamesPanel;
import cz.larpovadatabaze.components.panel.home.RecentPhotosPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.lang.Translator;
import cz.larpovadatabaze.services.PhotoService;
import cz.larpovadatabaze.services.impl.ImageServiceImpl;

/**
 *
 */
public class HomePage extends CsldBasePage {
    @SpringBean
    private PhotoService photoService;
    @SpringBean
    private ImageServiceImpl imageService;
    @SpringBean
    private Translator<Game> gameTranslator;

    public HomePage(){
        setVersioned(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new RecentGamesPanel("recentGames"));

        add(new LarpCzCalendarPanel("calendar"));

        add(new LastCommentsPanel("lastComments"));

        add(new NewsListPanel("news"));

        add(new RecentPhotosPanel("recentPhotos"));

        add(new WebMarkupContainer("news").setVisible(false));
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);

        response.setHeader("Cache-Control", "no-cache,no-store,private,must-revalidate,max-stale=0,post-check=0,pre-check=0");
        response.setHeader("Expires","0");
        response.setHeader("Pragma", "no-cache");
        response.disableCaching();
    }
    
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class, "js/homePage.js")));
    }
}
