package cz.larpovadatabaze.components.common.i18n;

import cz.larpovadatabaze.lang.AvailableLanguages;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import java.util.Locale;

/**
 * Created by jbalhar on 11/30/2014.
 */
public class LocalePicker extends Panel{

    public LocalePicker(String id) {
        super(id);
    }

    private Image createLocaleFlag(Locale locale) {
        return new Image("flag", locale.getLanguage()+".png");
    }

    private Label createLocaleLabel(Locale locale) {
        return new Label("name", locale.getLanguage().toUpperCase());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        CsldAuthenticatedWebSession session = CsldAuthenticatedWebSession.get();
        if(session.isSignedIn()) {
            String userChosenLang = session.getLoggedUser().getDefaultLang();
            if (userChosenLang != null && session.isSetLanguage()) {
                session.setLocale(Locale.forLanguageTag(userChosenLang));
                session.setSetLanguage(false);
            }
        }

        // Draw current locale
        Locale currentLocale = Session.get().getLocale();
//        add(createLocaleFlag(currentLocale));
        add(createLocaleLabel(currentLocale));

        // Create view
        RepeatingView locales = new RepeatingView("locales");

        // Draw other locales
        for(Locale locale : AvailableLanguages.availableLocale()) {
            if (!locale.getLanguage().equals(currentLocale.getLanguage())) { // Do not add selected locale
                Fragment localePanel = new Fragment(locales.newChildId(), "localeItemAvailable", this);
                AjaxLink<Locale> link = new AjaxLink<Locale>("link", Model.of(locale)) {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        Session.get().setLocale(getModelObject());
                        throw new RestartResponseException(getPage().getPageClass());
                    }
                };
//                link.add(createLocaleFlag(locale));
                link.add(createLocaleLabel(locale));
                localePanel.add(link);
                locales.add(localePanel);
            }
        }

        add(locales);
    }
}