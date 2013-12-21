package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.entities.UserPlayedGame;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * It shows detail of user.
 */
public class PersonDetailPanel extends Panel {
    private final CsldUser user;

    public PersonDetailPanel(String id, CsldUser user) {
        super(id);
        this.user = user;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Image userImage = new Image("userImage",
                new PackageResourceReference(Csld.class, user.getImage().getPath()));
        add(userImage);

        Person person = user.getPerson();
        add(new Label("nickname",person.getNickNameView()));
        add(new Label("name",person.getName()));

        add(new Label("age",person.getAge()));
        add(new Label("city",person.getCity()));
        int played = 0;
        for(UserPlayedGame playedGame: user.getPlayedGames()){
            if(playedGame.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.PLAYED)){
                played++;
            }
        }
        add(new Label("played",played));
        add(new Label("organized",user.getAuthorOf().size()));

        add(new Label("description",person.getDescription()).setEscapeModelStrings(false));
    }
}
