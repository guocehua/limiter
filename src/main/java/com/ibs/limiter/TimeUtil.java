package com.ibs.limiter;

import java.util.Calendar;

public class TimeUtil {
    private static Calendar calendar =Calendar.getInstance();
    public static int getDayOfMonth(long time){
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static int getMonthNumber(long time){
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH);
    }
}
