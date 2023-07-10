package io.paraga.moneytrackerdev.services

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.views.MainActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Random

class LocalNotificationService: BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        val channelId = Constants.THREE_DAY_UNOPENED_CHANNEL_ID
        val id= Random(System.currentTimeMillis()).nextInt(1000) // to make notification unique and not replace each other

        // specify which activity should navigate to when click on notification
        val notifIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            context, Constants.THREE_DAY_UNOPENED_REQUEST_CODE,
            notifIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        notifIntent.putExtra(Enums.Extras.NOTIFICATION_ID.value, id)
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(Constants.APP_NAME)
            .setContentText(Enums.Messages.THREE_DAY_UNOPENED_ALERT_MESSAGE.value)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.default_og))
            .setContentIntent(contentIntent)
        if (ActivityCompat.checkSelfPermission(
                context,
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
        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    companion object {
        fun createNotificationChannel(context: Context) {
            val channelName = "3days alert"
            val channelId = Constants.THREE_DAY_UNOPENED_CHANNEL_ID
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        fun initNoUseAppNotification(context: Context) {
            val notifIntent = Intent(context, LocalNotificationService::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, Constants.THREE_DAY_UNOPENED_REQUEST_CODE,
            notifIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            // alert in next three days
            val dateToAlert = LocalDate.now().plusDays(3)
            val time = LocalTime.of(20,
                15,
                0,
                0)
            val dateTimeToAlert = LocalDateTime.of(
                dateToAlert,
                time
            )
            val dateTimeToAlertInMillis = dateTimeToAlert.atZone(ZoneId.systemDefault())
            .toInstant()
                .toEpochMilli()

            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                dateTimeToAlertInMillis,
            pendingIntent)
        }
    }


}