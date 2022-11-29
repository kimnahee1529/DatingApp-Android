package com.example.datingapp.message.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.datingapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//앱에서는 메시지를 받아서 -> ok
//앱에서는 알람을 띄워줌 -> ok

//하고 싶은 것은 유저의 토큰 정보를 받아와서 -> ok
//Firebase 서버로 메세지 보내라고 명령하고
//Firebase 서버에서 앱으로 메세지 보내주고


//앱에서 메시지를 받는 부분
class FirebaseService : FirebaseMessagingService(){

    private val TAG = "FirebaseService"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    //파이어베이스에서 메세지를 보내주면 앱에서 메시지를 받아주는 부분
    //메시지를 수신할 때
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

////        //앱에서는 메시지를 받아서
        Log.e(TAG, message.notification?.title.toString())
        Log.e(TAG, message.notification?.body.toString())
////
////        //파이어베이스 콘솔에서야 이런 식으로 가져올 수 있음
//        val title = message.notification?.title.toString()
//        val body = message.notification?.body.toString()

        val title = message.data["title"].toString()
        val body = message.data["content"].toString()

        createNotificationChannel()

        //받은 메시지를 앱에서 알람으로 띄워줌
        sendNotification(title, body)

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

    private fun sendNotification(title: String, body: String){
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)){
            notify(123, builder.build())
        }

    }
}