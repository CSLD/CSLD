package cz.larpovadatabaze.common.behavior.dotdotdot;

import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;
import cz.larpovadatabaze.common.exceptions.CsldRuntimeException;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * Behavior to apply the dotdotdot javascript to cut text and append dots
 * @link http://dotdotdot.frebsite.nl/
 *
 * User: Michal Kara Date: 15.3.15 Time: 16:43
 */
public class DotDotDotBehavior extends Behavior {
    // Possible wrapping modes
    public enum Wrap { WORD, LETTER, CHILDREN }

    private String ellipsis;
    private Wrap wrap;
    private Boolean fallbackToLetter;
    private String afterComponentId;
    private Boolean watch = Boolean.TRUE; // Always watch by default
    private Integer height;
    private Integer tolerance;

    /**
     * @param ellipsis Custom ellipsis text
     */
    public DotDotDotBehavior setEllipsis(String ellipsis) {
        this.ellipsis = ellipsis;
        return this;
    }

    /**
     * @param wrap Custom wrapping mode
     */
    public DotDotDotBehavior setWrap(Wrap wrap) {
        this.wrap = wrap;
        return this;
    }

    /**
     * @param fallbackToLetter Set custom value for "fallback to letter wrapping on long words"
     */
    public DotDotDotBehavior setFallbackToLetter(Boolean fallbackToLetter) {
        this.fallbackToLetter = fallbackToLetter;
        return this;
    }

    /**
     * @param afterComponentId ID of component to put after text
     */
    public DotDotDotBehavior setAfterComponentId(String afterComponentId) {
        this.afterComponentId = afterComponentId;
        return this;
    }

    /**
     * @param watch Set whether to watch for window size changes (default is true)
     */
    public DotDotDotBehavior setWatch(Boolean watch) {
        this.watch = watch;
        return this;
    }

    /**
     * @param height Force height of the container
     */
    public DotDotDotBehavior setHeight(Integer height) {
        this.height = height;
        return this;
    }

    /**
     * @param tolerance Set tolerance for height
     */
    public DotDotDotBehavior setTolerance(Integer tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    /**
     * @xparam hideAfterComponentOnNoEllipsis If true and after component id is set, "after" component will be hidden when text is not shortened
    public DotDotDotBehavior setHideAfterComponentOnNoEllipsis(boolean hideAfterComponentOnNoEllipsis) {
        this.hideAfterComponentOnNoEllipsis = hideAfterComponentOnNoEllipsis;
        return this;
    }
     */

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        if (tag.isAutoComponentTag()) {
            throw new IllegalArgumentException("Behavior would not work on auto component tags");
        }
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        // Create config
        JSONObject config = new JSONObject();
        try {
            if (ellipsis != null) {
                config.put("ellipsis", ellipsis);
            }
            if (wrap != null) {
                config.put("wrap", wrap.name().toLowerCase());
            }
            if (fallbackToLetter != null) {
                config.put("fallbackToLetter", fallbackToLetter);
            }
            if (afterComponentId != null) {
                config.put("after", "#"+afterComponentId);
            }
            if (watch != null) {
                config.put("watch", watch);
            }
            if (height != null) {
                config.put("height", height);
            }
            if (tolerance != null) {
                config.put("tolerance", tolerance);
            }
        } catch (JSONException e) {
            // Should not happen
            throw new CsldRuntimeException(e);
        }

        // Render JS to init dotdotdot
        response.render(OnDomReadyHeaderItem.forScript("$('#"+component.getMarkupId(true)+"').dotdotdot("+config.toString()+")"));

        // Require JS for the component
        response.render(JavaScriptHeaderItem.forReference(DotDotDotResourceReference.get()));
    }
}
