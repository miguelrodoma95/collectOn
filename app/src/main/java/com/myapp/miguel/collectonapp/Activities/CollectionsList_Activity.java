package com.myapp.miguel.collectonapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.miguel.collectonapp.Adapters.CollectionsAdapter;
import com.myapp.miguel.collectonapp.R;

import java.util.ArrayList;

public class CollectionsList_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar = null;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private String selectedTheme;
    private ListView collectionsListView;
    private ArrayList<String> collectionArray;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_list);

        drawerAndToolbarSettings();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedTheme = sharedPreferences.getString("selectedTheme", "error");
        ((AppCompatActivity)this).getSupportActionBar().setTitle(selectedTheme + " Collections");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                populateList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to get data.", error.toException());
            }
        });
    }

    private void populateList(DataSnapshot dataSnapshot) {
        collectionArray = new ArrayList<String>();
        myRef = database.getReference(selectedTheme);

        for(DataSnapshot ds : dataSnapshot.child(selectedTheme).child("Categories").getChildren()){
            String collectionName = ds.getKey();

            collectionArray.add(collectionName);
        }
        collectionsListAdapter();
    }

    private void collectionsListAdapter() {
        collectionsListView = findViewById(R.id.collectionsListView);
        CollectionsAdapter customAdapter = new CollectionsAdapter(CollectionsList_Activity.this, collectionArray);
        collectionsListView.setAdapter(customAdapter);

        collectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
                sharedPreferences.edit().putString("selectedCollection", collectionArray.get(i)).apply();

                Intent itemsIntent = new Intent(CollectionsList_Activity.this, ItemsList_Activity.class);
                startActivity(itemsIntent);
            }
        });
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

    private void drawerAndToolbarSettings() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_bottom_navigation view item clicks here.
        int id=item.getItemId();
        switch (id){
            case R.id.Profile:
                Intent profileIntent = new Intent(CollectionsList_Activity.this, UserProfileSettings_Activity.class);
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
