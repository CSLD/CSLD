package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.page.CreateNewEventPage;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.OwlCarouselResourceReference;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.components.page.news.CreateOrUpdateNewsPage;
import cz.larpovadatabaze.components.page.user.CreateUserPage;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.Advertisement;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Michal Kara Date: 7.3.15 Time: 18:43
 */
public class AdvertisementPanel extends AbstractCsldPanel<List<Advertisement>> {
    @SpringBean
    private Environment environment;

    /**
     * Model to load advertisements
     */
    private static class AdvertisementModel extends LoadableDetachableModel<List<Advertisement>> {

        /**
         * Where the advertisements lead for anonymous user
         */
        private static final Class<? extends WebPage> AD_PAGE_CLASSES_ANONYMOUS[] = new Class[] {
            CreateUserPage.class, // 01
            CreateUserPage.class, // 02
            ListGamePage.class, // 03
            ListGamePage.class, // 04
            CreateOrUpdateGamePage.class, // 05
            CreateOrUpdateGamePage.class // 06
        };

        /**
         * Where the advertisements lead for logged user
         */
        private static final Class<? extends WebPage> AD_PAGE_CLASSES_LOGGED[] = new Class[] {
            UserDetailPage.class, // 01
            UserDetailPage.class, // 02
            ListGamePage.class, // 03
            ListGamePage.class, // 04
            CreateOrUpdateGamePage.class, // 05
            CreateOrUpdateGamePage.class // 06
        };

        @Override
        protected List<Advertisement> load() {
            List<Advertisement> res = new ArrayList<Advertisement>();

            int no = 1;
            for(Class<? extends WebPage> pageClass : CsldAuthenticatedWebSession.get().isSignedIn()?AD_PAGE_CLASSES_LOGGED:AD_PAGE_CLASSES_ANONYMOUS) {
                Advertisement a1 = new Advertisement();
                a1.setPageClass(pageClass);
                a1.setImage(String.format("games/ld-header-%02d.jpg", no++));
                res.add(a1);
            }

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
                BookmarkablePageLink<CsldBasePage> link = new BookmarkablePageLink<CsldBasePage>("link", item.getModelObject().getPageClass());
                item.add(link);

                // Add image
                link.add(new Image("image", new PackageResourceReference(getClass(), item.getModelObject().getImage())));
            }
        });

        // Add add link
        add(new BookmarkablePageLink<CsldBasePage>("addGameLink", CreateOrUpdateGamePage.class));
        add(new BookmarkablePageLink<CsldBasePage>("addEventLink", CreateNewEventPage.class){
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(Boolean.parseBoolean(environment.getProperty(Toggles.CALENDAR)) ||
                        CsldAuthenticatedWebSession.get().isAtLeastEditor());
            }
        });
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
