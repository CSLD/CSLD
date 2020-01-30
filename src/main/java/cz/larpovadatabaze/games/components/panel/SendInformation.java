package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.models.SelectedUser;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.components.panel.CheckBoxSelectionUsers;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This one really should contain also the CheckboxSelection Users.
 */
public class SendInformation extends AbstractCsldPanel<Game> {
    @SpringBean
    private MailService mailService;
    @SpringBean
    private Games games;
    @SpringBean
    private AppUsers appUsers;

    public Component getWantedToPlay() {
        return wantedToPlay;
    }

    /**
     * Model for users who want to play the game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class WantedByModel implements IModel<List<CsldUser>> {

        @Override
        public List<CsldUser> getObject() {
            if (getModelObject() == null || getModelObject().getRatings() == null) {
                return new ArrayList<>();
            }

            return getModelObject().getRatings().stream()
                    .filter(gameState -> gameState.getStateEnum().equals(Rating.GameState.WANT_TO_PLAY))
                    .map(Rating::getUser)
                    .collect(Collectors.toList());
        }
    }

    /*
    Internal model property. Used directly by wicket.
     */
    private String mail;
    private CheckBoxSelectionUsers wantedToPlay;

    public SendInformation(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        wantedToPlay = new CheckBoxSelectionUsers("wantsToPlay", new WantedByModel()) {
            @Override
            public CsldRoles getRoleOfActualUser() {
                if (appUsers.isEditor()) {
                    return CsldRoles.EDITOR;
                } else if (appUsers.isAdmin()) {
                    return CsldRoles.ADMIN;
                } else if (games.canEditGame(getModelObject())) {
                    return CsldRoles.AUTHOR;
                } else if (appUsers.isSignedIn()) {
                    return CsldRoles.USER;
                } else {
                    return CsldRoles.ANONYMOUS;
                }
            }
        };
        wantedToPlay.setOutputMarkupId(true);


        Form<Void> sendInfo = new Form<>("sendInfoForm") {
            // TODO: Move to service and thread.
            @Override
            protected void onSubmit() {
                Game currentGame = SendInformation.this.getModelObject();
                if (games.canEditGame(currentGame)) {
                    mailService.sendInfoToInterestedUsers(
                            getAllSelected(),
                            currentGame.getName(),
                            mail,
                            getString("mail.from.author.subject"));
                } else {
                    mailService.sendInfoToAuthors(
                            currentGame,
                            mail,
                            getString("mail.from.user.subject"));
                }
                info(getString("mail.success"));
            }
        };

        FeedbackPanel panel = new FeedbackPanel("feedback");
        sendInfo.add(panel);

        TextArea<String> emailText = new TextArea<String>("mailBody", new PropertyModel<String>(this, "mail"));
        sendInfo.add(emailText);

        sendInfo.add(wantedToPlay);

        add(sendInfo);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(appUsers.isSignedIn());
    }

    /**
     * This method will usually be overwritten.
     *
     * @return All people to whom the data should be send.
     */
    public List<SelectedUser> getAllSelected() {
        return wantedToPlay.getSelectedUsers();
    }
}
