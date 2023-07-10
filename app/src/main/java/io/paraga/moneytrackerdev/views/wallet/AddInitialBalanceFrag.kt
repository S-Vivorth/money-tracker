package io.paraga.moneytrackerdev.views.wallet

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentAddInitialBalanceBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.budget.CreateBudget
import org.checkerframework.checker.units.qual.s


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddInitialBalanceFrag.newInstance] factory method to
 * create an instance of this fragment.
 */

class AddInitialBalanceFrag() : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAddInitialBalanceBinding
    var fromEditWallet = false
    var isFromBudget = false
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
        binding = FragmentAddInitialBalanceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Extension.showKeyboard(binding.balanceEditText, isNumberInput = true, activity = activity as Activity)
        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }

        if (fromEditWallet) {
//            binding.balanceEditText.hint = editWallet.binding.balanceText.text.toString()
//            binding.balanceEditText.setText(String.format("%.2f",editWallet.balance))
            binding.balanceEditText.setSelection(binding.balanceEditText.length())
        }
        else {
//            binding.balanceEditText.hint = createWallet.binding.initialBalanceText.text.toString()
//            binding.balanceEditText.setText(createWallet.transAmount.toString())
            binding.balanceEditText.setSelection(binding.balanceEditText.length())
        }




        binding.saveBtn.setOnClickListener {

            if (fromEditWallet) {

                val editWallet = context as EditWallet
                val currentInputBalance: Double
                if (binding.balanceEditText.text.toString() == "") {
                    currentInputBalance = 0.0
                }
                else {
                    try {
                        currentInputBalance = binding.balanceEditText.text.toString().replace(',', '.').toDouble()
                    } catch (e: NumberFormatException) {
                        Toast(context).showCustomToast(
                            context?.getString(R.string.invalid_inputs) ?: "",
                            editWallet
                        )
                        return@setOnClickListener
                        //Error
                    }
                }
                if (editWallet.balance.toDouble() < currentInputBalance) {
                    editWallet.transType = Enums.TransTypes.INCOME.value
                    editWallet.binding.balanceText.text = requireContext().getString(
                        R.string.blank,
                        Extension.getCurrencySymbol(
                            editWallet.wallet.currency.toString()
                        ) + currentInputBalance.toString()
                    )
                    editWallet.transAmount = currentInputBalance - editWallet.balance.toDouble()
                }
                else{
                    editWallet.transType = Enums.TransTypes.EXPENSE.value
                    editWallet.binding.balanceText.text = requireContext().getString(
                        R.string.blank,
                        Extension.getCurrencySymbol(
                            editWallet.wallet.currency.toString()
                        ) + currentInputBalance.toString()
                    )
                    editWallet.transAmount = editWallet.balance.toDouble() - currentInputBalance
                }
                editWallet.balance = currentInputBalance
                editWallet.isBalanceChanged = true
            }
            else if (isFromBudget) {
                val createBudget = context as CreateBudget
                val currentInputBalance: Double
                if (binding.balanceEditText.text.toString() == "") {
                    currentInputBalance = 0.0
                }
                else {
                    try {
                        currentInputBalance = binding.balanceEditText.text.toString().replace(',', '.').toDouble()
                    } catch (e: NumberFormatException) {
                        Toast(context).showCustomToast(
                            context?.getString(R.string.invalid_inputs) ?: "",
                            createBudget
                        )
                        return@setOnClickListener
                        //Error
                    }
                }
                createBudget.binding.amountBudget.text = requireContext().getString(
                    R.string.blank,
                    createBudget.selectedWallet.currency.toString()
                            + " "
                            + currentInputBalance.toString()
                )
                createBudget.budget.amount = currentInputBalance
            }
            else{
                val createWallet = context as CreateWallet
                val currentInputBalance: Double

                if (binding.balanceEditText.text.toString() == "") {
                    currentInputBalance = 0.0
                }
                else {
                    try {
                        currentInputBalance = binding.balanceEditText.text.toString().replace(',', '.').toDouble()
                    } catch (e: NumberFormatException) {
                        Toast(context).showCustomToast(
                            context?.getString(R.string.invalid_inputs) ?: "",
                            createWallet
                        )
                        return@setOnClickListener
                        //Error
                    }
                }
                createWallet.transType = Enums.TransTypes.INCOME.value
                createWallet.binding.initialBalanceText.text = getString(
                    R.string.blank,
                    Extension.getCurrencySymbol(
                        createWallet.wallet.currency.toString()
                    ) + currentInputBalance.toString()
                )
                createWallet.transAmount = currentInputBalance
                createWallet.balance = currentInputBalance
                createWallet.isBalanceChanged = true

            }
        dismiss()
    }

    }

}
