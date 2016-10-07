package com.jccarrillo.alcgo.fueltracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jccarrillo.alcgo.fueltracker.R;
import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.domain.RefuelValue;
import com.jccarrillo.alcgo.fueltracker.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by Juan Carlos on 04/10/2016.
 */

public class RefuelValueAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<RefuelValue> mValues;
    private String mCurrency = " €";
    private String mQuantity = " l.";
    private boolean mModeQuantity = true;


    private static class RefuelValueAdapterItem {
        public TextView textView1;
        public TextView textView2;
    }

    public RefuelValueAdapter(Context context, List<RefuelValue> valueList ){
        mInflater = LayoutInflater.from( context );
        mValues = valueList;
    }

    public boolean getModeQuantity(){
        return mModeQuantity;
    }

    public void setModeQuantity( boolean modeQuantity ){
        mModeQuantity = modeQuantity;
    }

    public void setValueList( List<RefuelValue> list ){
        mValues = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mValues == null ? 0 : mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues == null ? null : mValues.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RefuelValueAdapterItem viewHolder = null;

        if( convertView == null ){
            convertView = mInflater.inflate(R.layout.item_refuelvalue, parent, false );

            viewHolder = new RefuelValueAdapterItem();
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.textView01);
            viewHolder.textView2 = (TextView) convertView.findViewById(R.id.textView02);

            convertView.setTag( viewHolder );
        } else
            viewHolder = (RefuelValueAdapterItem) convertView.getTag();

        RefuelValue item = (RefuelValue) getItem( position );

        if( item != null ) {
            viewHolder.textView1.setText( item.getDate() == null ? "" : DateUtils.toString( item.getDate() ) );
            if( mModeQuantity )
                viewHolder.textView2.setText( doubleRounded( item.getQuantity(), 2 ) + mQuantity );
            else
                viewHolder.textView2.setText( doubleRounded( item.getCost(), 2 ) + mCurrency );
        }
        return convertView;
    }

    private String doubleRounded( double value, int decimals ){
        if( decimals < 0 )
            decimals = 0;

        value = value * Math.pow( 10, decimals );
        value = Math.round( value );
        value = value / Math.pow( 10, decimals );

        return String.valueOf( value );
    }
}
