package com.myapp.miguel.collectonapp.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.myapp.miguel.collectonapp.Adapters.ThemesAdapter

import com.myapp.miguel.collectonapp.R

class MyOwnCollectionsThemes_Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private val mAuth = FirebaseAuth.getInstance()
    private var userFirebaseApp: FirebaseApp = FirebaseApp.getInstance("secondary")
    private var secondaryDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance(userFirebaseApp)
    private var myRef: DatabaseReference? = secondaryDatabase?.getReference("users")?.child(mAuth.currentUser?.uid!!)
            ?.child("Collections")?.child("Own")
    val ownThemesList : ArrayList<String> = ArrayList()
    val ownThemeLogoList : ArrayList<String> = ArrayList()
    var ownItemKey : String? = null
    lateinit var sharedPrefs : SharedPreferences
    val PREFS_VALSNAME = "com.myapp.miguel.collectonapp.Activities"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collections_themes_)

        drawerAndToolbarViewContents()

        sharedPrefs = this.getSharedPreferences(PREFS_VALSNAME,0)
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

        themesList.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            sharedPrefs.edit().putString("ownTheme", ownThemesList[i]).apply()
            Toast.makeText(this, "${ownThemesList[i]} selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun drawerAndToolbarViewContents() {

        val drawer : DrawerLayout? = findViewById(R.id.drawer_layout) as? DrawerLayout
        val toolbar : Toolbar? = findViewById(R.id.toolbar) as? Toolbar

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.addDrawerListener(toggle)
        toggle.syncState()
        
        var navigationView: NavigationView? = findViewById(R.id.nav_view) as? NavigationView
        navigationView?.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            when (id) {
                R.id.Profile -> {
                    val profileIntent = Intent(this, UserProfileSettings_Activity::class.java)
                    startActivity(profileIntent)
                }
                R.id.Facebook -> {
                }
                R.id.Twitter -> {
                }
                R.id.Instagram -> {
                }
                R.id.About -> {
                }
                R.id.Logout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    signOut()
                }
            }

            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            return true
        }

    fun signOut() {
        mAuth.signOut()
        LoginManager.getInstance().logOut()
        val loginIntent = Intent(this, Login_Activity::class.java)
        startActivity(loginIntent)
        userFirebaseApp.delete()
        finish()
    }
}
