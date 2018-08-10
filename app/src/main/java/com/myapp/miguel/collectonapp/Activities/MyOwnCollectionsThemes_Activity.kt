package com.myapp.miguel.collectonapp.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.myapp.miguel.collectonapp.Adapters.ThemesAdapter

import com.myapp.miguel.collectonapp.R

class MyOwnCollectionsThemes_Activity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private var userFirebaseApp: FirebaseApp = FirebaseApp.getInstance("secondary")
    private var secondaryDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance(userFirebaseApp)
    private var myRef: DatabaseReference? = secondaryDatabase?.getReference("users")?.child(mAuth.currentUser?.uid!!)
            ?.child("Collections")?.child("Own")
    val ownThemesList : ArrayList<String> = ArrayList()
    val ownThemeLogoList : ArrayList<String> = ArrayList()
    var ownItemKey : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collections_themes_)

        listOfOwnedThemes()
    }

    private fun listOfOwnedThemes() {
        myRef?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds1 in dataSnapshot.child("Items").getChildren()) {
                    ownItemKey = ds1.key
                    for(ds2 in dataSnapshot.child("Items").child(ownItemKey.toString()).getChildren()){
                        if (ds2.key == "ItemMainCollections") {
                            if(!ownThemesList.contains(ds2.value)){
                                ownThemesList.add(ds2.value!!.toString())
                            }
                        }
                        if (ds2.key == "CollectionLogo") {
                            if(!ownThemeLogoList.contains(ds2.value)){
                                ownThemeLogoList.add(ds2.value!!.toString())
                            }
                        }
                    }
                }
                themesAdapter()
            }
        })
    }

    private fun themesAdapter() {
        var themesList : ListView = findViewById(R.id.mainListView)

        var themesAdapter = ThemesAdapter(this, ownThemesList, ownThemeLogoList)
        themesList.setAdapter(themesAdapter)
    }
}
