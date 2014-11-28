package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.LastGamesPanel;
import cz.larpovadatabaze.components.panel.home.RandomLarpPanel;
import cz.larpovadatabaze.components.panel.home.StatisticsPanel;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.services.PhotoService;
import cz.larpovadatabaze.services.impl.ImageServiceImpl;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
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

    public HomePage(){
        add(new LastGamesPanel("lastGames"));
        add(new LastCommentsPanel("lastComments"));

        add(new AddGamePanel("createGamePanel"));
        add(new RandomLarpPanel("randomLarpPanel"));
        add(new StatisticsPanel("statisticsPanel"));

        RepeatingView images = new RepeatingView("randomImages");
        List<Photo> randomPhotos = photoService.getRandomPhotos(5);
        for(Photo photo: randomPhotos) {
            images.add(new Image(images.newChildId(), imageService.getImageResource(photo)));
        }
        add(images);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }
}
