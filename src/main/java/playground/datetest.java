package playground;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Eric on 1/30/2015.
 */
public class datetest {
    //trying to figure out how to construct calendar object from string
    //in order to send JSON date string which can be converted to Calendar object for sessions and employees
    //
    public static void main(String[] args) throws Exception{
        //idea receive date as string mm/dd/yyyy
        //then in setter methods for beans, convert to proper constructor parameters for gregorian calendar function

        String dateFromJquery = "12/5/1999";
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
        Date date = sdf.parse(dateFromJquery); //throws exception
        System.out.println("Date parse from mm/dd/yyyy string: " + date);
        System.out.println("Formatted string when date is passed back to simple date formatter: " + sdf.format(date));

    }
}
