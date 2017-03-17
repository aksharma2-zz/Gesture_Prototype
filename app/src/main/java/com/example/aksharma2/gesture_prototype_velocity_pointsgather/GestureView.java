package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by aksharma2 on 17-02-2017.
 */

public class GestureView extends View{

    public ImageView gesture_image;
    public TextView gesture_name;
    public TextView gesture_id;
    public TextView gestureNameRef;
    public Button delButton;



    public GestureView(Context context) {
        super(context);
    }
}
