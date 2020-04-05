package cz.larpovadatabaze.common.components.home;

import cz.larpovadatabaze.calendar.component.page.CreateOrUpdateEventPage;
import cz.larpovadatabaze.common.Toggles;
import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.OwlCarouselResourceReference;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Advertisement;
import cz.larpovadatabaze.games.components.page.CreateOrUpdateGamePage;
import cz.larpovadatabaze.games.components.page.ListGamePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.page.CreateUserPage;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Michal Kara Date: 7.3.15 Time: 18:43
 */
public class AdvertisementPanel extends AbstractCsldPanel<List<Advertisement>> {
    private final static Logger logger = Logger.getLogger(AdvertisementPanel.class);

    public AdvertisementPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

    }
}
