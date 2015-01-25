package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import cz.larpovadatabaze.components.panel.game.ListGamePanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.LabelService;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class ListGame extends CsldBasePage {
    @SpringBean
    LabelService labelService;
    @SpringBean
    CsldUserService csldUserService;
    private Integer label;

    public ListGame(PageParameters params) {
        CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();

        int ALL = -1;
        int NONE = -2;
        label = params.get("label").toInt(NONE);

        if(logged != null && label != NONE ) {
            // Save for logged user actual state
            logged.setLastRating(label);

            csldUserService.saveOrUpdate(logged);
        }

        if(label == NONE) {
            label = ALL;
            // Some user is logged.
            if(logged != null) {
                // Get what was last rating of the logged user
                label = logged.getLastRating();
                if(label == null) {
                    // If nothing set label to all
                    label = ALL;
                }
            }
        }

        List<Label> requiredLabels = labelService.getRequired();
        add(new ListView<Label>("requiredLabels", requiredLabels) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                Label labelObj = item.getModelObject();

                Link link = new BookmarkablePageLink<CsldBasePage>("requiredOne", ListGame.class, new PageParameters().set("label", labelObj.getId()));
                link.add(new org.apache.wicket.markup.html.basic.Label("text", WordUtils.capitalize(labelObj.getName())));
                item.add(link);

                if(labelObj.getId().equals(label)){
                    link.add(AttributeModifier.replace("class","active"));
                }
            }
        });

        Link allLink = new BookmarkablePageLink<CsldBasePage>("all", ListGame.class, new PageParameters().set("label", ALL));
        add(allLink);

        if(label.equals(ALL)) {
            allLink.add(AttributeModifier.replace("class","active"));
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
