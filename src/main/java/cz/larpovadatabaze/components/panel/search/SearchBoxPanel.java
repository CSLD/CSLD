package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.page.search.SearchResults;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Box containing field for queryString and submitting button.
 */
public class SearchBoxPanel extends Panel {

    public final static String QUERY_PARAMETER_NAME = "queryString";

    public SearchBoxPanel(String id) {
        super(id);

        String query = getWebRequest().getQueryParameters().getParameterValue(QUERY_PARAMETER_NAME).toString(null);

        final Model<String> searchText = Model.of(query);
        Form<String> search = new Form<String>("search"){
            @Override
            protected void onSubmit() {
                super.onSubmit();

                sendSearchToResults(searchText.getObject());
            }
        };

        search.add(new TextField<String>("searchExpression", searchText));
        search.add(new Button("sendSearch"));

        add(search);
    }

    private void sendSearchToResults(String searchText){
        PageParameters params = new PageParameters();
        params.add(QUERY_PARAMETER_NAME, searchText);

        throw new RestartResponseException(SearchResults.class, params);
    }
}
