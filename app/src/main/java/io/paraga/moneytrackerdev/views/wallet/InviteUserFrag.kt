package io.paraga.moneytrackerdev.views.wallet

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.databinding.FragmentInviteUserBinding
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.NotificationManager
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Validation
import io.paraga.moneytrackerdev.viewmodels.account.ProfileVM
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InviteUserFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class InviteUserFrag(val activity: AppCompatActivity, val shareUserFrag: ShareUserFrag) :
    BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentInviteUserBinding
    lateinit var firebaseManager: FirebaseManager
    private val profileVM = ProfileVM()
    var notificationManager: NotificationManager = NotificationManager()
    var receiver = User()
    val value = TypedValue()
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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInviteUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseManager = FirebaseManager(requireContext())
        notificationManager.firebaseManager = firebaseManager
        Extension.showKeyboard(binding.searchView, activity = activity as Activity)

        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }

        binding.clearAllBtn.setOnClickListener {
            binding.searchView.text.clear()
            binding.resultUserLayout.visibility = View.GONE
        }

        binding.inviteUserBtn.setOnClickListener {
            shareUserFrag.walletTrans.value?.wallet?.sharedWith?.put(
                receiver.userid.toString(),
                Enums.InvitationType.PENDING.value
            )
            if (activity is EditWallet) {
                notificationManager.sendWalletInvitation(
                    shareUserFrag.walletTrans.value,
                    receiver,
                    requireContext().getString(R.string.wallet_invitation),
                    requireContext().getString(
                        R.string.blank,
                        requireContext().getString(R.string.you_were_invited_to_join_a_wallet)
                                + " "
                                + shareUserFrag.walletTrans.value?.wallet?.name
                                + " "
                                + requireContext().getString(R.string.from)
                                + " "
                                + user?.email
                    ),
                    ""
                )
            }

            requireActivity().theme.resolveAttribute(R.attr.buttonDisableBgColor, value, true)
            binding.inviteText.text = requireContext().getString(R.string.invited)
            binding.inviteUserBtn.isEnabled = false
            binding.inviteUserBtn.setCardBackgroundColor(value.data)

            shareUserFrag.walletTrans.value = shareUserFrag.walletTrans.value
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Validation().emailValidation(s.toString())) {
                    firebaseManager.searchUserByEmail(s.toString(),
                        onSuccess = {
                            if (it == User()) {
                                binding.resultUserLayout.visibility = View.GONE
                            } else {
                                receiver = it

                                binding.profileImg.setImageResource(R.drawable.person)
                                binding.resultUserLayout.visibility = View.VISIBLE
                                if (shareUserFrag.walletTrans.value?.wallet?.sharedWith?.get(it.userid) == Enums.InvitationType.ACCEPTED.value
                                    || it.userid == user?.userid
                                ) {
                                    binding.inviteUserBtn.visibility = View.GONE
                                } else if (shareUserFrag.walletTrans.value?.wallet?.sharedWith?.get(it.userid) == Enums.InvitationType.PENDING.value) {
                                    requireActivity().theme.resolveAttribute(
                                        R.attr.buttonDisableBgColor,
                                        value,
                                        true
                                    )
                                    binding.inviteText.text =
                                        requireContext().getString(R.string.invited)
                                    binding.inviteUserBtn.isEnabled = false
                                    binding.inviteUserBtn.visibility = View.VISIBLE
                                    binding.inviteUserBtn.setCardBackgroundColor(value.data)
                                } else {
                                    requireActivity().theme.resolveAttribute(
                                        R.attr.ogBtnColor,
                                        value,
                                        true
                                    )
                                    binding.inviteText.text =
                                        requireContext().getString(R.string.invite)
                                    binding.inviteUserBtn.isEnabled = true
                                    binding.inviteUserBtn.visibility = View.VISIBLE
                                    binding.inviteUserBtn.setCardBackgroundColor(value.data)
                                }
                                profileVM.getProfileUrl(
                                    it.userid, onSuccess = {
                                        Glide.with(requireContext()).load(it)
                                            .placeholder(R.drawable.person).into(binding.profileImg)
                                    },
                                    onFailure = {
                                    })
                                binding.userName.text = it.displayName
                                binding.userEmail.text = it.email
                            }
                        })
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InviteUserFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            param1: String,
            param2: String,
            activity: AppCompatActivity,
            shareUserFrag: ShareUserFrag
        ) =
            InviteUserFrag(activity, shareUserFrag).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}