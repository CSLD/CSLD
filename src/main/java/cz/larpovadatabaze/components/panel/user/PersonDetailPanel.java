package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.entities.UserPlayedGame;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 14.5.13
 * Time: 9:57
 */
public class PersonDetailPanel extends Panel {
    public PersonDetailPanel(String id, CsldUser user) {
        super(id);

        final Image userImage = new Image("userImage",
                new ContextRelativeResource(user.getImage().getPath()));
        add(userImage);

        Person person = user.getPerson();
        add(new Label("nickname",person.getNickname()));
        add(new Label("name",person.getName()));

        add(new Label("age",person.getAge()));
        add(new Label("city",person.getCity()));
        int played = 0;
        for(UserPlayedGame playedGame: user.getPlayedGames()){
            if(playedGame.getState() == UserPlayedGame.PLAYED){
                played++;
            }
        }
        add(new Label("played",played));
        add(new Label("organized",user.getAuthorOf().size()));

        add(new Label("description",person.getDescription()));
    }
}
