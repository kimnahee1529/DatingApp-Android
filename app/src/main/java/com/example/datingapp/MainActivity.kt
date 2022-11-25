package com.example.datingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.setting.SettingActivity
import com.example.datingapp.slider.CardStackAdapter
import com.example.datingapp.utils.FirebaseAuthUtils
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

    private var userCount = 0

    //현재 유저의 성별 정보
    private lateinit var currentUserGender : String

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //나와 다른 성별의 유저 받아오기
        //1. 일단 나의 성별을 알아야 함
        //2. 전체 유저 중에서 성별이 다른 사람의 정보를 가져오기

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
                if(direction == Direction.Right) {
//                    Toast.makeText(this@MainActivity, "right", Toast.LENGTH_SHORT).show()
                    //지금 넘기고 있는 사람의 uid값 알아내기
//                    Log.d(TAG, usersDataList[userCount].uid.toString())

                    userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                }

                if(direction == Direction.Left) {
//                    Toast.makeText(this@MainActivity, "left", Toast.LENGTH_SHORT).show()
                }

                userCount = userCount + 1

                if(userCount == usersDataList.count()) {
                    getUserDataList(currentUserGender)
                    Toast.makeText(this@MainActivity, "유저 새롭게 받아옵니다", Toast.LENGTH_LONG).show()
                }
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

//        getUserDataList()
        getMyUserData()
    }
    //내 정보 가져오기
    private fun getMyUserData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, dataSnapshot.toString())
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                //내 성별 가져오기
//                data?.gender.toString()
                Log.d(TAG, data?.gender.toString())

                currentUserGender = data?.gender.toString()

                getUserDataList(currentUserGender)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)

    }

    private fun getUserDataList(currentUserGender : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                //dataSnapshot을 하면 유저들의 데이터 덩어리가 하나로 쫙 나옴
//                Log.d(TAG, dataSnapshot.toString())

                //이 데이터 덩어리를 반복문으로 하나하나 뽑아올 것
                for (dataModel in dataSnapshot.children) {

                    //리스트에 유저 추가
                    val user = dataModel.getValue(UserDataModel::class.java)

                    if(user!!.gender.toString().equals(currentUserGender)) {

                    } else {

                        usersDataList.add(user!!)

                    }


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

    //유저의 좋아요를 표시하는 부분
    //데이터에서 값을 저장해야 하는데 어떤 값을 저장할까?
    //나의 uid, 내가 좋아요한 사람의 uid
    private fun userLikeOtherUser(myUid : String, otherUid : String){

        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")

        getOtherUserLikeList(otherUid)

    }

    // 내가 좋아요한 사람이 누구를 좋아요 했는지 알 수 있음
    private fun getOtherUserLikeList(otherUid : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 여기 리스트안에서 나의 UID가 있는지 확인만 해주면 됨.
                // 내가 좋아요한 사람(민지)의 좋아요 리스트를 불러와서
                // 여기서 내 uid가 있는지 체크만 해주면 됨.
                for (dataModel in dataSnapshot.children) {

                    val likeUserKey = dataModel.key.toString()
                    if(likeUserKey.equals(uid)){
                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)


    }

    //Notification
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료되었습니다 그 사람도 나를 좋아해요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(123, builder.build())
        }
    }
}