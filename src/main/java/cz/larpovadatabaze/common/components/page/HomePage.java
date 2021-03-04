package cz.larpovadatabaze.common.components.page;

import org.apache.wicket.Page;
import org.apache.wicket.markup.head.IHeaderResponse;

/**
 *
 */
public class HomePage extends Page {
    public HomePage(){
        setVersioned(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
    
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }
}
