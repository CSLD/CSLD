package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.games.services.GamesWithState;
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
    private GamesWithState games;
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
        add(new Label("age", age).setVisible(age != null));
        add(new Label("played", games.getAmountOfGamesPlayedBy(user)));
        add(new Label("organized", user.getAuthorOf().size()));
    }
}
