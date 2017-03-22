package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureRecognizeActivity extends AppCompatActivity {

    Gesture g1 = new Gesture();
    Gesture testGesture = new Gesture();
    private Gesture finalGesture;
    Gesture g2 = new Gesture();
    private GestureLibrary gLib;
    boolean gestureExists;
    private static final String TAG = "SaveGestureActivity";
    private boolean mGestureDrawn;                      //tc
    private Gesture mCurrentGesture;
    private GestureStroke finalGestureStroke;
    public float gesture_length;
    private float[] centroid ={};
    static GesturePoint[] points;
    private String mGesturename;
    private ArrayList<GesturePoint>translatedPoints = new ArrayList<>(); // new translated PersonalGesture points
    private ArrayList<GestureStroke>allGestureStrokes = new ArrayList<>(); // all gesture strokes of gesture
    private ArrayList<GesturePoint>allGesturePoints = new ArrayList<>();
    private ArrayList<MyGestureStroke>myGestureStrokes = new ArrayList<>();
    static GesturePoint[] gps;
    int index = 0; // keep track of gesture stroke index of loaded gesture
    static ArrayList<GesturePoint> gp1 = new ArrayList<>();
    GesturePoint p1 = new GesturePoint(220,60, SystemClock.currentThreadTimeMillis());
    GesturePoint p2 = new GesturePoint(300,60, SystemClock.currentThreadTimeMillis());
    GesturePoint p3 = new GesturePoint(380,60, SystemClock.currentThreadTimeMillis());
    GesturePoint p4 = new GesturePoint(580,60, SystemClock.currentThreadTimeMillis());
    GesturePoint p5 = new GesturePoint(900,60, SystemClock.currentThreadTimeMillis());

    Button b, resetButton;
    long start;
    double dist = 0;
    float lengthDiff = 0;
    float timeDiff = 0;
    float end;
    float totalTime;
    float gestureCompute = 0;
    SharedPreferences preferences;
    float templateGestureTime;
    String templateGestureName;
    float[] templatePoints;
    float[] samplePoints;
    double cosineDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_gesture);
        resetButton = (Button)findViewById(R.id.gesture_test_button);
        openDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.test_gesture);
        gestures.addOnGestureListener(mGestureListener);
      //  gestures.addOnGesturePerformedListener(onGesturePerformedListener);
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

        GestureStroke gs1 = new GestureStroke(gp1);

        g1.addStroke(gs1);
        b = (Button)findViewById(R.id.gesture_value_button);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Length is", ":" +dist);
                showToast("difference: "+dist);
                //Toast.makeText(GestureRecognizeActivity.this, ""+dist, Toast.LENGTH_SHORT).show();

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.gesture_test:
                Log.i("Gesture"," Difference: "+dist);
                showToast("Difference: "+dist);
                Log.i(" Test Gesture "," Length: "+testGesture.getLength());
                Log.i("Sample Gesture"," Length: "+finalGesture.getLength());
                showStrokeLength(testGesture);
                Log.i("Sample","Gesture");
                showStrokeLength(finalGesture);
                Log.i("Gesture"," Difference: "+dist);
                showToast("Length difference: "+lengthDiff);
                Log.i("Gesture", "Time Difference: "+timeDiff);
                showToast("Time Difference between Gestures: "+timeDiff +" seconds");
                Log.i("Gesture", "Cosine Distance: "+cosineDistance);
                showToast("Gesture Similarity : "+(100 - (cosineDistance*100))+"%");

                if(cosineDistance<0.3) {
                    showToast("Gesture detected");
                }
                break;

            case R.id.gesture_remove:
                Log.i("Gesture", "Gesture Reset");
                reDrawGestureView();
                break;

            default: return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            gestureExists = true;
            Log.d(TAG, "New PersonalGesture" + SystemClock.elapsedRealtime());
            allGesturePoints.clear(); // remove all existing gesture points
            finalGesture = new Gesture();
            lengthDiff = 0;
            start = System.currentTimeMillis();
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

                end = (float)((System.currentTimeMillis() - start) / 1000.000) ;
                totalTime += (float)end;
                gesture_length = mCurrentGesture.getLength();
                Log.d("Total stroke length ", "is " + mCurrentGesture.getLength());
                Log.d("stroke count is ", "" + mCurrentGesture.getStrokesCount());
                ArrayList<GestureStroke> strokes = mCurrentGesture.getStrokes();
                Log.d("First point"," is"+ strokes.get(0).points[0]);
                mCurrentGesture = new Gesture();
                allGesturePoints.clear();

                for (GestureStroke gs : strokes) {
                    //convert float[] points of stroke to GesturePoint array
                    GesturePoint[] gps = GestureUtility.floatToGP(gs.points);
                    //Spatially sample GesturePoints
                    for(GesturePoint gp:gps){
                        Log.d("x point "," is"+gp.x +" "+ gp.y);
                    }

                    ArrayList<GesturePoint> gp = new ArrayList<>(Arrays.asList(gps));
                    gp = GestureUtility.resample(gp,8);
                    Log.d("spaced point is "," "+gps[0].x);
                    Log.d("spaced point is "," "+gps[1].x);
                    centroid = GestureUtility.computeCentroid(new ArrayList<GesturePoint>(Arrays.asList(gps))); // centroid of gesture

                    // float[] newPoints = GestureUtils.temporalSampling(gs, 5); // samples them to 5 pairs of points
                    //  gps = GestureUtility.translated(gps,centroid, gestureView); //translates gesture points of gesture to centroid of gesture being translated to center of screen
                    Log.d("number of points"," is"+ gp.size());


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
            allGestureStrokes.add(finalGestureStroke);

            gesture_length = 0;
           // Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);
            Arrays.fill(centroid,0); // make centroid -> 0
            //   Log.d("translated point ", " is x " + translatedPoints.get(0).x + " y " + translatedPoints.get(0).y);

            for (GesturePoint g : allGesturePoints) {
                Log.d("Translated point is x ", Float.toString(g.x) + " y: " + Float.toString(g.y));
            }

            Log.i(TAG, "Stroke ended");
            for(GestureStroke gs: allGestureStrokes){
                finalGesture.addStroke(gs);
            }

            //dist = euclidDistance(testGesture);
            dist = calcDiff(finalGesture, testGesture);
            lengthDiff = calcLengthDiff(finalGesture, testGesture);
            timeDiff = Math.abs(totalTime - templateGestureTime);
           // gestureCompute = GestureUtility.gestureCompute(testGesture, finalGesture);


            Log.i(TAG, "Gesture time difference: "+timeDiff);
            samplePoints = finalGestureStroke.points;

            cosineDistance = GestureUtility.angleDiff(finalGesture, testGesture);

            if(cosineDistance > 1){
                cosineDistance = 1;
            }

        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "cancel");
        }
    };


   /* private GestureOverlayView.OnGesturePerformedListener onGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
            gestureOverlayView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            centroid = GestureUtility.computeCentroid(allGesturePoints);

            //translate points wrt to centroid
            allGesturePoints = GestureUtility.translated(allGesturePoints,centroid, gestureOverlayView);

            Log.d("Centroid of points is ", " "+ centroid[0] + " " + centroid[1]);

            for(GesturePoint gp: allGesturePoints) {
                Log.d("Translated point ", " x " + gp.x + " y " + gp.y); // translated gesture point x and y
            }

            Log.d("length of gesture ", "is " + gesture_length);

            //rotate the gesture points
            Rectangle r = GestureUtility.BoundingBox(allGesturePoints, new Rectangle());
            allGesturePoints = GestureUtility.RotateToZero(allGesturePoints,centroid, r);
            Log.d("rotated point is", " "+ allGesturePoints.get(0).x); // translated gesture point x
            Log.d("rotated point is", " "+ allGesturePoints.get(0).y);

            //translate centroid to centre -> no need
            centroid = GestureUtility.translateCentroid(centroid, gestureOverlayView);

            finalGestureStroke = new GestureStroke(allGesturePoints);
            finalGesture = new Gesture();
            finalGesture.addStroke(finalGestureStroke);

            samplePoints = finalGestureStroke.points;

            //  translatedPoints=translatePoints(centroid,allGesturePoints);
            gesture_length = 0;
            Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);

            Arrays.fill(centroid,0); // make centroid -> 0
            //   Log.d("translated point ", " is x " + translatedPoints.get(0).x + " y " + translatedPoints.get(0).y);
        }
    }; */




    public static double euclidDistance(GesturePoint pt1, GesturePoint pt2){
        return Math.sqrt(Math.pow(pt2.x - pt1.x,2) + Math.pow(pt2.y - pt1.y,2));
    }

    /*public static double euclidDistance(Gesture testGesture){
        double diff=0;
        double diff1=0;
        double diff2=0;
        GestureStroke gs = testGesture.getStrokes().get(0);
        points = GestureUtility.floatToGP(gs.points);

        for(int i=0;i<points.length;i++){
            Log.i("Loaded points ","x "+points[i].x + " y "+points[i].y);
        }
      //  GestureStroke gs1 = g1.getStrokes().get(0);
       // GestureStroke gs2 = g2.getStrokes().get(0);

        for(int i=0;i<7;i++){
            diff1 = euclidDistance(points[i], allGesturePoints.get(i));
            //diff2 = euclidDistance(gps[i],x);
            diff += Math.abs(diff1); // diff += Math.abs(diff2 - diff1);
        }
        return diff;
    } */



    private void resetEverything(){
        mGestureDrawn = false;
        mCurrentGesture = null;
        mGesturename = "";
        index = 0;
        allGesturePoints.clear();
        allGestureStrokes.clear();
        lengthDiff = 0;
        timeDiff = 0;
        totalTime = 0;
    }

    private void reDrawGestureView() {
        dist=0;
        Log.i("Action", "RESET GESTURE");
        setContentView(R.layout.test_gesture);
        resetButton = (Button) findViewById(R.id.gesture_test_button);
        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reDrawGestureView();
                    }
                }
        );
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.test_gesture);
        gestures.removeAllOnGestureListeners();
        gestures.addOnGestureListener(mGestureListener);
        resetEverything();
    }

    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    public void openDialog(){
        final EditText ed = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input gesture name to load");
        builder.setView(ed);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(GestureRecognizeActivity.this, GestureListActivity.class);
                startActivity(intent);
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                boolean gesture_found = false;
                String text;
                text = ed.getText().toString();
                templateGestureName = text;

                if(text.matches("")){
                    Log.e("Gesture:", " Invalid name");
                    showToast("Please enter gesture name");
                    openDialog();
                }

                gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gest.txt");
                gLib.load();

                try {
                    Set<String> gestureSet = gLib.getGestureEntries();
                    for (String gName : gestureSet) {
                        ArrayList<Gesture> list = gLib.getGestures(text);
                        testGesture=list.get(0);
                        //   testGesture = gLib.getGestures(text).get(0);
                    }
                    Log.i("Gesture ", "Stroke Count = " + testGesture.getStrokesCount());
                    templatePoints = testGesture.getStrokes().get(0).points;

                    for(int j=0; j<templatePoints.length; j+=2){
                        Log.i("Template point x"," "+templatePoints[j]);
                        Log.i("Template point y"," "+templatePoints[j+1]);
                    }

                }catch (NullPointerException npe){

                    Log.e("Gesture:", " Doesn't exist");
                    showToast("Gesture with name does not exist");
                    openDialog();
                }
                templateGestureTime = preferences.getFloat(templateGestureName,0);
                Log.i(TAG, "Template Gesture time: "+ templateGestureTime);



            }
        });
        builder.show();
        Log.i("Stroke count: ",""+testGesture.getStrokesCount());
       // testGesture.

    }

    private double calcDiff(Gesture g1, Gesture g2){
        double difference = 0;
        ArrayList<GestureStroke> g1_strokes = g1.getStrokes();
        ArrayList<GestureStroke> g2_strokes = g2.getStrokes();


        if(g2_strokes.size() != g1_strokes.size()){
            difference = Double.MAX_VALUE;
            return difference;
        }

        for(int i=0; i<g1_strokes.size(); i++){
            GesturePoint[] gp1 = GestureUtility.floatToGP(g1_strokes.get(i).points);
            GesturePoint[] gp2 = GestureUtility.floatToGP(g2_strokes.get(i).points);

            for(int j=0; i<gp1.length; i++){
                difference += euclidDistance(gp1[j], gp2[j]);
            }
        }
        return difference;
    }

    private float calcLengthDiff(Gesture g1, Gesture g2){
        float diff = 0;
        ArrayList<GestureStroke> g1_strokes = g1.getStrokes();
        ArrayList<GestureStroke> g2_strokes = g2.getStrokes();
        double gp1_initial_diff = 0;
        double gp2_initial_diff = 0;
        //Log.i("Stroke length 0","g1 "+ g1_strokes.get(0).length);
        for(int i=0; i<g1_strokes.size(); i++){
            Log.i("Stroke length 0","g2 "+ (g2_strokes.get(i).length - gp2_initial_diff));
            Log.i("Stroke length 1","g2 "+ (g1_strokes.get(i).length - gp1_initial_diff));

            diff += Math.abs((g2_strokes.get(i).length - gp2_initial_diff) - (g1_strokes.get(i).length - gp1_initial_diff));
            gp1_initial_diff = g1_strokes.get(i).length;
            gp2_initial_diff = g2_strokes.get(i).length;
        }
        return diff;
    }

    void showGestureSpecs() {
        GestureStroke gs = testGesture.getStrokes().get(0);
        GesturePoint[] gp = GestureUtility.floatToGP(gs.points);

        for (int i = 0; i < gp.length; i++) {
            Log.i("Points", " X " + gp[0].x + " Y " + gp[0].y);
        }
    }

    void showStrokeLength(Gesture g){
        ArrayList<GestureStroke>gs = g.getStrokes();
        for(GestureStroke gss: gs){
            Log.i("length stroke",""+gss.length);

        }
    }

    public double velocityDifference(Gesture g1, Gesture g2){
        double vel_diff = 0;
        ArrayList<MyGestureStroke> mgs1 = GestureUtility.convertToMyGestureStroke(g1.getStrokes());
        ArrayList<MyGestureStroke> mgs2 = GestureUtility.convertToMyGestureStroke(g2.getStrokes());

        for(int i=0; i<mgs2.size(); i++){
            vel_diff += Math.abs(mgs2.get(i).getStroke_velocity() - mgs1.get(i).getStroke_velocity());
        }
        return vel_diff;
    }

}
