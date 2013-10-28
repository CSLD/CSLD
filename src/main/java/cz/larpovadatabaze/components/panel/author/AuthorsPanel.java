package cz.larpovadatabaze.components.panel.author;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * It combines panel allowing to create new author with panel showing author, who created most
 * games.
 */
public class AuthorsPanel extends Panel {
    public AuthorsPanel(String id) {
        super(id);

        add(new AddAuthorPanel("addAuthor"));
        add(new MostActiveCreators("mostActiveCreators"));
    }
}
