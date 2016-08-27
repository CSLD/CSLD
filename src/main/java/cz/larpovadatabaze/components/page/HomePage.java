package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.home.LarpCzCalendarPanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.RecentGamesPanel;
import cz.larpovadatabaze.components.panel.home.RecentPhotosPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 */
public class HomePage extends CsldBasePage {
    public HomePage(){
        setVersioned(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new RecentGamesPanel("recentGames"));

        add(new LarpCzCalendarPanel("calendar"));

        add(new LastCommentsPanel("lastComments"));

        add(new RecentPhotosPanel("recentPhotos"));
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
