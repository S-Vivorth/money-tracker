package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Notification
import io.paraga.moneytrackerdev.models.ShareWalletNotificationData
import io.paraga.moneytrackerdev.networks.notificationList
import io.paraga.moneytrackerdev.views.notification.WalletInvitationFrag
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class NotificationAdapter (val notificationActivity: io.paraga.moneytrackerdev.views.notification.Notification): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private val value = TypedValue()
    private val now = LocalDate.now()
    private var dateString = ""


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var date: TextView
        var context: Context
        init {
            title = view.findViewById(R.id.title)
            date = view.findViewById(R.id.date)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = notificationList.value?.get(position)?.description
        setNotificationDate(position, holder)

        if (notificationList.value?.get(position)?.isRead == true) {
            holder.context.theme.resolveAttribute(R.attr.quaternaryBgColor, value, true)
            holder.itemView.setBackgroundColor(value.data)
        }
        holder.itemView.setOnClickListener {
            if (notificationList.value?.get(position)?.type == Enums.NotificationType.WALLET_INVITATION.value) {
                val data = ShareWalletNotificationData(
                    (notificationList.value?.get(position) as Notification).data?.get(Enums.DB.USER_ID_FIELD.value).toString(),
                    (notificationList.value?.get(position) as Notification).data?.get(Enums.DB.WALLET_ID_FIELD.value).toString(),
                    (notificationList.value?.get(position) as Notification).data?.get(Enums.DB.IS_ACTION_TAKEN.value) as Boolean
                )
                val walletInvitation = WalletInvitationFrag(data, holder.context as AppCompatActivity, position)
                walletInvitation.show((holder.context as AppCompatActivity).supportFragmentManager, "")

                //update read status
            }
            if (notificationList.value?.get(position)?.isRead != true) {
                Thread {
                    notificationList.value?.get(position)?.isRead = true
                    notificationActivity.firebaseManager.updateReadNotifStatus(notificationList.value?.get(position) ?: Notification())
                    notificationActivity.runOnUiThread {
                        kotlin.run {
                            notifyItemChanged(position)
                        }
                    }
                }.start()
            }
        }


    }

    fun setNotificationDate(position: Int, holder: ViewHolder) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        val notifDate = notificationList.value?.get(position)?.createdTime
            ?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        dateString = holder.context.getString(
            R.string.blank,
            String.format("%02d", notifDate?.dayOfMonth)
                    + " "
                    + notifDate?.month?.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " "
                    + notifDate?.year.toString()
        )
        var selectedDate: LocalDate
        try {
            selectedDate = LocalDate.parse(dateString, formatter)
        }
        catch (exc: Exception) {
            selectedDate = now
        }
        when (selectedDate) {
            now -> {
                holder.date.text = Enums.General.TODAY.value
            }
            now.plusDays(1) -> {
                holder.date.text = Enums.General.TOMORROW.value
            }
            now.minusDays(1) -> {
                holder.date.text = Enums.General.YESTERDAY.value
            }
            else -> {
                holder.date.text = dateString
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.value?.size ?: 0
    }
}
