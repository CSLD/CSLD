package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.HtmlProcessor;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HtmlProcessorIT {
    private static String makeLink(String url) {
        return "<a href=\"" + url + "\" target=\"_blank\">" + url +"</a>";
    }

    @Test
    public void justTheLink() {
        String res = HtmlProcessor.createLinksFromUrls("https://www.centrum.cz");

        assertThat(res, equalTo(makeLink("https://www.centrum.cz")));
    }

    @Test
    public void testSimpleLink() {
        String res = HtmlProcessor.createLinksFromUrls("Go to http://www.centrum.cz");

        assertThat(res, equalTo("Go to "+makeLink("http://www.centrum.cz")));
    }

    @Test
    public void testMultipleLinks() {
        String res = HtmlProcessor.createLinksFromUrls("Go to http://www.centrum.cz, not to http://www.seznam.cz/centrum!");

        assertThat(res, equalTo("Go to "+
                    makeLink("http://www.centrum.cz") +
                    ", not to "+
                    makeLink("http://www.seznam.cz/centrum") + "!"
                ));
    }

    @Test
    public void testIgnoreWhenThereIsAlreadyALink() {
        String input = "Go to http://www.centrum.cz, not to <a href=\"http://www.seznam.cz\">http://www.seznam.cz</a>!";
        String res = HtmlProcessor.createLinksFromUrls(input);

        assertThat(res, equalTo(input));
    }

    @Test
    public void testLongLink() {
        String url = "https://larpovadatabaze.cz/larp/krizova-vyprava-chudiny-1096/cs/50400";
        String res = HtmlProcessor.createLinksFromUrls(url);

        assertThat(res, equalTo(makeLink(url)));
    }
}
