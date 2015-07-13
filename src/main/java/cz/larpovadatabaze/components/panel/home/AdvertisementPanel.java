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
import org.apache.wicket.spring.injection.annot.SpringBean;
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
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageService;

/**
 * User: Michal Kara Date: 7.3.15 Time: 18:43
 */
public class AdvertisementPanel extends AbstractCsldPanel<List<Advertisement>> {

    private WebMarkupContainer carousel;

    @SpringBean
    private GameService gameService;

    @SpringBean
    private ImageService imageService;

    public AdvertisementPanel(String id) {
        super(id);
        setDefaultModel(new AdvertisementModel());
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

                // Add image
                link.add(new Image("image", item.getModelObject().getImage()));
            }
        });

        // Add add link
        add(new BookmarkablePageLink<CsldBasePage>("addGameLink", CreateOrUpdateGamePage.class));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisible(!getModelObject().isEmpty());
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

    /**
     * Model to load advertisements
     */
    private class AdvertisementModel extends LoadableDetachableModel<List<Advertisement>> {

        @Override
        protected List<Advertisement> load() {

            List<Advertisement> res = new ArrayList<Advertisement>();
            for(Game g : gameService.getGamesWithAdvertisements()) {
                res.add(new Advertisement(g.getId(), imageService.getImageResource(g.getCoverImage(), g.getDefaultImage())));
            }

            return res;
        }
    }

}
