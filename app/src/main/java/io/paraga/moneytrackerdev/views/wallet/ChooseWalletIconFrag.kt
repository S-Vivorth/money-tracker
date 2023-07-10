package io.paraga.moneytrackerdev.views.wallet

import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentChooseWalletIconBinding
import io.paraga.moneytrackerdev.utils.helper.Extension

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseWalletIconFrag.newInstance] factory method to
 * create an instance of this fragment.
 */

class ChooseWalletIconFrag : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentChooseWalletIconBinding
    lateinit var editWallet: EditWallet
    lateinit var createWallet: CreateWallet
    lateinit var previousSelectedLayout: RelativeLayout
//    var selectedIconIndex = 0
    lateinit var currentIconLayout: String
    private var isFirstLoaded: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context is EditWallet) {
            editWallet = context as EditWallet
        }
        else {
            createWallet = context as CreateWallet
        }
        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }



        binding.firstRowIcons.children.forEach {

            // to get only relative layout
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }
            }

        }
        binding.secondRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }
        binding.thirdRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }
        binding.forthRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }
        binding.fifthRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }
        binding.sixthRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }

        binding.seventhRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }
            }
        }

        binding.eightRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }

        binding.ninthRowIcons.children.forEach {
            if (it is RelativeLayout) {
                onIconLayoutClick(it)
                if (currentIconLayout == resources.getResourceEntryName(it.id)) {
                    isFirstLoaded = true
                    it.performClick()
                }

            }
        }
//        setUpIconLayout()


//        binding.accountBalanceWallet.setOnClickListener {
//            it.setBackgroundResource(R.drawable.rounded_wallet_icon_layout)
//            editWallet.walletList[editWallet.position].wallet.symbol = resources.getResourceEntryName(it.id).toString().replace("_","-")
//            Toast.makeText(context, editWallet.walletList[editWallet.position].wallet.symbol, Toast.LENGTH_SHORT).show()
//        }
//        setUpIconLayout()





    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseWalletIconBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

//    private fun setUpIconLayout() {
//        if (selectedIconIndex  <= 5) {
//            binding.firstRowIcons.children.elementAt(selectedIconIndex).performClick()
//        }
//        else if (selectedIconIndex in 6..9) {
//            binding.secondRowIcons.children.elementAt(selectedIconIndex-5).performClick()
//        }
//        else if (selectedIconIndex in 10..14) {
//            binding.thirdRowIcons.children.elementAt(selectedIconIndex-9).performClick()
//        }
//        else if (selectedIconIndex in 15..18) {
//            binding.forthRowIcons.children.elementAt(selectedIconIndex-14).performClick()
//        }
//        else if (selectedIconIndex in 19..23) {
//            binding.fifthRowIcons.children.elementAt(selectedIconIndex-18).performClick()
//        }
//        else if (selectedIconIndex in 24..28) {
//            binding.sixthRowIcons.children.elementAt(selectedIconIndex-23).performClick()
//        }
//        else if (selectedIconIndex in 29..33) {
//            binding.seventhRowIcons.children.elementAt(selectedIconIndex-28).performClick()
//        }
//        else if (selectedIconIndex in 34..38) {
//            binding.eightRowIcons.children.elementAt(selectedIconIndex-33).performClick()
//        }
//        else if (selectedIconIndex in 39..43) {
//            binding.ninthRowIcons.children.elementAt(selectedIconIndex-38).performClick()
//        }
//
//    }

    private fun onIconLayoutClick(layout: RelativeLayout) {
        layout.setOnClickListener {

            layout.setBackgroundResource(R.drawable.rounded_wallet_icon_layout)
            val currentIdName =  resources.getResourceEntryName(layout.id)
            if (context is EditWallet) {
                editWallet.walletList[editWallet.position].wallet.symbol = resources.getResourceEntryName(layout.id).toString().replace("_","-")
                editWallet.binding.walletIcon.setBackgroundResource(
                    Extension.getResouceId(editWallet, editWallet.walletList[editWallet.position].wallet.symbol)
                )
            }
            else {
                createWallet.binding.walletIcon.setBackgroundResource(
                    Extension.getResouceId(createWallet, currentIdName)
                )
                createWallet.selectedIconName = currentIdName
                createWallet.wallet.symbol = currentIdName.replace("_", "-")
            }

            if (isFirstLoaded) {
                previousSelectedLayout = layout
                isFirstLoaded = false
            }
            else{
                previousSelectedLayout.setBackgroundResource(0)
                previousSelectedLayout = layout
                dismiss()
            }

        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseWalletIconFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseWalletIconFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}