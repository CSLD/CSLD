package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class UserResultsPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    public UserResultsPanel(String id, String query) {
        super(id);

        if(query == null) {
            query = "";
        }

        List<CsldUser> allResults = csldUserService.getAll();
        List<CsldUser> searchResults = new ArrayList<CsldUser>();
        for(CsldUser result: allResults) {
            if(result.getAutoCompleteData().toLowerCase().contains(query.toLowerCase())){
                searchResults.add(result);
            }
        }

        List<CsldUser> fullResults;
        List<CsldUser> shortResults;

        if(searchResults.size() > 2){
            fullResults = searchResults.subList(0,3);
            shortResults = searchResults.subList(3, searchResults.size());
        } else {
            fullResults = searchResults.subList(0,searchResults.size());
            shortResults = new ArrayList<CsldUser>();
        }
        ListView<CsldUser> fullList = new ListView<CsldUser>("fullResults", fullResults) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser actualUser = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", actualUser.getId());

                if(actualUser.getImage() == null) {
                    actualUser.setImage(cz.larpovadatabaze.entities.Image.getDefaultUser());
                }

                final BookmarkablePageLink<CsldBasePage> moderatorLink =
                        new BookmarkablePageLink<CsldBasePage>("authorLink", UserDetail.class, params);
                final Image moderatorImage = new Image("authorLinkImage",
                        new ContextRelativeResource(actualUser.getImage().getPath()));
                moderatorLink.add(moderatorImage);
                item.add(moderatorLink);

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("authorLinkContent", UserDetail.class, params);
                final Label moderatorNick = new Label("authorNick", actualUser.getPerson().getNickname());
                final Label moderatorName = new Label("authorName", actualUser.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);

                Label year = new Label("year", Model.of(actualUser.getPerson().getAge()));
                item.add(year);

                Label city = new Label("city", Model.of(actualUser.getPerson().getCity()));
                item.add(city);
            }
        };
        add(fullList);

        ListView<CsldUser> othersList = new ListView<CsldUser>("shortResults", shortResults) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser actualUser = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", actualUser.getId());

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("authorShortLink", UserDetail.class, params);
                final Label moderatorNick = new Label("authorShortNick", actualUser.getPerson().getNickname());
                final Label moderatorName = new Label("authorShortName", actualUser.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);
            }
        };
        add(othersList);
    }
}
