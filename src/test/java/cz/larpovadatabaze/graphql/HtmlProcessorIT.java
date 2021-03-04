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

    @Test
    public void testSanitizePass() {
        String input = "<h1>Heading 1</h1>\n<h2>Heading 2</h2>\n<h3>Heading 3</h3>\n<p>Paragraph</p>";
        String expected = "<h1>Heading 1</h1> \n<h2>Heading 2</h2> \n<h3>Heading 3</h3> \n<p>Paragraph</p>";
        String output = HtmlProcessor.sanitizeHtml(input);
        assertThat(output, equalTo(expected));
    }

    @Test
    public void testRemoveJSSchema() {
        String input = "<a href=\"javascript:alert('aaa')\" onClick=\"alert('bbb')\">xxx</a>";
        String expected = "<a rel=\"nofollow noreferrer noopener\" target=\"_blank\">xxx</a>";
        String output = HtmlProcessor.sanitizeHtml(input);
        assertThat(output, equalTo(expected));
    }

    @Test
    public void testConvertLink() {
        String input = "<a href=\"https://www.centrum.cz\">xxx</a>";
        String expected = "<a href=\"https://www.centrum.cz\" rel=\"nofollow noreferrer noopener\" target=\"_blank\">xxx</a>";
        String output = HtmlProcessor.sanitizeHtml(input);
        assertThat(output, equalTo(expected));
    }
}
