package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.Strings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It shows user results of search.
 */
public class UserResultsPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    public UserResultsPanel(String id, String query) {
        super(id);

        if(query == null) {
            query = "";
        }

        List<CsldUser> allResults = csldUserService.getAll();
        List<CsldUser> searchResults = new ArrayList<CsldUser>();
        for(CsldUser result: allResults) {
            if(Strings.containsIgnoreCaseAndAccents(result.getAutoCompleteData(), query)){
                searchResults.add(result);
            }
        }

        List<CsldUser> fullResults;
        List<CsldUser> shortResults;

        if(searchResults.size() > 2){
            fullResults = new ArrayList<CsldUser>(searchResults.subList(0,3));
            shortResults = new ArrayList<CsldUser>(searchResults.subList(3, searchResults.size()));
        } else {
            fullResults = new ArrayList<CsldUser>(searchResults.subList(0,searchResults.size()));
            shortResults = new ArrayList<CsldUser>();
        }
        ListView<CsldUser> fullList = new ListView<CsldUser>("fullResults", fullResults) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser actualUser = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", actualUser.getId());

                final BookmarkablePageLink<CsldBasePage> moderatorLink =
                        new BookmarkablePageLink<CsldBasePage>("authorLink", UserDetail.class, params);
                final UserIcon moderatorImage = new UserIcon("authorLinkImage", item.getModel());
                moderatorLink.add(moderatorImage);
                item.add(moderatorLink);

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("authorLinkContent", UserDetail.class, params);
                final Label moderatorNick = new Label("authorNick", actualUser.getPerson().getNickNameView());
                final Label moderatorName = new Label("authorName", actualUser.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);

                Integer age = actualUser.getPerson().getAge();
                Label year = new Label("year", age);
                year.setVisible(age != null);
                item.add(year);

                Label city = new Label("city", Model.of(actualUser.getPerson().getCity()));
                item.add(city);
            }
        };
        add(fullList);

        add(new WebMarkupContainer("moreResults").setVisible(!shortResults.isEmpty()));

        ListView<CsldUser> othersList = new ListView<CsldUser>("shortResults", shortResults) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser actualUser = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", actualUser.getId());

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("authorShortLink", UserDetail.class, params);
                final Label moderatorNick = new Label("authorShortNick", actualUser.getPerson().getNickNameView());
                final Label moderatorName = new Label("authorShortName", actualUser.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);
            }
        };
        add(othersList);
    }
}
