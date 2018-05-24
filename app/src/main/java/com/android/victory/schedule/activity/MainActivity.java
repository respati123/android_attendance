package com.android.victory.schedule.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.android.victory.schedule.Adapter.AdapterExpandableList;
import com.android.victory.schedule.R;
import com.android.victory.schedule.data.Constants;
import com.android.victory.schedule.data.Model;
import com.android.victory.schedule.service.NetworkService;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private String IS_CHECK_BG = "#15beb8";
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;


    //view main
    private TextView nickname;
    private Button indonesia, english;
    private ImageView monitorImageView, scheduleImageView, checkInImageView, checkOutImageView ;
    private AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set INIT
        scheduleImageView = (ImageView) findViewById(R.id.scheduleImgView);
        monitorImageView = (ImageView) findViewById(R.id.monitorImgView);
        nickname = (TextView) findViewById(R.id.nicknameHeader);
        LinearLayout layLang = (LinearLayout) findViewById(R.id.menu_lag);
        checkInImageView = (ImageView) findViewById(R.id.checkInView);
        checkOutImageView = (ImageView) findViewById(R.id.checkOutView);



        //Click Init

        scheduleImageView.setOnClickListener(this);
        monitorImageView.setOnClickListener(this);
        checkInImageView.setOnClickListener(this);
        checkOutImageView.setOnClickListener(this);


        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        java.util.Collections.sort(expandableListTitle);
        expandableListAdapter = new AdapterExpandableList(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setGroupIndicator(getResources().getDrawable(R.drawable.non_indicator));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                switch (groupPosition)
                {
                    case 1:
                        Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + "list" ,Toast.LENGTH_SHORT).show();

                }
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                if(groupPosition == 0){

                    switch (childPosition){

                        case 0:
                            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this);
                            break;
                        case 1:
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Password");
                            dialog.setMessage("Enter Password");

                            //layout

                            final EditText input = new EditText(MainActivity.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            lp.setMargins(10,10,10,10);
                            input.setLayoutParams(lp);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            dialog.setView(input);

                            dialog.setPositiveButton("Submit",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(input.getText().toString().isEmpty()) input.setError("Password Needed!");
                                            else {
                                                Model.getInstance().password = input.getText().toString();
                                                Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            dialog.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        }
                                    });

                            dialog.show();
                            return false;
                        default:
                            return false;
                    }
                } else if (groupPosition == 2){

                    switch (childPosition){
                        case 0:
                            createDialogButtonLang();
                            break;
                        case 1:
                            createDialogInputAge();
                            break;
                    }
                }
                return false;

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setHeaderNavigation();
    }

    private void createDialogInputAge() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Age local");
        final EditText edittxt = new EditText(MainActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER;
        edittxt.setId(R.id.buttonAge);
        edittxt.setLayoutParams(layoutParams);
        dialog.setView(edittxt);

        dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edittxt.getText().toString().isEmpty())edittxt.setError("Age is needed");
                else {

                    Model.getInstance().localPeriodTime = Integer.parseInt(edittxt.getText().toString()) * 30 * 24 * 60 * 1000;
                    startService(new Intent(MainActivity.this, NetworkService.class));
                    Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        dialog.show();

    }

    private void createDialogButtonLang() {


       LayoutInflater inflater = getLayoutInflater();
       View view = inflater.inflate(R.layout.lang_menu, (ViewGroup) findViewById(R.id.menu_lag));
       final AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
       .setTitle("Language")
       .setView(view)
       .setPositiveButton("Choose", null)
       .setPositiveButton("Cancel", null)
       .create();

       alert.show();


       indonesia = (Button) view.findViewById(R.id.buttonInd);
       english = (Button) view.findViewById(R.id.buttonIng);
       isUsingLang();
       indonesia.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Model.getInstance().isEnglish = false;
               isUsingLang();
               alert.dismiss();

           }
       });

       english.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Model.getInstance().isEnglish = true;
               isUsingLang();
               alert.dismiss();
           }
       });

    }

    private void isUsingLang() {

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.check_icon);
        if(!Model.getInstance().isEnglish){
            indonesia.setPadding(20, 0, 20, 0);
            indonesia.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            indonesia.setBackgroundColor(Color.parseColor("#15beb8"));

        } else {

            english.setPadding(20, 0, 20, 0);
            english.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            english.setBackgroundColor(Color.parseColor("#15beb8"));
        }
    }

    private void setHeaderNavigation() {

        String name;
        name = Model.getInstance().nickname;
        nickname.setText(name);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.scheduleImgView:
                startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                finish();
                break;
            case R.id.monitorImgView:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                finish();
                break;
            case R.id.checkInView:
                startActivity(new Intent(MainActivity.this,CheckInActivity.class));
                finish();
                break;
            case R.id.checkOutView:
                startActivity(new Intent(MainActivity.this, CheckOutActivity.class));
                finish();
                break;
                default:
                    break;

        }
    }
}
