package cz.larpovadatabaze.api;

import cz.larpovadatabaze.common.api.NoResultException;
import cz.larpovadatabaze.common.api.NullOptional;
import cz.larpovadatabaze.common.api.Option;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NullOptionalTest {
    @Test(expected = NoResultException.class)
    public void testNullThrowsException() {
        String result = null;
        Option<String> option = new NullOptional<String>(result);
        option.getResult();
    }

    @Test
    public void testNullMeansNotPresent() {
        String result = null;
        Option<String> option = new NullOptional<String>(result);
        assertThat(option.isPresent(), is(false));
    }

    @Test
    public void testNotNullMeansPresent() {
        String result = "correctResult";
        Option<String> option = new NullOptional<String>(result);
        assertThat(option.isPresent(), is(true));
    }

    @Test
    public void testReturnTheCorrectResultWhenNotNull(){
        String result = "correctResult";
        Option<String> option = new NullOptional<String>(result);
        assertThat(result, is(option.getResult()));
    }
}
