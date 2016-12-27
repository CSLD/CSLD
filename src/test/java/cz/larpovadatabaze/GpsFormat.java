package cz.larpovadatabaze;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jbalhar on 27. 12. 2016.
 */
public class GpsFormat {
    @Test
    public void testGoogle() {
        assertTrue("50.076721, 14.431727".matches("^[0-9]+(.[0-9]+)?, [0-9]+(.[0-9]+)?$"));
    }

    @Test
    public void testGoogleInvalid() {
        assertFalse("50.076721N, 14.431727E".matches("^[0-9]+(.[0-9]+)?, [0-9]+(.[0-9]+)?$"));
    }

    @Test
    public void testMapy() {
        assertTrue("50.0822503N, 14.5147475E".matches("^[0-9]+(.[0-9]+)?N, [0-9]+(.[0-9]+)?E$"));
    }

    @Test
    public void testMapyInvalid() {
        assertFalse("50.0822503, 14.5147475".matches("^[0-9]+(.[0-9]+)?N, [0-9]+(.[0-9]+)?E$"));
    }
}
