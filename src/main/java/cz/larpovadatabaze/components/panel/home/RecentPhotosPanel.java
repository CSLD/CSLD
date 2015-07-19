package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.OwlCarouselResourceReference;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.services.PhotoService;

/**
 * Panel with recent photos
 *
 * User: Michal Kara Date: 7.3.15 Time: 22:43
 */
public class RecentPhotosPanel extends Panel {

    private final static int SHOW_PHOTOS = 10;

    @SpringBean
    private PhotoService photoService;

    @SpringBean
    private ImageService imageService;

    private WebMarkupContainer carousel;

    public RecentPhotosPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        carousel = new WebMarkupContainer("carousel");
        carousel.setOutputMarkupId(true);
        add(carousel);

        carousel.add(new ListView<Photo>("photos", photoService.getRandomPhotos(SHOW_PHOTOS)) {
            @Override
            protected void populateItem(ListItem<Photo> item) {
                Photo photo = item.getModelObject();

                // Link
                BookmarkablePageLink<CsldBasePage> link = new BookmarkablePageLink<CsldBasePage>("link", GameDetail.class, GameDetail.paramsForGame(photo.getGame()));
                item.add(link);

                // Image
                link.add(new NonCachingImage("gameImage", imageService.getImageResource(photo)) {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        // Move src attribute to data-src
                        tag.put("data-src", tag.getAttribute("src"));
                        tag.remove("src");
                    }
                });

                // Name
                link.add(new Label("gameName", photo.getGame().getName()));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // We need carousel
        response.render(JavaScriptHeaderItem.forReference(OwlCarouselResourceReference.get()));

        // Render carousel init
        PackageTextTemplate tt = new PackageTextTemplate(getClass(), "RecentPhotosPanel.js");
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("carouselId", carousel.getMarkupId());
        response.render(OnDomReadyHeaderItem.forScript(tt.asString(args)));
    }
}
