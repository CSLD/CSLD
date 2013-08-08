package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.models.FilterGame;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

/**
 *  //TODO finish this panel.
 */
public class FilterGamesPanel extends Panel {
    public FilterGamesPanel(String id) {
        super(id);

        Form<FilterGame> filterGames = new Form<FilterGame>("filterForm", new CompoundPropertyModel<FilterGame>(new FilterGame())){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();
                if(!hasError()){
                    System.out.println("Got Here");
                }
            }
        };

        filterGames.add(new NumberTextField<Integer>("minPlayers", Model.of(0), Integer.class));
        filterGames.add(new NumberTextField<Integer>("maxPlayers", Model.of(0), Integer.class));
        filterGames.add(new NumberTextField<Integer>("minHours", Model.of(0), Integer.class));
        filterGames.add(new NumberTextField<Integer>("maxHours", Model.of(0), Integer.class));
        filterGames.add(new NumberTextField<Integer>("minDays", Model.of(0), Integer.class));
        filterGames.add(new NumberTextField<Integer>("maxDays", Model.of(0), Integer.class));

        filterGames.add(new Button("submit"));

        add(filterGames);
    }

    protected void onConfigure() {
        setVisibilityAllowed(false);
    }
}
