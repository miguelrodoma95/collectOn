package com.myapp.miguel.collectonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.myapp.miguel.collectonapp.Model.UserInfo;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


public class UserProfileSettings_Activity extends AppCompatActivity {

    private Button doneBT;
    private EditText etUserName, etCountry, etCity, etSex, etEmail;
    private FirebaseDatabase secondaryDatabase;
    private FirebaseApp userFirebaseApp;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String defaultUsername, defaultCountry, defaultSex, defaultEmail, defaultBirthdate;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_settings_);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("userInfo", "");
        userInfo = gson.fromJson(json, com.myapp.miguel.collectonapp.Model.UserInfo.class);

        setViewContent();
        defaultValues();
        dataToModel();
    }

    private void saveUserModel() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        prefsEditor.putString("userInfo", json);
        prefsEditor.commit();
    }

    private void defaultValues() {
        userFirebaseApp = FirebaseApp.getInstance("secondary");
        secondaryDatabase = FirebaseDatabase.getInstance(userFirebaseApp);
        DatabaseReference userInfoRef = secondaryDatabase.getReference("users");

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                defaultUsername = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("userName").getValue(String.class);
                defaultCountry = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("country").getValue(String.class);
                defaultSex = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("gender").getValue(String.class);
                defaultEmail = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue(String.class);
                defaultBirthdate = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("birth_date").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void dataToModel() {
        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String country = etCountry.getText().toString();
                String sex = etSex.getText().toString();
                String email = etEmail.getText().toString();

                if(!userName.equals("")){
                    userInfo.setUserName(etUserName.getText().toString());
                } else {
                    userInfo.setUserName(defaultUsername);
                }
                if(!country.equals("")){
                    userInfo.setCountry(etCountry.getText().toString());
                } else {
                    userInfo.setCountry(defaultCountry);
                }
                if(!sex.equals("")){
                    userInfo.setGender(etSex.getText().toString());
                } else {
                    userInfo.setGender(defaultSex);
                }
                if(!email.equals("")){
                    userInfo.setEmail(etEmail.getText().toString());
                } else {
                    userInfo.setEmail(defaultEmail);
                }
                userInfo.setBirth_date(defaultBirthdate);

                DatabaseReference userInfoRef = secondaryDatabase.getReference("users");
                userInfoRef.child(userInfo.getUserId()).setValue(userInfo);

                saveUserModel();

                Intent mainIntent = new Intent(UserProfileSettings_Activity.this, MainFeature_Activity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private void setViewContent() {
        etUserName = findViewById(R.id.et_userName);
        etCity = findViewById(R.id.et_userCity);
        etCountry = findViewById(R.id.et_userCountry);
        etSex = findViewById(R.id.et_userSex);
        etEmail = findViewById(R.id.et_userEmail);
        doneBT = findViewById(R.id.doneButton);

        userFirebaseApp = FirebaseApp.getInstance("secondary");
        secondaryDatabase = FirebaseDatabase.getInstance(userFirebaseApp);
        DatabaseReference userInfoRef = secondaryDatabase.getReference("users");

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String userName = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("userName").getValue(String.class);
                etUserName.setHint(userName);
                String country = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("country").getValue(String.class);
                etCountry.setHint(country);
                String gender = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("gender").getValue(String.class);
                etSex.setHint(gender);
                String email = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue(String.class);
                etEmail.setHint(email);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }
}
