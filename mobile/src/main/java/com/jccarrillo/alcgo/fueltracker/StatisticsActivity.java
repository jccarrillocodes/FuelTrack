package com.jccarrillo.alcgo.fueltracker;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.domain.RefuelValue;
import com.jccarrillo.alcgo.fueltracker.util.DateUtils;
import com.jccarrillo.alcgo.fueltracker.util.FormatUtils;
import com.jccarrillo.alcgo.fueltracker.util.Global;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.Bar;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.BarGraph;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.BarStackSegment;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.Line;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.LineGraph;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.LinePoint;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.MultiSeriesDonutGraph;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.MultiSeriesDonutSlice;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.PieGraph;
import com.jccarrillo.alcgo.fueltracker.util.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {

    public static final String BUNDLE_CARINFO = "statisticsActivity:bundle_carinfo";

    private CarInfo mCarInfo;
    private PieGraph mPieGraphDrivingStyle;
    private TextView mTextViewDrivingType;
    private LineGraph mLineGraph;
    private BarGraph mBarGraphCost;
    private BarGraph mBarGraphDistance;
    private BarGraph mBarGraphQuantity;
    private TextView mTextViewTotalCost;
    private TextView mTextViewTotalDistance;
    private TextView mTextViewTotalQuantity;
    private LinearLayout mLinearLayoutLegend;
    private TextView mTextViewPartialCost;
    private TextView mTextViewPartialDistance;
    private TextView mTextViewPartialQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getBundleArguments();
        initialize();
        populate();
        listeners();
    }

    private void getBundleArguments(){
        Bundle args =  getIntent().getExtras();
        if (args != null)
            if( args.containsKey( BUNDLE_CARINFO ) )
                mCarInfo = (CarInfo) args.getSerializable(BUNDLE_CARINFO);
    }

    private void initialize(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPieGraphDrivingStyle = (PieGraph)findViewById(R.id.piegraphDrivingType);
        mTextViewDrivingType = (TextView) findViewById(R.id.textViewDrivingType);
        mLineGraph = (LineGraph)findViewById(R.id.linegraph);
        mBarGraphCost = (BarGraph)findViewById(R.id.bargraphCost);
        mBarGraphDistance = (BarGraph)findViewById(R.id.bargraphDistance);
        mBarGraphQuantity = (BarGraph)findViewById(R.id.bargraphQuantity);
        mTextViewTotalCost = (TextView) findViewById(R.id.textViewTotalCost);
        mTextViewTotalDistance = (TextView) findViewById(R.id.textViewTotalDistance);
        mTextViewTotalQuantity = (TextView) findViewById(R.id.textViewTotalQuantity);
        mLinearLayoutLegend = (LinearLayout) findViewById(R.id.linearLayoutLegend);
        mTextViewPartialCost = (TextView) findViewById(R.id.textViewPartialCost);
        mTextViewPartialQuantity = (TextView) findViewById(R.id.textViewPartialQuantity);
        mTextViewPartialDistance = (TextView) findViewById(R.id.textViewPartialDistance);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    private void populate(){
        if( mCarInfo != null ) {
            populateDrivingStyle();
            populateTimeline();
            populateBarGraphCost();
            populateBarGraphDistance();
            populateBarGraphQuantity();
        } else {
            finish();
        }
    }

    private void populateBarGraphQuantity(){

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE, agregate = 0, distance = 0;

        for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i ){
            RefuelValue value = mCarInfo.getRefuelValues().get( i );

            agregate += value.getQuantity();
            distance += value.getDistance();

            if( value.getQuantity() < min )
                min = value.getQuantity();

            if( value.getQuantity() > max )
                max = value.getQuantity();
        }

        if( min == Double.MAX_VALUE )
            min = 0;
        if( max == Double.MIN_VALUE )
            max = 0;

        ArrayList<Bar> points = new ArrayList<Bar>();
        Bar d = new Bar();
        d.setColor(Color.parseColor("#99CC00"));
        d.setName( getString( R.string.min ) );
        d.setValue((float) min);

        Bar d2 = new Bar();
        d2.setColor(Color.parseColor("#FFBB33"));
        d2.setName( getString( R.string.average ) );
        d2.setValue((float) (agregate/(float)mCarInfo.getRefuelValues().size()));

        Bar d3 = new Bar();
        d3.setColor(Color.parseColor("#AA66CC"));
        d3.setName( getString( R.string.max ) );
        d3.setValue((float) max);

        points.add(d);
        points.add(d2);
        points.add(d3);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mBarGraphQuantity.setUnit( preferences.getString(Global.PREF_QUANTITY, Global.DEFAULT_QUANTITY));

        mBarGraphQuantity.appendUnit(true);
        mBarGraphQuantity.setBars(points);

        mBarGraphQuantity.setOnBarClickedListener(new BarGraph.OnBarClickedListener(){

            @Override
            public void onClick(int index) {

            }

        });

        double average = agregate / ( distance == 0 ? 1 : distance ) * 100.0;

        mTextViewTotalQuantity.setText(
                getString( R.string.total_ ) + " " + FormatUtils.doubleRounded( agregate, 2 ) + mBarGraphQuantity.getUnit() + "\n" +
                getString( R.string.average_ ) + " " + FormatUtils.doubleRounded( average, 2 ) + mBarGraphQuantity.getUnit() + "/100" + preferences.getString( Global.PREF_DISNTACE, Global.DEFAULT_DISTANCE ));
    }

    private void populateBarGraphDistance(){

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE, agregate = 0;

        for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i ){
            RefuelValue value = mCarInfo.getRefuelValues().get( i );

            agregate += value.getDistance();

            if( value.getDistance() < min )
                min = value.getDistance();

            if( value.getDistance() > max )
                max = value.getDistance();
        }

        if( min == Double.MAX_VALUE )
            min = 0;
        if( max == Double.MIN_VALUE )
            max = 0;

        ArrayList<Bar> points = new ArrayList<Bar>();
        Bar d = new Bar();
        d.setColor(Color.parseColor("#99CC00"));
        d.setName( getString( R.string.min ) );
        d.setValue((float) min);

        Bar d2 = new Bar();
        d2.setColor(Color.parseColor("#FFBB33"));
        d2.setName( getString( R.string.average ) );
        d2.setValue((float) (agregate/(float)mCarInfo.getRefuelValues().size()));

        Bar d3 = new Bar();
        d3.setColor(Color.parseColor("#AA66CC"));
        d3.setName( getString( R.string.max ) );
        d3.setValue((float) max);

        points.add(d);
        points.add(d2);
        points.add(d3);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mBarGraphDistance.setUnit( preferences.getString(Global.PREF_DISNTACE, Global.DEFAULT_DISTANCE));

        mBarGraphDistance.appendUnit(true);
        mBarGraphDistance.setBars(points);

        mBarGraphDistance.setOnBarClickedListener(new BarGraph.OnBarClickedListener(){

            @Override
            public void onClick(int index) {

            }

        });

        mTextViewTotalDistance.setText( FormatUtils.doubleRounded( agregate, 2 ) + mBarGraphDistance.getUnit() );
    }

    private void populateBarGraphCost(){

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE, agregate = 0, distance = 0;

        for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i ){
            RefuelValue value = mCarInfo.getRefuelValues().get( i );

            agregate += value.getCost();
            distance += value.getDistance();

            if( value.getCost() < min )
                min = value.getCost();

            if( value.getCost() > max )
                max = value.getCost();
        }

        if( min == Double.MAX_VALUE )
            min = 0;
        if( max == Double.MIN_VALUE )
            max = 0;

        ArrayList<Bar> points = new ArrayList<Bar>();
        Bar d = new Bar();
        d.setColor(Color.parseColor("#99CC00"));
        d.setName( getString( R.string.min ) );
        d.setValue((float) min);

        Bar d2 = new Bar();
        d2.setColor(Color.parseColor("#FFBB33"));
        d2.setName( getString( R.string.average ) );
        d2.setValue((float) (agregate/(float)mCarInfo.getRefuelValues().size()));

        Bar d3 = new Bar();
        d3.setColor(Color.parseColor("#AA66CC"));
        d3.setName( getString( R.string.max ) );
        d3.setValue((float) max);

        points.add(d);
        points.add(d2);
        points.add(d3);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mBarGraphCost.setUnit( preferences.getString(Global.PREF_CURRENCY, Global.DEFAULT_CURRENCY));

        mBarGraphCost.appendUnit(true);
        mBarGraphCost.setBars(points);

        mBarGraphCost.setOnBarClickedListener(new BarGraph.OnBarClickedListener(){

            @Override
            public void onClick(int index) {

            }

        });

        double average = agregate / ( distance == 0 ? 1 : distance ) * 100.0;

        mTextViewTotalCost.setText(
                getString( R.string.total_ ) + " " + FormatUtils.doubleRounded( agregate, 2 ) + mBarGraphCost.getUnit() + "\n" +
                getString( R.string.average_ ) + " " + FormatUtils.doubleRounded( average, 2 ) + mBarGraphCost.getUnit() + "/100" + preferences.getString( Global.PREF_DISNTACE, Global.DEFAULT_DISTANCE ) );
    }

    private void populateTimeline(){

        Line lineDistance = new Line();
        Line lineQuantity = new Line();
        final Line lineCost = new Line();

        // Setup
        lineCost.setColor(Color.parseColor("#FFBB33"));
        lineDistance.setColor(Color.parseColor("#99CC00"));
        lineQuantity.setColor(Color.parseColor("#AA66CC"));

        // Find max and mins
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Date dateMin = new Date(Long.MAX_VALUE);
        Date dateMax = new Date(0);
        Date tmp;

        for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i ){
            tmp = mCarInfo.getRefuelValues().get( i ).getDate();
            if( tmp != null ){
                if( tmp.after( dateMax ) )
                    dateMax = tmp;
                if( tmp.before( dateMin ) )
                    dateMin = tmp;
            }
        }

        if( dateMin.after( dateMax ) )
            return;

        int mod = Math.abs( DateUtils.daysBetween( dateMin, dateMax ) ) / 3;
        if( mod < 1 )
            mod = 1;
        boolean labeled = false;
        int max = 0;

        // Fill
        calendar.setTime(dateMax);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        dateMax = calendar.getTime();

        calendar.setTime(dateMin);
        int pos = 0;
        while( calendar.getTime().before( dateMax ) ){
            LinePoint lpCost = null;
            LinePoint lpQuantity = null;
            LinePoint lpDistance = null;
            for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i ){
                RefuelValue value = mCarInfo.getRefuelValues().get( i );
                if( value != null && value.getDate() != null ){
                    calendar2.setTime( value.getDate() );
                    if( DateUtils.isSameDay( calendar, calendar2 ) ){
                        if( lpCost == null )
                            lpCost = new LinePoint(pos,(float)(double)value.getCost());
                        else
                            lpCost.setY( lpCost.getY() + (float)(double)value.getCost() );

                        if( lpDistance == null )
                            lpDistance = new LinePoint(pos, (float)(double)value.getDistance() );
                        else
                            lpDistance.setY( lpDistance.getY() + (float)(double)value.getDistance() );

                        if( lpQuantity == null )
                            lpQuantity = new LinePoint(pos, (float)(double)value.getQuantity() );
                        else
                            lpQuantity.setY( lpQuantity.getY() + (float)(double)value.getQuantity() );
                    }

                }
            }

            if( lpCost != null ){

                if( !labeled ) {
                    lpCost.setLabel_string(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                    labeled = true;
                }

                lineCost.addPoint( lpCost );
                lineDistance.addPoint( lpDistance );
                lineQuantity.addPoint( lpQuantity );

                if( lpCost.getY() > max )
                    max = (int) lpCost.getY();

                if( lpDistance.getY() > max )
                    max = (int) lpDistance.getY();

                if( lpQuantity.getY() > max )
                    max = (int) lpQuantity.getY();
            }

            calendar.add(Calendar.DAY_OF_MONTH,1);
            ++pos;
            if( pos % mod == 0 )
                labeled = false;
        }

        mLineGraph.addLine(lineCost);
        mLineGraph.addLine(lineQuantity);
        mLineGraph.addLine(lineDistance);

        mLineGraph.setRangeY(0, max);
        mLineGraph.setLineToFill(-1);
        mLineGraph.setTextSize(14);

        final int maxPosD2 = pos / 2;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLineGraph.setOnPointClickedListener(new LineGraph.OnPointClickedListener(){

            @Override
            public void onClick(int lineIndex, int pointIndex) {
                LinePoint linePoint = mLineGraph.getLine( lineIndex ).getPoint( pointIndex );
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                if( linePoint.getX() < maxPosD2 )
                    params.gravity = Gravity.RIGHT | Gravity.TOP;
                else
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                mLinearLayoutLegend.setGravity( params.gravity );
                mLinearLayoutLegend.setLayoutParams( params );

                mTextViewPartialCost.setText( FormatUtils.doubleRounded( mLineGraph.getLine( 0 ).getPoint( pointIndex ).getY(), 2 ) + preferences.getString( Global.PREF_CURRENCY, Global.DEFAULT_CURRENCY ) );
                mTextViewPartialQuantity.setText( FormatUtils.doubleRounded( mLineGraph.getLine( 1 ).getPoint( pointIndex ).getY(), 2 ) + preferences.getString( Global.PREF_QUANTITY, Global.DEFAULT_QUANTITY ) );
                mTextViewPartialDistance.setText( FormatUtils.doubleRounded( mLineGraph.getLine( 2 ).getPoint( pointIndex ).getY(), 2 ) + preferences.getString( Global.PREF_DISNTACE, Global.DEFAULT_DISTANCE ) );
            }

        });

    }

    private void populateDrivingStyle(){
        float density = getResources().getDisplayMetrics().density;
        float thickness = 45.0f * density;
        mPieGraphDrivingStyle.setThickness((int) thickness);

        int countCity = 0, countMixed = 0, countHighway = 0;
        for( int i = 0; i < mCarInfo.getRefuelValues().size(); ++i )
            switch (mCarInfo.getRefuelValues().get( i ).getDrivingType() ){
                case CITY:
                    ++countCity;
                    break;
                case MIXED:
                    ++countMixed;
                    break;
                case HIGHWAY:
                    ++countHighway;
                    break;
            }

        PieSlice slice = new PieSlice();
        slice.setColor(Color.parseColor("#99CC00"));
        slice.setTitle(getString(R.string.drivingtype_city));
        slice.setValue(countCity);
        mPieGraphDrivingStyle.addSlice(slice);

        slice = new PieSlice();
        slice.setColor(Color.parseColor("#FFBB33"));
        slice.setTitle(getString(R.string.drivingtype_mixed));
        slice.setValue(countMixed);
        mPieGraphDrivingStyle.addSlice(slice);

        slice = new PieSlice();
        slice.setColor(Color.parseColor("#AA66CC"));
        slice.setTitle(getString(R.string.drivingtype_highway));
        slice.setValue(countHighway);
        mPieGraphDrivingStyle.addSlice(slice);

    }

    private void listeners(){
        mPieGraphDrivingStyle.setOnSliceClickedListener(mOnDrivingStyleSliceClicked);
    }

    private PieGraph.OnSliceClickedListener mOnDrivingStyleSliceClicked = new PieGraph.OnSliceClickedListener() {
        @Override
        public void onClick(int index) {
            if( index >= 0 && index < mPieGraphDrivingStyle.getSlices().size() ) {
                PieSlice slice = mPieGraphDrivingStyle.getSlice(index);
                mTextViewDrivingType.setText( slice.getTitle() );
                mTextViewDrivingType.setVisibility(View.VISIBLE);
            } else
                mTextViewDrivingType.setVisibility(View.GONE);
        }
    };
}
