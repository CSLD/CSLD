package cz.larpovadatabaze.common.components.multiac;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * User: Michal Kara Date: 27.6.15 Time: 15:44
 */
public class MagicSuggestCssReference extends CssResourceReference {
    private static final MagicSuggestCssReference INSTANCE = new MagicSuggestCssReference();

    public static MagicSuggestCssReference get() { return INSTANCE; }

    public MagicSuggestCssReference() {
        super(MagicSuggestCssReference.class, "magicsuggest.css");
    }
}
