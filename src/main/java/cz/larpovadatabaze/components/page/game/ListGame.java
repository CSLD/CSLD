package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import cz.larpovadatabaze.components.panel.game.ListGamePanel;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class ListGame extends CsldBasePage {
    @SpringBean
    LabelService labelService;

    public ListGame(PageParameters params) {
        int ALL = -1;
        Label chamber = labelService.getByName("komorní");
        int label = params.get("label").toInt(chamber.getId());

        Label dramatic = labelService.getByName("dramatický");
        Label battle = labelService.getByName("bitva");
        Label world = labelService.getByName("svět");
        Label city = labelService.getByName("městský");
        Label group = labelService.getByName("družinovka");

        Link chamberLink = new BookmarkablePageLink<CsldBasePage>("chamber", ListGame.class, new PageParameters().set("label", chamber.getId()));
        add(chamberLink);
        Link dramaticLink = new BookmarkablePageLink<CsldBasePage>("dramatic", ListGame.class, new PageParameters().set("label", dramatic.getId()));
        add(dramaticLink);
        Link battleLink = new BookmarkablePageLink<CsldBasePage>("battle", ListGame.class, new PageParameters().set("label", battle.getId()));
        add(battleLink);
        Link worldLink = new BookmarkablePageLink<CsldBasePage>("world", ListGame.class, new PageParameters().set("label", world.getId()));
        add(worldLink);
        Link cityLink = new BookmarkablePageLink<CsldBasePage>("city", ListGame.class, new PageParameters().set("label", city.getId()));
        add(cityLink);
        Link groupLink = new BookmarkablePageLink<CsldBasePage>("group", ListGame.class, new PageParameters().set("label", group.getId()));
        add(groupLink);
        Link allLink = new BookmarkablePageLink<CsldBasePage>("all", ListGame.class, new PageParameters().set("label", ALL));
        add(allLink);

        if(label == ALL) {
            allLink.add(AttributeModifier.replace("class","active"));
        } else if(label == chamber.getId()) {
            chamberLink.add(AttributeModifier.replace("class","active"));
        } else if(label == dramatic.getId()) {
            dramaticLink.add(AttributeModifier.replace("class","active"));
        } else if(label == battle.getId()) {
            battleLink.add(AttributeModifier.replace("class","active"));
        } else if(label == world.getId()) {
            worldLink.add(AttributeModifier.replace("class","active"));
        } else if(label == city.getId()) {
            cityLink.add(AttributeModifier.replace("class","active"));
        } else {
            groupLink.add(AttributeModifier.replace("class","active"));
        }

        final ListGamePanel listGamePanel = new ListGamePanel("listGame", label);
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
