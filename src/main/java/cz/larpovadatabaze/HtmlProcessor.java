package cz.larpovadatabaze;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {
    private static final Whitelist whitelist = Whitelist
            .basic()
            .addTags("h1", "h2", "h3", "h4", "h5")
            .addEnforcedAttribute("a", "rel", "nofollow noreferrer noopener")
            .addEnforcedAttribute("a", "target", "_blank");

    private static final Pattern urlPattern = Pattern.compile("https?://[-=%_\\p{L}\\./\\d\\?]+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    /**
     * Replace URLs in text by proper links
     *
     * @param input
     *
     * @return
     */
    public static String createLinksFromUrls(String input) {
        if (input.toLowerCase().indexOf("</a>") >= 0) {
            // Already has A tags - do not create links
            return input;
        }

        Matcher m = urlPattern.matcher(input);
        return m.replaceAll(matchResult -> {
            String url = matchResult.group();

            return "<a href=\""+url+"\" target=\"_blank\">" + url + "</a>";
        });
    }

    /**
     * Sanitize HTML
     *
     * @param input Input html
     *
     * @return Sanitized HTML
     */
    public static String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        return Jsoup.clean(input, whitelist);
    }
}
