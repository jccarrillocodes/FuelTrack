package com.jccarrillo.alcgo.fueltracker.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Juan Carlos on 05/10/2016.
 */

public class KeyboardUtils {

    public static void hide( Context context, View view ){
        InputMethodManager im = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
