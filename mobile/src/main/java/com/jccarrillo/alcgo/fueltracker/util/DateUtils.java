package com.jccarrillo.alcgo.fueltracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
