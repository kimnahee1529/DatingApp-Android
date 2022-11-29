package com.example.datingapp.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.datingapp.R
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.message.MyLikeListActivity
import com.example.datingapp.message.MyMsgActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // 1. 앱에서 코드로 notification 띄우기
        // 2. Firebase 콘솔에서 앱을 사용하는 모든 사용자에게 push 보내기
        // 3. Firebase 콘솔에서 특정 사용자에게 메시지 보내기
        // 4. 앱에서 직접 다른 사람에게 푸시메시지 보내기

        val mybtn = findViewById<Button>(R.id.myPageBtn)
        mybtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val mylikeBtn = findViewById<Button>(R.id.mylikeList)
        mylikeBtn.setOnClickListener {
            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)
        }

        val myMsg = findViewById<Button>(R.id.myMsg)
        myMsg.setOnClickListener {
            val intent = Intent(this, MyMsgActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
    }
}