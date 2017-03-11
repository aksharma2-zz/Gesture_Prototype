package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureRecognizeActivity extends Activity {

    Gesture g1 = new Gesture();
    Gesture g2 = new Gesture();

   static ArrayList<GesturePoint> gp1 = new ArrayList<>();
    static ArrayList<GesturePoint> gp2 = new ArrayList<>();


   static GesturePoint x = new GesturePoint(540,690,SystemClock.currentThreadTimeMillis());

    GesturePoint p1 = new GesturePoint(50,50, SystemClock.currentThreadTimeMillis());
    GesturePoint p2 = new GesturePoint(70,70, SystemClock.currentThreadTimeMillis());
    GesturePoint p3 = new GesturePoint(90,90, SystemClock.currentThreadTimeMillis());
    GesturePoint p4 = new GesturePoint(110,110, SystemClock.currentThreadTimeMillis());


    GesturePoint q1 = new GesturePoint(60,60, SystemClock.currentThreadTimeMillis());
    GesturePoint q2 = new GesturePoint(80,80, SystemClock.currentThreadTimeMillis());
    GesturePoint q3 = new GesturePoint(100,100, SystemClock.currentThreadTimeMillis());
    GesturePoint q4 = new GesturePoint(120,120, SystemClock.currentThreadTimeMillis());

    Button b;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_gesture);

        gp1.add(p1); gp1.add(p2); gp1.add(p3); gp1.add(p4);
        gp2.add(q1); gp2.add(q2); gp2.add(q3); gp2.add(q4);

        GestureStroke gs1 = new GestureStroke(gp1);
        GestureStroke gs2 = new GestureStroke(gp2);

        g1.addStroke(gs1);
        g1.addStroke(gs2);

        b = (Button)findViewById(R.id.gesture_value_button);
         final double dist = euclidDistance();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Length is", ":" +dist);
                //Toast.makeText(GestureRecognizeActivity.this, ""+dist, Toast.LENGTH_SHORT).show();
                if(dist<70)
                    Toast.makeText(GestureRecognizeActivity.this, "Gesture recognized", Toast.LENGTH_SHORT).show();

            }
        });



    }

    public static double euclidDistance(GesturePoint pt1, GesturePoint pt2){
        return Math.sqrt(Math.pow(pt2.x - pt1.x,2) + Math.pow(pt2.y - pt1.y,2));
    }

    public static double euclidDistance(){
        double diff=0;
        double diff1=0;
        double diff2=0;
      //  GestureStroke gs1 = g1.getStrokes().get(0);
       // GestureStroke gs2 = g2.getStrokes().get(0);

        for(int i=0;i<4;i++){
            diff1 = euclidDistance(gp1.get(i),x);
            diff2 = euclidDistance(gp2.get(i),x);
            diff += Math.abs(diff2 - diff1);
        }
        return diff;
    }
}
