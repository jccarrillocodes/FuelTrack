package com.jccarrillo.alcgo.fueltracker.util;

/**
 * Created by Juan Carlos on 17/10/2016.
 */

public class FormatUtils {
    public static String doubleRounded( double value, int decimals ){
        if( decimals < 0 )
            decimals = 0;

        value = value * Math.pow( 10, decimals );
        value = Math.round( value );
        value = value / Math.pow( 10, decimals );

        return String.valueOf( value );
    }
}
