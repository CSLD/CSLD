package cz.larpovadatabaze.behavior;

import org.apache.wicket.request.resource.CssResourceReference;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * Tiny MCE behaviour which initializes TinyMCE the way WE want it!
 *
 * User: Michal Kara
 * Date: 22.12.13
 * Time: 9:38
 */
public class CSLDTinyMceBehavior extends TinyMceBehavior {

    private static class CLSDTinyMceSettings extends TinyMCESettings {
        public CLSDTinyMceSettings() {
            super();
            setContentCss(new CssResourceReference(CSLDTinyMceBehavior.class, "tinymce_custom.css"));
        }
    }

    public CSLDTinyMceBehavior() {
        // Initialize with default settings
        super(new CLSDTinyMceSettings());
    }
}
