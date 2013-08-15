package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import cz.larpovadatabaze.components.panel.game.ListGamePanel;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListGame extends CsldBasePage {
    @SpringBean
    GameService gameService;

    public ListGame(PageParameters params) {
        Image chartsIcon = new Image("chartsIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getChartsIconPath()));
        add(chartsIcon);

        final ListGamePanel listGamePanel = new ListGamePanel("listGame");
        listGamePanel.setOutputMarkupId(true);
        add(listGamePanel);

        add(new AddGamePanel("addGame"));
        add(new FilterGamesPanel("filterGames"){
            @Override
            public void onCsldEvent(AjaxRequestTarget target, Form<?> form, List<cz.larpovadatabaze.entities.Label> labels) {
                FilterGame filterGame = (FilterGame) form.getModelObject();
                listGamePanel.reload(target, filterGame, labels);
            }
        });
    }
}
