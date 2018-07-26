package com.myapp.miguel.collectonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.myapp.miguel.collectonapp.Model.UserInfo;
import com.myapp.miguel.collectonapp.mainFragments.CompleteListFragment;
import com.myapp.miguel.collectonapp.mainFragments.MyCollectionFragment;
import com.myapp.miguel.collectonapp.mainFragments.ExchangeFragment;
import com.myapp.miguel.collectonapp.mainFragments.CommunityFragment;


public class MainFeature_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawer;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase secondaryDatabase;
    private FirebaseApp userFirebaseApp;
    Fragment completeListFragment, myCollectionFragment, exchangeFragment,communityFragment;
    NavigationView navigationView;
    Toolbar toolbar = null;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        //UserInfo Object
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("userInfo", "");
        userInfo = gson.fromJson(json, UserInfo.class);

        Log.d("pruebaUser", userInfo.getCountry() + " " + userInfo.getBirth_date() + " " + userInfo.getGender()
                + " " + userInfo.getUserName() + " " + userInfo.getEmail() + " " + userInfo.getUserId());

        drawerAndToolbarViewContents();

        completeListFragment = new CompleteListFragment();
        myCollectionFragment = new MyCollectionFragment();
        exchangeFragment = new ExchangeFragment();
        communityFragment = new CommunityFragment();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, completeListFragment).commit();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationBar();
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

    private void drawerAndToolbarViewContents() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

    }

    private void bottomNavigationBar() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();

                if (item.getItemId() == R.id.navigation_home) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, completeListFragment).commit();
                } else if (item.getItemId() == R.id.navigation_dashboard) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, myCollectionFragment).commit();
                } else if (item.getItemId() == R.id.navigation_notifications) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, exchangeFragment).commit();
                } else if (item.getItemId() == R.id.navigation_community) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, communityFragment).commit();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_bottom_navigation view item clicks here.
        int id=item.getItemId();
        switch (id){
            case R.id.Profile:
                Intent profileIntent = new Intent(MainFeature_Activity.this, UserProfileSettings_Activity.class);
                startActivity(profileIntent);
                break;
            case R.id.Facebook:
                break;
            case R.id.Twitter:
                break;
            case R.id.Instagram:
                break;
            case R.id.About:
                break;
            case R.id.Logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                signOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent loginIntent = new Intent(this, Login_Activity.class);
        startActivity(loginIntent);
        finish();
    }
}