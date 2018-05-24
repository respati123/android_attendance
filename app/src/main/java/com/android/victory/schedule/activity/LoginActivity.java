package com.android.victory.schedule.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.victory.schedule.R;
import com.android.victory.schedule.data.Model;
import com.android.victory.schedule.data.StateSaveActivity;
import com.android.victory.schedule.service.NetworkService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.validation.Validator;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String TAG = "LoginActivity";

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public static final int RequestPermissionCode  = 1 ;

    private TextInputEditText compText, usernameText, pwdEditText;
    private PinEntryEditText otpText;
    private Button btnSign;
    private SwitchCompat btnSwitch;
    private ProgressBar prgBar, prgBarActive;
    public LinearLayout accountLayout,otpLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        EnableRuntimePermission();

        //TODO INITIALIAZE
        compText = findViewById(R.id.comp_edittext);
        usernameText = findViewById(R.id.username_edittext);
        pwdEditText = findViewById(R.id.pwd_edittext);
        otpText = findViewById(R.id.otp_edittext);
        accountLayout = findViewById(R.id.account_Layout);
        otpLayout = findViewById(R.id.otp_layout);
        btnSign = findViewById(R.id.btnSign);
        pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnSign.setEnabled(false);
        btnSwitch = findViewById(R.id.pass_switch);
        prgBar = findViewById(R.id.progress);
        prgBarActive = findViewById(R.id.progress1);


        // TODO Action
        btnSign.setOnClickListener(this);
        compText.addTextChangedListener(textWatcher);
        usernameText.addTextChangedListener(textWatcher);
        pwdEditText.addTextChangedListener(textWatcher);


        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    if(pwdEditText.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){

                        pwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    }
                }else {

                    pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.otp_edittext);
        if(pinEntry != null){
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    prgBarActive.setVisibility(View.VISIBLE);
                    new AsyncOTP().execute(compText.getText().toString(), str.toString());
                }
            });
        }
//        signinTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (compText.getText().toString().isEmpty()){
//                    compText.setError("Comp is needed!");
//                    return;
//                }
//
//                if (usernameText.getText().toString().isEmpty()){
//                    usernameText.setError("User Name is needed!");
//                    return;
//                }
//
//                if (pwdEditText.getText().toString().isEmpty()){
//                    pwdEditText.setError("Password is needed!");
//                    return;
//                }
//                if (Model.getInstance().isNetworkAvailable(LoginActivity.this)){
//                    Model.getInstance().isVailable = true;
//                    signinTextView.setText("--Signing--");
//                    new AsyncLogin().execute(compText.getText().toString(), usernameText.getText().toString(), pwdEditText.getText().toString());
//
//                    /*startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    startService(new Intent(LoginActivity.this,NetworkService.class ));
//                    Model.getInstance().username = usernameText.getText().toString();
//                    Model.getInstance().comp = compText.getText().toString();
//                    Model.getInstance().password = pwdEditText.getText().toString();
//                    StateSaveActivity.writeFirstTime(LoginActivity.this, true);
//                    StateSaveActivity.writeUserName(LoginActivity.this,usernameText.getText().toString());
//                    StateSaveActivity.writePassword(LoginActivity.this,pwdEditText.getText().toString());
//                    StateSaveActivity.writeComp(LoginActivity.this,compText.getText().toString());*/
//                }else {
//                    Model.getInstance().isVailable = false;
//                    if (StateSaveActivity.readFirstTime(LoginActivity.this)){
//                        Toast.makeText(LoginActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
//                    }else {
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    }
//                }
//
//            }
//        });

//        otpTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (otpText.getText().toString().isEmpty()){
//                    otpText.setError("OTP is needed!!!");
//                    return;
//                }
//                if (Model.getInstance().isNetworkAvailable(LoginActivity.this)){
//                    Model.getInstance().isVailable = true;
//                    otpTextView.setText("--Activating--");
//                    new AsyncOTP().execute(compText.getText().toString(), otpText.getText().toString());
//                }else {
//                    Model.getInstance().isVailable = false;
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//                }
//
//            }
//        });
//        if (!StateSaveActivity.readFirstTime(LoginActivity.this)){
//            StateSaveActivity.writeTime(LoginActivity.this, System.currentTimeMillis());
//        }
    }




    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(compText.getText().hashCode() == s.hashCode()){
                if(compText.getText().length() < 1){
                    compText.setError("Company Field is needed!!");
                }
            } else if(usernameText.getText().hashCode() == s.hashCode()){
                if(usernameText.getText().length() < 1){
                    usernameText.setError("Username Field is needed!!");
                }
            } else if(pwdEditText.getText().hashCode() == s.hashCode()) {
                if (pwdEditText.getText().length() < 1) {
                    pwdEditText.setError("Password Field is needed!!");
                }
            }


            if(!compText.getText().toString().isEmpty() && !usernameText.getText().toString().isEmpty() && !pwdEditText.getText().toString().isEmpty()){

                btnSign.setEnabled(true);

            } else btnSign.setEnabled(false);


        }
    };

    //TODO EnbleRuntimePermission
    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.CAMERA)){

        }else ActivityCompat.requestPermissions(LoginActivity.this,new String[]{
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnSign:
                if(Model.getInstance().isNetworkAvailable(LoginActivity.this)){

                    Model.getInstance().isVailable = true;
                    btnSign.setVisibility(View.GONE);
                    prgBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(this, compText.getText().toString()+ " " + usernameText.getText().toString() + " " + pwdEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    new AsyncLogin().execute(compText.getText().toString().trim(), usernameText.getText().toString().trim(), pwdEditText.getText().toString().trim());

                }else {

                    Model.getInstance().isVailable = false;
                    if (StateSaveActivity.readFirstTime(LoginActivity.this)){
                        Toast.makeText(LoginActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
                    }else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }

                break;
        }
    }



    //TODO AsyncLogin
    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        HttpsURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loadingImageView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                //url = new URL(model.domainString + "act.php?login=" + model.activateCode + "&uid=" + model.macAddress);
                url = new URL("https://www.myquantumhr.com/ords/pdb1/gluon/data/a_login/" + strings[0] + "/" + strings[1] + "/" + strings[2]);
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setSSLSocketFactory(Model.getInstance().getSSLContext(LoginActivity.this).getSocketFactory());
                int response = conn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK){
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line);
                    }
                    Log.d("Dataaaa",result.toString());
                    return result.toString();
                }else{
                    prgBar.setVisibility(View.GONE);
                    btnSign.setVisibility(View.VISIBLE);
                    return "unsuccessful";
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                prgBar.setVisibility(View.GONE);
                btnSign.setVisibility(View.VISIBLE);
                return "Network Timeout";
            } finally {
                conn.disconnect();

            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s != null){
                if (!s.equals("unsuccessful")){
                    try {
                        JSONArray jsonArray = new JSONObject(s).getJSONArray("items");
                        if (jsonArray.length() != 0){
                            Model.getInstance().nickcode = jsonArray.getJSONObject(0).getString("sal_code");
                            Model.getInstance().nickname = jsonArray.getJSONObject(0).getString("sal_name");
                            Model.getInstance().username = usernameText.getText().toString();
                            Model.getInstance().comp = compText.getText().toString();
                            Model.getInstance().password = pwdEditText.getText().toString();
                            StateSaveActivity.writeFirstTime(LoginActivity.this, true);
                            StateSaveActivity.writeUserName(LoginActivity.this,usernameText.getText().toString());
                            StateSaveActivity.writePassword(LoginActivity.this,pwdEditText.getText().toString());
                            StateSaveActivity.writeComp(LoginActivity.this,compText.getText().toString());
                            accountLayout.setVisibility(View.GONE);
                            otpLayout.setVisibility(View.VISIBLE);
                            prgBar.setVisibility(View.GONE);
                            Model.getInstance().displayMode = StateSaveActivity.readMode(LoginActivity.this).isEmpty()?"1":StateSaveActivity.readMode(LoginActivity.this);
                            startService(new Intent(LoginActivity.this,NetworkService.class ));
                        }else{

                            prgBar.setVisibility(View.GONE);
                            btnSign.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Your account is locked...", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        prgBar.setVisibility(View.GONE);
                        btnSign.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }else{

                    prgBar.setVisibility(View.GONE);
                    btnSign.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Your account is locked...", Toast.LENGTH_SHORT).show();
                }

            }else {

                prgBar.setVisibility(View.GONE);
                btnSign.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Network Timeout.", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }

    //TODO AsyncOTP
    private class AsyncOTP extends AsyncTask<String, String, String>
    {
        HttpsURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loadingImageView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url = new URL("https://www.myquantumhr.com/ords/pdb1/gluon/data/a_otp/" + strings[0] + "/" + strings[1]);
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setSSLSocketFactory(Model.getInstance().getSSLContext(LoginActivity.this).getSocketFactory());
                int response = conn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK){
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line);
                    }
                    conn.disconnect();
                    return result.toString();

                }else{
                    return "unsuccessful";
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                return "Network Timeout";
            }finally {
                conn.disconnect();

            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                if (!s.equals("unsuccessful")){
                    try {
                        JSONArray jsonArray = new JSONObject(s).getJSONArray("items");
                        if (jsonArray.length() != 0){

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Your account is locked...", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){

                        e.printStackTrace();
                    }
                }else {
                    prgBarActive.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }else {
                prgBarActive.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Network Timeout!!!", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }



}

