package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.CommentsListPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.user.PersonDetailPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 17:30
 */
public class UserDetail extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public UserDetail(PageParameters params){
        Integer authorId = params.get("id").to(Integer.class);
        CsldUser user = csldUserService.getById(authorId);

        add(new PersonDetailPanel("personDetail",user));
        add(new CommentsListPanel("comments", user.getCommented()));

        List<Game> playedGames = new ArrayList<Game>();
        List<Game> wantedGames = new ArrayList<Game>();
        for(UserPlayedGame played: user.getPlayedGames()){
            if(played.getState() == UserPlayedGame.WANT_TO_PLAY){
                wantedGames.add(played.getPlayedBy());
            } else if(played.getState() == UserPlayedGame.PLAYED) {
                playedGames.add(played.getPlayedBy());
            } else {

            }
        }
        add(new GameListPanel("playedGamesPanel",playedGames));
        add(new GameListPanel("wantedGamesPanel",wantedGames));
        add(new GameListPanel("authoredGamesPanel",user.getAuthorOf()));
    }
}
