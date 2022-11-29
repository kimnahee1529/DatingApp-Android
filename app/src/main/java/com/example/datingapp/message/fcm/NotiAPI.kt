package com.example.datingapp.message.fcm

import com.example.datingapp.message.fcm.Repo.Companion.CONTENT_TYPE
import com.example.datingapp.message.fcm.Repo.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

//서버로 메시지 보내라고 명령하는 부분
//메시지의 내용을 PushNotification에 담아서 보내줌
interface NotiAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>

}