package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 *
 */
public class FilterGamesPanel extends Panel {
    private ChooseLabelsPanel labels;

    public FilterGamesPanel(String id) {
        super(id);

        Form<FilterGame> filterGames = new Form<FilterGame>("filterForm", new CompoundPropertyModel<FilterGame>(new FilterGame())){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();
                if(!hasError()){
                    FilterGame filter = getModelObject();
                    List<Label> selectedLabels = labels.getSelected();

                    PageParameters params = new PageParameters();
                    params.add("minPlayers", filter.getMinPlayers());
                    params.add("maxPlayers", filter.getMaxPlayers());
                    params.add("minHours", filter.getMinHours());
                    params.add("maxHours", filter.getMaxHours());
                    params.add("minDays", filter.getMinDays());
                    params.add("maxDays", filter.getMaxDays());

                    StringBuilder labelsText = new StringBuilder();
                    for(Label label: selectedLabels){
                        labelsText.append(label.getName() + "&");
                    }
                    if(labelsText.length() > 0){
                        labelsText.deleteCharAt(labelsText.length() - 1);
                    }
                    params.add("labels", labelsText.toString());

                    throw new RestartResponseException(getPage().getClass(), params);
                }
            }
        };

        filterGames.add(new NumberTextField<Integer>("minPlayers"));
        filterGames.add(new NumberTextField<Integer>("maxPlayers"));
        filterGames.add(new NumberTextField<Integer>("minHours"));
        filterGames.add(new NumberTextField<Integer>("maxHours"));
        filterGames.add(new NumberTextField<Integer>("minDays"));
        filterGames.add(new NumberTextField<Integer>("maxDays"));

        labels = new ChooseLabelsPanel("filterByLabels");
        filterGames.add(labels);

        filterGames.add(new Button("submit"));

        add(filterGames);
    }
}
