package com.jccarrillo.alcgo.fueltracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.jccarrillo.alcgo.fueltracker.R;
import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;

import java.util.List;

/**
 * Created by Juan Carlos on 17/10/2016.
 */

public class CarsAdapter extends BaseAdapter {

    private List<CarInfo> mList;
    private LayoutInflater mLayoutInflater;

    private static class ViewHolder {
        public TextView tv1;
        public TextView tv2;
    }

    public CarsAdapter(Context context, List<CarInfo> list ){
        mLayoutInflater = LayoutInflater.from( context );
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if( convertView == null ){
            convertView = mLayoutInflater.inflate(R.layout.item_car, parent, false );
            viewHolder = new ViewHolder();
            viewHolder.tv1 = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.tv2 = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag( viewHolder );
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        CarInfo carInfo = (CarInfo) getItem( position );
        viewHolder.tv2.setText( carInfo.getCompany() );
        viewHolder.tv1.setText( carInfo.getModel() );

        return convertView;
    }
}
