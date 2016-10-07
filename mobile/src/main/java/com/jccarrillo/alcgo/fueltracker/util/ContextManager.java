package com.jccarrillo.alcgo.fueltracker.util;

import android.content.Context;

/**
 * Created by Juan Carlos on 04/10/2016.
 */

public class ContextManager {

    private static ContextManager mINSTANCE = null;

    public static  ContextManager getInstance(){
        if( mINSTANCE == null )
            mINSTANCE = new ContextManager();
        return mINSTANCE;
    }

    private Context mContext;

    private ContextManager(){

    }


    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}
