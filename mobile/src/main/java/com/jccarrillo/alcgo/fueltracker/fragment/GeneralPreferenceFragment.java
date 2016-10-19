package com.jccarrillo.alcgo.fueltracker.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.jccarrillo.alcgo.fueltracker.R;
import com.jccarrillo.alcgo.fueltracker.SettingsActivity;
import com.jccarrillo.alcgo.fueltracker.util.Global;

/**
 * Created by Juan Carlos on 16/10/2016.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        bindPreferenceSummaryToValue(findPreference("quantity"), Global.DEFAULT_QUANTITY);
        bindPreferenceSummaryToValue(findPreference("currency"), Global.DEFAULT_CURRENCY);
        bindPreferenceSummaryToValue(findPreference("distance"), Global.DEFAULT_DISTANCE);
        bindPreferenceSummaryToValue(findPreference("drivingtype"), Global.DEFAULT_DRIVING );
        bindPreferenceSummaryToValue(findPreference("value_in_list"), Global.DEFAULT_VALUE_IN_LIST );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}