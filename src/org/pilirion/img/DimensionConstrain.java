package org.pilirion.img;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 3.1.13
 * Time: 19:37
 */
public class DimensionConstrain extends com.mortennobel.imagescaling.DimensionConstrain{
    private int width;
    private int height;

    public DimensionConstrain(int width, int height){
        this.width = width;
        this.height = height;
    }

    public java.awt.Dimension getDimension(java.awt.Dimension dimension) {
        double ratio;
        if(dimension.height > height){
            ratio = height / (double) dimension.height;
            width = (int) (dimension.width * ratio);
            return new Dimension(width, height);
        } else {
            return dimension;
        }
    }
}
