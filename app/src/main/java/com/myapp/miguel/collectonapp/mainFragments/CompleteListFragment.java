package com.myapp.miguel.collectonapp.mainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.myapp.miguel.collectonapp.R;

import java.util.ArrayList;

import static com.facebook.GraphRequest.TAG;

public class CompleteListFragment extends Fragment {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference myRef;
    private ListView mListView;
    private ArrayList<String> collectionArray;
    private String collectionName;
    private ListView mainListView;
    int[] IMAGES = {R.drawable.dccomics, R.drawable.disney, R.drawable.hotwheels, R.drawable.funkopop, R.drawable.got, R.drawable.hotwheels, R.drawable.marvel, R.drawable.nintendo, R.drawable.starwaarslogo};


    public CompleteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mainListView = (ListView)getActivity().findViewById(R.id.mainListView);


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
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
            collectionName = ds.getKey();

            //display all the information
            Log.d(TAG, "Collection name: " + collectionName);
            collectionArray.add(collectionName);
        }

        CustomAdapter customAdapter = new CustomAdapter();
        mainListView.setAdapter(customAdapter);

//        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,collectionArray);
//        mainListView.setAdapter(adapter);
//        Log.d(TAG, "Collection array: " + collectionArray);
//
//        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                int itemPosition = i;
//                String  itemValue    = (String) mainListView.getItemAtPosition(i); //Todo: enviar itemValue para definir que childs desplegar
//
//
//                Toast.makeText(getContext(), "Position : "+itemPosition+"  List Item : " +itemValue , Toast.LENGTH_LONG).show();            }
//        });
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

            ImageView imageView = view.findViewById(R.id.imageView);
            TextView textView = view.findViewById(R.id.textTema);

            String[] stringArray = new String[collectionArray.size()];
            stringArray = collectionArray.toArray(stringArray);

            for(String s : stringArray)

            imageView.setImageResource(IMAGES[position]);
            textView.setText(stringArray[position]);
            return view;
        }
    }

}
