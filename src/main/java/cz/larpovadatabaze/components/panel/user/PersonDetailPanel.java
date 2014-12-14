package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows detail of user.
 */
public class PersonDetailPanel extends AbstractCsldPanel<CsldUser> {

    @SpringBean
    private ImageService imageService;

    public PersonDetailPanel(String id, IModel<CsldUser> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final UserIcon userImage = new UserIcon("userImage", getModel());
        add(userImage);

        CsldUser user = getModelObject();
        Person person = user.getPerson();
        add(new Label("nickname",person.getNickNameView()));
        add(new Label("name",person.getName()));

        Label emailLabel = new Label("email", person.getEmail());
        emailLabel.setVisibilityAllowed(UserUtils.isAtLeastEditor());
        add(emailLabel);

        Integer age = person.getAge();
        add(new Label("age",age).setVisible(age != null));

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
