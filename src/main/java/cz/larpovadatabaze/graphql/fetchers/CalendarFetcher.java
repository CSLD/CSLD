package cz.larpovadatabaze.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.GregorianCalendar;

/**
 * Fetcher to be used on fields that has Calendar value - prints it as an ISO date+time
 */
@Component
public class CalendarFetcher implements DataFetcher<String> {
    @Override
    public String get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        String fieldName = dataFetchingEnvironment.getField().getName();
        Object parent = dataFetchingEnvironment.getSource();

        Method getter = parent.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
        Object value = getter.invoke(parent);
        if (value == null || !(value instanceof GregorianCalendar)) {
            return null;
        }

        return FetcherUtils.formatDateTime((GregorianCalendar)value);
    }
}
