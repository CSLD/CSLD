package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.LastGamesPanel;
import cz.larpovadatabaze.components.panel.home.RandomLarpPanel;
import cz.larpovadatabaze.components.panel.home.StatisticsPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.lang.Translator;
import cz.larpovadatabaze.services.PhotoService;
import cz.larpovadatabaze.services.impl.ImageServiceImpl;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

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

        add(new LastGamesPanel("lastGames"));
        add(new LastCommentsPanel("lastComments"));

        add(new AddGamePanel("createGamePanel"));
        add(new RandomLarpPanel("randomLarpPanel"));
        add(new StatisticsPanel("statisticsPanel"));

        RepeatingView images = new RepeatingView("randomImages");
        List<Photo> randomPhotos = photoService.getRandomPhotos(1);
        for(Photo photo: randomPhotos) {
            Image img = new NonCachingImage(images.newChildId(), imageService.getImageResource(photo));
            images.add(img);
            Game gameAssociatedWithImage = photo.getGame();
            add(new BookmarkableLinkWithLabel(
                    "linkToThisGame",
                    GameDetail.class,
                    Model.of(gameAssociatedWithImage.getName()),
                    Model.of(GameDetail.paramsForGame(gameAssociatedWithImage))));
        }
        add(images);
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
