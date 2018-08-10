package com.myapp.miguel.collectonapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.myapp.miguel.collectonapp.Activities.MyOwnCollectionsThemes_Activity

import com.myapp.miguel.collectonapp.R

//Kotlin

class MyCollectionFragment : Fragment() {

    private val mAuth = FirebaseAuth.getInstance()
    private var userFirebaseApp: FirebaseApp = FirebaseApp.getInstance("secondary")
    private var secondaryDatabase:FirebaseDatabase? = FirebaseDatabase.getInstance(userFirebaseApp)
    private var myRef: DatabaseReference? = secondaryDatabase?.getReference("users")?.child(mAuth.currentUser?.uid!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserName()

        var btnOwnCollect = activity?.findViewById<Button>(R.id.btn_ownCollections)

        btnOwnCollect?.setOnClickListener{
            val myOwnCollIntent = Intent(activity, MyOwnCollectionsThemes_Activity::class.java)
            startActivity(myOwnCollIntent)

            Toast.makeText(activity, "Go to owned collections", Toast.LENGTH_SHORT).show()
        }

    }

    private fun displayUserName() {
        var collectorName = activity!!.findViewById<TextView>(R.id.tv_collectorName)
        var userName: String
        var userLastName: String

        myRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userName = dataSnapshot.child("userName").value.toString()
                userLastName = dataSnapshot.child("userLastName").value.toString()

                collectorName.text = "$userName " + userLastName
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

