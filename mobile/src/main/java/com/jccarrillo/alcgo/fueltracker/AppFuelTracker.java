package com.jccarrillo.alcgo.fueltracker;

import android.app.Application;

import com.jccarrillo.alcgo.fueltracker.util.ContextManager;

/**
 * Created by Juan Carlos on 17/10/2016.
 */

public class AppFuelTracker extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextManager.getInstance().setContext(this);
    }
}
