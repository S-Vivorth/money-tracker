package io.paraga.moneytrackerdev.views.wallet

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.WalletUserAdapter
import io.paraga.moneytrackerdev.databinding.FragmentShareUserBinding
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShareUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShareUserFrag(var activity: AppCompatActivity) : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentShareUserBinding
    lateinit var walletUserAdapter: WalletUserAdapter
    lateinit var firebaseManager: FirebaseManager
    private var sharedUserList: ArrayList<User> = ArrayList()
    var walletTrans: MutableLiveData<WalletTrans> = MutableLiveData(WalletTrans())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 1)
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShareUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager(activity)
        if (activity is EditWallet) {
            walletTrans.value = (activity as EditWallet).currentWalletTrans
        }
        else {
            walletTrans.value?.wallet = (activity as CreateWallet).wallet
        }

        walletTrans.observeForever {
            if (activity is CreateWallet) {
                (activity as CreateWallet).wallet = walletTrans.value?.wallet ?: Wallet()
            }
            Thread {
                kotlin.run {
                    firebaseManager.getSharedUserList(getSharedWalletUserIdList() ?: ArrayList()) {
                        sharedUserList = it
                        if (activity is CreateWallet) {
                            (activity as CreateWallet).sharedUserList = ArrayList(it.filter {
                                it.userid != user?.userid.toString()
                            }.distinctBy { it.userid })
                        }
                        activity.runOnUiThread {
                            kotlin.run {
                                initRecyclerView(
                                    ArrayList(
                                        sharedUserList.sortedWith(compareBy { it.userid == walletTrans.value?.wallet?.userid })
                                            .distinct().reversed()
                                    )
                                )
                            }
                        }
                    }
                    walletList.value?.forEach {
                        if (it.walletId == walletTrans.value?.walletId) {
                            it.wallet = walletTrans.value!!.wallet
                            return@forEach
                        }
                    }
                }
            }.start()
            if (activity is EditWallet) {
                val shareUsers = walletTrans.value?.wallet?.sharedWith?.values?.filter { it == Enums.InvitationType.ACCEPTED.value }
                (activity as EditWallet).binding.numberOfSharedUser.text =
                    requireContext().getString(
                        R.string.blank,
                        ((shareUsers?.size
                            ?: 0) + 1).toString()
                                + " "
                                + requireContext().getString(R.string.users)
                    )
            }
        }

        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }



        if (walletTrans.value?.wallet?.userid != user?.userid) {
            binding.inviteUserBtn.visibility = View.GONE
        }

        binding.inviteUserBtn.setOnClickListener {
            if (isProUser.value == false) {
                val premiumSubFrag = PremiumSubFrag()
                premiumSubFrag.show(activity.supportFragmentManager, "")
            }
            else {
                val inviteUserFrag = InviteUserFrag(activity, this)
                inviteUserFrag.show(activity.supportFragmentManager, "")
            }

        }


    }

    private fun getSharedWalletUserIdList(): ArrayList<String>? {
        var walletIdList: ArrayList<String>? = ArrayList()
        if (walletTrans.value?.wallet?.userid == user?.userid) {
            walletIdList = walletTrans.value?.wallet?.sharedWith?.keys?.let { ArrayList(it) }
        } else {
            walletTrans.value?.wallet?.sharedWith?.forEach {
                if (it.value == Enums.InvitationType.ACCEPTED.value) {
                    walletIdList?.add(it.key)
                }
            }
        }
        walletIdList?.add(walletTrans.value?.wallet?.userid.toString())
        return walletIdList
    }

    fun initRecyclerView(sharedUserList: ArrayList<User>) {
        walletUserAdapter = WalletUserAdapter(sharedUserList, walletTrans.value!!, this)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.sharedUserRecyclerView.layoutManager = linearLayoutManager
        binding.sharedUserRecyclerView.adapter = walletUserAdapter
        binding.sharedUserRecyclerView.setHasFixedSize(true)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShareUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            param1: String, param2: String,
            activity: AppCompatActivity
        ) =
            ShareUserFrag(activity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}