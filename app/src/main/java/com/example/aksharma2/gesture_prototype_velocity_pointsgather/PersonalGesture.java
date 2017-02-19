package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

/**
 * Created by aksharma2 on 19-02-2017.
 */

public class PersonalGesture extends android.gesture.Gesture {

    private String gesture_name;

    public PersonalGesture(String gesture_name){
        super();
        this.gesture_name = gesture_name;
    }

    public String getGesture_name() {
        return gesture_name;
    }

    public void setGesture_name(String gesture_name) {
        this.gesture_name = gesture_name;
    }
}
