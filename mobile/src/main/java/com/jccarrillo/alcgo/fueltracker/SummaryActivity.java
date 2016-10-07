package com.jccarrillo.alcgo.fueltracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.jccarrillo.alcgo.fueltracker.util.ListViewAnimationHelper;

import java.util.Date;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    public static final int REQUESTCODE_ADD = 0xFB34;
    public static final int REQUESTCODE_MODIFY = 0xFB35;

    private TextView mModel;
    private TextView mCompany;
    private TextView mNoData;
    private ListView mListView;
    private RefuelValueAdapter mAdapter;
    private CarInfo mCarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContextManager.getInstance().setContext(this);

        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        if (id == R.id.action_settings) {
            return true;
        } else if( id == R.id.action_add_car ){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize(){

        List<Integer> all = CarInfoRepository.getListOfCarInfo();
        if( all != null || all.size() == 0 ) {
            // Mock values
            mCarInfo = new CarInfo();
            mCarInfo.setModel("Meriva");
            mCarInfo.setCompany("Opel");

            RefuelValue value = new RefuelValue();
            value.setDate(new Date());
            value.setCost(100.126);
            value.setQuantity(45.126);
            value.setDistance(234.0);
            value.setDrivingType(DrivingType.MIXED);

            for (int i = 0; i < 100; ++i) {
                mCarInfo.getRefuelValues().add(value);
                value = new RefuelValue();
                value.setDate(new Date());
                value.setCost(94.126);
                value.setQuantity(55.126);
                value.setDistance(i * 1.5);
                mCarInfo.getRefuelValues().add(value);
                value.setDrivingType(DrivingType.CITY);
            }

            // End of mock values
        } else {
            mCarInfo = CarInfoRepository.getCarInfo( all.get( 0 ) );
        }

        mModel = (TextView) findViewById(R.id.textView01);
        mCompany = (TextView) findViewById(R.id.textView02);
        mNoData = (TextView) findViewById(R.id.textView03);
        mListView = (ListView) findViewById(R.id.listView01);
        mListView.setEmptyView( mNoData );
        mAdapter = new RefuelValueAdapter( this, null );
    }

    private void populate(){
        mModel.setText( mCarInfo.getModel() );
        mCompany.setText( mCarInfo.getCompany() );

        mAdapter.setValueList( mCarInfo.getRefuelValues() );
        mListView.setAdapter( mAdapter );
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
                }catch (Exception ex ){
                    Log.e(SummaryActivity.class.toString(),"Error modificando valor");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
