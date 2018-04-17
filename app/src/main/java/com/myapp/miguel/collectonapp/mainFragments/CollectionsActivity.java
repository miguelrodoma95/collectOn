package com.myapp.miguel.collectonapp.mainFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.miguel.collectonapp.R;

import java.util.ArrayList;

import static com.facebook.GraphRequest.TAG;

public class CollectionsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference myRef;
    private ArrayList<String> collectionList;
    SharedPreferences sharedPreferences;
    String selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedTheme = sharedPreferences.getString("selectedTheme", "error");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Log.d("selected theme", selectedTheme);


        //collectionList = (ListView)findViewById(R.id.collectionList);

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
//        collectionArray = new ArrayList<String>();
//        collectionImages = new ArrayList<String>();
        myRef = database.getReference(selectedTheme);
        for(DataSnapshot ds : dataSnapshot.child(selectedTheme).child("Categories").getChildren()){
            //mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
            String collectionName = ds.getKey();
            //collectionImagesURL = myRef.child("Categories").toString();
            //String collectionImagesURL = dataSnapshot.child(collectionName).child("Logo").getValue().toString();

            ///
            Log.d(TAG, "Lista de colecciones " + collectionName);
            //Log.d("url de " + collectionName, " : " + collectionImagesURL);
//            collectionArray.add(collectionName);
//            collectionImages.add(collectionImagesURL);
        }

//        CompleteListFragment.CustomAdapter customAdapter = new CompleteListFragment.CustomAdapter();
//        mainListView.setAdapter(customAdapter);  //Custom ListView. Todo: onClickListener que me mande a Fragmento con la info del tema seleccionado.
//
//        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
////                FragmentManager fragmentManager = getFragmentManager();
////                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                fragmentTransaction.replace(R.id.fragmentContainer, collectionsFragment).commit();  //cambio de fragment.
//
//                Intent collectionsIntent = new Intent(getActivity(), CollectionsActivity.class);
//                startActivity(collectionsIntent); //Fragment a Activity con intent
//
//                //Todo: save selection to go on to // themes -> COLLECTIONS -> sub-collections -> Articles//
//
//                String selectedTheme = stringCollectionArray[i];
//                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
////                String selectedThemeTry = sharedPreferences.getString("selectedTheme", "error");// prueba de sharedPref
////                Log.d("selecionaste", selectedThemeTry);
//
//
//                //Log.i("person selected", selectedTheme);
//                Toast.makeText(getActivity(), (CharSequence) selectedTheme + " SELECTED", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
