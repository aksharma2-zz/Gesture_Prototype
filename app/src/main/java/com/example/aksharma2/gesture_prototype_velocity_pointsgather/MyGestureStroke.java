package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.gesture.Gesture;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;

import java.util.ArrayList;

/**
 * Created by kalpana on 19/3/2017.
 */

public class MyGestureStroke extends GestureStroke {

    double stroke_velocity;

    public MyGestureStroke(ArrayList<GesturePoint> points) {
        super(points);
    }

    public double getStroke_velocity() {
        return stroke_velocity;
    }

    public void setStroke_velocity(double stroke_velocity) {
        this.stroke_velocity = stroke_velocity;
    }

}
