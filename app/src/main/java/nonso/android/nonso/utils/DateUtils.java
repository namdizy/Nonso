package nonso.android.nonso.utils;

import android.content.Context;
import android.text.format.DateFormat;

import nonso.android.nonso.R;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {


    public  String getTimeAgo(Date date, Context ctx) {

        long smsTimeInMilis = date.getTime();
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();


        final String timeFormatString = "h:mm a";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm a";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today at " + DateFormat.format(timeFormatString, date);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday at " + DateFormat.format(timeFormatString, date);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, date).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", date).toString();
        }
    }

}
