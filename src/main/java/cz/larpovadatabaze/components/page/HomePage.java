package cz.larpovadatabaze.components.page;

import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.panel.home.AdvertisementPanel;
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

        add(new AdvertisementPanel("advertisements"));

        add(new RecentGamesPanel("recentGames"));

        add(new LarpCzCalendarPanel("calendar"));

        add(new LastCommentsPanel("lastComments"));

        add(new RecentPhotosPanel("recentPhotos"));

        /*
        add(new RandomLarpPanel("randomLarpPanel"));
        add(new StatisticsPanel("statisticsPanel"));

        RepeatingView images = new RepeatingView("randomImages");
        List<Photo> randomPhotos = photoService.getRandomPhotos(1);
        for(Photo photo: randomPhotos) {
            images.add(new Image(images.newChildId(), imageService.getImageResource(photo)));
            Game gameAssociatedWithImage = gameTranslator.translate(photo.getGame());
            add(new BookmarkableLinkWithLabel(
                    "linkToThisGame",
                    GameDetail.class,
                    Model.of(gameAssociatedWithImage.getName()),
                    Model.of(GameDetail.paramsForGame(gameAssociatedWithImage))));
        }
        add(images);
        */
    }
}
