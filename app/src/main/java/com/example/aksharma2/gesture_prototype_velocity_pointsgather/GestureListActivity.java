package com.example.aksharma2.gesture_prototype_velocity_pointsgather;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private Button delButton,renameButton, addButton, testButton;
    //private ImageView mMenuItemView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestures_list);

      //  openOptionsMenu();

        gestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GesturePlaceHolder>();
        makeList();
        gestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        gestureListView.setLongClickable(true);
        gestureListView.setAdapter(gestureAdapter);
        delButton=(Button)findViewById(R.id.delete_button);
        renameButton=(Button)findViewById(R.id.rename_button);
        gestureListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopup(v);
                Log.i("hi","there");
                return true;
            }
        });
       // addButton = (Button) findViewById(R.id.button_gesture_add);
       // testButton = (Button) findViewById(R.id.button_gesture_test);

        // displays the popup context top_menu to either delete or resend measurement
        this.registerForContextMenu(gestureListView);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popup.getMenu());
        popup.show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("Touch", "Test");
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        setContentView(R.layout.gestures_list);
        Log.d(TAG, getApplicationInfo().dataDir);

       // openOptionsMenu();

        gestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GesturePlaceHolder>();
        makeList();
        gestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        gestureListView.setLongClickable(true);
        gestureListView.setAdapter(gestureAdapter);

        gestureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("I","click");
              //  GestureView gv = (GestureView)parent.getItemAtPosition(position);
              //  mCurrentGestureName = gv.gesture_name.toString();

            }
        });
        // displays the popup context top_menu to either delete or resend measurement
        // registerForContextMenu(gestureListView);


    }

    public void getName(View v){
        LinearLayout parentRow = (LinearLayout)v.getParent().getParent();
        TextView tv = (TextView)parentRow.findViewById(R.id.gesture_name);
        String name = tv.getText().toString();
        Log.i("Name is"," "+name);
    }

    private void makeList() {
        try {
            mGestureList = new ArrayList<GesturePlaceHolder>();
            gestureLibrary = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gestr.txt");
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
        LinearLayout vwParentRow = (LinearLayout)view.getParent().getParent();
        TextView tv = (TextView)vwParentRow.findViewById(R.id.gesture_name);
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
      //  mCurrentGestureName=v.getParent().
        LinearLayout ll = (LinearLayout)v.getParent().getParent();
        TextView tv = (TextView)ll.findViewById(R.id.gesture_name);
        mCurrentGestureName = tv.getText().toString();
        Log.i("Click"," Delete Gesture");
        Log.i("Gesture name"," :"+mCurrentGestureName);
      /*  gestureLibrary.removeEntry(mCurrentGestureName);
        gestureLibrary.save();
        mCurrentGestureName = "";
        gestureAdapter.notifyDataSetChanged();
        onResume(); */
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