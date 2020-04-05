package cz.larpovadatabaze.common.components.page;

import cz.larpovadatabaze.common.components.home.AdvertisementPanel;
import cz.larpovadatabaze.common.components.home.CalendarPanel;
import cz.larpovadatabaze.common.components.home.LastCommentsPanel;
import cz.larpovadatabaze.common.components.home.RecentGamesPanel;
import cz.larpovadatabaze.common.components.home.RecentPhotosPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.http.WebResponse;

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

        add(new CalendarPanel("calendar"));

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
    }
}
