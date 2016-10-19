package com.jccarrillo.alcgo.fueltracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jccarrillo.alcgo.fueltracker.adapter.RefuelValueAdapter;
import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.domain.DrivingType;
import com.jccarrillo.alcgo.fueltracker.domain.RefuelValue;
import com.jccarrillo.alcgo.fueltracker.repository.CarInfoRepository;
import com.jccarrillo.alcgo.fueltracker.util.ContextManager;
import com.jccarrillo.alcgo.fueltracker.util.Global;
import com.jccarrillo.alcgo.fueltracker.util.ListViewAnimationHelper;

import java.util.Date;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    public static final int REQUESTCODE_ADD = 0xFB34;
    public static final int REQUESTCODE_MODIFY = 0xFB35;
    public static final int REQUESTCODE_ADD_CAR = 0xA354;
    public static final int REQUESTCODE_MODIFY_CAR = 0xB20;
    public static final String BUNDLE_CARINFO = "summaryactivty:carinfo";

    private TextView mModel;
    private TextView mCompany;
    private TextView mNoData;
    private ListView mListView;
    private RefuelValueAdapter mAdapter;
    private CarInfo mCarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getBundleArguments();
        initialize();
        populate();
        listeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == android.R.id.home ){
            Intent intent = new Intent( this, CarsListActivity.class );
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent( this, SettingsActivity.class );
            startActivity(intent);
            return true;
        } else if( id == R.id.action_statistics ){
            Intent intent = new Intent( this, StatisticsActivity.class );
            intent.putExtra(StatisticsActivity.BUNDLE_CARINFO, mCarInfo);
            startActivity( intent );
        } else if( id == R.id.action_edit_car ){
            Intent intent = new Intent( this, CarSettingsActivity.class );
            intent.putExtra(CarSettingsActivity.BUNDLE_CAR,mCarInfo);
            startActivityForResult( intent, REQUESTCODE_MODIFY_CAR );
        } else if( id == R.id.action_add_car ){
            Intent intent = new Intent( this, CarSettingsActivity.class );
            startActivityForResult( intent, REQUESTCODE_ADD_CAR );
        }

        return super.onOptionsItemSelected(item);
    }

    private void getBundleArguments(){
        Bundle args =  getIntent().getExtras();
        if (args != null)
            if( args.containsKey( BUNDLE_CARINFO ) )
                mCarInfo = (CarInfo) args.getSerializable(BUNDLE_CARINFO);
    }

    private void initialize(){
        List<Integer> all = CarInfoRepository.getListOfCarInfo();
        if( mCarInfo == null ) {
            // Añadimos
            Intent intent = new Intent( this, CarSettingsActivity.class );
            startActivityForResult( intent, REQUESTCODE_ADD_CAR );
        }

        if (all.size() > 1) {
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mModel = (TextView) findViewById(R.id.textView01);
        mCompany = (TextView) findViewById(R.id.textView02);
        mNoData = (TextView) findViewById(R.id.textView03);
        mListView = (ListView) findViewById(R.id.listView01);
        mListView.setEmptyView( mNoData );
        mAdapter = new RefuelValueAdapter( this, null );
    }

    private void populate(){
        if( mCarInfo != null ) {
            mCompany.setText(mCarInfo.getModel());
            mModel.setText(mCarInfo.getCompany());

            mAdapter.setValueList(mCarInfo.getRefuelValues());
            mListView.setAdapter(mAdapter);
        }
    }

    private void listeners(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( SummaryActivity.this, AddValueActivity.class );
                startActivityForResult( intent, REQUESTCODE_ADD );
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent( SummaryActivity.this, AddValueActivity.class );

                RefuelValue value = (RefuelValue) mAdapter.getItem( position );

                intent.putExtra( AddValueActivity.BUNDLE_VALUE, value );
                intent.putExtra( AddValueActivity.BUNDLE_POSITION, position );

                startActivityForResult( intent, REQUESTCODE_MODIFY );
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder( SummaryActivity.this );

                builder.setTitle(R.string.remove_value_title);
                builder.setMessage(R.string.remove_value_msg);
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListViewAnimationHelper<RefuelValue> helper = new ListViewAnimationHelper<RefuelValue>(
                                mAdapter,
                                mListView,
                                mCarInfo.getRefuelValues()
                        );
                        helper.animateRemoval(mListView, view, true);
                        CarInfoRepository.setCarInfo( mCarInfo );
                        Snackbar.make(SummaryActivity.this.mListView, R.string.value_removed , Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        if( mAdapter != null ) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            mAdapter.setCurrency(preferences.getString(Global.PREF_CURRENCY, Global.DEFAULT_CURRENCY));
            mAdapter.setQuantity(preferences.getString(Global.PREF_QUANTITY, Global.DEFAULT_QUANTITY));
            mAdapter.setQuantity(preferences.getString(Global.PREF_QUANTITY, Global.DEFAULT_DISTANCE));
            RefuelValueAdapter.DISPLAYMODE mode = RefuelValueAdapter.DISPLAYMODE.QUANTITY;
            String d = preferences.getString(Global.PREF_VALUE_IN_LIST, "0" );
            if( "0".equals( d ) )
                mode = RefuelValueAdapter.DISPLAYMODE.QUANTITY;
            else if( "1".equals( d ) )
                mode = RefuelValueAdapter.DISPLAYMODE.CURRENCY;
            else if( "2".equals( d ) )
                mode = RefuelValueAdapter.DISPLAYMODE.DISTANCE;
            mAdapter.setMode(mode);
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == REQUESTCODE_ADD ){
            if( resultCode == RESULT_OK ){

                try {
                    RefuelValue value = (RefuelValue) data.getSerializableExtra(AddValueActivity.BUNDLE_VALUE);

                    mCarInfo.addValue( value );

                    CarInfoRepository.setCarInfo( mCarInfo );

                    Snackbar.make(SummaryActivity.this.mListView, R.string.value_added , Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    populate();
                }catch ( Exception ex ){
                    Log.e(SummaryActivity.class.toString(),"Error añadiendo valor");
                }
            } else {
                if( mCarInfo == null )
                    finish();
            }
        }

        if( requestCode == REQUESTCODE_MODIFY ){
            if( resultCode == RESULT_OK ){

                try {
                    RefuelValue value = (RefuelValue) data.getSerializableExtra(AddValueActivity.BUNDLE_VALUE);
                    int position = data.getIntExtra(AddValueActivity.BUNDLE_POSITION,0);
                    mCarInfo.replaceValue(value,position);

                    CarInfoRepository.setCarInfo( mCarInfo );

                    Snackbar.make(SummaryActivity.this.mListView, R.string.value_saved, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    populate();

                    mListView.setSelection( position );
                    mListView.smoothScrollToPositionFromTop( position, 0, 10 );
                }catch (Exception ex ){
                    Log.e(SummaryActivity.class.toString(),"Error modificando valor");
                }
            }
        }

        if( requestCode == REQUESTCODE_MODIFY_CAR && resultCode == RESULT_OK ){
            mCarInfo = (CarInfo) data.getSerializableExtra(CarSettingsActivity.BUNDLE_CAR);
            populate();
        }

        if( requestCode == REQUESTCODE_ADD_CAR ){
            if( resultCode == RESULT_OK ) {
                mCarInfo = (CarInfo) data.getSerializableExtra(CarSettingsActivity.BUNDLE_CAR);
                initialize();
                populate();
                listeners();
            } else
                if( mCarInfo == null )
                    finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
