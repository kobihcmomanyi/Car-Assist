package com.carassist.carassist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.carassist.carassist.R;
import com.carassist.carassist.data.Cars;

import java.util.ArrayList;

/**
 * Created by user on 6/9/2016.
 */
public class CarListArrayAdapter extends ArrayAdapter<Cars> {
    Context context;
    ArrayList<Cars> items;

    //constructor
    public CarListArrayAdapter(Context context,ArrayList<Cars> items){
        super(context, R.layout.car_list_layout, items);
        this.context = context;
        this.items = items;
    }

    static class ViewHolder{

        public CheckBox carCheckBox;
        public TextView carName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        //reuse views
        if(rowView == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.car_list_layout,parent,false);

            //configure view holder
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.carCheckBox = (CheckBox) rowView.findViewById(R.id.car_list_checkbox);
            viewHolder.carName = (TextView)rowView.findViewById(R.id.car_list_textview);
            rowView.setTag(viewHolder);

        }

        //fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.carName.setText(items.get(position).getCarModel());

        return rowView;
    }
}

