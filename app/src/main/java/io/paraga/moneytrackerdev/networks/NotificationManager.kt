package io.paraga.moneytrackerdev.networks

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Notification
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.WalletTrans
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*


class NotificationManager: FirebaseMessagingService() {
    var firebaseManager: FirebaseManager = FirebaseManager(this)

    override fun onNewToken(token: String) {
        if (user != null) {
            firebaseManager.updateUserToken(token)
        }
        super.onNewToken(token)
    }

    @SuppressLint("RestrictedApi")
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
        val text = message.notification?.body.toString()
        val CHANNEL_ID = "MESSAGE"
        val channnel = NotificationChannel(
            CHANNEL_ID,
            "Notification",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        val id= Random(System.currentTimeMillis()).nextInt(1000) // to make notification unique and not replace each other
        val notifIntent = Intent(this, io.paraga.moneytrackerdev.views.notification.Notification::class.java)
        notifIntent.putExtra(Enums.Extras.NOTIFICATION_ID.value, id)
//            .setAction("1")
//        notifIntent.setAction(".views.notification.Notification")
//        notifIntent.addCategory("android.intent.category.DEFAULT")
//        notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        val contentIntent = PendingIntent.getActivity(
            this, Constants.WALLET_INVITATION_REQUEST_CODE,
            notifIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channnel)
//        val drawable =  resources.getDrawable(R.drawable.app_icon_yellow, theme)
//        val myLogo = drawable.toBitmap()
//        val bitmap = ContextCompat.getDrawable(this, R.drawable.app_icon_yellow)?.toBitmap()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
//            .setSmallIcon(IconCompat.createFromIcon(Icon.createWithBitmap(bitmap)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.default_og))
//            .addAction(Notification.Action(R.drawable.ic_notification, "Action", contentIntent))
            .setContentIntent(contentIntent)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Log.d("",NotificationManagerCompat.from(this).areNotificationsEnabled().toString())

        NotificationManagerCompat.from(this).notify(id, builder.build())
        super.onMessageReceived(message)
    }


    override fun handleIntent(intent: Intent?) {
        val action = intent?.action

        super.handleIntent(intent)
    }
    fun getCurrentToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result


            Log.d("token", token)
            firebaseManager.updateUserToken(token)
        })
    }

    fun sendNotification(receiver: User?, notification: Notification) {

        firebaseManager.addNotification(
            notification,
            onSuccess = {
                send(receiver, notification.title.toString(),
                    notification.description.toString(),
                    notification.icon.toString())
            },
            onFailure = {

            }
        )
    }


    private fun send(receiver: User?, title: String, body: String, icon: String) {
        val mainObj = JSONObject()
        try {
            mainObj.put("to", receiver?.token.toString())
            val notificationObj = JSONObject()
            notificationObj.put("title", title)
            notificationObj.put("body", body)
            notificationObj.put("icon", "ic_notification")
            mainObj.put("notification", notificationObj)

            val client = OkHttpClient()
            val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

            val request =
                Request.Builder().url("https://fcm.googleapis.com/fcm/send").post(
                    mainObj.toString().toRequestBody(mediaTypeJson)
                )
                    .header("Authorization", "key="+Config.FCM_SERVER_KEY)
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("exc", e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                }
            })

        }
        catch (exc:Exception) {
            Log.d("exc", exc.toString())
        }
    }


    fun sendWalletInvitation(wallet: WalletTrans?, receiver: User?, title: String, body: String, icon: String,
    isUpdateShareUser: Boolean? = true) {
        val notification: io.paraga.moneytrackerdev.models.Notification = io.paraga.moneytrackerdev.models.Notification()
        val data: HashMap<String, Any> = HashMap()
        data[Enums.DB.USER_ID_FIELD.value] = user?.userid.toString()
        data[Enums.DB.WALLET_ID_FIELD.value] = wallet?.walletId.toString()
        data[Enums.DB.IS_ACTION_TAKEN.value] = false
        notification.userid = receiver?.userid.toString()
        notification.type = Enums.NotificationType.WALLET_INVITATION.value
        notification.title = title
        notification.description = body
        notification.icon = icon
        notification.data = data
        if (isUpdateShareUser == false) {
            sendNotification(receiver, notification)
        }
        else {
            firebaseManager.updateShareUser(wallet) {
                sendNotification(receiver, notification)
            }
        }

    }
}
