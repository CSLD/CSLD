package cz.larpovadatabaze.common.components.multiac;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * User: Michal Kara Date: 27.6.15 Time: 15:33
 */
public class MagicSuggestJavaScriptReference extends JavaScriptResourceReference {
    private static final MagicSuggestJavaScriptReference INSTANCE = new MagicSuggestJavaScriptReference();

    public static MagicSuggestJavaScriptReference get() { return INSTANCE; }

    public MagicSuggestJavaScriptReference() {
        super(MagicSuggestJavaScriptReference.class, "magicsuggest.js");
    }

}
