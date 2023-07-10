package io.paraga.moneytrackerdev.views.transaction

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.adapters.ChooseWalletAdapter
import io.paraga.moneytrackerdev.databinding.FragmentChooseWalletBinding
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.viewmodels.wallet.WalletVM
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.importFile.activity.ImportDetailActivity
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import io.paraga.moneytrackerdev.views.wallet.CreateWallet
import io.paraga.moneytrackerdev.views.wallet.Wallet

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseWalletFrag.newInstance] factory method to
 * create an instance of this fragment.
 */

class ChooseWalletFrag : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentChooseWalletBinding
    lateinit var chooseWalletAdapter: ChooseWalletAdapter
    var isFromMainActivity = true
    var isFromImportActivity = true
    var isFromBudget = false
    val walletVM = WalletVM()

    var addTrans: AddTrans = AddTrans()
    var mainActivity: MainActivity = MainActivity()
    var importDetailBinding: ImportDetailActivity = ImportDetailActivity()
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
        binding = FragmentChooseWalletBinding.inflate(layoutInflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        binding.manageWalletBtn.setOnClickListener {
            Extension.goToNewActivity(requireContext(), Wallet::class.java)
            dismiss()
        }

        binding.backBtnLayout.setOnClickListener {
            dismiss()
        }

        if (!isFromMainActivity) {
            binding.manageWalletLayout.visibility = View.GONE
        }

        binding.addWalletBtn.setOnClickListener {
            checkProUser()
        }

        if (isFromBudget) {
            binding.manageWalletLayout.visibility = View.GONE
        }

    }

    private fun initRecyclerView() {
        chooseWalletAdapter = ChooseWalletAdapter(this)
        if (context != null) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.walletRecyclerView.layoutManager = linearLayoutManager
            binding.walletRecyclerView.adapter = chooseWalletAdapter
            binding.walletRecyclerView.setHasFixedSize(true)
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseWalletFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseWalletFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun checkProUser() {
        if (isProUser.value == true) {
            Extension.goToNewActivity(requireContext(), CreateWallet::class.java)
            dismiss()
        }
        else {
            notProAccess()
        }
    }
    private fun notProAccess() {
        if (walletList.value?.size!! < 2) {
            Extension.goToNewActivity(requireContext(), CreateWallet::class.java)
            dismiss()
        }
        else {
            val premiumSubFrag = PremiumSubFrag()
            premiumSubFrag.show(requireActivity().supportFragmentManager,"")
        }
    }
}