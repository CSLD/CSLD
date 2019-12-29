package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.entities.Game;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * List of games
 */
public class ListGamesWithAnnotations extends Panel {

    private final SortableDataProvider<Game,String> dataProvider;

    public ListGamesWithAnnotations(String id,
                                    SortableDataProvider<Game,String> dataProvider) {
        super(id);
        this.dataProvider = dataProvider;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        RepeatingView rowRepeater = new RepeatingView("rows");
        add(rowRepeater);

        int gameNo = 0;
        Iterator<? extends Game> games = dataProvider.iterator(0, 999);
        MarkupContainer lastRow = null;

        while(games.hasNext()) {
            if (gameNo == 2) {
                // Add to last row
                lastRow.add(new GameBoxPanel("game2", Model.of(games.next())));
                gameNo++;
            }
            else {
                // Create new row
                lastRow = new WebMarkupContainer(rowRepeater.newChildId());
                rowRepeater.add(lastRow);
                lastRow.add(new GameBoxPanel("game1", Model.of(games.next())));
                gameNo = 2;
            }
        }

        if (gameNo == 2) {
            // Add empty last right container
            lastRow.add(new WebMarkupContainer("game2").setVisible(false));
        }
//        add(new PagingNavigator("navigator", gamesView));
    }
}
