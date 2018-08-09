package com.myapp.miguel.collectonapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.myapp.miguel.collectonapp.R

class MyCollectionFragment : Fragment() {

    private var btnOwnCollections: Button? = null
    private var btnCustomCollections:Button? = null
    private var btnWishList:Button? = null
    private var tvCollectorName: TextView? = null
    private var userName : String? = null
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirebaseApp: FirebaseApp = FirebaseApp.getInstance("secondary")
    private var secondaryDatabase:FirebaseDatabase? = FirebaseDatabase.getInstance(userFirebaseApp)
    private var myRef: DatabaseReference? = secondaryDatabase?.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvCollectorName = activity?.findViewById<View>(R.id.tv_collectorName) as TextView?

        myRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds1 in dataSnapshot.child(mAuth.currentUser!!.uid).children) {
                    tvCollectorName?.text = ds1.child("userName").value!!.toString()
                    var userCountr : String = ds1.child("country").value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_collections, container, false)
    }
}
