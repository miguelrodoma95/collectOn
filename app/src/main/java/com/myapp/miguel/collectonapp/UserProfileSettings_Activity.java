package com.myapp.miguel.collectonapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.myapp.miguel.collectonapp.Model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class UserProfileSettings_Activity extends AppCompatActivity {

    private Button doneButton;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private EditText etBirthdate;
    private FirebaseDatabase secondaryDatabase;
    private FirebaseApp userFirebaseApp;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Gson gson;
    private RadioButton rbMale, rbFemale, rbOther;
    private Spinner spiCountries;
    private String stSelectedCountry, stUserGender, stUserBirthdate;
    private TextView tvCountry, tvGender, tvBirthdate;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_settings_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        Gson gson = new Gson();

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("userInfo", "");
        userInfo = gson.fromJson(json, UserInfo.class);

        setViewElements();
        countrySelection();
        calendarPupUpSelectBirthdate();
        DoneButton();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addInfoToDatabase() {
        userFirebaseApp = FirebaseApp.getInstance("secondary");
        secondaryDatabase = FirebaseDatabase.getInstance(userFirebaseApp);
        DatabaseReference userInfoRef = secondaryDatabase.getReference("users");
        //Todo: get id para actualizar info
        userInfoRef.child(userInfo.getUserId()).setValue(userInfo);
    }

    private void DoneButton() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGender();

                gson = new Gson();

                String userInfoObjAsString = gson.toJson(userInfo);

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
                    Toast.makeText(UserProfileSettings_Activity.this, "Field(s) missing!", Toast.LENGTH_SHORT).show();
                } else {
                    addInfoToDatabase(); //set register_process = 1, and add user info

                    Intent doneIntent = new Intent(UserProfileSettings_Activity.this, MainFeature_Activity.class);
                    doneIntent.putExtra("userInfoObj", userInfoObjAsString);
                    startActivity(doneIntent);
                }
            }
        });
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
                new DatePickerDialog(UserProfileSettings_Activity.this, AlertDialog.THEME_HOLO_DARK, date, myCalendar.get(Calendar.YEAR),
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

        //Todo: Poner selección en el país de que esté en firebase (get)
        /*int spinnerPosition = adapter.getPosition(userInfo.getCountry());
        spiCountries.setSelection(spinnerPosition);*/
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
}
