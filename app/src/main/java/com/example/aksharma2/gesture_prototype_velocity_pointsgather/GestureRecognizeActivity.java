package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureRecognizeActivity extends Activity {

    Gesture g1 = new Gesture();
    Gesture g2 = new Gesture();
    private GestureLibrary gLib;
    private static final String TAG = "SaveGestureActivity";
    private boolean mGestureDrawn;                      //tc
    private Gesture mCurrentGesture;
    private GestureStroke finalGestureStroke;
    private Gesture finalGesture;
    public float gesture_length;
    private float[] centroid ={};
    private String mGesturename;
    private GestureLibrary gesture_lib;
    private ArrayList<GesturePoint>allGesturePoints = new ArrayList<>(); // to calculate centroid of all gesture points
    private ArrayList<GesturePoint>translatedPoints = new ArrayList<>(); // new translated PersonalGesture points
    static GesturePoint[] gps;
     double dist=0;

    static ArrayList<GesturePoint> gp1 = new ArrayList<>();
    static ArrayList<GesturePoint> gp2 = new ArrayList<>();


   static GesturePoint x = new GesturePoint(540,690,SystemClock.currentThreadTimeMillis());

    GesturePoint p1 = new GesturePoint(50,50, SystemClock.currentThreadTimeMillis());
    GesturePoint p2 = new GesturePoint(70,70, SystemClock.currentThreadTimeMillis());
    GesturePoint p3 = new GesturePoint(90,90, SystemClock.currentThreadTimeMillis());
    GesturePoint p4 = new GesturePoint(110,110, SystemClock.currentThreadTimeMillis());
    GesturePoint p5 = new GesturePoint(130,130, SystemClock.currentThreadTimeMillis());


    GesturePoint q1 = new GesturePoint(60,60, SystemClock.currentThreadTimeMillis());
    GesturePoint q2 = new GesturePoint(80,80, SystemClock.currentThreadTimeMillis());
    GesturePoint q3 = new GesturePoint(100,100, SystemClock.currentThreadTimeMillis());
    GesturePoint q4 = new GesturePoint(120,120, SystemClock.currentThreadTimeMillis());

    Button b, resetButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_gesture);
        resetButton = (Button)findViewById(R.id.gesture_test_button);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.test_gesture);
        gestures.addOnGestureListener(mGestureListener);
      //  gestures.addOnGesturePerformedListener(onGesturePerformedListener);
        resetEverything();
        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reDrawGestureView();
                    }
                }
        );



        gp1.add(p1); gp1.add(p2); gp1.add(p3); gp1.add(p4); gp1.add(p5);
        gp2.add(q1); gp2.add(q2); gp2.add(q3); gp2.add(q4);

        GestureStroke gs1 = new GestureStroke(gp1);
        GestureStroke gs2 = new GestureStroke(gp2);

        g1.addStroke(gs1);
        g1.addStroke(gs2);

        b = (Button)findViewById(R.id.gesture_value_button);


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


    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            mGestureDrawn = true;
            Log.d(TAG, "New PersonalGesture" + SystemClock.elapsedRealtime());
            allGesturePoints.clear(); // remove all existing gesture points
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(final GestureOverlayView gestureView, MotionEvent motion) {
            Log.d(TAG, "PersonalGesture stroke ended");
            try {

                if(!GestureUtility.strokeLengthThreshold(gestureView, gestureView.getGesture(), 150)){
                    showToast("Gesture stroke is too small. Please try again");
                    reDrawGestureView();
                    return;
                }

                gesture_length = mCurrentGesture.getLength();
                Log.d("Total stroke length ", "is " + mCurrentGesture.getLength());
                Log.d("stroke count is ", "" + mCurrentGesture.getStrokesCount());
                ArrayList<GestureStroke> strokes = mCurrentGesture.getStrokes();
                Log.d("First point"," is"+ strokes.get(0).points[0]);
                mCurrentGesture = new Gesture();
                allGesturePoints.clear();

                for (GestureStroke gs : strokes) {
                    //convert float[] points of stroke to GesturePoint array
                     gps = GestureUtility.floatToGP(gs.points);
                    //Spatially sample GesturePoints
                    for(GesturePoint gp:gps){
                        Log.d("x point "," is"+gp.x +" "+ gp.y);
                    }

                    gps = GestureUtility.spatialSample(gps,5);
                    Log.d("spaced point is "," "+gps[0].x);
                    Log.d("spaced point is "," "+gps[1].x);
                    centroid = GestureUtility.computeCentroid(new ArrayList<GesturePoint>(Arrays.asList(gps))); // centroid of gesture

                    // float[] newPoints = GestureUtils.temporalSampling(gs, 5); // samples them to 5 pairs of points
                    //  gps = GestureUtility.translated(gps,centroid, gestureView); //translates gesture points of gesture to centroid of gesture being translated to center of screen
                    Log.d("number of points"," is"+ gps.length);
                    ArrayList<GesturePoint> gp = new ArrayList<>(Arrays.asList(gps));

                    //add gesture points to allGesturePoints which will be used in OnGesturePerformed
                    for(GesturePoint point: gp) {
                        allGesturePoints.add(point);
                    }


                    gs = new GestureStroke(gp); // same gesture but sampled to 5 pairs of points
                    for (GesturePoint g : gp) {
                        Log.d("point is x ", Float.toString(g.x) + " y: " + Float.toString(g.y));
                    }
                    mCurrentGesture.addStroke(gs);
                }
                // Centroid of entire gesture
                // centroid = translateCentroid(centroid, gestureView);
                // Log.d("centroid "," is "+centroid[0]+" "+centroid[1]);
                Log.d("PersonalGesture length ","is "+ mCurrentGesture.getLength());

            }catch(Exception e){
                Log.d("Exception occured ", e.getMessage());
                reDrawGestureView();
            }
            // gestureView.draw();



            centroid = GestureUtility.computeCentroid(allGesturePoints);

            //translate points wrt to centroid
            allGesturePoints = GestureUtility.translated(allGesturePoints,centroid, gestureView);

            Log.d("Centroid of points is ", " "+ centroid[0] + " " + centroid[1]);
            Log.d("performed point is", " "+ allGesturePoints.get(0).x); // translated gesture point x
            Log.d("performed point is", " "+ allGesturePoints.get(0).y); // translated gesture point x
            Log.d("length of gesture ", "is " + gesture_length);

            //rotate the gesture points
          /*  Rectangle r = GestureUtility.BoundingBox(allGesturePoints, new Rectangle());
            allGesturePoints = GestureUtility.RotateToZero(allGesturePoints,centroid, r);
            Log.d("rotated point is", " "+ allGesturePoints.get(0).x); // translated gesture point x
            Log.d("rotated point is", " "+ allGesturePoints.get(0).y); */

            //translate centroid
            centroid = GestureUtility.translateCentroid(centroid, gestureView);

            finalGestureStroke = new GestureStroke(allGesturePoints);
            finalGesture = new Gesture();
            finalGesture.addStroke(finalGestureStroke);

            //  translatedPoints=translatePoints(centroid,allGesturePoints);
            gesture_length = 0;
            Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);
            Arrays.fill(centroid,0); // make centroid -> 0
            //   Log.d("translated point ", " is x " + translatedPoints.get(0).x + " y " + translatedPoints.get(0).y);


            dist = euclidDistance();

        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "cancel");
        }
    };





    public static double euclidDistance(GesturePoint pt1, GesturePoint pt2){
        return Math.sqrt(Math.pow(pt2.x - pt1.x,2) + Math.pow(pt2.y - pt1.y,2));
    }

    public static double euclidDistance(){
        double diff=0;
        double diff1=0;
        double diff2=0;
      //  GestureStroke gs1 = g1.getStrokes().get(0);
       // GestureStroke gs2 = g2.getStrokes().get(0);

        for(int i=0;i<5;i++){
            diff1 = euclidDistance(gp1.get(i),x);
            diff2 = euclidDistance(gps[i],x);
            diff += Math.abs(diff2 - diff1);
        }
        return diff;
    }



    private void resetEverything(){
        mGestureDrawn = false;
        mCurrentGesture = null;
        mGesturename = "";
    }

    private void reDrawGestureView() {
        Log.i("RESET", " GESTURE");
        setContentView(R.layout.save_gesture);
        resetButton = (Button) findViewById(R.id.gesture_reset_button);
        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reDrawGestureView();
                    }
                }
        );
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.save_gesture);
        gestures.removeAllOnGestureListeners();
        gestures.addOnGestureListener(mGestureListener);
        resetEverything();
    }

    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
