package com.example.bookwhale.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bookwhale.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "onMessageReceived")
        Log.d(TAG, "From: ${remoteMessage.from}")

        Log.d(TAG, "From: ${remoteMessage.data.toString()}")


        // 작업표시줄에 noti를 띄운다.
        sendNotification(remoteMessage.data)

//        Log.e("remoteMessageData", remoteMessage.data.toString())
        //App.prefs.notificationCount++
//        sendData(remoteMessage.data)

        // Check if message contains a data payload.
        // notification만 있으면 background에서만 작동한다. (별도의 notification 채널을 이용하여 작동함)
        // data를 담으면, foreground상태에서는 sendNotification 실행, intent를 통해 바로 게시판 이동등의 동작 구현가능
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//            //Log.d(TAG, "Message data payload: ${remoteMessage.notification}")
//            sendNotification(remoteMessage.notification?.body!!)
//            //sendNotification(remoteMessage.data)
//        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e(TAG, "new token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    /*
           data: {
              title : board.title,
              body : "새 대댓글이 등록되었습니다",
              board : alarm.board.id
              }
    */
//    private fun sendData(messageBody: Map<String, String>) {
//        val intent = Intent("custom-event-name")
//
//        Log.e("fcm intent message",messageBody.toString())
//
//        intent.putExtra ("body", messageBody["body"])
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }

    private fun sendNotification(messageBody: Map<String, String>) {
//        val intent = Intent(this, DetailActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        // messageBody["notification_id"]
//        intent.putExtra("board_id",messageBody["board"])
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )

        Log.d("MessageBody",messageBody.toString())
        Log.d("MessageBody2",messageBody["title"].toString())
        Log.d("MessageBody3",messageBody["body"].toString())

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(messageBody["title"])
            .setContentText(messageBody["body"])
            .setAutoCancel(true)
            .setSound(null)
            //.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}