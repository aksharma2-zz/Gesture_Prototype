package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureAdapter extends ArrayAdapter<GesturePlaceHolder> {

    private ArrayList<GesturePlaceHolder> gestureList;
    private Context context;
    private GestureLibrary gestureLibrary;
    View.OnClickListener onClickListener;
    SharedPreferences preferences;

    public GestureAdapter(ArrayList<GesturePlaceHolder> gestureList, Context ctx){
        super(ctx, R.layout.gestures_list, gestureList);
        this.gestureList = gestureList;
        this.context = ctx;
      //  gestureLibrary = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gestureLibrary = GestureLibraries.fromFile(context.getExternalFilesDir(null) + "/" + "gestr.txt");
        gestureLibrary.load();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
            gestureView.showButton = (Button)convertView.findViewById(R.id.show_button);
            convertView.setTag(gestureView);
            gestureView.delButton.setTag(position);
            gestureView.gesture_name.setTag(position);

        }

        else {
            gestureView = (GestureView) convertView.getTag();
        }

        GesturePlaceHolder gesturePlaceHolder = gestureList.get(position);
        gestureView.gesture_name.setText(gesturePlaceHolder.getGestureName());
        gestureView.gestureNameRef.setText(gesturePlaceHolder.getGestureName());
        gestureView.gesture_id.setText(Long.toString(gesturePlaceHolder.getGesture().getID()));

        String gesture_img = preferences.getString(gesturePlaceHolder.getGestureName()+"img","null");
        byte [] encodeByte= Base64.decode(gesture_img,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        gestureView.gesture_image.setImageBitmap(bitmap);
                //gesturePlaceHolder.getGesture().toBitmap(30,30,3, Color.RED));


        gestureView.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parent = (LinearLayout)v.getParent().getParent();
                TextView tv = (TextView)parent.findViewById(R.id.gesture_name_ref);
                String s = tv.getText().toString();
                Log.i("Click", " "+s);
                int index = (int)v.getTag();
                gestureList.remove(index);
                gestureLibrary.removeEntry(s);
                gestureLibrary.save();
                notifyDataSetChanged();
            }
        });

        gestureView.showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parent = (LinearLayout)v.getParent().getParent();
                TextView tv = (TextView)parent.findViewById(R.id.gesture_name_ref);
                String gesture_name = tv.getText().toString();
                Intent i = new Intent(context, ShowGestureActivity.class);
                Bundle b = new Bundle();
                b.putString("gesture_name", gesture_name);
                i.putExtras(b);
                context.startActivity(i);
            }
        });

        return convertView;
        }

        public void setOnButtonClickListener(View.OnClickListener onClickListener){
            this.onClickListener = onClickListener;
        }

    }


