package com.myapp.miguel.collectonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
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

public class ItemsList_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String selectedCollection, selectecTheme, collectionImagesURL;
    private SharedPreferences sharedPreferences;
    private ArrayList<Item> collectionList;
    private ArrayList<String> collectionImages;
    private int x;

    //
    private ListView lvCollectionHeader;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list_);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


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

        lvCollectionHeader = (ListView) findViewById(R.id.lvCountry);

        collectionList = new ArrayList<ItemsList_Activity.Item>();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.items_list_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void showData(DataSnapshot dataSnapshot) {
        //countryList = new ArrayList<Item>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedCollection = sharedPreferences.getString("selectedCollection", " ");
        selectecTheme = sharedPreferences.getString("selectedTheme", " ");
        collectionImages = new ArrayList<String>();
        myRef = database.getReference(selectedCollection);

        //for statement por si hay colecciones dentro de la colección
        for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").getChildren()){
            if(dataSnapshot.exists()){
                String subCollection = ds.getKey();
                collectionList.add(new SectionItem(subCollection));
                Log.d("subCollection", subCollection);

                collectionImages.add("https://tpc.googlesyndication.com/simgad/16094122936629946910");
                for (DataSnapshot dsItem : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").child(subCollection).child("collection").getChildren()){

                    String subItem = String.valueOf(dsItem.child("Name").getValue());
                    collectionList.add(new EntryItem(subItem));

                    collectionImagesURL = String.valueOf(dsItem.child("ImageURL").getValue());
                    collectionImages.add(collectionImagesURL);

                    Log.d("subCollectionItems", subItem);
                    Log.d("subCollectionURL", collectionImagesURL);

                }

                // set adapter
                final CountryAdapter adapter = new CountryAdapter(this, collectionList);
                lvCollectionHeader.setAdapter(adapter);
                lvCollectionHeader.setTextFilterEnabled(true);
                x++;
            }
        }

        //for statement si no hay colecciones dentro de la colección (caso normal).
        for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collection").getChildren()){
            if(dataSnapshot.exists()){
                Log.d("collection", ds.getKey());
            }
        }

//
//        collectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent itemsIntent = new Intent(CollectionsList_Activity.this, ItemsList_Activity.class);
//                startActivity(itemsIntent); //Fragment a Activity con intent
//
//                //Todo: save selection to go on to // themes -> collection -> SUB-COLLECTIONS&ARTICLES//
//
//                String selectedCollection = stringCollectionArray[i];
//
//                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
//                sharedPreferences.edit().putString("selectedCollection", selectedCollection).apply();
//
//                Toast.makeText(CollectionsList_Activity.this, (CharSequence) selectedCollection + " SELECTED", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent loginIntent = new Intent(this, Login_Activity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * row item
     */
    public interface Item {
        public boolean isSection();
        public String getTitle();
    }
    /**
     * Section Item
     */
    public class SectionItem implements Item {
        private final String title;

        public SectionItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
        
        @Override
        public boolean isSection() {
            return true;
        }
    }
    /**
     * Entry Item
     */
    public class EntryItem implements Item {
        public final String title;

        public EntryItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public boolean isSection() {
            return false;
        }
    }
    /**
     * Adapter
     */
    public class CountryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Item> item;
        private ArrayList<Item> originalItem;
        private ImageView itemImg;


        public CountryAdapter() {
            super();
        }

        public CountryAdapter(Context context, ArrayList<Item> item) {
            this.context = context;
            this.item = item;
            //this.originalItem = item;
        }

        @Override
        public int getCount() {
            return item.size();
        }

        @Override
        public Object getItem(int position) {
            return item.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (item.get(position).isSection()) {
                // if section header
                convertView = inflater.inflate(R.layout.layout_section, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvSectionTitle);
                tvSectionTitle.setText(((SectionItem) item.get(position)).getTitle());
            }
            else
            {
                // if item
                String[] imageURLArray = new String[collectionImages.size()];
                imageURLArray = collectionImages.toArray(imageURLArray);  //url´s en un arreglo.
                Log.d("arrayURL", String.valueOf(imageURLArray));

                convertView = inflater.inflate(R.layout.layout_item, parent, false);
                TextView tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
                tvItemTitle.setText(((EntryItem) item.get(position)).getTitle());

                //Todo: imagenes se desfasa por los headers 
                itemImg = (ImageView) convertView.findViewById(R.id.itemImg);
                Picasso.get().load(imageURLArray[position]).into(itemImg); //load image form URL arrays
            }
            return convertView;
        }
    }

}
