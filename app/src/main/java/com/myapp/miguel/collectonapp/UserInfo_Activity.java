package com.myapp.miguel.collectonapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.myapp.miguel.collectonapp.Model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UserInfo_Activity extends AppCompatActivity {

    private Button doneButton;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private EditText etBirthdate;
    private FirebaseDatabase secondaryDatabase;
    private FirebaseApp userFirebaseApp;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Gson gson;
    private int status;
    private RadioButton rbMale, rbFemale, rbOther;
    SharedPreferences mPrefs;
    private Spinner spiCountries;
    private String stSelectedCountry, stUserGender, stUserBirthdate;
    private TextView tvCountry, tvGender, tvBirthdate;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_);

        //Retreive userInfo Model from sharedPref
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("userInfo", "");
        userInfo = gson.fromJson(json, UserInfo.class);

        setViewElements();
        userCollectionsFirebaseDatabase();
        countrySelection();
        calendarPupUpSelectBirthdate();
        DoneButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        status = mPrefs.getInt("status", 0);
        if (status == 1){
            Intent mainIntent = new Intent(UserInfo_Activity.this, MainFeature_Activity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void saveUserModel() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        prefsEditor.putString("userInfo", json);
        prefsEditor.commit();
    }

    private void DoneButton() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                mPrefs.edit().putInt("status", status).apply();
                selectGender();

                if(stUserGender == null || stSelectedCountry == null || stUserBirthdate== null){
                    if(stUserBirthdate == null){
                        tvBirthdate.setError("Please select your birthdate");
                    }
                    if(stSelectedCountry == null){
                        tvCountry.setError("Please select your country");
                    }
                    if(stUserGender == null){
                        tvGender.setError("Please select your gender");
                    }
                    Toast.makeText(UserInfo_Activity.this, "Field(s) missing!", Toast.LENGTH_SHORT).show();
                } else {
                    addInfoToDatabase(); //set register_process = 1, and add user info

                    saveUserModel(); //Save info in UserInfo Model Object locally via sharedPref

                    Intent doneIntent = new Intent(UserInfo_Activity.this, MainFeature_Activity.class);
                    startActivity(doneIntent);
                }
            }
        });
    }

    private void addInfoToDatabase() {
        DatabaseReference registerStatusRef = secondaryDatabase.getReference("register_process");
        DatabaseReference userInfoRef = secondaryDatabase.getReference("users");

        registerStatusRef.child(userInfo.getUserId()).child("status").setValue("1");
        userInfoRef.child(userInfo.getUserId()).setValue(userInfo);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please complete the registration process", Toast.LENGTH_SHORT).show();
    }

    private void selectGender() {
        if(rbMale.isChecked()){
            stUserGender = "Male";
        } else if (rbFemale.isChecked()){
            stUserGender = "Female";
        } else if (rbOther.isChecked()) {
            stUserGender = "Other";
        }
        userInfo.setGender(stUserGender);
    }

    private void calendarPupUpSelectBirthdate() {
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UserInfo_Activity.this, AlertDialog.THEME_HOLO_DARK, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void countrySelection() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, countries);
        spiCountries.setAdapter(adapter);

        spiCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stSelectedCountry = spiCountries.getSelectedItem().toString();
                userInfo.setCountry(stSelectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stSelectedCountry = null;
            }
        });
    }

    private void setViewElements() {
        spiCountries = findViewById(R.id.sp_countries);
        etBirthdate = findViewById(R.id.et_birthdate);
        doneButton = findViewById(R.id.bt_done);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);
        tvCountry = findViewById(R.id.tv_country);
        tvBirthdate = findViewById(R.id.tv_birthdate);
        tvGender = findViewById(R.id.tv_gender);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etBirthdate.setText(sdf.format(myCalendar.getTime()));
        stUserBirthdate = etBirthdate.getText().toString();
        userInfo.setBirth_date(stUserBirthdate);
    }

    private void userCollectionsFirebaseDatabase() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:87906663366:android:717d3ee683a9390e") // Required for Analytics.
                .setApiKey("AIzaSyA8muv77PUHFuk95K48RSGMCe441nHbvEI ") // Required for Auth.
                .setDatabaseUrl("https://collectonusers.firebaseio.com/") // Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this, options, "secondary");

        userFirebaseApp = FirebaseApp.getInstance("secondary");
        secondaryDatabase = FirebaseDatabase.getInstance(userFirebaseApp);
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent loginIntent = new Intent(this, Login_Activity.class);
        startActivity(loginIntent);
        finish();
    }
}

