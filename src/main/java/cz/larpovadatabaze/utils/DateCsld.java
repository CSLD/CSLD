package cz.larpovadatabaze.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 23.6.12
 * Time: 21:03
 */
public class DateCsld {
    public static String toCzech(Date format){
        Calendar cal = new GregorianCalendar();
        cal.setTime(format);
        int month = cal.get(Calendar.MONTH) + 1;
        return cal.get(Calendar.DAY_OF_MONTH) + "." + month + "." + cal.get(Calendar.YEAR);
    }

    /**
     * It accepts date as String in formats like: dd.mm.YYYY or yyyy.mm.yy or
     * dd-mm-yyyy or yyyy-mm-yy
     *
     * @param dateString
     * @return
     */
    public static Date parse(String dateString){
        if(dateString.indexOf(".") != -1){
            return DateCsld.parseCzech(dateString);
        } else if(dateString.indexOf("-") != -1){
            return DateCsld.parseInternational(dateString);
        } else {
            // This should never happen, but with user nothing is absolutely certain,
            return null;
        }
    }

    public static Date parseCzech(String dateString){
        if(dateString == null){
            Calendar cal = new GregorianCalendar();
            return cal.getTime();
        }
        String[] dateParts = dateString.split("\\.");
        return DateCsld.getDateFromParts(dateParts);
    }

    public static Date parseInternational(String dateString) {
        if(dateString == null){
            Calendar cal = new GregorianCalendar();
            return cal.getTime();
        }
        String[] dateParts = dateString.split("\\-");
        return DateCsld.getDateFromParts(dateParts);
    }

    static Date getDateFromParts(String[] dateParts){
        if(dateParts.length < 3){
            Calendar cal = new GregorianCalendar();
            return cal.getTime();
        }
        Calendar cal;
        if(dateParts[0].length() > 2){
            cal = new GregorianCalendar(Integer.parseInt(dateParts[0]),Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[2]),0,0,0);
        } else if (dateParts[2].length() < 4) {
            cal = new GregorianCalendar();
        } else {
            cal = new GregorianCalendar(Integer.parseInt(dateParts[2]),Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[0]),0,0,0);
        }
        return cal.getTime();
    }
}
