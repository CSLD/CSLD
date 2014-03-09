package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.components.page.CsldBasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class BookmarkableLinkWithLabel extends Panel {
    private Class linkPage;
    private IModel<String> text;
    private IModel<PageParameters> params;

    public BookmarkableLinkWithLabel(String id,
                                     Class linkPage,
                                     IModel<String> text) {
        this(id, linkPage, text, Model.of(new PageParameters()));
    }

    public BookmarkableLinkWithLabel(String id,
                                     Class linkPage,
                                     IModel<String> text,
                                     IModel<PageParameters> params) {
        super(id);

        this.linkPage = linkPage;
        this.text = text;
        this.params = params;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BookmarkablePageLink<CsldBasePage> link =
                new BookmarkablePageLink<CsldBasePage>("link", linkPage, params.getObject());
        link.add(new Label("label", text));
        add(link);
    }
}
