package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.datingapp.R
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.message.fcm.NotiModel
import com.example.datingapp.message.fcm.PushNotification
import com.example.datingapp.message.fcm.RetrofitInstance
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.example.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//쌍방으로 좋아요 누른 유저들의 정보를 모아놓은 리스트
class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "MyLikeListActivity"
    private val uid = FirebaseAuthUtils.getUid()

    //내가 좋아요한 사람들의 정보를 담는 리스트
    private val likeUserList = mutableListOf<UserDataModel>()
    //내가 좋아요한 사람들의 uid를 담는 리스트
    private val likeUserListUid = mutableListOf<String>()

    lateinit var listViewAdapter: ListViewAdapter
    lateinit var getterUid : String
    lateinit var getterToken : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        val userListView = findViewById<ListView>(R.id.userListView)

        listViewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listViewAdapter

        //전체 유저 데이터 받아오기
//        getUserDataList()

        //내가 좋아요한 사람들(리스트 받아오기)과
        getMyLikeList()

        //나를 좋아요한 사람들의 리스트 받아오기

        //내가 하고 싶은것은
        //전체 유저 중에서 내가 좋아요 한 사람들을 가져와서
        //이 사람이 나와 매칭이 되어있는지 확인하는 것

//        userListView.setOnItemClickListener{ parent, view, position, id ->
//
////            Log.d(TAG, likeUserList[position].uid.toString())
//            checkMatching(likeUserList[position].uid.toString())
//
//            //notification 문자
//            val notiModel = NotiModel("a", "b")
//
////            c0uMKap2SiadrHdyO_-tJq:APA91bE__bRkJB9ZY8XWac8UhRpRarqDV_JjQeKekjZwhSOoDqGDwfPhcsZh0Y73ROnPjKe0FGqGhHU3j30avnLqndGidI7j87fJr6cOc0jQQsD0AAtuu-d1ZdAw6bjgbqEnlVoxFVLo
//            val pushModel = PushNotification(notiModel, likeUserList[position].token.toString())
//
//            testPush(pushModel)
//            //여기까지 하면 메시지를 보내기까진 완료.
//            //하지만 어디서 보내주고 어떻게 받고 있는지는 다시 해야 함
//
//        }

        userListView.setOnItemLongClickListener { parent, view, position, id ->
//            Toast.makeText(this, "test", Toast.LENGTH_LONG).show()

            checkMatching(likeUserList[position].uid.toString())
            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()

            return@setOnItemLongClickListener(true)
        }
        //내가 좋아요한 유저를 클릭(Long Click)하면
        //만약에 서로 좋아요한 사람이 아니면 메세지 못 보내도록 하고
        //서로 좋아요했다면 메시지 보내기 창이 떠서 메세지를 보낼 수 있게 하고(당연히 받는 사람 부분도 만들어주기)
        //메세지 보내고 상대방에게 PUSH 알람 띄워주고
    }

    //클릭한 사람의 uid 받아오기
    private fun checkMatching(otherUid: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

//                Log.e(TAG, otherUid)
//                Log.e(TAG, dataSnapshot.toString())

                if(dataSnapshot.children.count() == 0){

                    Toast.makeText(this@MyLikeListActivity, "상대방이 좋아요한 사람이 아무도 없어요. ", Toast.LENGTH_SHORT).show()

                } else{
                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if(likeUserKey.equals(uid)){
                            Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다. ", Toast.LENGTH_SHORT).show()
                            showDialog()
                            //Dialog


                        } else{
//                            Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다. ", Toast.LENGTH_SHORT).show()
                        }

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

    //내가 좋아요한 사람들의 리스트 가져오기
    private fun getMyLikeList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                //dataSnapshot을 하면 유저들의 데이터 덩어리가 하나로 쫙 나옴
//                Log.d(TAG, dataSnapshot.toString())

                //이 데이터 덩어리를 반복문으로 하나하나 뽑아올 것
                for (dataModel in dataSnapshot.children) {
                    //이 리스트 안에서 나의 UID가 있는지 확인만 해주면 됨
                    //내가 좋아요한 사람(민지)의 좋아요 리스트를 불러와서 내 UID가 있는지 확인

                    //내가 좋아요한 사람들의 uid를 likeUserList에 하나하나씩 담는 과정
                    likeUserListUid.add(dataModel.key.toString())
//                    Log.e(TAG, likeUserListUid.toString())
                }
                getUserDataList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)

    }

    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)

                    //전체 유저 중에 내가 좋아요한 사람들의 정보만 add함

                    //만약 유저의 uid를 포함하고 있으면 유저 리스트에 유저정보를 더한다.
                    //내가 좋아요한 사람들의 정보만 뽑아올 수 있음
                    if(likeUserListUid.contains(user?.uid)){
                        likeUserList.add(user!!)
                    }

                }
                //데이터를 다 받아오고 나서 다시 그려주기
                listViewAdapter.notifyDataSetChanged()
//                Log.e(TAG, likeUserList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    //PUSH
    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)

    }

    //Dialog
    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메시지 보내기")

        val mAlertDialog = mBuilder.show()

        val btn = mAlertDialog.findViewById<Button>(R.id.sendBtnArea)

        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)
        btn?.setOnClickListener {

            val msgText = textArea!!.text.toString()
            val msgModel = MsgModel(MyInfo.myNickname, textArea!!.text.toString())

            FirebaseRef.userMsgRef.child(getterUid).push().setValue(msgModel)

            val notiModel = NotiModel(MyInfo.myNickname, msgText)

            val pushModel = PushNotification(notiModel, getterToken)
//
            testPush(pushModel)

            //dialog 종료
            mAlertDialog.dismiss()
        }

        //message
        //받는 사람 uid
        //Messasge
        //누가 보냈는지

    }
}