package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.common.entities.UserPlayedGame;
import cz.larpovadatabaze.users.components.icons.UserIcon;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows detail of user.
 */
public class PersonDetailPanel extends AbstractCsldPanel<CsldUser> {
    @SpringBean
    private AppUsers appUsers;

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
        emailLabel.setVisibilityAllowed(appUsers.isAtLeastEditor());
        add(emailLabel);

        Integer age = person.getAge();
        add(new Label("age",age).setVisible(age != null));

  //      add(new Label("city",person.getCity()));
        int played = 0;
        for(UserPlayedGame playedGame: user.getPlayedGames()){
            if(playedGame.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.PLAYED)){
                played++;
            }
        }
        add(new Label("played",played));
        add(new Label("organized",user.getAuthorOf().size()));

//        add(new Label("description",person.getDescription()).setEscapeModelStrings(false));
    }
}