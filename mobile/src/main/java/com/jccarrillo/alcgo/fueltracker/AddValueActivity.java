package com.jccarrillo.alcgo.fueltracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.jccarrillo.alcgo.fueltracker.domain.DrivingType;
import com.jccarrillo.alcgo.fueltracker.domain.RefuelValue;
import com.jccarrillo.alcgo.fueltracker.util.DateUtils;
import com.jccarrillo.alcgo.fueltracker.util.KeyboardUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class AddValueActivity extends AppCompatActivity {

    public static String BUNDLE_VALUE = "addvalueactivity:value";
    public static String BUNDLE_POSITION = "addvalueactivity:position";

    // UI references
    private EditText mEditTextDate;
    private EditText mEditTextDistance;
    private EditText mEditTextQuantity;
    private EditText mEditTextCost;
    private Spinner mSpinner01;
    private RefuelValue mValue;
    private Button mButtonSave;
    private Button mButtonAdd;
    private boolean mIsNew = true;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getBundleArguments();
        initialize();
        populate();
        listeners();
    }

    protected void getBundleArguments() {
        Bundle args =  getIntent().getExtras();
        if (args != null) {
            if( args.containsKey( BUNDLE_VALUE ) ){
                mValue = (RefuelValue) args.getSerializable(BUNDLE_VALUE);
                mIsNew = false;
            }

            if( args.containsKey( BUNDLE_POSITION ) )
                mPosition = args.getInt( BUNDLE_POSITION );
        }
    }

    private void initialize(){
        mEditTextDate = (EditText) findViewById(R.id.editTextDate);
        mEditTextDistance = (EditText) findViewById(R.id.editTextDistance);
        mEditTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        mEditTextCost = (EditText) findViewById(R.id.editTextCost);
        mSpinner01 = (Spinner) findViewById(R.id.spinner01);
        mButtonSave = (Button) findViewById(R.id.buttonSave);
        mButtonAdd = (Button) findViewById(R.id.buttonAdd);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mEditTextDate.setShowSoftInputOnFocus(false);

        if( mIsNew ) {
            // Mockup
            mValue = new RefuelValue();
            mValue.setQuantity(10.12);
            mValue.setCost(100.4);
            mValue.setDate(new Date());
            mValue.setDistance(1000.0);
            mValue.setDrivingType(DrivingType.MIXED);
        }

    }

    private void populate(){
        mEditTextDate.setText(DateUtils.toString( mValue.getDate() ) );
        mEditTextDistance.setText( String.valueOf( mValue.getDistance() ) );
        mEditTextCost.setText( String.valueOf( mValue.getCost() ) );
        mEditTextQuantity.setText( String.valueOf( mValue.getQuantity() ) );
        switch (mValue.getDrivingType()){
            case CITY:
                mSpinner01.setSelection(0);
                break;
            case MIXED:
                mSpinner01.setSelection(1);
                break;
            case HIGHWAY:
                mSpinner01.setSelection(2);
                break;
        }

        mButtonAdd.setVisibility( mIsNew ? View.VISIBLE : View.GONE );
        mButtonSave.setVisibility( !mIsNew ? View.VISIBLE : View.GONE );
    }

    private void listeners(){
        mEditTextDate.setOnFocusChangeListener(mOnFocusEditTextDate);
        mEditTextDistance.addTextChangedListener(mOnEditTextChange);
        mEditTextQuantity.addTextChangedListener(mOnEditTextChange);
        mEditTextCost.addTextChangedListener(mOnEditTextChange);
        mSpinner01.setOnItemSelectedListener(mOnSpinnerDrivingItemListener);
        mButtonSave.setOnClickListener(mOnSaveClick);
        mButtonAdd.setOnClickListener(mOnAddClick);
    }

    private OnClickListener mOnAddClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra(BUNDLE_VALUE,mValue);
            setResult(RESULT_OK, data );
            finish();
        }
    };

    private OnClickListener mOnSaveClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra(BUNDLE_VALUE, mValue );
            data.putExtra(BUNDLE_POSITION,mPosition);
            setResult(RESULT_OK, data );
            finish();
        }
    };

    private AdapterView.OnItemSelectedListener mOnSpinnerDrivingItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    mValue.setDrivingType(DrivingType.CITY);
                    break;
                case 1:
                    mValue.setDrivingType(DrivingType.MIXED);
                    break;
                case 2:
                    mValue.setDrivingType(DrivingType.HIGHWAY);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private TextWatcher mOnEditTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            try{
                mValue.setDistance( Double.parseDouble( mEditTextDistance.getText().toString() ) );
            }catch (Exception exception ){

            }
            try{
                mValue.setCost( Double.parseDouble( mEditTextCost.getText().toString() ) );
            }catch (Exception ex ){

            }
            try{
                mValue.setQuantity( Double.parseDouble( mEditTextQuantity.getText().toString() ) );
            }catch (Exception ex ){

            }
        }
    };

    private View.OnFocusChangeListener mOnFocusEditTextDate = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if( hasFocus ){
                KeyboardUtils.hide( AddValueActivity.this, v );
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(AddValueActivity.this, mOnPickedDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener mOnPickedDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mValue.setDate( calendar.getTime() );
            mEditTextDate.setText(DateUtils.toString( mValue.getDate() ) );

        }
    };

    public boolean onOptionsItemSelected(MenuItem item){
        setResult(RESULT_CANCELED);
        finish();
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_value, menu);
        return true;
    }
}

