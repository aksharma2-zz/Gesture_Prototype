package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureAdapter extends ArrayAdapter<GesturePlaceHolder> {

    private ArrayList<GesturePlaceHolder> gestureList;
    private Context context;


    public GestureAdapter(ArrayList<GesturePlaceHolder> gestureList, Context ctx){
        super(ctx, R.layout.gestures_list);
        this.gestureList = gestureList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        GestureView gestureView = new GestureView();
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            layoutInflater.inflate(R.layout.gesture_list_item, null);

            gestureView.gesture_name = (TextView) convertView.findViewById(R.id.gesture_name);
            gestureView.gesture_id = (TextView) convertView.findViewById(R.id.gesture_id);
            gestureView.gesture_image = (ImageView) convertView.findViewById(R.id.gesture_image);

            ImageView dropDownMenu = (ImageView) convertView.findViewById(R.id.menu_item_options);
            convertView.setTag(gestureView);
        }

        else {
            gestureView = (GestureView) convertView.getTag();
        }

        GesturePlaceHolder gesturePlaceHolder = gestureList.get(position);
        gestureView.gesture_name.setText(gesturePlaceHolder.getGestureName());
        gestureView.gesture_id.setText(Long.toString(gesturePlaceHolder.getGesture().getID()));
        gestureView.gesture_image.setImageBitmap(gesturePlaceHolder.getGesture().toBitmap(30,30,5, Color.RED));

        return convertView;
        }
    }


