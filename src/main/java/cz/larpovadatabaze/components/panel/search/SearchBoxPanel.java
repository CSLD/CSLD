package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.form.SearchForm;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 14:18
 */
public class SearchBoxPanel extends Panel {

    public SearchBoxPanel(String id) {
        super(id);
        add(new SearchForm("search"));
    }
}
