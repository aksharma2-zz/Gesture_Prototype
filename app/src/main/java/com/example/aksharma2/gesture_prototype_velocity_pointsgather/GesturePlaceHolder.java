package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.gesture.Gesture;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GesturePlaceHolder {

    private Gesture gesture;
    private String gesture_name;

    public Gesture getGesture() {
        return gesture;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

    public String getGestureName() {
        return gesture_name;
    }

    public void setGesture_id(String gesture_name) {
        this.gesture_name = gesture_name;
    }

    public GesturePlaceHolder(Gesture gesture, String gesture_id){
        this.gesture=gesture;
        this.gesture_name = gesture_name;
    }



}

