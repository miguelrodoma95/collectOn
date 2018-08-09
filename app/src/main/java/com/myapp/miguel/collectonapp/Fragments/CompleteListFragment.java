package com.myapp.miguel.collectonapp.Fragments;

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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.myapp.miguel.collectonapp.Adapters.ThemesAdapter;
import com.myapp.miguel.collectonapp.Activities.CollectionsList_Activity;
import com.myapp.miguel.collectonapp.R;

import java.util.ArrayList;

public class CompleteListFragment extends Fragment {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ArrayList<String> collectionArray, collectionImages;
    private SharedPreferences sharedPreferences;
    private String collectionName, collectionImagesURL;
    private ListView mainListView;


    public CompleteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                populateList(dataSnapshot);
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

    private void populateList(DataSnapshot dataSnapshot) {
        collectionArray = new ArrayList<String>();
        collectionImages = new ArrayList<String>();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            collectionName = ds.getKey();
            collectionImagesURL = dataSnapshot.child(collectionName).child("Logo").getValue().toString();

            collectionArray.add(collectionName);
            collectionImages.add(collectionImagesURL);
        }
        themesAdapter();
    }

    private void themesAdapter() {
        mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
        ThemesAdapter themesAdapter = new ThemesAdapter(getActivity(), collectionArray, collectionImages);
        mainListView.setAdapter(themesAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putString("selectedTheme", collectionArray.get(i)).apply();

                Intent collectionsIntent = new Intent(getActivity(), CollectionsList_Activity.class);
                startActivity(collectionsIntent); //Fragment a Activity con intent
            }
        });
    }
}
