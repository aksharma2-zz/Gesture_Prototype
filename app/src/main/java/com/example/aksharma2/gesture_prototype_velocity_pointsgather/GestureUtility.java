package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.os.SystemClock;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aksharma2 on 31-01-2017.
 */

public class GestureUtility {

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
            GesturePoint gesturePoint = new GesturePoint(x,y, SystemClock.currentThreadTimeMillis());
            newPoints.add(gesturePoint);
        }
        //   newPoints = points;
        return newPoints;
    }

    //creates one stroke from all translate gesture points
    static GestureStroke makeStroke(ArrayList<GesturePoint>gp){
        return new GestureStroke(gp);
    }

    static ArrayList<GesturePoint> translated(ArrayList<GesturePoint> points, float[] centroid, View v) {
        int size = points.size();
        ArrayList<GesturePoint> tPoints = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            //points[i].x += dx;
            float x = points.get(i).x;
            x= ((v.getWidth()/2) - centroid[0]) + x;
            //points[i + 1].y += dy;
            float y = points.get(i).y;
            y=((v.getHeight()/2) - centroid[1]) + y;
            tPoints.add(new GesturePoint(x,y,SystemClock.currentThreadTimeMillis()));
        }
        //GesturePoint[] translatedPoints = new GesturePoint[tPoints.size()];
        return tPoints;
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

    //converts array of float to array of PersonalGesture Points
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


    public static GesturePoint[] spatialSample(GesturePoint[] pts, int n){
        GesturePoint[] newPoints = new GesturePoint[n];
        newPoints[0] = pts[0];
        double increment = pts.length/n-2;

        for(int i=1;i<n-1;i++){
            float x = pts[(int)Math.floor(increment)].x; //(float)(newPoints[i-1].x + xIncDist);
            float y = pts[(int)Math.floor(increment)].y;//(float)(newPoints[i-1].y + yIncDist);
            newPoints[i] = new GesturePoint(x,y,SystemClock.currentThreadTimeMillis());
            increment += increment;
        }

        newPoints[n-1] = pts[pts.length-1];
        return newPoints;
    }


    public static ArrayList<GesturePoint> resample(ArrayList<GesturePoint> points, int n){
        float I = pathLength(points)/(n-1);
        float D = 0;
        ArrayList<GesturePoint> newPoints = new ArrayList<>();
        newPoints.add(new GesturePoint(points.get(0).x,points.get(0).y,SystemClock.currentThreadTimeMillis()));
        float x=0;
        float y=0;

        for(int i=1;i<points.size();i++){
            double d = euclidDistance(points.get(i-1),points.get(i));
            if((D+d) >= I) {
                x = (float) (points.get(i - 1).x + ((I - D) / d) * (points.get(i).x - points.get(i - 1).x));
                y = (float) (points.get(i - 1).y + ((I - D) / d) * (points.get(i).y - points.get(i - 1).y));
                newPoints.add(new GesturePoint(x, y, SystemClock.currentThreadTimeMillis()));
                points.add(i, new GesturePoint(x, y, SystemClock.currentThreadTimeMillis()));
                D=0;
            }
            else
                D += d;
        }
        return newPoints;
    }



    static double avgXValue(GesturePoint[] pts){
        double xValue=0;
        for(int i=0;i < pts.length; i++){
            xValue += pts[i].x;
        }
        return xValue/pts.length;
    }

    static double avgYValue(GesturePoint[] pts){
        double yValue=0;
        for(int i=0;i < pts.length; i++){
            yValue += pts[i].y;
        }
        return yValue/pts.length;
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

    public static float pathLength(ArrayList<GesturePoint> points){
        float length=0;
        for(int i=1;i<points.size();i++){
            length += Math.sqrt(Math.pow(points.get(i).x - points.get(i-1).x,2) + Math.pow(points.get(i).y - points.get(i-1).y,2));
        }
        return length;
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
            boundingBox = BoundingBox(points, boundingBox);

        return rotate(points, -theta, centroid);
    }

    public static Rectangle BoundingBox(ArrayList<GesturePoint> points, Rectangle dst) {
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

        return dst;
    }

    public static boolean strokeLengthThreshold(GestureOverlayView overlay, Gesture g, int threshold){
        g = overlay.getGesture();
        ArrayList<GestureStroke> strokes = g.getStrokes();
        for(GestureStroke gs: strokes){
            if(gs.length < threshold){
                return false;
            }
        }
        return true;
    }


    public static ArrayList<MyGestureStroke> convertToMyGestureStroke(ArrayList<GestureStroke> gs){
        ArrayList<MyGestureStroke> mgs = new ArrayList<>();

        for(int i=0; i<gs.size(); i++){
            GestureStroke gestureStroke = gs.get(i);
            GesturePoint[] point = floatToGP(gestureStroke.points);
            ArrayList<GesturePoint> gp = new ArrayList<>(Arrays.asList(point));
            MyGestureStroke mg = new MyGestureStroke(gp);
            mgs.add(mg);
        }
        return mgs;
    }


    public static double gestureScoreCompute(double t_dist, float t_length_diff, float t_time_diff, double s_dist, float s_length_diff, float s_time_diff){
        return 1 - ((0.3 * calcPercentage(t_dist, s_dist)) + (0.4 * calcPercentage(t_length_diff, s_length_diff)) +
                (0.3 * calcPercentage(t_time_diff, s_time_diff))
        );
    }

    public static double calcPercentage(double x, double y){
        return Math.abs((x-y)/x);
    }

    public static double calcGestureLength(Gesture g){
        double length = 0;
        ArrayList<GestureStroke> gs = g.getStrokes();
        for(GestureStroke gestureStroke: gs){
            length += gestureStroke.length;
        }
        return length;
    }




}
