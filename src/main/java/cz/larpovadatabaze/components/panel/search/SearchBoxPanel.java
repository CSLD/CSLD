package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.page.search.GameSearchProvider;
import cz.larpovadatabaze.components.page.search.SearchResultsPage;
import cz.larpovadatabaze.components.panel.game.GameBoxPanel;
import cz.larpovadatabaze.entities.Game;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Box containing field for queryString and submitting button.
 */
public class SearchBoxPanel extends Panel {

    public final static String QUERY_PARAMETER_NAME = "queryString";
    private static final int MAX_RESULTS_IN_DROPDOWN = 5;

    /**
     * After how many seconds after keyup search-on-type starts
     */
    private static final long TIMEOUT_MS = 100;

    /**
     * Minimum length of query for search-on-type
     */
    private static final long MIN_TERM_LENGTH = 2;

    private GameSearchProvider gameSearchProvider;

    private WebMarkupContainer searchResultWrapper;
    private TextField<String> searchExpression;
    private AjaxSubmitLink previewLink;

    public SearchBoxPanel(String id) {
        super(id);

        String query = getWebRequest().getQueryParameters().getParameterValue(QUERY_PARAMETER_NAME).toString(null);

        final Model<String> searchText = Model.of(query);
        Form<String> search = new Form<String>("search"){
            @Override
            protected void onSubmit() {
                super.onSubmit();

                if (findSubmittingButton() != previewLink) {
                    sendSearchToResults(searchText.getObject());
                }
            }
        };

        searchExpression = new TextField<>("searchExpression", searchText);
        searchExpression.setOutputMarkupId(true);
        search.add(searchExpression);
        search.add(new Button("sendSearch"));

        previewLink = new AjaxSubmitLink("previewLink", search) {
            public void onSubmit(AjaxRequestTarget target, Form form) {
                // Set new query
                gameSearchProvider.setQuery(searchExpression.getModelObject());

                // Refresh preview box
                searchResultWrapper.setVisible(true);
                target.add(searchResultWrapper);
            }
        };
        search.add(previewLink);

        searchResultWrapper = new WebMarkupContainer("searchResultWrapper");
        searchResultWrapper.setOutputMarkupId(true);
        searchResultWrapper.setOutputMarkupPlaceholderTag(true);
        searchResultWrapper.setVisible(false);
        search.add(searchResultWrapper);

        gameSearchProvider = new GameSearchProvider();
        gameSearchProvider.setMaxResults(MAX_RESULTS_IN_DROPDOWN);

        searchResultWrapper.add(new ListView<Game>("games", new AbstractReadOnlyModel<List<? extends Game>>() {
            @Override
            public List<? extends Game> getObject() {
                return gameSearchProvider.getGameList();
            }
        }) {
            @Override
            protected void populateItem(ListItem<Game> item) {
                item.add(new GameBoxPanel("game", item.getModel()));
            }
        });

        searchResultWrapper.add(new WebMarkupContainer("showAllLink") {
            @Override
            public boolean isVisible() {
                return gameSearchProvider.isMoreAvailable();
            }
        });

        add(search);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Render javascript for search-on-type
        PackageTextTemplate tt = new PackageTextTemplate(getClass(), "SearchBoxPanel.js");
        Map<String, String> args = new HashMap<>();
        args.put("textInputId", searchExpression.getMarkupId(true));
        args.put("searchLinkId", previewLink.getMarkupId(true));
        args.put("resultWrapperId", searchResultWrapper.getMarkupId(true));
        args.put("timeoutMS", Long.toString(TIMEOUT_MS));
        args.put("minTermLength", Long.toString(MIN_TERM_LENGTH));
        response.render(OnDomReadyHeaderItem.forScript(tt.asString(args)));
    }

    /**
     * Display search results page for given search text
     *
     * @param searchText Search text
     */
    private void sendSearchToResults(String searchText){
        PageParameters params = new PageParameters();
        params.add(QUERY_PARAMETER_NAME, searchText);

        throw new RestartResponseException(SearchResultsPage.class, params);
    }
}
