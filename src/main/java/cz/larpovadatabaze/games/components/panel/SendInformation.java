package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.UserPlayedGame;
import cz.larpovadatabaze.common.services.wicket.MailClient;
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
    private MailClient mailClient;
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
            if (getModelObject() == null || getModelObject().getPlayed() == null) {
                return new ArrayList<>();
            }

            return getModelObject().getPlayed().stream()
                    .filter(played -> played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY))
                    .map(UserPlayedGame::getPlayerOfGame)
                    .collect(Collectors.toList());
        }
    }


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


        Form sendInfo = new Form("sendInfoForm") {
            @Override
            protected void onSubmit() {
                if (games.canEditGame(SendInformation.this.getModelObject())) {
                    List<SelectedUser> recipients = getAllSelected();
                    for (SelectedUser recipient : recipients) {
                        mailClient.sendMail(
                                String.format(getString("mail.from.author"), SendInformation.this.getModelObject().getName(), mail),
                                recipient.getEmail(),
                                String.format(getString("mail.from.author.subject"), SendInformation.this.getModelObject().getName()));
                    }
                } else {
                    CsldUser loggedUser = appUsers.getLoggedUser();
                    for (CsldUser author : SendInformation.this.getModelObject().getAuthors()) {
                        mailClient.sendMail(
                                String.format(getString("mail.from.user"),
                                        loggedUser.getPerson().getEmail(), SendInformation.this.getModelObject().getName(), mail),
                                author.getPerson().getEmail(),
                                String.format(getString("mail.from.user.subject"), loggedUser.getPerson().getName(), SendInformation.this.getModelObject().getName()));
                    }
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
