package com.myapp.miguel.collectonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.GraphRequest.TAG;

public class CollectionsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawer;
    Toolbar toolbar = null;
    NavigationView navigationView;
    SharedPreferences sharedPreferences;
    String selectedTheme;
    ListView collectionsListView;
    String[] stringCollectionArray;
    private ArrayList<String> collectionArray;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedTheme = sharedPreferences.getString("selectedTheme", "error");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Log.d("selected theme", selectedTheme);

//        getActionBar().setTitle(selectedTheme);
//        getSupportActionBar().setTitle(selectedTheme);

        ((AppCompatActivity)this).getSupportActionBar().setTitle(selectedTheme + " Collections");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        collectionArray = new ArrayList<String>();
        myRef = database.getReference(selectedTheme);
        for(DataSnapshot ds : dataSnapshot.child(selectedTheme).child("Categories").getChildren()){

            collectionsListView = findViewById(R.id.collectionsListView);
            String collectionName = ds.getKey();

            Log.d(TAG, "Lista de colecciones " + collectionName);

            collectionArray.add(collectionName);
        }
        Log.d("Colecciones array", String.valueOf(collectionArray));

        stringCollectionArray = new String[collectionArray.size()];
        stringCollectionArray = collectionArray.toArray(stringCollectionArray);

        CustomAdapter customAdapter = new CustomAdapter();
        collectionsListView.setAdapter(customAdapter);  //Custom ListView. Todo: onClickListener que me mande a Fragmento con la info del tema seleccionado.

        collectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                Intent collectionsIntent = new Intent(getActivity(), CollectionsListActivity.class);
//                startActivity(collectionsIntent); //Fragment a Activity con intent

                //Todo: save selection to go on to // themes -> COLLECTIONS -> sub-collections -> Articles//

                String selectedCollection = stringCollectionArray[i];

                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
                sharedPreferences.edit().putString("selectedCollection", selectedCollection).apply();

                Toast.makeText(CollectionsListActivity.this, (CharSequence) selectedCollection + " SELECTED", Toast.LENGTH_SHORT).show();
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_bottom_navigation view item clicks here.
        int id=item.getItemId();
        switch (id){

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
            // this is done, now let us go and intialise the home page.
            // after this lets start copying the above.
            // FOLLOW MEEEEE>>>
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return collectionArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            view = getLayoutInflater().inflate(R.layout.custom_collections_list_view,null);

            TextView textView = view.findViewById(R.id.textTema);

            stringCollectionArray = new String[collectionArray.size()];
            stringCollectionArray = collectionArray.toArray(stringCollectionArray);
            Log.d("array de temas", String.valueOf(stringCollectionArray));

            textView.setText(stringCollectionArray[position]);
            return view;
        }
    }

}
