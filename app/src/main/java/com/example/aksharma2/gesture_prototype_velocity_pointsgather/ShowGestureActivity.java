package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by kalpana on 28/3/2017.
 */

public class ShowGestureActivity extends AppCompatActivity {
    String gesture_name;
    GestureLibrary gLib;
    Gesture gesture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Bitmap myBitmap = gesture.toBitmap(1000,800,5, Color.RED);
            canvas.drawBitmap(myBitmap, 0, 0, myPaint);
        }
    }

}
