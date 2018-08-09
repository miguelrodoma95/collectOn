package com.myapp.miguel.collectonapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import com.myapp.miguel.collectonapp.R

//Kotlin
class ExchangeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exchange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = activity!!.findViewById<Button>(R.id.btn_buy)
        button.setOnClickListener {
            Toast.makeText(activity, "Not available!", Toast.LENGTH_SHORT).show()
        }
    }
}