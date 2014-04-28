package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.resource.loader.IStringResourceLoader;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class contains utility methods useful when handling Strings.
 */
public class Strings {
    private static final Pattern ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

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


    public static boolean containsIgnoreCaseAndAccents(String haystack, String needle) {
        final String hsToCompare = removeAccents(haystack).toLowerCase();
        final String nToCompare = removeAccents(needle).toLowerCase();

        return hsToCompare.contains(nToCompare);
    }

    public static String removeAccents(String string) {
        return ACCENTS_PATTERN.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).replaceAll("");
    }

    public static String getResourceString(final Class clazz, final String propertyName) {
        List<IStringResourceLoader> loaderList = Application.get().getResourceSettings().getStringResourceLoaders();
        for (IStringResourceLoader loader : loaderList) {
            String value = loader.loadStringResource(clazz, propertyName, null, CsldAuthenticatedWebSession.get().getStyle(), null);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
