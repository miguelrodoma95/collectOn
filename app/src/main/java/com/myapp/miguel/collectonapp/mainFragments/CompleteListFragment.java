package com.myapp.miguel.collectonapp.mainFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.myapp.miguel.collectonapp.CollectionsList_Activity;
import com.myapp.miguel.collectonapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.GraphRequest.TAG;

public class CompleteListFragment extends Fragment {


    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference myRef;
    private ImageView imageView;
    private ArrayList<String> collectionArray, collectionImages;
    private SharedPreferences sharedPreferences;
    private String collectionName, collectionImagesURL;
    String[] stringCollectionArray;
    private ListView mainListView;


    public CompleteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complete_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showData(DataSnapshot dataSnapshot) {
        collectionArray = new ArrayList<String>();
        collectionImages = new ArrayList<String>();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
            collectionName = ds.getKey();
            collectionImagesURL = dataSnapshot.child(collectionName).child("Logo").getValue().toString();

            ///
            collectionArray.add(collectionName);
            collectionImages.add(collectionImagesURL);
        }

        CustomAdapter customAdapter = new CustomAdapter();
        mainListView.setAdapter(customAdapter);  //Custom ListView. Todo: onClickListener que me mande a Fragmento con la info del tema seleccionado.

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent collectionsIntent = new Intent(getActivity(), CollectionsList_Activity.class);
                startActivity(collectionsIntent); //Fragment a Activity con intent

                String selectedTheme = stringCollectionArray[i];
                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
            }
        });
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
            view = getLayoutInflater().inflate(R.layout.custom_list_view,null);

            imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView = view.findViewById(R.id.textTema);

            stringCollectionArray = new String[collectionArray.size()];
            stringCollectionArray = collectionArray.toArray(stringCollectionArray);

            String[] imageURLArray = new String[collectionImages.size()];
            imageURLArray = collectionImages.toArray(imageURLArray);  //url´s en un arreglo.

            Picasso.get().load(imageURLArray[position]).into(imageView); //load image form URL arrays.
            textView.setText(stringCollectionArray[position]);
            return view;
        }
    }

}
