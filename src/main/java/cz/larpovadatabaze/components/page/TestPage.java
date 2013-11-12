package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.ImagePanel;
import cz.larpovadatabaze.entities.Image;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import java.io.Serializable;

/**
 *
 */
public class TestPage extends CsldBasePage {
    private ImageO images;

    public TestPage(){
        images = new ImageO();
        final Form<ImageO> form = new Form<ImageO>("imageForm", new CompoundPropertyModel<ImageO>(images));

        final ImagePanel panel = new ImagePanel("image");
        form.add(panel);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                super.onSubmit();

                panel.getModelObject();
                ImageO image = form.getModelObject();
                image.getImage();
            }
        });

        add(form);
    }

    private class ImageO implements Serializable {
        private Image image;

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }
    }
}
