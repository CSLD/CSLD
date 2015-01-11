package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.panel.user.CheckBoxSelectionUsers;
import cz.larpovadatabaze.dto.SelectedUser;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.MailClient;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 *  This one really should contain also the CheckboxSelection Users.
 */
public class SendInformation extends Panel {
    @SpringBean
    private MailClient mailClient;
    @SpringBean
    private GameService gameService;

    public Component getWantedToPlay() {
        return wantedToPlay;
    }

    /**
     * Model for users who want to play the game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class WantedByModel extends AbstractReadOnlyModel<List<CsldUser>> {

        @Override
        public List<CsldUser> getObject() {
            List<CsldUser> wantedBy = new ArrayList<CsldUser>();
            for(UserPlayedGame played : getGame().getPlayed()){
                if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY)) {
                    wantedBy.add(played.getPlayerOfGame());
                }
            }

            return wantedBy;
        }
    }


    private String mail;
    private CheckBoxSelectionUsers wantedToPlay;

    public SendInformation(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        wantedToPlay =  new CheckBoxSelectionUsers("wantsToPlay", new WantedByModel()){
            @Override
            public CsldRoles getRoleOfActualUser() {
                if(UserUtils.isEditor()) {
                    return CsldRoles.EDITOR;
                } else if(UserUtils.isAdmin()) {
                    return CsldRoles.ADMIN;
                } else if(gameService.canEditGame(getGame())) {
                    return CsldRoles.AUTHOR;
                } else if(UserUtils.isSignedIn()) {
                    return CsldRoles.USER;
                } else{
                    return CsldRoles.ANONYMOUS;
                }
            }
        };
        wantedToPlay.setOutputMarkupId(true);


        Form sendInfo = new Form("sendInfoForm") {
            @Override
            protected void onSubmit() {
                if(gameService.canEditGame(getGame())){
                    List<SelectedUser> recipients = getAllSelected();
                    for(SelectedUser recipient: recipients){
                        mailClient.sendMail(
                                String.format(getString("mail.from.author"),getGame().getName(),mail),
                                recipient.getEmail(),
                                String.format(getString("mail.from.author.subject"), getGame().getName()));
                    }
                } else {
                    CsldUser loggedUser = UserUtils.getLoggedUser();
                    for(CsldUser author: getGame().getAuthors()){
                        mailClient.sendMail(
                                String.format(getString("mail.from.user"),
                                        loggedUser.getPerson().getEmail(), getGame().getName(), mail),
                                author.getPerson().getEmail(),
                                String.format(getString("mail.from.user.subject"), loggedUser.getPerson().getName(), getGame().getName()));
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
        setVisibilityAllowed(UserUtils.isSignedIn());
    }

    /**
     * This method will usually be overwritten.
     *
     * @return All people to whom the data should be send.
     */
    public List<SelectedUser> getAllSelected(){
        return wantedToPlay.getSelectedUsers();
    }

    public Game getGame(){return Game.getEmptyGame();}
}
