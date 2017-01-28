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
import java.util.Arrays;
import java.util.List;

//import pack.GestureApp.R;

public class SaveGesturesActivity extends AppCompatActivity implements View.OnClickListener {
    private GestureLibrary gLib;
    private static final String TAG = "SaveGestureActivity";
    private boolean mGestureDrawn;                      //tc
    private Gesture mCurrentGesture;
    public float gesture_length;
    private float[] centroid ={};
    private String mGesturename;
    private Button resetButton;
    private ArrayList<GesturePoint>allGesturePoints = new ArrayList<>(); // to calculate centroid of all gesture points
    private ArrayList<GesturePoint>translatedPoints = new ArrayList<>(); // new translated Gesture points

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("New ","app ");
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

    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
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
            Log.d(TAG, "Gesture stroke ended");
            try {
                gesture_length = mCurrentGesture.getLength();
                Log.d("Total stroke length ", "is " + mCurrentGesture.getLength());
                Log.d("stroke count is ", "" + mCurrentGesture.getStrokesCount());
                ArrayList<GestureStroke> strokes = mCurrentGesture.getStrokes();
                Log.d("First point"," is"+ strokes.get(0).points[0]);
                mCurrentGesture = new Gesture();
                allGesturePoints.clear();

                for (GestureStroke gs : strokes) {
                    //convert float[] points of stroke to GesturePoint array
                    GesturePoint[] gps = floatToGP(gs.points);
                    //Spatially sample GesturePoints
                    for(GesturePoint gp:gps){
                        Log.d("point "," is"+gp.x);
                    }
                    gps = spatialSample(gps,10);
                    Log.d("spaced point is "," "+gps[0].x);
                    Log.d("spaced point is "," "+gps[1].x);
                    centroid = computeCentroid(new ArrayList<GesturePoint>(Arrays.asList(gps))); // centroid of gesture
                   // float[] newPoints = GestureUtils.temporalSampling(gs, 5); // samples them to 5 pairs of points
                    gps = translated(gps,centroid, gestureView); //translates gesture points of gesture to centroid of gesture being translated to center of screen
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
            gestureOverlayView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            Log.d("performed point is", " "+ allGesturePoints.get(0).x); // translated gesture point x
            Log.d("performed point is", " "+ allGesturePoints.get(0).y); // translated gesture point x
            Log.d("length of gesture ", "is " + gesture_length);
      //      Log.d("centroid ", " is " + centroid[0] + " " + centroid[1]);
            //translateCentroid(centroid, gestureOverlayView.getWidth()/2,gestureOverlayView.getHeight()/2);
            float  centre[] = translateCentroid(centroid, gestureOverlayView);
            translatedPoints=translatePoints(centroid,allGesturePoints);
            gesture_length = 0;
            Log.d("translated centroid ", " is " + centroid[0] + " " + centroid[1]);
            Arrays.fill(centroid,0); // make centroid -> 0
         //   Log.d("translated point ", " is x " + translatedPoints.get(0).x + " y " + translatedPoints.get(0).y);
        }
    };


    // *** Helper methods underneath ***

    static float[] computeCentroid(ArrayList<GesturePoint> points) {
        float centerX = 0;
        float centerY = 0;
        int count = points.size();

        for (int i = 0; i < count; i++) {
            centerX += points.get(i).x;
            centerY += points.get(i).y;
        }
        float[] center = new float[2];
        center[0] =  centerX / count;
        center[1] =  centerY / count;
        return center;
    }

    static float[] translateCentroid(float[] center, View v){
       // centroid[0]+=centreX-centroid[0];
        float[] centroid = new float[center.length];
        centroid[0]=v.getX()+v.getWidth()/2;
        centroid[1]=v.getY()+v.getHeight()/2;

        return centroid;
    }

    // return all translated gesture points GP
    static ArrayList<GesturePoint> translatePoints(float[]centroid, ArrayList<GesturePoint> gesture_points){
        ArrayList<GesturePoint>newPoints = new ArrayList<>();
        for(GesturePoint gp:gesture_points){
            float x=0,y=0;
            x = centroid[0] - gp.x;
            y = centroid[1] - gp.y;
            GesturePoint gesturePoint = new GesturePoint(x,y,SystemClock.currentThreadTimeMillis());
            newPoints.add(gesturePoint);
        }
     //   newPoints = points;
        return newPoints;
    }

    //creates one stroke from all translate gesture points
    static GestureStroke makeStroke(ArrayList<GesturePoint>gp){
        return new GestureStroke(gp);
    }

    static GesturePoint[] translated(GesturePoint[] points, float[] centroid, View v) {
        int size = points.length;
        ArrayList<GesturePoint> tPoints = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            //points[i].x += dx;
            float x = points[i].x;
            x= ((v.getWidth()/2) - centroid[0]) + x;
            //points[i + 1].y += dy;
            float y = points[i].y;
            y=((v.getHeight()/2) - centroid[1]) + y;
            tPoints.add(new GesturePoint(x,y,SystemClock.currentThreadTimeMillis()));
        }
        GesturePoint[] translatedPoints = new GesturePoint[tPoints.size()];
        return tPoints.toArray(translatedPoints);
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

    //converts array of float to array of Gesture Points
    static GesturePoint[] floatToGP(float[] points){
        GesturePoint[] gp = new GesturePoint[points.length/2];
        for(int i=0;i<points.length/2;i++){
            gp[i] = new GesturePoint(points[2*i], points[(2*i)+1], SystemClock.currentThreadTimeMillis());
        }
        return gp;
    }

    static float[] spatialSampling(GestureStroke gs, int points){
        float[] newPoints = {0};
        float equiLength = gs.length/points;
        for(int i=0;i<points;i++){
            newPoints[i]=(i+1) * equiLength;
        }
        return newPoints;
    }

    public static ArrayList <GesturePoint> resample(GesturePoint[] pts, int n){
        float incrementLength = pathLength(pts)/(n-1);
        float Dist=0;
        ArrayList<GesturePoint> newPoints = new ArrayList<>();
        for(int i=1; i<pts.length;i++){
            double d= euclidDistance(pts[i],pts[i-1]);
            if((Dist + d)>= incrementLength){
                float newX =(float) (pts[i-1].x + ((incrementLength - Dist) / d) * (pts[i].x - pts[i-1].x));
                float newY =(float) (pts[i-1].y + ((incrementLength - Dist) / d) * (pts[i].y - pts[i-1].y));
                GesturePoint newPoint = new GesturePoint(newX,newY,SystemClock.currentThreadTimeMillis());
                newPoints.add(newPoint);
                pts[i] = newPoint;
            }
            else{
                Dist += d;
            }
        }
        return newPoints;
    }

    public static GesturePoint[] spatialSample(GesturePoint[] pts, int n){
        GesturePoint[] newPoints = new GesturePoint[n];
        newPoints[0] = pts[0];
        double xIncDist = (pts[pts.length-1].x - pts[0].x)/(n-1);//euclidDistance(pts[pts.length-1],pts[0]);
        double yIncDist = (pts[pts.length-1].y - pts[0].y)/(n-1);
        for(int i=1;i<n-1;i++){
            float x = (float)(newPoints[i-1].x + xIncDist);
            float y = (float)(newPoints[i-1].y + yIncDist);
            newPoints[i] = new GesturePoint(x,y,SystemClock.currentThreadTimeMillis());
        }
        newPoints[n-1] = pts[pts.length-1];
        return newPoints;
    }

    public static double euclidDistance(GesturePoint pt1, GesturePoint pt2){
        return Math.sqrt(Math.pow(pt2.x - pt1.x,2) + Math.pow(pt2.y - pt1.y,2));
    }

    public static float pathLength(GesturePoint[] points){
        float length=0;
        for(int i=1;i<points.length;i++){
            length += Math.sqrt(Math.pow(points[i].x - points[i-1].x,2) + Math.pow(points[i].y - points[i-1].y,2));
        }
        return length;
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

    public static ArrayList<GesturePoint> rotate(ArrayList<GesturePoint> points, double radians, float[] centroid) {
        ArrayList<GesturePoint>newPoints = new ArrayList<GesturePoint>(points.size());
        //Point c = Centroid(points);
        float _cos = (float)Math.cos(radians);
        float _sin =(float)Math.sin(radians);
        float cx = centroid[0];
        float cy = centroid[1];
        for (int i = 0; i < points.size(); i++)
        {
            //Point p = (Point) points.elementAt(i);

            float dx = points.get(i).x - cx;
            float dy = points.get(i).y - cy;

            newPoints.add(
                    new GesturePoint(
                            (dx * _cos) - (dy * _sin) + cx, (dx * _sin) + (dy * _cos) + cy, SystemClock.currentThreadTimeMillis()
                    ));

        }
        return newPoints;
    }

    public static ArrayList<GesturePoint> RotateToZero(ArrayList<GesturePoint> points, float[] centroid, Rectangle boundingBox)
    {
       // Point c = Centroid(points);
       // Point first = (Point)points.elementAt(0);
       // double theta = Trigonometric.atan2(c.Y - first.Y, c.X - first.X);
        float theta =(float) Math.atan2(centroid[1] - points.get(0).y, centroid[0] - points.get(0).x);

       /* if (centroid != null)
            centroid.copy(c); */

        if (boundingBox != null)
            BoundingBox(points, boundingBox);

        return rotate(points, -theta, centroid);
    }

    public static void BoundingBox(ArrayList<GesturePoint> points, Rectangle dst) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        // Enumeration e = points.elements();

//		foreach (Point p in points)
        for (GesturePoint gp: points)
        {
           // Point p = (Point)e.nextElement();

            if (gp.x < minX)
                minX = gp.x;
            if (gp.x > maxX)
                maxX = gp.x;

            if (gp.y < minY)
                minY = gp.y;
            if (gp.y > maxY)
                maxY = gp.y;
        }

        dst.X = minX;
        dst.Y = minY;
        dst.Width = maxX - minX;
        dst.Height = maxY - minY;
    }


    @Override
    public void onClick(View view) {

    }
}
