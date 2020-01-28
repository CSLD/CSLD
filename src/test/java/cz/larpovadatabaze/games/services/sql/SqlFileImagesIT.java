package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.games.services.Images;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SqlFileImagesIT extends WithDatabase {
    private Images underTest;

    @Before
    public void prepareImages() {
        underTest = new SqlFileImages(sessionFactory,
                Mockito.mock(FileService.class));
    }

    @Test
    public void testGetImageResource() {

    }
}
