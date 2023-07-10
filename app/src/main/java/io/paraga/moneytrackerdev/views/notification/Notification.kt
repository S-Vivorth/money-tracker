package io.paraga.moneytrackerdev.views.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.NotificationAdapter
import io.paraga.moneytrackerdev.databinding.ActivityNotificationBinding
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.notificationList
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Preferences


class Notification : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    lateinit var firebaseManager: FirebaseManager
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // dismiss notification
        NotificationManagerCompat.from(this)
            .cancel(intent.getIntExtra(Enums.Extras.NOTIFICATION_ID.value, 0))

        firebaseManager = FirebaseManager(this)
        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        initRecyclerView()

        if (notificationList.value?.size == 0) {
            binding.noNotificationText.visibility = View.VISIBLE
        }
        else {
            binding.noNotificationText.visibility = View.GONE
        }
        binding.clearAllBtn.setOnClickListener {
            if (notificationList.value?.size != 0) {
                DialogHelper.showPrimaryDialog(this,
                Enums.DialogType.Notification.value,
                onOkayPressed = {
                    binding.noNotificationText.visibility = View.VISIBLE
                    firebaseManager.clearAllNotification {
                        notificationAdapter.notifyDataSetChanged()
                        Toast(this).showCustomToast(getString(R.string.clear_all_successfully), this)
                    }
                    Preferences().getInstance().setLatestNumbOfNotif(this, 0)
                })
            }
        }
    }

    private fun initRecyclerView() {


        notificationAdapter = NotificationAdapter(this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.notificationRecyclerView.layoutManager = linearLayoutManager
        binding.notificationRecyclerView.adapter = notificationAdapter
        binding.notificationRecyclerView.setHasFixedSize(true)
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                firebaseManager.deleteNotification(
                    notificationList.value?.get(position)?.documentId.toString(),
                    onSuccess = {

                    }
                )
                notificationList.value?.removeAt(position)
                if (notificationList.value?.size == 0) {
                    binding.noNotificationText.visibility = View.VISIBLE
                }
                else {
                    binding.noNotificationText.visibility = View.GONE
                }
                notificationAdapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.notificationRecyclerView)
    }
}