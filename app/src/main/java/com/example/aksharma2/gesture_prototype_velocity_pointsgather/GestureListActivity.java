package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class GestureListActivity extends Activity {
    private static final String TAG = "GestureListActivity";
    private String mCurrentGestureName,navuNaam;
    private ListView gestureListView;
    private static ArrayList<GesturePlaceHolder> mGestureList;
    private GestureAdapter gestureAdapter;
    private GestureLibrary gestureLibrary;
    private Button delButton,renameButton;
    //private ImageView mMenuItemView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestures_list);
        
        openOptionsMenu();

        gestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GesturePlaceHolder>();
        makeList();
        gestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        gestureListView.setLongClickable(true);
        gestureListView.setAdapter(gestureAdapter);
        delButton=(Button)findViewById(R.id.delete_button);
        renameButton=(Button)findViewById(R.id.rename_button);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(gestureListView);
    }



    @Override
    public void onResume(){
        super.onResume();
        setContentView(R.layout.gestures_list);
        Log.d(TAG, getApplicationInfo().dataDir);

        openOptionsMenu();

        gestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GesturePlaceHolder>();
        makeList();
        gestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        gestureListView.setLongClickable(true);
        gestureListView.setAdapter(gestureAdapter);
        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(gestureListView);

    }

    /**
     * badha gestures laine emne list ma mukse
     */
    private void makeList() {
        try {
            mGestureList = new ArrayList<GesturePlaceHolder>();
            gestureLibrary = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
            gestureLibrary.load();
            Set<String> gestureSet = gestureLibrary.getGestureEntries();
            for(String gestureNaam: gestureSet){
                ArrayList<Gesture> list = gestureLibrary.getGestures(gestureNaam);
                for(Gesture g : list) {
                    mGestureList.add(new GesturePlaceHolder(g, gestureNaam));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void populateMenu(View view){
        //ImageView idView = (ImageView) view.findViewById(R.id.gesture_id);
        //Log.d(TAG, "ha ha" + idView.getText().toString());
        LinearLayout vwParentRow = (LinearLayout)view.getParent().getParent();
        TextView tv = (TextView)vwParentRow.findViewById(R.id.gesture_name_ref);
        mCurrentGestureName = tv.getText().toString();
        PopupMenu popup = new PopupMenu(this, view);
        // popup.getMenuInflater().inflate(R.menu.gesture_item_options, popup.getMenu());
        popup.show();
    }

    public void addButtonClick(View view){
        Intent saveGesture = new Intent(GestureListActivity.this, SaveGesturesActivity.class);
        startActivity(saveGesture);
    }

    public void testButtonClick(View view){
        Intent testGesture = new Intent(GestureListActivity.this, GestureRecognizeActivity.class);
        startActivity(testGesture);
    }

    public void deleteButtonClick(View v){
        gestureLibrary.removeEntry(mCurrentGestureName);
        gestureLibrary.save();
        mCurrentGestureName = "";
        onResume();
    }

   /* public void renameButtonClick(View v){

        AlertDialog.Builder namePopup = new AlertDialog.Builder(GestureListActivity.this);
        namePopup.setTitle(getString(R.string.enter_new_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(GestureListActivity.this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().matches("")) {
                    navuNaam = nameField.getText().toString();
                    saveGesture();
                } else {
                    renameButtonClick(null);  //TODO : validation
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                navuNaam = "";
                mCurrentGestureNaam = "";
                return;
            }
        });

        namePopup.show();
    } */


    /*public void renameButtonClick(MenuItem item){

        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enter_new_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().matches("")) {
                    navuNaam = nameField.getText().toString();
                    saveGesture();
                } else {
                    renameButtonClick(null);  //TODO : validation
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                navuNaam = "";
                mCurrentGestureNaam = "";
                return;
            }
        });

        namePopup.show();
    } */

    private void saveGesture() {
        ArrayList<Gesture> list = gestureLibrary.getGestures(mCurrentGestureName);
        if (list.size() > 0) {
            gestureLibrary.removeEntry(mCurrentGestureName);
            gestureLibrary.addGesture(navuNaam, list.get(0));
            if (gestureLibrary.save()) {
                Log.e(TAG, "gesture renamed!");
                onResume();
            }
        }
        navuNaam = "";
    }
    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}