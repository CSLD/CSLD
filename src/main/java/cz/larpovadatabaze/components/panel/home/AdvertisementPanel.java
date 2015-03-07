package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.OwlCarouselResourceReference;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Advertisement;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;

/**
 * User: Michal Kara Date: 7.3.15 Time: 18:43
 */
public class AdvertisementPanel extends AbstractCsldPanel<List<Advertisement>> {

    /**
     * Model to load advertisements
     */
    private static class AdvertisementModel extends LoadableDetachableModel<List<Advertisement>> {

        @Override
        protected List<Advertisement> load() {
            /**
             * TODO - load from somewhere - TODO
             */
            List<Advertisement> res = new ArrayList<Advertisement>();

            Advertisement a1 = new Advertisement();
            a1.setGameId(1);
            a1.setImage("games/ld-header-01.jpg");
            res.add(a1);

            Advertisement a2 = new Advertisement();
            a2.setGameId(2);
            a2.setImage("games/ld-header-02.jpg");
            res.add(a2);

            Advertisement a3 = new Advertisement();
            a3.setGameId(3);
            a3.setImage("games/ld-header-03.jpg");
            res.add(a3);

            Advertisement a4 = new Advertisement();
            a4.setGameId(4);
            a4.setImage("games/ld-header-04.jpg");
            res.add(a4);

            Advertisement a5 = new Advertisement();
            a5.setGameId(5);
            a5.setImage("games/ld-header-05.jpg");
            res.add(a5);

            Advertisement a6 = new Advertisement();
            a6.setGameId(6);
            a6.setImage("games/ld-header-06.jpg");
            res.add(a6);


            return res;
        }
    }

    private WebMarkupContainer carousel;

    public AdvertisementPanel(String id) {
        super(id, new AdvertisementModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        carousel = new WebMarkupContainer("carousel");
        carousel.setOutputMarkupId(true);
        add(carousel);

        // Add advertisements
        carousel.add(new ListView<Advertisement>("games", getModelObject()) {
            @Override
            protected void populateItem(ListItem<Advertisement> item) {
                // Add link
                PageParameters pp = new PageParameters();
                pp.add("id", item.getModelObject().getGameId());
                BookmarkablePageLink<CsldBasePage> link = new BookmarkablePageLink<CsldBasePage>("link", GameDetail.class, pp);
                item.add(link);

                // Add image TODO - make reference some other way - TODO
                link.add(new Image("image", new PackageResourceReference(getClass(), item.getModelObject().getImage())));
            }
        });

        // Add add link
        add(new BookmarkablePageLink<CsldBasePage>("addGameLink", CreateOrUpdateGamePage.class));
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // We need carousel
        response.render(JavaScriptHeaderItem.forReference(OwlCarouselResourceReference.get()));

        /// Init carousel
        PackageTextTemplate tt = new PackageTextTemplate(getClass(), "AdvertisementPanel.js");
        Map<String, String> args = new HashMap<String, String>();
        args.put("carouselId", carousel.getMarkupId());
        response.render(OnDomReadyHeaderItem.forScript(tt.asString(args)));
    }
}
