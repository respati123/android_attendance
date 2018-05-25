package com.android.victory.schedule.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.victory.schedule.R;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivityTry extends AppCompatActivity {

    TextView txtDate, txtTime;
    int mYear, mMonth, mDay;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule_try);

        txtDate  = (TextView) findViewById(R.id.textDate);
        txtTime = (TextView) findViewById(R.id.textTime);

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

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        android.R.style.Theme_Material_Dialog_MinWidth, timeSetListener, )
            default:
                return null;
        }
    }


}
