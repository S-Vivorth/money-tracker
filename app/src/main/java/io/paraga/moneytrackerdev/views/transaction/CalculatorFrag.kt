package io.paraga.moneytrackerdev.views.transaction

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.RecentCurrencyAdapter
import io.paraga.moneytrackerdev.databinding.FragmentCalculatorBinding
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.currency.ChooseCurrencyFrag
import kotlin.math.abs


class CalculatorFrag() : BottomSheetDialogFragment(){
    lateinit var binding: FragmentCalculatorBinding
    private var firstOperand: String = "0"
    private var secondOperand: String = ""
    private var result: String = "0"
    var hasOperator: Boolean = false
    var operator: String = ""
    lateinit var inputText: TextView
    var isInRightOperand = false
    var isInOperator = false
    var isInLeftOperand = true
    var isExpense = false
    var currentCurrencyName: String = ""
    lateinit var recentCurrencyList: ArrayList<String>
    lateinit var recentCurrencyAdapter: RecentCurrencyAdapter
    var wallet: Wallet = Wallet()
    var selectedIndex:Int = 0
    lateinit var addTrans: AddTrans
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(layoutInflater)
        view?.setBackgroundResource(android.R.color.transparent)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1)
        return bottomSheetDialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        addTrans.isCalculatorOpened = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context is AddTrans) {
            addTrans = context as AddTrans
        }
        checkZeroAmount()

        inputText = binding.inputText
        inputText.text = result
        binding.selectedCurrencyName.text = currentCurrencyName
        addTrans.isCalculatorOpened = true
        getRecentCurrency()
        initRecyclerView()


        if (addTrans.isEditTrans == true) {
            binding.switchBtn.isEnabled = false
            binding.switchBtn.alpha = 0.5F
        }


        binding.chooseCurrencyBtn.setOnClickListener {
            Log.d("selectedCurrency", currentCurrencyName)

            currentCurrencyName = getString(
                R.string.blank,
                currentCurrencyName + " - " +
                        Extension.getCurrencyObj(currentCurrencyName).name
            )
            val chooseCurrencyFrag = ChooseCurrencyFrag()
            chooseCurrencyFrag.currentCurrencyName = currentCurrencyName
            chooseCurrencyFrag.calculatorFrag = this
            chooseCurrencyFrag.show(requireActivity().supportFragmentManager, "")
        }

        binding.switchBtn.setOnClickListener {
            if (!isExpense) {
                addTrans.theme.resolveAttribute(R.attr.incomeButtonBgColor, addTrans.value, true)
                binding.saveBtn.setCardBackgroundColor(addTrans.value.data)
                binding.inputText.setTextColor(ContextCompat.getColor(requireContext(),R.color.primaryGreen))
                binding.doneBtn.setCardBackgroundColor(addTrans.value.data)
                binding.switchText.setText(R.string.expense)
                binding.switchText.setTextColor(ContextCompat.getColor(requireContext(),R.color.primaryRed))
                addTrans.binding.incomeBtn.performClick()


            }
            else {
                addTrans.theme.resolveAttribute(R.attr.expenseButtonBgColor, addTrans.value, true)
                binding.saveBtn.setCardBackgroundColor(addTrans.value.data)
                binding.inputText.setTextColor(ContextCompat.getColor(requireContext(),R.color.primaryRed))
                binding.doneBtn.setCardBackgroundColor(addTrans.value.data)
                binding.switchText.setText(R.string.income)
                binding.switchText.setTextColor(ContextCompat.getColor(requireContext(),R.color.primaryGreen))

                addTrans.binding.expenseBtn.performClick()

            }
            addTrans.isCalculatorOpened = false
            isExpense = !isExpense
            checkZeroAmount()
        }


        binding.switchBtn.performClick()


        binding.clearAllBtn.setOnClickListener {
            setErrorDigitVisibility(false)
            firstOperand = "0"
            secondOperand = ""
            result = "0"
            hasOperator = false
            inputText.text = firstOperand
            checkZeroAmount()
        }

        binding.deleteBtn.setOnClickListener {
            if (firstOperand.length < 16 && secondOperand.length < 16) {
                setErrorDigitVisibility(false)
            }

            if (isInLeftOperand) {
                if (firstOperand.length > 1) {
                    firstOperand = firstOperand.slice(0..firstOperand.length-2)
                    inputText.text = firstOperand
                    result = firstOperand
                }
                else {
                    firstOperand = "0"
                    inputText.text = firstOperand
                    result = firstOperand
                }
            }

            if (isInRightOperand) {
                if (secondOperand.isNotEmpty()) {
                    secondOperand = secondOperand.slice(0..secondOperand.length-2)
                    inputText.text = firstOperand + operator + secondOperand
                    result = firstOperand + operator + secondOperand
                }
                else {
                    isInRightOperand = false
                    isInOperator = true
                }
            }
            if (isInOperator) {
                inputText.text = firstOperand
                hasOperator = false
                isInLeftOperand = true
            }
            checkZeroAmount()
        }

        binding.plusBtn.setOnClickListener {
            isInOperator = true
            isInLeftOperand = false
            hasOperator = true
            operator = "+"
            inputText.text = firstOperand + operator


        }

        binding.minusBtn.setOnClickListener {
            isInOperator = true
            isInLeftOperand = false

            hasOperator = true
            operator = "-"
            inputText.text = firstOperand + operator
        }

        binding.multiplyBtn.setOnClickListener {
            isInOperator = true
            isInLeftOperand = false

            hasOperator = true
            operator = "*"
            inputText.text = firstOperand + operator
        }

        binding.divisionBtn.setOnClickListener {
            isInOperator = true
            isInLeftOperand = false

            hasOperator = true
            operator = "/"
            inputText.text = firstOperand + operator
        }

        binding.equalBtn.setOnClickListener {
            if (operator != "" && hasOperator && !isInOperator) {
                if (operator == "+") {
                    result = (firstOperand.toFloat() + secondOperand.toFloat()).toString()
                }
                else if (operator == "-") {
                    result = (firstOperand.toFloat() - secondOperand.toFloat()).toString()

                }
                else if (operator == "*") {
                    result = (firstOperand.toFloat() * secondOperand.toFloat()).toString()

                }
                else if (operator == "/"){
                    if (secondOperand == "0") {
                        result = "N/A"
                        firstOperand = "0"
                        secondOperand = "0"
                    }
                    else {
                        result = (firstOperand.toFloat() / secondOperand.toFloat()).toString()
                    }
                }
                secondOperand = ""
                if (result != "N/A") {
                    if (result == result.toFloat().toInt().toFloat().toString()) {
                        firstOperand = result.toFloat().toInt().toString()
                        result = result.toFloat().toInt().toString()
                    }
                    else {
                        firstOperand = result
                    }
                    addTrans.binding.amount.text = Extension.toBigDecimal(abs(result.toDouble()).toString())
                }
                inputText.text = result
                hasOperator = false
                checkZeroAmount()
            }
        }

        binding.noOneBtn.setOnClickListener {
            numberIsClick(number = 1)
        }

        binding.noTwoBtn.setOnClickListener {
            numberIsClick(number = 2)
        }

        binding.noThreeBtn.setOnClickListener {
            numberIsClick(number = 3)

        }

        binding.noFourBtn.setOnClickListener {
            numberIsClick(number = 4)

        }

        binding.noFiveBtn.setOnClickListener {
            numberIsClick(number = 5)

        }


        binding.noSixBtn.setOnClickListener {
            numberIsClick(number = 6)

        }

        binding.noSevenBtn.setOnClickListener {
            numberIsClick(number = 7)

        }

        binding.noEightBtn.setOnClickListener {
            numberIsClick(number = 8)

        }

        binding.noNineBtn.setOnClickListener {
            numberIsClick(number = 9)

        }

        binding.zeroBtn.setOnClickListener {
            numberIsClick(number = 0)

        }

        binding.dotBtn.setOnClickListener {
            if (!hasOperator) {
                if (!(firstOperand.contains("."))) {
                    if (firstOperand != "0") {

                        firstOperand += "."

                    }
                    else {
                        firstOperand = "."
                    }
                    inputText.text = firstOperand
                }
            }
            else {
                if (!(secondOperand.contains("."))) {
                    secondOperand+= "."
                    inputText.text = firstOperand + operator + secondOperand
                }
            }
        }


        binding.nextBtn.setOnClickListener {
            binding.equalBtn.performClick()
            addTrans.binding.chooseCategory.performClick()
            addTrans.binding.amount.text = Extension.toBigDecimal(abs(result.toDouble()).toString())
            //validation add transaction
            addTrans.enableButton()
            checkZeroAmount()
            dismiss()
        }

        if (addTrans.binding.amount.text.toString() == "0") {
            //validation add transaction
            addTrans.theme.resolveAttribute(R.attr.buttonDisableBgColor, addTrans.value, true)
            addTrans.binding.saveBtnLayout.apply {
                isEnabled = false
                setCardBackgroundColor(
                    addTrans.value.data
                )
            }
            addTrans.theme.resolveAttribute(R.attr.buttonDisabledTextColor, addTrans.value, true)
            addTrans.binding.saveBtn.alpha = 0.38F
            addTrans.binding.saveBtn.setTextColor(addTrans.value.data)
        }
        binding.saveBtn.setOnClickListener {
            binding.equalBtn.performClick()
            try {
                addTrans.binding.amount.text = Extension.toBigDecimal(abs(result.toDouble()).toString())
            }
            catch (exc: java.lang.NumberFormatException) {
                Toast(requireContext()).showCustomToast(requireContext().getString(R.string.invalid_trans_amount), requireActivity())
                return@setOnClickListener
            }
            checkZeroAmount()
            if (addTrans.binding.amount.text.toString() == "0") {
                Toast(requireContext()).showCustomToast(requireContext().getString(R.string.invalid_trans_amount), requireActivity())
            }
            else {
                addTrans.binding.saveBtnLayout.performClick()
                dismiss()
            }
        }

        binding.doneBtn.setOnClickListener {
            try {
                addTrans.binding.amount.text = Extension.toBigDecimal(abs(result.toDouble()).toString())
            }
            catch (exc: NumberFormatException) {
                return@setOnClickListener
            }
            addTrans.binding.amount.text = Extension.toBigDecimal(abs(result.toDouble()).toString())
            addTrans.enableButton()
            if (currentCurrencyName == addTrans.selectedWallet.currency) {
                addTrans.binding.exchangeRate.visibility = View.GONE
            }
            else {
                val convertedAmount = addTrans.getString(
                    R.string.blank,
                    addTrans.selectedWallet.currency
                + " "
                + String.format("%.2f",Extension.convertCurrency(currentCurrencyName,
                        addTrans.selectedWallet.currency.toString(),
                        result.toDouble(), requireContext()))
                )
                addTrans.binding.exchangeRate.text = convertedAmount
                addTrans.binding.exchangeRate.visibility = View.VISIBLE

            }


            checkZeroAmount()

            dismiss()
        }
    }
    private fun numberIsClick(number: Int){
        if (firstOperand.length < 15) {
            if (secondOperand.length < 15) {
                Log.d("operands", firstOperand.length.toString())
                if (!hasOperator) {
                    if (firstOperand != "0") {
                        firstOperand += number.toString()
                    }
                    else {
                        firstOperand = number.toString()
                    }
                    result = firstOperand
                    isInLeftOperand = true
                    isInOperator = false
                    inputText.text = firstOperand
                }
                else {
                    secondOperand+= number.toString()
                    inputText.text = firstOperand + operator + secondOperand
                    isInRightOperand = true
                    isInOperator = false
                }
                checkZeroAmount()
            }
            else {
                setErrorDigitVisibility(true)
            }
        }
        else {
            setErrorDigitVisibility(true)
        }

    }



    private fun setErrorDigitVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.exceedDigitText.visibility = View.VISIBLE
//            binding.saveBtn.isEnabled = false
//            binding.doneBtn.isEnabled = false
//            binding.nextBtn.isEnabled = false
//            binding.doneBtn.alpha = 0.5F
//            binding.saveBtn.alpha = 0.5F
//            binding.nextBtn.alpha = 0.5F
            changeBtnState(true)

        }
        else {
            binding.exceedDigitText.visibility = View.GONE
//            binding.saveBtn.isEnabled = true
//            binding.doneBtn.isEnabled = true
//            binding.nextBtn.isEnabled = true
//            binding.doneBtn.alpha = 1F
//            binding.saveBtn.alpha = 1F
//            binding.nextBtn.alpha = 1F
            changeBtnState(false)

        }

    }

    private fun checkRecentCurrency(): Boolean {

        val pref = requireActivity().getSharedPreferences(Enums.SharePref.RECENT_CURRENCY.value,
            AppCompatActivity.MODE_PRIVATE
        )
        if (pref.getString(Enums.SharePref.RECENT_CURRENCY.value, "")?.toList()?.isEmpty() == true) {
            return false
        }
        return  true
    }

    private fun getRecentCurrency() {
        if (!checkRecentCurrency()) {
            val editor =  requireContext().getSharedPreferences(
                Enums.SharePref.RECENT_CURRENCY.value,
                AppCompatActivity.MODE_PRIVATE
            )?.edit()


            val pref = requireActivity().getSharedPreferences(Enums.SharePref.RECENT_CURRENCY.value,
                AppCompatActivity.MODE_PRIVATE
            )

            val currencyList = pref.getString(Enums.SharePref.RECENT_CURRENCY.value, wallet.currency.toString())
            editor?.putString(Enums.SharePref.RECENT_CURRENCY.value, currencyList)
            editor?.apply()
            recentCurrencyList = ArrayList(currencyList!!.split(",").asReversed())


        }
        else {
            val pref = requireActivity().getSharedPreferences(Enums.SharePref.RECENT_CURRENCY.value,
                AppCompatActivity.MODE_PRIVATE
            )
            var currencyList = pref.getString(Enums.SharePref.RECENT_CURRENCY.value, "")
            val currencySplit = ArrayList(currencyList?.split(",")!!.asReversed())
            Log.d("currentCurrencyName",addTrans.currentCurrencyName)
            if (addTrans.currentCurrencyName == wallet.currency) {
                if (currencySplit.contains(wallet.currency)) {
                    currencySplit.remove(wallet.currency)
                    currencySplit.add(wallet.currency)
                    currencyList = (currencySplit.toString()).replace("[","").replace("]","").replace(" ","")
                }
                else {
                    currencySplit.add(wallet.currency)
                    currencyList = (currencySplit.toString()).replace("[","").replace("]","").replace(" ","")
                }
            }
            else {
                currencySplit.remove(addTrans.currentCurrencyName)
                currencySplit.add(addTrans.currentCurrencyName)
                currencyList = (currencySplit.toString()).replace("[","").replace("]","").replace(" ","")
            }


            recentCurrencyList = ArrayList(currencyList.split(",").asReversed())
            if (currencySplit.size == 5) {
                recentCurrencyList.removeAt(1)
            }

        }
    }

    private fun initRecyclerView() {
        recentCurrencyAdapter = RecentCurrencyAdapter(this, selectedIndex, wallet)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recentCurRecyclerView.layoutManager = linearLayoutManager
        binding.recentCurRecyclerView.adapter = recentCurrencyAdapter
        binding.recentCurRecyclerView.setHasFixedSize(true)
    }

    private fun checkZeroAmount() {
        // remove sign when amount = 0
        if (addTrans.binding.amount.text.toString() == "0") {
            addTrans.binding.amountSign.visibility = View.GONE
            changeBtnState()

        }
        else {
            addTrans.binding.amountSign.visibility = View.VISIBLE
            changeBtnState()

        }
    }
    private fun disableBtn() {
        binding.nextBtn.isEnabled = false
        binding.arrowForwardIcon.alpha = 0.38F
        binding.nextText.alpha = 0.38F
        addTrans.theme.resolveAttribute(R.attr.buttonDisabledTextColor, addTrans.value, true)
        binding.nextText.setTextColor(addTrans.value.data)
        binding.arrowForwardIcon.setColorFilter(
           addTrans.value.data,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        addTrans.theme.resolveAttribute(R.attr.buttonDisableBgColor, addTrans.value, true)
        binding.nextBtn.setCardBackgroundColor(addTrans.value.data)

        addTrans.theme.resolveAttribute(R.attr.buttonDisableBgColor, addTrans.value, true)
        binding.saveBtn.apply {
            isEnabled = false
            setCardBackgroundColor(addTrans.value.data)
        }
        addTrans.theme.resolveAttribute(R.attr.buttonDisabledTextColor, addTrans.value, true)
        binding.saveText.setTextColor(addTrans.value.data)
        binding.saveText.alpha = 0.38F
        addTrans.theme.resolveAttribute(R.attr.buttonDisableBgColor, addTrans.value, true)
        binding.doneBtn.apply {
            isEnabled = false
            setCardBackgroundColor(addTrans.value.data)
        }
        addTrans.theme.resolveAttribute(R.attr.buttonDisabledTextColor, addTrans.value, true)
        binding.tickIcon.apply {
            setColorFilter(
                addTrans.value.data,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            alpha = 0.38F
        }
    }
    private fun changeBtnState(isDisable: Boolean = false) {
        if (result == "0" || result == "N/A") {
            disableBtn()
        }
        else {
            if (isDisable) {
                disableBtn()
            }
            else {
                binding.nextBtn.isEnabled = true
                addTrans.theme.resolveAttribute(R.attr.nextButtonBgColor, addTrans.value, true)
                binding.nextBtn.setCardBackgroundColor(addTrans.value.data)
                binding.nextText.alpha = 1F
                binding.arrowForwardIcon.alpha = 1F
                addTrans.theme.resolveAttribute(R.attr.nextButtonTextColor, addTrans.value, true)
                binding.nextText.setTextColor(addTrans.value.data)
                binding.arrowForwardIcon.setColorFilter(
                    addTrans.value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.saveBtn.apply {
                    isEnabled = true
                    alpha = 1F
                    if (!isExpense) {
                        setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryRed))
                    }
                    else {
                        setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryGreen))
                    }

                }
                binding.saveText.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkPrimaryTextColor))
                binding.saveText.alpha = 1F
                binding.doneBtn.apply {
                    isEnabled = true
                    alpha = 1F
                    if (!isExpense) {
                        setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryRed))
                    }
                    else {
                        setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryGreen))
                    }
                }

                binding.tickIcon.apply {
                    setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.white),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    alpha = 1F
                }
            }

        }
    }

}