package com.example.datingapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.datingapp.LoginActivity
import com.example.datingapp.R

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val loginBtn : Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val intent = Intent( this, LoginActivity::class.java)
            startActivity(intent)
        }

        val joinBtn = findViewById<Button>(R.id.joinBtn)
//        val joinBtn : Button = findViewById(R.id.joinBtn) //이렇게 해도 되고 gradle파일의 extension을 해도 됨
        joinBtn.setOnClickListener {

            val intent = Intent( this, JoinActivity::class.java)
            startActivity(intent)

        }

        //extensions을 썼을 때
//        joinBtn.setOnClickListener {
//            val intent = Intent(this, JoinActivity::class.java)
//            startActivity(intent)
//        }
    }
}