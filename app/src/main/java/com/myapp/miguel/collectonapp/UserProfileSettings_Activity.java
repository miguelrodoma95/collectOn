package com.myapp.miguel.collectonapp;

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
        dataToModel();
    }

    private void dataToModel() {
        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.setUserName(etUserName.getText().toString());
                userInfo.setCountry(etCountry.getText().toString());
                userInfo.setGender(etSex.getText().toString());
                userInfo.setEmail(etEmail.getText().toString());

                DatabaseReference userInfoRef = secondaryDatabase.getReference("users");
                userInfoRef.child(userInfo.getUserId()).setValue(userInfo);
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
