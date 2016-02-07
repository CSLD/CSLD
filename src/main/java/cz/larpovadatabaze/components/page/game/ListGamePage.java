package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AbstractListGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGameTabsPanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesSidePanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.LabelService;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class ListGamePage extends CsldBasePage {
    @SpringBean
    LabelService labelService;
    @SpringBean
    CsldUserService csldUserService;

    /**
     * Filter model used throughout the page
     */
    private final IModel<FilterGame> filterModel = new Model(new FilterGame());

    private FilterGameTabsPanel tabsPanel;
    private WebMarkupContainer requiredLabelsWrapper;
    private FilterGamesSidePanel sidePanel;
    private AbstractListGamePanel listGamePanel;

    public ListGamePage(PageParameters params) {
        initModel(params);

    }

    private final void initModel(PageParameters params) {
        /**
         * Initialize label filter from parameters
         */
        int ALL = -1;
        int NONE = -2;
        Integer labelId = params.get("label").toInt(NONE);

        CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();
        if(logged != null && labelId != NONE ) {
            // Save for logged user actual state
            logged.setLastRating(labelId);

            csldUserService.saveOrUpdate(logged);
        }

        if(labelId == NONE) {
            labelId = ALL;
            // Some user is logged.
            if(logged != null) {
                // Get what was last rating of the logged user
                labelId = logged.getLastRating();
                if(labelId == null) {
                    // If nothing set label to all
                    labelId = ALL;
                }
            }
        }

        List<Label> requiredLabels = labelService.getRequired();

        // Check if selected label is one of required labels
        List<Label> filterRequiredLabels = filterModel.getObject().getRequiredLabels();
        List<Label> filterOtherLabels = filterModel.getObject().getOtherLabels();
        filterRequiredLabels.clear();
        filterOtherLabels.clear();
        for(Label l : requiredLabels) {
            if (labelId.equals(l.getId())) {
                // Set to filter just this label
                filterRequiredLabels.add(l);
                return;
            }
        }

        if (labelId != ALL) {
            // Non-required label selected - add it to narrow filter
            filterOtherLabels.add(labelService.getById(labelId));
        }

        // Add user's locale as language
        filterModel.getObject().getLanguages().add((CsldAuthenticatedWebSession.get()).getLocale());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add panel for top tabs
        tabsPanel = new FilterGameTabsPanel("filterTabs", filterModel);
        tabsPanel.setOutputMarkupId(true);
        add(tabsPanel);

        // Required labels
        requiredLabelsWrapper = new WebMarkupContainer("requiredLabelsWrapper");
        requiredLabelsWrapper.setOutputMarkupId(true);
        add(requiredLabelsWrapper);
        requiredLabelsWrapper.add(new ListView<Label>("requiredLabels", filterModel.getObject().getRequiredLabels()) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                final Label labelObj = item.getModelObject();

                // Add wrapping link
                AjaxLink link = new AjaxLink<Label>("requiredOne", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        // Set just this link to model
                        FilterGame filter = filterModel.getObject();
                        filter.getRequiredLabels().clear();
                        filter.getRequiredLabels().add(labelObj);

                        // Refresh
                        filterChanged(false, true, false);
                    }
                };
                link.add(new org.apache.wicket.markup.html.basic.Label("text", WordUtils.capitalize(labelObj.getName())));
                item.add(link);

                // Add remove link (we must do it by behavior because it is link within link)
                WebMarkupContainer removeLink = new WebMarkupContainer("removeLink");
                link.add(removeLink);
                removeLink.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        FilterGame filter = filterModel.getObject();
                        filter.getRequiredLabels().remove(labelObj);

                        filterChanged(false, true, false);
                    }
                });
            }
        });

        listGamePanel = new AbstractListGamePanel<FilterGame>("listGame", filterModel) {
            @Override
            protected SortableDataProvider<Game, String> getDataProvider() {
                return new SortableGameProvider(getModel());
            }
        };
        listGamePanel.setOutputMarkupId(true);
        add(listGamePanel);

        sidePanel = new FilterGamesSidePanel("filterGames", filterModel);
        sidePanel.setOutputMarkupId(true);
        add(sidePanel);
    }

    public void filterChanged(boolean sortChanged, boolean requiredLabelsChanged, boolean otherLabelsChanged) {
        // Re-render what is needed
        AjaxRequestTarget art = getRequestCycle().find(AjaxRequestTarget.class);

        art.add(listGamePanel);

        if (sortChanged) {
            art.add(tabsPanel);
        }

        if (requiredLabelsChanged) {
            art.add(requiredLabelsWrapper);
            art.add(sidePanel.getRequiredLabelsWrapper());
        }

        if (otherLabelsChanged) {
            art.add(sidePanel);
        }
    }

    /**
     * @param labelId Label ID
     *
     * @return Page parameters to show games with this labe;
     */
    public static PageParameters getParametersForLabel(Integer labelId) {
        PageParameters pp = new PageParameters();
        pp.add("label", labelId);
        return pp;
    }
}
