package cz.larpovadatabaze.components.page.detail;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.PersonDetailPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 9:46
 */
public class AuthorDetail extends CsldBasePage {
    @SpringBean
    private CsldUserService csldUserService;

    public AuthorDetail(PageParameters params){
        Integer authorId = params.get("id").to(Integer.class);
        CsldUser author = csldUserService.getById(authorId);

        add(new PersonDetailPanel("personDetail",author));

        add(new GameListPanel("gamesPanel",author.getAuthorOf()));
    }
}
