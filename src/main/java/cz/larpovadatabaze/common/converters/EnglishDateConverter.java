package cz.larpovadatabaze.common.converters;


import org.apache.wicket.util.convert.converter.DateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by jbalhar on 28. 8. 2016.
 */
public class EnglishDateConverter extends DateConverter {
    /**
     * @param locale
     * @return Returns the date format.
     */
    public DateFormat getDateFormat(Locale locale)
    {
        return new SimpleDateFormat("dd.MM.yyyy");
    }
}
