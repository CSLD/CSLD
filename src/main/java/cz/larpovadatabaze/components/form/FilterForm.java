package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 3.5.13
 * Time: 12:47
 */
public class FilterForm extends Form<FilterGame> {
    @SpringBean
    private LabelService labelService;

    public FilterForm(String id) {
        super(id, new CompoundPropertyModel<FilterGame>(new FilterGame()));

        add(new NumberTextField<Integer>("minPlayers", new Model(), Integer.class));
        add(new NumberTextField<Integer>("maxPlayers", new Model(), Integer.class));
        add(new NumberTextField<Integer>("minHours", new Model(), Integer.class));
        add(new NumberTextField<Integer>("maxHours", new Model(), Integer.class));
        add(new NumberTextField<Integer>("minDays", new Model(), Integer.class));
        add(new NumberTextField<Integer>("maxDays", new Model(), Integer.class));

        List<Label> labelsData = labelService.getAll();
        ListView<Label> labels = new ListView<Label>("listLabels", labelsData) {
            @Override
            protected void populateItem(ListItem<Label> listItem) {
                Label labelData = listItem.getModelObject();
                final org.apache.wicket.markup.html.basic.Label labelLabel =
                        new org.apache.wicket.markup.html.basic.Label("labelName", labelData.getName());
                listItem.add(labelLabel);
                final CheckBox box = new CheckBox("labelCheck");
                listItem.add(box);
            }
        };
        add(labels);

        add(new Button("submit"));
    }

    @Override
    protected void onSubmit(){
        validate();
        if(!hasError()){
            System.out.println("Got Here");
        }
    }
}
