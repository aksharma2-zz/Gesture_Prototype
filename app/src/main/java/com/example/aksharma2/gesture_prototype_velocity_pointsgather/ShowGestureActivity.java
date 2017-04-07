package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by kalpana on 28/3/2017.
 */

public class ShowGestureActivity extends AppCompatActivity {
    String gesture_name;
    String gesture_img;
    GestureLibrary gLib;
    Gesture gesture;
    SharedPreferences preferences;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle b = getIntent().getExtras();
        if(b!=null) {
            gesture_name = b.getString("gesture_name");
        }
        gesture = loadGesture(gesture_name);
        Log.i("name", "is "+gesture_name);
        setContentView(new myView(this));
    }

    public Gesture loadGesture(String gesture_name){
        Gesture g;
        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gestr.txt");
        gLib.load();
        Set<String> gestureSet = gLib.getGestureEntries();
        ArrayList<Gesture> list = gLib.getGestures(gesture_name);
        g=list.get(0);
        gesture_img = preferences.getString(gesture_name+"img","null");
        byte [] encodeByte= Base64.decode(gesture_img,Base64.DEFAULT);
        bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        Log.i("name", "is "+gesture_name);
        return g;
    }

    private class myView extends View {

        public myView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            Paint myPaint = new Paint();
            myPaint.setStrokeWidth(50);
            Bitmap myBitmap = bitmap;
            canvas.drawBitmap(myBitmap, null, new RectF(400, 400, 700, 900), null);
        }
    }

}
