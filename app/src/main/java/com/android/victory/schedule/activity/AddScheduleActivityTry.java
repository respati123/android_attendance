package com.android.victory.schedule.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.victory.schedule.R;
import com.android.victory.schedule.data.CloudDatabase;
import com.android.victory.schedule.data.Model;
import com.android.victory.schedule.data.ScheduleDatabase;
import com.android.victory.schedule.service.NetworkService;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivityTry extends AppCompatActivity implements View.OnClickListener {

    TextView txtDate, txtTime;
    Button btnSave, btnCancel;
    int mYear, mMonth, mDay;
    EditText meetEditText,descEditText;
    Spinner salesSpinner,clientSpinner, jobSpinner,serviceSpinner;


    private boolean isEdited = false, isDone = false;
    private int scheduleIndex = 0;

    public SQLiteDatabase db;
    private String doneDate = "";

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    public ArrayList<String> salesRowIdList, clientRowIdList, jobRowIdList, serviceRowIdList;
    public ArrayList<String> salesRowNameList, clientRowNameList, jobRowNameList, serviceRowNameList;

    int salSelectIndex = 0, cliSelectIndex = 0, jobSelectIndex = 0, srvSelectIndex = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule_try);

        salesSpinner = (Spinner)findViewById(R.id.sales_spinner);
        clientSpinner = (Spinner)findViewById(R.id.client_spinner);
        jobSpinner = (Spinner)findViewById(R.id.job_spinner);
        serviceSpinner = (Spinner)findViewById(R.id.service_spinner);
        txtDate  = (TextView) findViewById(R.id.textDate);
        txtTime = (TextView) findViewById(R.id.textTime);
        btnSave = (Button) findViewById(R.id.sch_save);
        btnCancel = (Button) findViewById(R.id.sch_cancel);
        meetEditText = (EditText)findViewById(R.id.meet_edittext);
        descEditText = (EditText)findViewById(R.id.desc_edittext);

        if (getIntent().getExtras() != null){
            isEdited = getIntent().getExtras().getBoolean("ISEDIT");
            scheduleIndex = getIntent().getExtras().getInt("INDEX");
            isDone = getIntent().getExtras().getBoolean("ISDONE");
            txtTime.setText(Model.getInstance().hasEditedSchedule.getSch_date().split("---")[1]);
            txtDate.setText(Model.getInstance().hasEditedSchedule.getSch_date().split("---")[0]);
            meetEditText.setText(Model.getInstance().hasEditedSchedule.getMeet_name());
            descEditText.setText(Model.getInstance().hasEditedSchedule.getKet());
        }
        if (isDone){
            doneDate = getIntent().getExtras().getString("DONE_DATE");
            salesSpinner.setEnabled(false);
            clientSpinner.setEnabled(false);
            jobSpinner.setEnabled(false);
            serviceSpinner.setEnabled(false);
            meetEditText.setEnabled(false);
            descEditText.setEnabled(false);
        }

        salesRowIdList = new ArrayList<>();
        clientRowIdList = new ArrayList<>();
        jobRowIdList = new ArrayList<>();
        serviceRowIdList = new ArrayList<>();
        salesRowNameList = new ArrayList<>();
        clientRowNameList = new ArrayList<>();
        jobRowNameList = new ArrayList<>();
        serviceRowNameList = new ArrayList<>();
        int index = 0;
        final CloudDatabase cloudDatabase = new CloudDatabase(AddScheduleActivityTry.this);
        db = cloudDatabase.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from " + CloudDatabase.TABLE_SALESMAN_NAME,null);

        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String salRowId = cursor.getString(cursor.getColumnIndex(CloudDatabase.SALESMAN_ROW_ID));
                String salRowName = cursor.getString(cursor.getColumnIndex(CloudDatabase.SALESMAN_NAME));
                if (isEdited){
                    if (salRowName.equals(Model.getInstance().hasEditedSchedule.getSal_name()))salSelectIndex = index;
                }
                salesRowIdList.add(salRowId);
                salesRowNameList.add(salRowName);
                cursor.moveToNext();
                index++;
            }
        }
        if (!salesRowIdList.isEmpty()){
            ArrayAdapter<String> client_adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_style, salesRowNameList);
            salesSpinner.setAdapter(client_adapter);
            salesSpinner.setSelection(salSelectIndex);
        }else {
            salesRowIdList.add(Model.getInstance().username);
            ArrayAdapter<String> client_adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_style, salesRowIdList);
            salesSpinner.setAdapter(client_adapter);
            salesSpinner.setSelection(0);
        }

        index = 0;

        cursor = db.rawQuery("select * from " + CloudDatabase.TABLE_CLIENT_NAME,null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String clientRowId = cursor.getString(cursor.getColumnIndex(CloudDatabase.CLIENT_ROW_ID));
                String clientRowName = cursor.getString(cursor.getColumnIndex(CloudDatabase.CLIENT_NAME));
                if (isEdited){
                    if (clientRowName.equals(Model.getInstance().hasEditedSchedule.getClient_name()))cliSelectIndex = index;
                }
                clientRowIdList.add(clientRowId);
                clientRowNameList.add(clientRowName);
                cursor.moveToNext();
                index++;
            }
        }
        if (!clientRowIdList.isEmpty()){
            ArrayAdapter<String> client_adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_style, clientRowNameList);
            clientSpinner.setAdapter(client_adapter);
            clientSpinner.setSelection(cliSelectIndex);
        }

        index = 0;

        cursor = db.rawQuery("select * from " + CloudDatabase.TABLE_JOB_NAME,null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String jobRowId = cursor.getString(cursor.getColumnIndex(CloudDatabase.JOB_ROW_ID));
                String jobName = cursor.getString(cursor.getColumnIndex(CloudDatabase.JOB_NAME));
                if (isEdited){
                    if (jobName.equals(Model.getInstance().hasEditedSchedule.getJob_name()))jobSelectIndex = index;
                }
                jobRowIdList.add(jobRowId);
                jobRowNameList.add(jobName);
                cursor.moveToNext();
                index++;
            }
        }

        if (!jobRowIdList.isEmpty()){
            ArrayAdapter<String> client_adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_style, jobRowNameList);
            jobSpinner.setAdapter(client_adapter);
            jobSpinner.setSelection(jobSelectIndex);
        }

        index = 0;

        cursor = db.rawQuery("select * from " + CloudDatabase.TABLE_SERVICE_NAME,null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String serviceRowId = cursor.getString(cursor.getColumnIndex(CloudDatabase.SERVICE_CODE));
                String serviceRowName = cursor.getString(cursor.getColumnIndex(CloudDatabase.SERVICE_NAME));
                if (isEdited){
                    if (serviceRowName.equals(Model.getInstance().hasEditedSchedule.getSrv_name()))srvSelectIndex = index;
                }
                serviceRowIdList.add(serviceRowId);
                serviceRowNameList.add(serviceRowName);
                cursor.moveToNext();
                index++;
            }
        }

        if (!serviceRowIdList.isEmpty()){
            ArrayAdapter<String> client_adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_style, serviceRowNameList);
            serviceSpinner.setAdapter(client_adapter);
            serviceSpinner.setSelection(srvSelectIndex);
        }

        //setOnclicklistener
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(txtDate.getId());
            }
        });



        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(txtTime.getId());
            }
        });


        dateSetListener = new DatePickerDialog.OnDateSetListener() {
           @SuppressLint("SetTextI18n")
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               mYear = year;
               mMonth = month;
               mDay = dayOfMonth;

               txtDate.setText(mYear+"/"+mMonth+"/"+mDay);

           }
       };


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id){

            case R.id.textDate:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Calendar cal = Calendar.getInstance();
                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DATE);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            android.R.style.Theme_Material_Dialog_MinWidth,dateSetListener,mYear, mMonth, mDay );
                    datePickerDialog.show();
                    return  datePickerDialog;
                }

                return null;
            case R.id.textTime:
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddScheduleActivityTry.this, R.style.CustomPickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtTime.setText(hourOfDay + " : " + minute);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            default:
                return null;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.sch_save:
                if (txtTime.getText().toString().isEmpty()){
                    txtTime.setError("Time is needed!!!");

                }else if (txtDate.getText().toString().isEmpty()){
                    txtDate.setError("Date is needed!!!");
                }else if (meetEditText.getText().toString().isEmpty()){
                    meetEditText.setError("Meet Name is needed!!!");
                }else if (descEditText.getText().toString().isEmpty()){
                    descEditText.setError("Description is needed!!!");
                }

                final ScheduleDatabase scheduleDatabase = new ScheduleDatabase(AddScheduleActivityTry.this);
                db = scheduleDatabase.getWritableDatabase();
                String rowID = System.currentTimeMillis()/1000 + "";
                if (isEdited){
                    Cursor res = db.rawQuery("select * from " + ScheduleDatabase.TABLE_SCHEDULE_NAME, null);
                    int index = 0;
                    if (res.moveToFirst()){
                        while (!res.isAfterLast()){
                            if (scheduleIndex == index){
                                rowID = res.getString(res.getColumnIndex(ScheduleDatabase.SCHEDULE_ROW_ID));
                                db.delete(ScheduleDatabase.TABLE_SCHEDULE_NAME, ScheduleDatabase.SCHEDULE_ID + " = '" + res.getString(res.getColumnIndex(ScheduleDatabase.SCHEDULE_ID)) + "'",null);
                                break;
                            }
                            index++;
                            res.moveToNext();
                        }
                    }
                }
                ContentValues contentValues = new ContentValues();
                contentValues.clear();
                contentValues.put(scheduleDatabase.SCHEDULE_ROW_ID, rowID);
                contentValues.put(scheduleDatabase.SCHEDULE_SAL_ID, salesRowIdList.get(salesSpinner.getSelectedItemPosition()));
                contentValues.put(scheduleDatabase.SCHEDULE_SAL_NAME, salesSpinner.getSelectedItem().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_CLIENT_ID, clientRowIdList.get(clientSpinner.getSelectedItemPosition()));
                contentValues.put(scheduleDatabase.SCHEDULE_CLIENT_NAME, clientSpinner.getSelectedItem().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_JOB_ID, jobRowIdList.get(jobSpinner.getSelectedItemPosition()));
                contentValues.put(scheduleDatabase.SCHEDULE_JOB_NAME, jobSpinner.getSelectedItem().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_SRV_ID, serviceRowIdList.get(serviceSpinner.getSelectedItemPosition()));
                contentValues.put(scheduleDatabase.SCHEDULE_SRV_NAME, serviceSpinner.getSelectedItem().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_KET,descEditText.getText().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_MEET_NAME,meetEditText.getText().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_SCH_DATE, txtDate.getText().toString() + "---" + txtTime.getText().toString());
                contentValues.put(scheduleDatabase.SCHEDULE_SYNC_DATE,"");
                contentValues.put(scheduleDatabase.SCHEDULE_DONE_DATE,doneDate);
                db.insert(scheduleDatabase.TABLE_SCHEDULE_NAME, null, contentValues);

                startActivity(new Intent(AddScheduleActivityTry.this, ScheduleActivity.class));
                finish();
                NetworkService.uploadSchedule(salesRowIdList.get(salesSpinner.getSelectedItemPosition()), clientRowIdList.get(clientSpinner.getSelectedItemPosition()), jobRowIdList.get(jobSpinner.getSelectedItemPosition()), serviceRowIdList.get(serviceSpinner.getSelectedItemPosition())
                        , descEditText.getText().toString(), meetEditText.getText().toString(), txtDate.getText().toString() + "---" + txtTime.getText().toString(), rowID);
                break;
            case R.id.sch_cancel:
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                finish();
                break;
                default:
                    break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ScheduleActivity.class));
        finish();
    }
}
