package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.components.page.list.SearchResults;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 21:39
 */
public class SearchForm extends Form<String> {
    public SearchForm(String id) {
        super(id);

        add(new RequiredTextField<String>("searchExpression"));

        add(new Button("sendSearch"));
    }

    protected void onSubmit(){
        String searchText = getModelObject();

        PageParameters params = new PageParameters();
        params.add("searchText", searchText);

        throw new RestartResponseException(SearchResults.class, params);
    }
}
