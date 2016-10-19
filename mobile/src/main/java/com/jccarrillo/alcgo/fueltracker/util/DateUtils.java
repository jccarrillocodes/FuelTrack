package com.jccarrillo.alcgo.fueltracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Juan Carlos on 05/10/2016.
 */

public class DateUtils {

    private static SimpleDateFormat mSimpleDateFormat = null;

    public static String toString( Date date ){
        if( mSimpleDateFormat == null )
            mSimpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy", Locale.getDefault() );
        if( date == null )
            return "";
        return mSimpleDateFormat.format( date );
    }

    public static Date toDate( String str ){
        if( mSimpleDateFormat == null )
            mSimpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy", Locale.getDefault() );
        try {
            return mSimpleDateFormat.parse( str );
        } catch (ParseException e) {
            return null;
        }
    }

    public static int daysBetween(Date day1, Date day2){
        Calendar d1 = Calendar.getInstance(Locale.getDefault());
        d1.setTime( day1 );
        Calendar d2 = Calendar.getInstance(Locale.getDefault());
        d2.setTime( day2 );
        return daysBetween(d1,d2);
    }

    public static int daysBetween(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }

    public static boolean isSameDay( Calendar c1, Calendar c2 ){
        return c1.get( Calendar.DAY_OF_MONTH ) == c2.get( Calendar.DAY_OF_MONTH ) &&
                c1.get( Calendar.MONTH ) == c2.get( Calendar.MONTH ) &&
                c1.get( Calendar.YEAR ) == c2.get( Calendar.YEAR );
    }
}
