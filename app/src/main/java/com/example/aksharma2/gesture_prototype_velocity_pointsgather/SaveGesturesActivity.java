package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static android.R.attr.bitmap;


public class SaveGesturesActivity extends AppCompatActivity {
    private GestureLibrary gLib;
    private static final String TAG = "SaveGestureActivity";
    private boolean gestureExists;
    private Gesture mCurrentGesture;
    private GestureStroke finalGestureStroke;
    private MyGestureStroke myGestureStroke;
    private Gesture finalGesture;
    public float gesture_length;
    private float[] centroid ={};
    private String mGesturename;
    private Button resetButton;
    private GestureLibrary gesture_lib;
    private ArrayList<GesturePoint>allGesturePoints = new ArrayList<>(); // to calculate centroid of all gesture points
    private ArrayList<GestureStroke>allGestureStrokes = new ArrayList<>(); // all gesture strokes of gesture
    private ArrayList<MyGestureStroke>myGestureStrokes = new ArrayList<>();
    long start;
    float end;
    float totalTime;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    GesturePoint lastPoint;
    float gesture_speed;
    String gesture_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("New ","app ");
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        setContentView(R.layout.save_gesture);
        resetButton = (Button) findViewById(R.id.gesture_reset_button);
        Log.d(TAG, "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());
        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gestr.txt");
        gLib.load();
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.save_gesture);
        gestures.addOnGestureListener(mGestureListener);
        gestures.addOnGesturePerformedListener(onGesturePerformedListener);
        resetEverything();
        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reDrawGestureView();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.DELETE:
                reDrawGestureView();
                break;
            case R.id.Save:
                for(GestureStroke gs: allGestureStrokes){
                    finalGesture.addStroke(gs);
                }
               openDialog("");
               break;
        }
        return super.onOptionsItemSelected(item);
    }

    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            gestureExists = true;
            Log.d(TAG, "New Gesture Stroke");
            allGesturePoints=new ArrayList<>(); // remove all existing gesture points
            finalGesture = new Gesture();
            start = System.currentTimeMillis();
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(final GestureOverlayView gestureView, MotionEvent motion) {
            try {

                if(!GestureUtility.strokeLengthThreshold(gestureView, gestureView.getGesture(), 120)){
                    showToast("Gesture stroke is too small. Please try again");
                    reDrawGestureView();
                    return;
                }
                end = (float)((System.currentTimeMillis() - start) / 1000.000) ;
                totalTime += (float)end;
                gesture_length = mCurrentGesture.getLength();
                gesture_speed = gesture_length/totalTime;
                Log.d("Total stroke length ", "is " + mCurrentGesture.getLength());
                Log.d("stroke count is ", "" + mCurrentGesture.getStrokesCount());
                ArrayList<GestureStroke> strokes = mCurrentGesture.getStrokes();
                GestureStroke gs_latest = strokes.get(strokes.size()-1);
                float[] points = gs_latest.points;
                float lastX = points[points.length-2];
                float lastY = points[points.length-1];
                lastPoint = new GesturePoint(lastX, lastY, SystemClock.currentThreadTimeMillis());

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
                    if(gp.size() < 8){
                        gp.add(lastPoint);
                    }
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

                    gs = new GestureStroke(gp); // same gesture but sampled to 8 pairs of points
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
            myGestureStroke = new MyGestureStroke(allGesturePoints);
            myGestureStroke.setStroke_velocity(end);

            allGestureStrokes.add(finalGestureStroke);
            myGestureStrokes.add(myGestureStroke);

            Log.i(TAG,"Gesture stroke ended");
            Log.i(TAG,"Gesture Stroke time: "+myGestureStroke.getStroke_velocity());

            gesture_length = 0;
            Arrays.fill(centroid,0); // make centroid -> 0
            start = 0;
            end = 0;
            Log.i(TAG,"Total time" +totalTime);
            Log.i(TAG,"Gesture Speed "+gesture_speed);
            Bitmap bitmap = gestureView.getGesture().toBitmap(30,30,3, Color.RED);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            gesture_image= Base64.encodeToString(b, Base64.DEFAULT);

        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "cancel");
        }
    };

    private GestureOverlayView.OnGesturePerformedListener onGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
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

            //translate centroid to centre -> no need
            centroid = GestureUtility.translateCentroid(centroid, gestureOverlayView);

            finalGestureStroke = new GestureStroke(allGesturePoints);
            finalGesture = new Gesture();
            finalGesture.addStroke(finalGestureStroke);

            //  translatedPoints=translatePoints(centroid,allGesturePoints);
            gesture_length = 0;
            Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);

            Arrays.fill(centroid,0); // make centroid -> 0
            //   Log.d("translated point ", " is x " + translatedPoints.get(0).x + " y " + translatedPoints.get(0).y);
        }
    };



                      // ******************** Helper methods underneath ********************



    private void openDialog(String name) {

        if(!gestureExists){
            showToast("Please draw gesture first");
            reDrawGestureView();
            return;
        }

        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle("Enter Personal Gesture Name");
        //namePopup.setMessage(R.string.enter_name);
        final EditText nameField = new EditText(this);
        nameField.setText(name);
        namePopup.setView(nameField);
        namePopup.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //db.updateExistingMeasurement(measurement);
                if (!nameField.getText().toString().matches("")) {
                    mGesturename = nameField.getText().toString();
                  //  editor.putFloat(mGesturename, totalTime);
                    saveGesture();
                } else {
                    openDialog("");
                    showToast("Please name your gesture!");
                }
                //return;
            }
        });
        namePopup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGesturename = "";
                return;
            }
        });
        namePopup.show();
    }

    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void saveGesture() {

        if(checkExistingName(mGesturename)){
            showToast("Gesture with name already exists");
            openDialog(mGesturename);
            return;
        }

        editor.putFloat(mGesturename, totalTime);
        editor.putFloat(mGesturename+"speed",gesture_speed);
        editor.putString(mGesturename+"img",gesture_image);
        editor.commit();
        gLib.addGesture(mGesturename, finalGesture);

        if (!gLib.save()) {
            Log.e(TAG, "gesture not saved!");
        }else {
            showToast("saved" + getExternalFilesDir(null) + "/gestr.txt");
            Log.i(TAG,"gesture saved!");
        }
        reDrawGestureView();
    }

    private void resetEverything(){
        gestureExists = false;
        mCurrentGesture = null;
        mGesturename = "";
        allGesturePoints.clear();
        allGestureStrokes.clear();
        myGestureStrokes.clear();
        totalTime = 0;
    }

    private void reDrawGestureView() {
        Log.i("RESET", " GESTURE");
        setContentView(R.layout.save_gesture);
       // resetButton = (Button) findViewById(R.id.gesture_reset_button);
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


    private boolean checkExistingName(String name){

        gesture_lib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gestr.txt");
        gesture_lib.load();
        Set<String> gestureSet = gesture_lib.getGestureEntries();
        for(String gestureName : gestureSet){
            if(gestureName.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
