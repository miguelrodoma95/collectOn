package com.myapp.miguel.collectonapp.mainFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.myapp.miguel.collectonapp.R;
import com.myapp.miguel.collectonapp.completeListFragments.CollectionsFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.GraphRequest.TAG;

public class CompleteListFragment extends Fragment {

    final Fragment collectionsFragment = new CollectionsFragment();

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference myRef;
    private ImageView imageView;
    private ListView mListView;
    private ArrayList<String> collectionArray;
    private ArrayList<String> collectionImages;
    private SharedPreferences sharedPreferences;
    private String collectionName;
    String[] stringCollectionArray;
    private String collectionImagesURL;
    private ListView mainListView;
    int[] IMAGES = {R.drawable.dccomics, R.drawable.disney, R.drawable.hotwheels, R.drawable.funkopop, R.drawable.got, R.drawable.hotwheels, R.drawable.marvel, R.drawable.nintendo, R.drawable.starwaarslogo};

    //
    ImageView imageViewTry;
    String url = "https://firebasestorage.googleapis.com/v0/b/collecton-a2bfb.appspot.com/o/Main%20Logos%2FHotWheels%20Logo.png?alt=media&token=77722b0a-3d21-4cbc-8316-787c7628cdb7";
    //

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
        collectionImages = new ArrayList<String>();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            mainListView = (ListView)getActivity().findViewById(R.id.mainListView);
            collectionName = ds.getKey();
            //collectionImagesURL = myRef.child("Categories").toString();
            collectionImagesURL = dataSnapshot.child(collectionName).child("Logo").getValue().toString();

            ///
            Log.d(TAG, "Collection name: " + collectionName);
            Log.d("url de " + collectionName, " : " + collectionImagesURL);
            collectionArray.add(collectionName);
            collectionImages.add(collectionImagesURL);
        }

        CustomAdapter customAdapter = new CustomAdapter();
        mainListView.setAdapter(customAdapter);  //Custom ListView. Todo: onClickListener que me mande a Fragmento con la info del tema seleccionado.

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainer, collectionsFragment).commit();  //cambio de fragment.

                Intent collectionsIntent = new Intent(getActivity(), CollectionsActivity.class);
                startActivity(collectionsIntent); //Fragment a Activity con intent

                //Todo: save selection to go on to // themes -> COLLECTIONS -> sub-collections -> Articles//

                String selectedTheme = stringCollectionArray[i];
                sharedPreferences.edit().putString("selectedTheme", selectedTheme).apply();
//                String selectedThemeTry = sharedPreferences.getString("selectedTheme", "error");// prueba de sharedPref
//                Log.d("selecionaste", selectedThemeTry);


                //Log.i("person selected", selectedTheme);
                Toast.makeText(getActivity(), (CharSequence) selectedTheme + " SELECTED", Toast.LENGTH_SHORT).show();
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
            Log.d("array de temas", String.valueOf(stringCollectionArray));

            String[] imageURLArray = new String[collectionImages.size()];
            imageURLArray = collectionImages.toArray(imageURLArray);  //url´s en un arreglo.
            Log.d("array de url", String.valueOf(imageURLArray));
            //System.out.println("arr: " + Arrays.toString(imageURLArray));

            //for(String s : stringCollectionArray)

            Picasso.get().load(imageURLArray[position]).into(imageView); //load image form URL arrays.
            //imageView.setImageResource(IMAGES[position]);
            textView.setText(stringCollectionArray[position]);
            return view;
        }
    }

}
