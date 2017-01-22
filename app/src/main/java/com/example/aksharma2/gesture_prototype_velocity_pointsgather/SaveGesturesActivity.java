package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.GestureUtils;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

//import pack.GestureApp.R;

public class SaveGesturesActivity extends AppCompatActivity implements View.OnClickListener {
    private GestureLibrary gLib;
    private static final String TAG = "SaveGestureActivity";
    private boolean mGestureDrawn;                      //tc
    private Gesture mCurrentGesture;
    private float[] centroid ={};
    private String mGesturename;
    private Button resetButton;
    private ArrayList<GesturePoint>allGesturePoints = new ArrayList<>(); // to calculate centroid of all gesture points
    //tc

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_gesture);
        resetButton = (Button) findViewById(R.id.reset_button);
        Log.d(TAG, "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());


        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
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
                reDrawGestureView();
                break;

            //TODO : Save gesture as image, dont delete this code
                /*
                String pattern = "mm ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String time = formatter.format(new Date());
                String path = ("/d-codepages" + time + ".png");
                File file = new File(Environment.getExternalStorageDirectory()
                        + path);
                try {
                    //DrawBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    //new FileOutputStream(file));
                    Toast.makeText(this, "File Saved ::" + path, Toast.LENGTH_SHORT)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(this, "ERROR" + e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }   */
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * our gesture listener
     */
    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mGestureDrawn = true;
            Log.d(TAG, "New Gesture");
            allGesturePoints.clear(); // remove all existing gesture points
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(final GestureOverlayView gestureView, MotionEvent motion) {
            Log.d(TAG, "bahar");
            try {

                Log.d("length ", "is " + mCurrentGesture.getLength());
                Log.d("stroke count is ", "" + mCurrentGesture.getStrokesCount());
                ArrayList<GestureStroke> strokes = mCurrentGesture.getStrokes();
                mCurrentGesture = new Gesture();
                for (GestureStroke gs : strokes) {
                    float[] newPoints = GestureUtils.temporalSampling(gs, 5); // samples them to 5 pairs of points
                    translate(newPoints,gestureView.getWidth(),gestureView.getHeight());
                    Log.d("number of points"," is"+ newPoints.length/2);
                    ArrayList<GesturePoint> gp = new ArrayList<>();


                    //ReCreating gesturepoints  with sampled points
                    for (int k = 0; k < newPoints.length; k = k + 2) {
                        GesturePoint gestPoint = new GesturePoint((newPoints[k]), (newPoints[k + 1]), SystemClock.currentThreadTimeMillis());
                        gp.add(gestPoint);
                        allGesturePoints.add(gestPoint);
                        //allGesturePoints.add(newPoints[k]);
                        //allGesturePoints.add(newPoints[k+1]);
                    }

                    gs = new GestureStroke(gp); // same gesture but sampled to 5 pairs of points
                    for (GesturePoint g : gp) {
                        Log.d("point is x ", Float.toString(g.x) + " y: " + Float.toString(g.y));
                    }
                   // Log.d("length ", "is " + Math.sqrt(Math.pow(newPoints[0]-newPoints[2]),2));
                    Log.d("length", "is " + (newPoints[4] - newPoints[2]));
                    mCurrentGesture.addStroke(gs);

                    centroid = computeCentroid(gs.points);

                    Log.d("centroid "," is "+centroid[0]+" "+centroid[1]);
                }
                centroid = computeCentroid(allGesturePoints); // Centroid of entire gesture
                Log.d("centroid "," is "+centroid[0]+" "+centroid[1]);
                Log.d("Gesture length ","is "+ mCurrentGesture.getLength());


            }catch(Exception e){
                Log.d("Exception occured ", e.getMessage());
                reDrawGestureView();
            }
           // gestureView.draw();
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "cancel");
        }
    };

    private GestureOverlayView.OnGesturePerformedListener onGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
            Log.d("length of gesture ", "is " + mCurrentGesture.getLength());
            Log.d("centroid ", " is " + centroid[0] + " " + centroid[1]);
            translateCentroid(centroid, gestureOverlayView.getWidth()/2,gestureOverlayView.getHeight()/2);
            Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);
        }
    };


    // *** Helper methods underneath ***

    static float[] computeCentroid(float[] points) {
        float centerX = 0;
        float centerY = 0;
        int count = points.length;
        for (int i = 0; i < count; i++) {
            centerX += points[i];
            i++;
            centerY += points[i];
        }
        float[] center = new float[2];
        center[0] = 2 * centerX / count;
        center[1] = 2 * centerY / count;
        return center;
    }

    static float[] computeCentroid(ArrayList<Float> points) {
        float centerX = 0;
        float centerY = 0;
        int count = points.size();

        for (int i = 0; i < count; i++) {
            centerX += points.get(i);
            i++;
            centerY += points.get(i);
        }
        float[] center = new float[2];
        center[0] = 2 * centerX / count;
        center[1] = 2 * centerY / count;
        return center;
    }

    static void translateCentroid(float[] centroid, float widthCenter, float heightCenter){
        centroid[0]+=widthCenter-centroid[0];
        centroid[1]+=heightCenter-centroid[1];
    }

    // return all translated gesture points GP
    static ArrayList<GesturePoint> translatePoints(float[]centroid, ArrayList<GesturePoint> gesture_points){
        ArrayList<GesturePoint>points = new ArrayList<>();
        for(GesturePoint gp:gesture_points){
            float x=0,y=0;
            x = centroid[0] - gp.x;
            y = centroid[1] - gp.y;
            GesturePoint gesturePoint = new GesturePoint(x,y,SystemClock.currentThreadTimeMillis());
            points.add(gesturePoint);
        }
        return points;
    }

    //creates one stroke from all translate gesture points
    static GestureStroke makeStroke(ArrayList<GesturePoint>gp){
        return new GestureStroke(gp);
    }

    static float[] translate(float[] points, float dx, float dy) {
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            points[i] += dx;
            points[i + 1] += dy;
        }
        return points;
    }

    static double pathLength(float[] points){
        double length=0;
        for(int i=2;i<points.length;i++){
            length+=points[i]-points[i-1];
        }
        return length/points.length;
    }

    private void getName() {
        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle("Enter name");
        //namePopup.setMessage(R.string.enter_name);
        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);
        namePopup.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //db.updateExistingMeasurement(measurement);
                if (!nameField.getText().toString().matches("")) {
                    mGesturename = nameField.getText().toString();
                    saveGesture();
                } else {
                    getName();  //TODO : set name field with old name string user added
                    showToast("invalid");
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
        // if(!mGesturename.matches("")) {
        //gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        //gLib.load();
        //TODO: check kar k same naam valu gesture che k nai
        gLib.addGesture(mGesturename, mCurrentGesture);
        if (!gLib.save()) {
            Log.e(TAG, "gesture not saved!");
        }else {
            showToast("saved" + getExternalFilesDir(null) + "/gesture.txt");
            Log.i(TAG,"gesture saved!");
        }
        reDrawGestureView();
        // }
    }
    private void resetEverything(){
        mGestureDrawn = false;
        mCurrentGesture = null;
        mGesturename = "";
    }

    private void reDrawGestureView() {
        setContentView(R.layout.save_gesture);
        resetButton = (Button) findViewById(R.id.reset_button);
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


    @Override
    public void onClick(View view) {

    }
}
