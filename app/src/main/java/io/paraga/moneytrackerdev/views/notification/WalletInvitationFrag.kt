package io.paraga.moneytrackerdev.views.notification

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentWalletInvitationBinding
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.models.Notification
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.viewmodels.account.ProfileVM
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import io.paraga.moneytrackerdev.views.selectedWalletId
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalletInvitation.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalletInvitationFrag(val data: ShareWalletNotificationData, val activity: AppCompatActivity, val position: Int) :
    BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var binding: FragmentWalletInvitationBinding
    private val profileVM = ProfileVM()
    private val thread = Thread()
    private var currentWalletTrans = WalletTrans()
    private var walletTransList: ArrayList<Transaction> = ArrayList()
    var notificationManager: NotificationManager = NotificationManager()
    var invitationSender = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWalletInvitationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        thread.interrupt()
        super.onDismiss(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager(activity)
        if (data.isActionTaken == true) {
            binding.mainView.visibility = View.INVISIBLE
            binding.alreadyTakenText.visibility = View.VISIBLE
        }
        else {
            thread.run {
                kotlin.run {
                    firebaseManager.getUser(data.userid.toString(), onSuccess = {
                        invitationSender = it
                        activity.runOnUiThread {
                            kotlin.run {
                                binding.userName.text = it.displayName
                                binding.userEmail.text = it.email
                                profileVM.getProfileUrl(
                                    it.userid, onSuccess = {
                                        Glide.with(activity).load(it)
                                            .placeholder(R.drawable.person).into(binding.profileImg)
                                    },
                                    onFailure = {
                                    })
                            }
                        }
                    })
                    firebaseManager.getWallet(data.walletID.toString(), onSuccess = { walletTrans ->
                        currentWalletTrans = walletTrans
                        activity.runOnUiThread {
                            kotlin.run {
                                binding.walletIcon.setBackgroundResource(
                                    Extension.getResouceId(activity, walletTrans.wallet.symbol)
                                )
                                binding.walletName.text = walletTrans.wallet.name
                                binding.walletIconChildLayout.setBackgroundColor(Color.parseColor(walletTrans.wallet.color.toString()))
                                binding.walletIcon.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor(walletTrans.wallet.color.toString()))
                            }
                        }
                    })
                }
            }
            thread.start()
        }

        binding.acceptBtn.setOnClickListener {
            if (isProUser.value != true && (walletList.value?.size ?: 0) >= 2 ) {
                val premiumSubFrag = PremiumSubFrag()
                premiumSubFrag.show(activity.supportFragmentManager, "")
            }
            else {
                updateInvitation {
                    dismiss()
                    activity.finish()
                }
            }

        }

        binding.rejectBtn.setOnClickListener {
            dismiss()
            updateInvitation(isReject = true, {})
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun updateInvitation(isReject: Boolean? = false, onSuccess: () -> Unit) {
        val notification = notificationList.value?.get(position)
        notification?.data?.set(Enums.DB.IS_ACTION_TAKEN.value, true)

        firebaseManager.updateNotificationData(notification ?: Notification(), onSuccess = {
            val actionNotification: io.paraga.moneytrackerdev.models.Notification = io.paraga.moneytrackerdev.models.Notification()
            actionNotification.userid = invitationSender.userid.toString()
            actionNotification.type = Enums.NotificationType.NO_ACTION.value
            actionNotification.title = activity.getString(R.string.invitation_response)
            actionNotification.icon = ""
            if (isReject == true) {
                currentWalletTrans.wallet.sharedWith?.set(user?.userid.toString(),
                    Enums.InvitationType.REJECTED.value
                )
                actionNotification.description = activity.getString(
                    R.string.blank,
                    activity.getString(R.string.your_request_to)
                    + " "
                    + user?.email
                    + " "
                    + activity.getString(R.string.has_been_rejectd)
                )
                onSuccess()
            }
            else {
                transList.value?.addAll(walletTransList)
                transListCopy.value = transList.value
                walletList.value?.add(currentWalletTrans)
                walletList.postValue(walletList.value?.distinct()?.let { ArrayList(it) })
                currentWalletTrans.wallet.sharedWith?.set(user?.userid.toString(),
                    Enums.InvitationType.ACCEPTED.value
                )
                actionNotification.description = activity.getString(
                    R.string.blank,
                            user?.email
                                    + " "
                                    +
                    activity.getString(R.string.has_join_the_wallet)
                            + " "
                            + currentWalletTrans.wallet.name
                )

                // update selected wallet to this wallet
                selectedWallet = currentWalletTrans.wallet
                selectedWalletId = currentWalletTrans.walletId
                activity.setResult(AppCompatActivity.RESULT_OK)
                onSuccess()
            }
            notificationManager.sendNotification(
                receiver = invitationSender,
                actionNotification
            )
            firebaseManager.updateShareUser(currentWalletTrans, onSuccess = {
            })
        })
    }



    private fun calculateWalletBalance(
        transList: ArrayList<Transaction>,
        wallet: Wallet,
        onSuccess: (Double) -> Unit
    ) {
        var balance = 0.0
        transList.forEach { trans ->
            if (trans.type == Enums.TransTypes.EXPENSE.value) {
                if (wallet.currency != trans.currency) {
                    balance -= Extension.convertCurrency(
                        trans.currency.toString(),
                        wallet.currency.toString(),
                        trans.amount?.toDouble() ?: 0.0, activity
                    )
                } else {
                    balance -= trans.amount?.toDouble() ?: 0.0
                }
            } else {
                if (wallet.currency != trans.currency) {
                    balance += Extension.convertCurrency(
                        trans.currency.toString(),
                        wallet.currency as String,
                        trans.amount?.toDouble() ?: 0.0, activity
                    )
                } else {
                    balance += trans.amount?.toDouble() ?: 0.0
                }
            }
        }
        onSuccess(balance)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WalletInvitation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            param1: String,
            param2: String,
            shareWalletNotificationData: ShareWalletNotificationData,
            activity: AppCompatActivity,
            position: Int
        ) =
            WalletInvitationFrag(shareWalletNotificationData, activity, position).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}