package com.jccarrillo.alcgo.fueltracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jccarrillo.alcgo.fueltracker.adapter.CarsAdapter;
import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.repository.CarInfoRepository;
import com.jccarrillo.alcgo.fueltracker.util.ContextManager;

import java.util.ArrayList;
import java.util.List;

public class CarsListActivity extends AppCompatActivity {

    private CarsAdapter mAdapter;
    private ListView mListView;
    private FloatingActionButton mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_list);

        initialize();
        populate();
        listeners();
    }

    private void initialize(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.content_cars_list);
        mAddButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void populate(){
        List<Integer> list = CarInfoRepository.getListOfCarInfo();
        List<CarInfo> cars = new ArrayList<>();
        for( Integer c_id: list ){
            CarInfo c = CarInfoRepository.getCarInfo( c_id );
            if( c != null )
                cars.add( c );
        }

        mAdapter = new CarsAdapter( this, cars );

        if( cars.size() == 0 ){
            // Añadimos a través del sumario
            showAddCar();
            finish();
        } else if( cars.size() == 1 ){
            // Mostramos
            showCarSummary( cars.get( 0 ) );
            finish();
        }
        mListView.setAdapter( mAdapter );
    }

    private void listeners(){
        mAddButton.setOnClickListener( mOnAddClick );
        mListView.setOnItemClickListener( mOnItemClick );
        mListView.setOnItemLongClickListener( mOnItemLongItemClick );
    }

    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CarInfo mCar = (CarInfo) mAdapter.getItem( position );
            showCarSummary( mCar );
        }
    };

    private void showCarSummary( CarInfo carInfo ){
        Intent intent = new Intent( this, SummaryActivity.class );
        intent.putExtra( SummaryActivity.BUNDLE_CARINFO, carInfo );
        startActivity( intent );
    }

    private void showAddCar(){
        showCarSummary( null );
    }

    private View.OnClickListener mOnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showAddCar();
        }
    };

    private AdapterView.OnItemLongClickListener mOnItemLongItemClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder( CarsListActivity.this );
            builder.setTitle(R.string.remove_car_title);
            builder.setMessage(R.string.remove_car_msg);
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CarInfo mCar = (CarInfo) mAdapter.getItem( position );
                    CarInfoRepository.removeObject( mCar );
                    populate();
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
    };

}
