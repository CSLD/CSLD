package org.pilirion.img;

import org.junit.Test;

import java.awt.*;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 3.1.13
 * Time: 19:48
 */
public class DimensionConstrainTest {
    @Test
    public void testGetDimension() throws Exception {
        DimensionConstrain dim = new DimensionConstrain(120,120);
        Dimension result = dim.getDimension(new Dimension(400,240));
        assertEquals((int) result.getWidth(), 200);
    }
}
