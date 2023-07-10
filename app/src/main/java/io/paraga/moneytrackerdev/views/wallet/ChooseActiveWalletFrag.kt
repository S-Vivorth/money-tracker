package io.paraga.moneytrackerdev.views.wallet

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.ChooseActiveWalletAdapter
import io.paraga.moneytrackerdev.databinding.FragmentChooseActiveWalletBinding
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseActiveWalletFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseActiveWalletFrag (private val activity: AppCompatActivity): BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var chooseActiveWalletAdapter: ChooseActiveWalletAdapter
    private lateinit var binding: FragmentChooseActiveWalletBinding
    var activeWalletIds: MutableSet<String> = mutableSetOf()
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
        binding = FragmentChooseActiveWalletBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActiveWalletIdsList()
        initRecyclerView()

        binding.doneBtn.setOnClickListener {
            Preferences().getInstance().setChosenTwoWalletIds(activity, activeWalletIds)
            walletList.value = (walletList.value)
            dismiss()
        }

        binding.subscribeBtn.setOnClickListener {
            val premiumSubFrag = PremiumSubFrag()
            premiumSubFrag.show(activity.supportFragmentManager, "")
            dismiss()
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }


    private fun initRecyclerView() {
        chooseActiveWalletAdapter = ChooseActiveWalletAdapter(activity, this)
        if (context != null) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.walletRecyclerView.layoutManager = linearLayoutManager
            binding.walletRecyclerView.adapter = chooseActiveWalletAdapter
            binding.walletRecyclerView.setHasFixedSize(true)
        }
    }

    private fun initActiveWalletIdsList() {
        (Preferences().getInstance().getChosenTwoWalletIds(activity) ?: mutableSetOf()).forEach {
            activeWalletIds.add(it)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseActiveWalletFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, activity: AppCompatActivity) =
            ChooseActiveWalletFrag(activity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}