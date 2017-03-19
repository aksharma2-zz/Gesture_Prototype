package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureAdapter extends ArrayAdapter<GesturePlaceHolder> {

    private ArrayList<GesturePlaceHolder> gestureList;
    private Context context;
    private GestureLibrary gestureLibrary;
    View.OnClickListener onClickListener;

    public GestureAdapter(ArrayList<GesturePlaceHolder> gestureList, Context ctx){
        super(ctx, R.layout.gestures_list, gestureList);
        this.gestureList = gestureList;
        this.context = ctx;
      //  gestureLibrary = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gestureLibrary = GestureLibraries.fromFile(context.getExternalFilesDir(null) + "/" + "gestures.txt");
        gestureLibrary.load();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        GestureView gestureView = new GestureView(context);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView =  layoutInflater.inflate(R.layout.gesture_list_item, null);

            gestureView.gesture_name = (TextView) convertView.findViewById(R.id.gesture_name);
            gestureView.gesture_id = (TextView) convertView.findViewById(R.id.gesture_id);
            gestureView.gesture_image = (ImageView) convertView.findViewById(R.id.gesture_image);
            gestureView.gestureNameRef = (TextView) convertView.findViewById(R.id.gesture_name_ref);
            gestureView.delButton = (Button)convertView.findViewById(R.id.delete_button);
            convertView.setTag(gestureView);
            gestureView.delButton.setTag(position);
            gestureView.gesture_name.setTag(position);

        }

        else {
            gestureView = (GestureView) convertView.getTag();
        }

        GesturePlaceHolder gesturePlaceHolder = gestureList.get(position);
        gestureView.gesture_name.setText(gesturePlaceHolder.getGestureName());
        gestureView.gesture_id.setText(Long.toString(gesturePlaceHolder.getGesture().getID()));
        gestureView.gesture_image.setImageBitmap(gesturePlaceHolder.getGesture().toBitmap(30,30,3, Color.RED));


        gestureView.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int)v.getTag();
                gestureList.remove(index);
                notifyDataSetChanged();
            }
        });


        return convertView;
        }

        public void setOnButtonClickListener(View.OnClickListener onClickListener){
            this.onClickListener = onClickListener;
        }

    }


