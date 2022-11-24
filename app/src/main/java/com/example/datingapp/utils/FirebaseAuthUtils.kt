package com.example.datingapp.utils

import com.google.firebase.auth.FirebaseAuth

//Uid를 받아오는 함수가 구현되어 있는 파일
class FirebaseAuthUtils {

    companion object {

        private lateinit var auth : FirebaseAuth

        fun getUid() : String {
            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
        }
    }
}