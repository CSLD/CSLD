package cz.larpovadatabaze.common.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * This class contains utility methods useful when handling Strings.
 */
public class Strings {
    private static final Pattern ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static boolean containsIgnoreCaseAndAccents(String haystack, String needle) {
        final String hsToCompare = removeAccents(haystack).toLowerCase();
        final String nToCompare = removeAccents(needle).toLowerCase();

        return hsToCompare.contains(nToCompare);
    }

    public static String removeAccents(String string) {
        return ACCENTS_PATTERN.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).replaceAll("");
    }
}
