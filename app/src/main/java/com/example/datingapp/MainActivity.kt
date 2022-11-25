package com.example.datingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.setting.SettingActivity
import com.example.datingapp.slider.CardStackAdapter
import com.example.datingapp.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager //뷰를 어떻게 보일건지

    private val TAG = "MainActivity"

    //닉네임을 담아둘 리스트
    private val usersDataList = mutableListOf<UserDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //이미지뷰를 클릭했을 때 로그아웃 되게 하는 기능
        val setting = findViewById<ImageView>(R.id.settingIcon)
        setting.setOnClickListener{

            //로그아웃하기
//            val auth = Firebase.auth
//            auth.signOut()
//
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })

        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        cardStackView.layoutManager = manager //레이아웃 매니저에 우리가 만든 manager를 연결해줌
        cardStackView.adapter = cardStackAdapter

        getUserDataList()
    }

    private fun getUserDataList(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                //dataSnapshot을 하면 유저들의 데이터 덩어리가 하나로 쫙 나옴
//                Log.d(TAG, dataSnapshot.toString())

                //이 데이터 덩어리를 반복문으로 하나하나 뽑아올 것
                for (dataModel in dataSnapshot.children) {

                    //리스트에 유저 추가
                    val user = dataModel.getValue(UserDataModel::class.java)
//                    Log.d(TAG, user?.nickname.toString()) //nickname 출력
                    usersDataList.add(user!!)

                }
                //데이터 동기화 시키기
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }
}