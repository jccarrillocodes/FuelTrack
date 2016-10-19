package com.jccarrillo.alcgo.fueltracker.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Juan Carlos on 18/10/2016.
 */

public class Permissions {

    public static boolean canMakeSmores(){
        return Build.VERSION.SDK_INT > 22;
    }

    @TargetApi(23)
    public static boolean hasPermission(Activity activity, String permission){
        if(canMakeSmores())
            return activity.checkSelfPermission( permission ) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    public static boolean hasPermissions( Activity activity, String[] permissions ){
        if( permissions != null ){
            for( String perm: permissions )
                if( !hasPermission(activity, perm) )
                    return false;
        }
        return true;
    }

    public static boolean shouldWeAsk( Context context, String permission){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        return sharedPreferences.getBoolean( permission, true );
    }

    public static boolean shouldWeAsk( Context context, String[] permissions ){
        if( permissions != null ){
            for( String perm: permissions )
                if( shouldWeAsk(context, perm))
                    return true;
        }
        return false;
    }

    public static void markAsAsked(Context context, String permission){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        sharedPreferences
                .edit()
                .putBoolean(permission, false)
                .apply();

    }

    public static Boolean onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults, int permAskedRequestcode, String[] askedPermissions ){
        if( permsRequestCode == permAskedRequestcode && askedPermissions != null ){
            int count = 0;

            for( int i = 0; i < permissions.length; ++i )
                for( String asked: askedPermissions )
                    if( asked.equals( permissions[ i ] ) )
                        if( grantResults[ i ] == PackageManager.PERMISSION_GRANTED ){
                            ++count;
                            break;
                        }

            return count == askedPermissions.length ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }

}
