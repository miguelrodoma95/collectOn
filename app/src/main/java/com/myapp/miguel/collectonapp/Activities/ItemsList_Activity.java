package com.myapp.miguel.collectonapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.miguel.collectonapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsList_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnOwn, btnWant, btnHunt;
    private DatabaseReference myRef;
    private FirebaseDatabase database, secondaryDatabase;
    private FirebaseApp userFirebaseApp;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String selectedCollection, selectecTheme, selectedSubCollection, selectedThemeLogo, subCollection, collectionImagesURL, ownItemKey,
            wantItemKey;
    private SharedPreferences sharedPreferences;
    private ArrayList<Item> collectionList;
    private ArrayList<String> collectionImages, ownItemsList, wantItemsList;
    private ImageView collectionBanner;
    private TextView collectionName;
    private ListView lvCollectionHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list_);

        collectionList = new ArrayList<ItemsList_Activity.Item>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedCollection = sharedPreferences.getString("selectedCollection", " ");
        selectecTheme = sharedPreferences.getString("selectedTheme", " ");
        selectedThemeLogo = sharedPreferences.getString("themeLogo", " ");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        setViewElements();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to get data", error.toException());
            }
        });
    }

    private void setViewElements() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((AppCompatActivity)this).getSupportActionBar().setTitle(selectecTheme + " - " + selectedCollection  + "  Collection");

        collectionBanner = findViewById(R.id.collectionBanner);
        collectionName = findViewById(R.id.collectionName);
        lvCollectionHeader = (ListView) findViewById(R.id.lvCountry);

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_bottom_navigation view item clicks here.
        int id=item.getItemId();
        switch (id){
            case R.id.Profile:
                Intent profileIntent = new Intent(ItemsList_Activity.this, UserProfileSettings_Activity.class);
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

    private void showData(DataSnapshot dataSnapshot) {
        //countryList = new ArrayList<Item>();

        collectionImages = new ArrayList<String>();
        myRef = database.getReference(selectedCollection);

        String bannerURL = dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("ImageBox").getValue().toString();
        collectionName.setText(selectedCollection);
        Picasso.get().load(bannerURL).into(collectionBanner); //load image form URL arrays

        //for statement por si hay colecciones dentro de la colección
        for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").getChildren()){
            if(dataSnapshot.exists()){
                subCollection = ds.getKey();
                collectionList.add(new SectionItem(subCollection));
                collectionImages.add("https://tpc.googlesyndication.com/simgad/16094122936629946910"); //img para los espacios de headers

                for (DataSnapshot dsItem : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").child(subCollection).child("collection").getChildren()){
                    String subItem = String.valueOf(dsItem.child("Name").getValue());
                    collectionList.add(new EntryItem(subItem));
                    collectionImagesURL = String.valueOf(dsItem.child("ImageURL").getValue());
                    collectionImages.add(collectionImagesURL);
                }

                // set adapter
                final CountryAdapter adapter = new CountryAdapter(this, collectionList);
                lvCollectionHeader.setAdapter(adapter);
                lvCollectionHeader.setTextFilterEnabled(true);
            }
        }
        //for statement si no hay colecciones dentro de la colección (caso normal).
        for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collection").getChildren()){
            if(dataSnapshot.exists()){
                String Item = String.valueOf(ds.child("Name").getValue());
                collectionList.add(new EntryItem(Item));
                Log.d("collection", Item);
                collectionImagesURL = String.valueOf(ds.child("ImageURL").getValue());
                collectionImages.add(collectionImagesURL);

                Log.d("collectionURL", collectionImagesURL);
            }
            final CountryAdapter adapter = new CountryAdapter(this, collectionList);
            lvCollectionHeader.setAdapter(adapter);
            lvCollectionHeader.setTextFilterEnabled(true);
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (item.get(position).isSection()) {
                // if section header
                convertView = inflater.inflate(R.layout.layout_section, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvSectionTitle);
                tvSectionTitle.setText(((SectionItem) item.get(position)).getTitle());
            }
            else {
                convertView = inflater.inflate(R.layout.layout_item, parent, false);

                TextView tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
                tvItemTitle.setText(((EntryItem) item.get(position)).getTitle());

                itemImg = (ImageView) convertView.findViewById(R.id.itemImg);
                Picasso.get().load(collectionImages.get(position)).into(itemImg); //load image form URL arrays

                clickOwnWantHunt(convertView, position);
            }
            return convertView;
        }

        private void clickOwnWantHunt(View convertView, final int position) {
            //Todo: sacar de qué sub colección es el item seleccionado
            btnOwn = (Button) convertView.findViewById(R.id.btnOwn);
            btnWant = (Button) convertView.findViewById(R.id.btnWant);
            btnHunt = (Button) convertView.findViewById(R.id.btnHunt);

            userFirebaseApp = FirebaseApp.getInstance("secondary");
            secondaryDatabase = FirebaseDatabase.getInstance(userFirebaseApp);

            final String selectedItem = item.get(position).getTitle();

            ownItemsList = new ArrayList<>();
            wantItemsList = new ArrayList<>();

            btnOwn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference existingItemRef = secondaryDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
                    myRef = database.getReference();

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").getChildren()){
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot ds2 : ds.child("collection").getChildren()){
                                        String subCollection = ds2.child("Name").getValue().toString();

                                        if(subCollection.equals(selectedItem)){
                                            selectedSubCollection = ds.getKey();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    existingItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds1 : dataSnapshot.child("Collections").child("Own").child("Items").getChildren()) {
                                ownItemKey = ds1.getKey();
                                for (DataSnapshot ds2 : dataSnapshot.child("Collections").child("Own").child("Items")
                                        .child(ownItemKey).getChildren()) {
                                    if (ds2.getKey().equals("ItemName")) {
                                        ownItemsList.add(ds2.getValue().toString());
                                    }
                                }
                            }
                            if(!ownItemsList.contains(item.get(position).getTitle())){
                                DatabaseReference userInfoRef = secondaryDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                                        .child("Collections").child("Own").child("Items").push();
                                userInfoRef.child("CollectionLogo").setValue(selectedThemeLogo);
                                userInfoRef.child("ItemMainCollections").setValue(selectecTheme);
                                userInfoRef.child("ItemName").setValue(item.get(position).getTitle());
                                userInfoRef.child("ItemSeriesCollection").setValue(selectedCollection);
                                if(selectedSubCollection == null){
                                    userInfoRef.child("ItemSection").setValue(selectedCollection);
                                } else {
                                    userInfoRef.child("ItemSection").setValue(selectedSubCollection);
                                }

                                Toast.makeText(ItemsList_Activity.this, item.get(position).getTitle() + " added to Owned Collections!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ItemsList_Activity.this, item.get(position).getTitle() + " is already in your Owned Collections list!", Toast.LENGTH_SHORT).show();
                            }

                            ownItemsList.clear();
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            });

            btnWant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference existingItemRef = secondaryDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
                    myRef = database.getReference();


                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.child(selectecTheme).child("Categories").child(selectedCollection).child("collections").getChildren()){
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot ds2 : ds.child("collection").getChildren()){
                                        String subCollection = ds2.child("Name").getValue().toString();

                                        if(subCollection.equals(selectedItem)){
                                            selectedSubCollection = ds.getKey();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    existingItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds1 : dataSnapshot.child("Collections").child("Want").child("Items").getChildren()) {
                                wantItemKey = ds1.getKey();
                                for (DataSnapshot ds2 : dataSnapshot.child("Collections").child("Want").child("Items")
                                        .child(wantItemKey).getChildren()) {
                                    if (ds2.getKey().equals("ItemName")) {
                                        wantItemsList.add(ds2.getValue().toString());
                                    }
                                }
                            }
                            if(!wantItemsList.contains(item.get(position).getTitle())){
                                DatabaseReference userInfoRef = secondaryDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                                        .child("Collections").child("Want").child("Items").push();
                                userInfoRef.child("ItemMainCollections").setValue(selectecTheme);
                                userInfoRef.child("ItemName").setValue(item.get(position).getTitle());
                                userInfoRef.child("ItemSeriesCollection").setValue(selectedCollection);
                                if(selectedSubCollection == null){
                                    userInfoRef.child("ItemSection").setValue(selectedCollection);
                                } else {
                                    userInfoRef.child("ItemSection").setValue(selectedSubCollection);
                                }

                                Toast.makeText(ItemsList_Activity.this, item.get(position).getTitle() + " added to Wish List!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ItemsList_Activity.this, item.get(position).getTitle() + " is already in your Wish List!", Toast.LENGTH_SHORT).show();
                            }
                            wantItemsList.clear();
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            });

            btnHunt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "'Hunt' feature coming soon", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
