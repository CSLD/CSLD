package cz.larpovadatabaze.components.panel;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 30.4.13
 * Time: 12:42
 */
public class AuthorsPanel extends Panel {
    public AuthorsPanel(String id) {
        super(id);

        add(new AddAuthorPanel("addAuthor"));
        add(new MostActiveCreators("mostActiveCreators"));
    }
}
