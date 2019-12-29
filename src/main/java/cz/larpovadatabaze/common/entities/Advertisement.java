package cz.larpovadatabaze.common.entities;

import org.apache.wicket.markup.html.WebPage;

import java.io.Serializable;

/**
 * Advertisement on the main page
 *
 * User: Michal Kara Date: 7.3.15 Time: 18:45
 */
public class Advertisement implements Serializable {
    /**
     * Page to link to
     */
    private Class<? extends WebPage> pageClass;

    /**
     * Image of the advertisement
     */
    private String image;

    public Class<? extends WebPage> getPageClass() {
        return pageClass;
    }

    public void setPageClass(Class<? extends WebPage> pageClass) {
        this.pageClass = pageClass;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
