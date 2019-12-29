package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.entities.Video;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * This panel can contain embedded Video from youtube.
 */
public class YouTubePanel extends Panel {
    private int width = 350;
    private int height = 350;

    public YouTubePanel(String id, IModel<Video> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new AttributeModifier("width", Model.of(width)));
        add(new AttributeModifier("height", Model.of(height)));
        add(new AttributeModifier("src", Model.of(((Video)getDefaultModelObject()).getPath())));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }
}