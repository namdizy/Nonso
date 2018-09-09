package nonso.android.nonso.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

//    private static final int SECOND_MILLIS = 1000;
//    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
//    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
//    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


//    private static Date currentDate() {
//        Calendar calendar = Calendar.getInstance();
//        return calendar.getTime();
//    }
//
//    public static String getTimeAgo(Date date, Context ctx) {
//        long time = date.getTime();
//        if (time < 1000000000000L) {
//            time *= 1000;
//        }
//
//        long now = currentDate().getTime();
//        if (time > now || time <= 0) {
//            return "in the future";
//        }
//
//        final long diff = now - time;
//        if (diff < MINUTE_MILLIS) {
//            return "moments ago";
//        } else if (diff < 60 * MINUTE_MILLIS) {
//            return diff / MINUTE_MILLIS + " m";
//        } else if (diff < 24 * HOUR_MILLIS) {
//            return diff / HOUR_MILLIS + " h";
//        } else {
//            return diff / DAY_MILLIS + " d";
//        }
//    }


    public static String getTimeAgoShort(Date date) {
        Date now = new Date();
        long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - date.getTime());
        long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime());
        long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime());
        long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime());


        if(seconds<60)
        {
            return seconds +"s";
        }
        else if(minutes<60)
        {
            return minutes + "m";
        }
        else if(hours<24)
        {
            return hours +"h";
        }
        else{
            return days + "d";
        }
    }

}
