package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.page.search.SearchResults;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class SearchBoxPanel extends Panel {

    public SearchBoxPanel(String id) {
        super(id);

        Form<String> search = new Form<String>("search"){
            @Override
            protected void onSubmit() {
                super.onSubmit();

                String searchText = getModelObject();
                sendSearchToResults(searchText);
            }
        };

        search.add(new RequiredTextField<String>("searchExpression"));
        search.add(new Button("sendSearch"));

        add(search);
    }

    private void sendSearchToResults(String searchText){
        PageParameters params = new PageParameters();
        params.add("searchText", searchText);

        throw new RestartResponseException(SearchResults.class, params);
    }
}
