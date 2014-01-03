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
            addCustomSetting("plugins: 'paste'");
            addCustomSetting("paste_text_sticky: true");
            addCustomSetting("paste_text_sticky_default: true");
            /* (if we ever want a bit formatting kept, remove "sticky" lines above and allow the ones below
            addCustomSetting("paste_auto_cleanup_on_paste: true");
            addCustomSetting("paste_remove_spans: true");
            addCustomSetting("paste_remove_styles: true");
            addCustomSetting("paste_remove_styles_if_webkit: true");
            addCustomSetting("paste_strip_class_attributes: 'all'");
            */
        }
    }

    public CSLDTinyMceBehavior() {
        // Initialize with default settings
        super(new CLSDTinyMceSettings());
    }
}
