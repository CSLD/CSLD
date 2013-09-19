package cz.larpovadatabaze.components.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class YouTubePanel extends Panel {
    private int width = 350;
    private int height = 350;
    private String src;

    private boolean isVisible;

    public YouTubePanel(String id, String src, boolean isVisible) {
        super(id);
        this.src = src;

        this.isVisible = isVisible;
        add(new AttributeModifier("width", new PropertyModel(this, "width")));
        add(new AttributeModifier("height", new PropertyModel(this, "height")));
        add(new AttributeModifier("src", new PropertyModel(this, "src")));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(isVisible);
    }
}