package com.jccarrillo.alcgo.fueltracker.fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jccarrillo.alcgo.fueltracker.SettingsActivity;

/**
 * Created by Juan Carlos on 16/10/2016.
 */

public class BasePreferenceFragment extends PreferenceFragment {

    protected void bindPreferenceSummaryToValue(Preference preference, String defaultValue ){
        if( getActivity() instanceof  SettingsActivity )
            (( SettingsActivity ) getActivity() ).bindPreferenceSummaryToValue( preference, defaultValue );
    }


}
