package com.example.datingapp.utils

import com.example.datingapp.utils.FirebaseRef.Companion.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {
    // Write a message to the database
//   val database = Firebase.database
//   val myRef = database.getReference("message")
//   myRef.setValue("Hello, World!")

    companion object {

        val database = Firebase.database

        val userInfoRef = database.getReference("userinfo")
    }
}