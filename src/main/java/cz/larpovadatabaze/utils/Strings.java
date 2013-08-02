package cz.larpovadatabaze.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains utility methods useful when handling Strings.
 */
public class Strings {
    /**
     * This method is useful to remove last character from String. When the String is empty it returns
     * empty String. When the String is null it returns empty String.
     *
     * @param toRemove String to shorten.
     * @return Shortened String
     */
    public static String removeLast(String toRemove){
        if(toRemove == null || toRemove.equals("")){
            return "";
        }
        return toRemove.substring(0, toRemove.length() - 1);
    }

    public static boolean isMailValid(String email){
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String stringToHTMLString(String string) {
        StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++)
        {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                }
                else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            }
            else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\'')
                    sb.append("''");
                else if (c == '"')
                    sb.append("\"\"");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("<br/>");
                else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
