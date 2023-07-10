package io.paraga.moneytrackerdev.views.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.Timestamp
import io.paraga.moneytrackerdev.databinding.ActivityAddTransBinding
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.constants.Constants.Companion.ACTION_UPDATE_WIDGET
import io.paraga.moneytrackerdev.constants.ConstantsHelper
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.*
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.viewmodels.transaction.AddTransVM
import io.paraga.moneytrackerdev.views.adManager
import io.paraga.moneytrackerdev.views.category.ChooseCategoryFrag
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.widget.SmallWidget
import java.text.DateFormat
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import io.paraga.moneytrackerdev.views.currentMonthYear
import java.lang.IllegalStateException
import java.time.YearMonth
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates
import io.paraga.moneytrackerdev.views.selectedWalletId



class AddTrans : AppCompatActivity() {
    lateinit var binding: ActivityAddTransBinding
    val addTransVM = AddTransVM()
    val today = LocalDate.now().dayOfMonth
    var tranType = Enums.TransTypes.EXPENSE.value
    lateinit var calculatorFrag: CalculatorFrag
    var isExpense = false
    val firebaseManager = FirebaseManager(this)
    var selectedWallet: Wallet = Wallet()
    var selectedWalletId: String = ""
    var currentCurrencyName: String = ""
    var trans: Transaction = Transaction()
    var date: String = ""
    var milliseconds by Delegates.notNull<Long>()
    var category: Category = Category()
    var defaultCategory = Category(color = "#BE4DE7",image = "ic-general", title = "Others")
    var categoryTemp: Category = Category()
    var transDate: LocalDateTime = LocalDateTime.now()
    var isEditTrans: Boolean = false
    var isFromBudget by Delegates.notNull<Boolean>()
    private var mSelectDate = ""
    private var addTranCalendar = false
    private var isEdited: Boolean = false
    val now = LocalDate.now()
    var transTemp: Transaction = Transaction()
    var selectedDate = ""
    private var mLastContentHeight = 0
    lateinit var keyboardLayoutListener:ViewTreeObserver.OnGlobalLayoutListener
    var isCalculatorOpened = false
    var adView: AdView? = null
    var previousSelectedWalletId = ""
    private var mInterstitialAd: InterstitialAd? = null
    val value = TypedValue()

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }
    @SuppressLint("SimpleDateFormat", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tranType = intent.getIntExtra("widget",1)
        keyboardHandling()
        setAd()
        getWallet {
            initViews()
        }
    }

    fun initViews() {
        binding.remarkEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                binding.clearRemarkBtn.setColorFilter(
                    value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                binding.amountBottomLine.layoutParams.width = binding.root.width
                (binding.saveBtnLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, 80F)
                (binding.dynamicLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
                binding.dynamicLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            else {
                binding.amountBottomLine.layoutParams.width = 0
                (binding.saveBtnLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, 10F)
                if (binding.remarkEditText.lineCount > 2) {
                    (binding.dynamicLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, -(binding.remarkEditText.measuredHeight.toFloat()-70F))
                }
                else {
                    (binding.dynamicLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, -40F)
                }
                if (binding.remarkEditText.text.isNotEmpty()) {
                    theme.resolveAttribute(R.attr.primaryText, value, true)
                    binding.clearRemarkBtn.setColorFilter(value.data)
                }
                else {
                    theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                    binding.clearRemarkBtn.setColorFilter(
                        value.data,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
        binding.remarkEditText.addTextChangedListener {
            if (binding.remarkEditText.text.isNotEmpty()) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                binding.clearRemarkBtn.setColorFilter(value.data)
            }
            else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                binding.clearRemarkBtn.setColorFilter(
                    value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
        binding.clearRemarkBtn.setOnClickListener {
            binding.remarkEditText.text.clear()
        }
        isFromBudget = intent.getBooleanExtra(Enums.Extras.IS_FROM_BUDGET.value, false)
        isEditTrans = intent.getBooleanExtra("isEditTrans", false)
        addTranCalendar = intent.getBooleanExtra(ConstantsHelper.Option.add_tran_calendar,false)

        if (isEditTrans) {

            category.image = intent.getStringExtra("categoryImage").toString()
            category.title = intent.getStringExtra("categoryTitle").toString()
            category.color = intent.getStringExtra("categoryColor").toString()
            category.selectedCount = intent.getIntExtra("selectedCount", 0)
            milliseconds = intent.getLongExtra("milliseconds",0)
            transDate = LocalDateTime.ofInstant(Date(milliseconds).toInstant(), ZoneId.systemDefault())
            trans.amount = intent.getDoubleExtra("amount", 0.0)
            trans.currency = intent.getStringExtra("currency").toString()
            trans.remark = intent.getStringExtra("remark").toString()
            trans.type = intent.getIntExtra("type", 0)
            trans.walletID = intent.getStringExtra("walletID").toString()
            trans.documentId = intent.getStringExtra("documentId").toString()
            trans.category = category
            trans.date = Timestamp(Date(milliseconds))
            // copy to a temp var
            transTemp.amount = trans.amount
            transTemp.currency = trans.currency
            transTemp.remark = trans.remark
            transTemp.type = trans.type
            transTemp.walletID = trans.walletID
            transTemp.documentId = trans.documentId
            transTemp.category = trans.category
            transTemp.date = trans.date
            if (transTemp == trans) {
                theme.resolveAttribute(R.attr.buttonDisableBgColor, value, true)
                binding.saveBtnLayout.apply {
                    isEnabled = false
                    setCardBackgroundColor(
                        value.data
                    )
                }
                theme.resolveAttribute(R.attr.buttonDisabledTextColor, value, true)
                binding.saveBtn.setTextColor(value.data)
                binding.saveBtn.alpha = 0.38F
            }
            binding.amount.doOnTextChanged { _, _, _, _ ->
                enableButton()
            }
            binding.remarkEditText.doOnTextChanged { _, _, _, _ ->
                enableButton()
            }
            binding.selectedDate.doOnTextChanged { _, _, _, _ ->
                enableButton()
            }
            binding.categoryName.doOnTextChanged { _, _, _, _ ->
                enableButton()
            }
            binding.amount.text = Extension.toBigDecimal(trans.amount.toString())
            currentCurrencyName = trans.currency.toString()

            binding.remarkEditText.setText(trans.remark)
            tranType = trans.type as Int
            selectedWalletId = trans.walletID.toString()
            previousSelectedWalletId = selectedWalletId
            binding.incomeBtn.isEnabled = false
            binding.expenseBtn.isEnabled = false
            if (trans.type == Constants.INCOME) {
                binding.layoutChip.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.secondaryGreen
                    )
                )
                theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
                binding.incomeText.setTextColor(value.data)
            } else {
                binding.layoutChip.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.secondaryRedBgColor
                    )
                )
                theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
                binding.expenseText.setTextColor(value.data)
            }
            addTransVM.selectedCategory = category
            binding.categoryIcon.setImageResource(
                Extension.getResouceId(this, category.image)
            )
            binding.categoryName.text = Extension.getStringResourceByName(this, category.title.toString())
            binding.currencySymbol.text = Extension.getCurrencySymbol(
                currentCurrencyName
            )
            walletList.value?.forEach {
                if (it.walletId == selectedWalletId) {
                    selectedWallet = it.wallet
                    changeSelectedWalletView()
                }
            }
            binding.selectedDate.text = getString(
                R.string.blank,
                String.format("%02d", transDate.dayOfMonth)
                        + " "
                        + transDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        + " "
                        + transDate.year.toString()
            )
            if (currentCurrencyName == selectedWallet.currency) {
                binding.exchangeRate.visibility = View.GONE
            }
            else {
                val convertedAmount = getString(
                    R.string.blank,
                    selectedWallet.currency
                            + " "
                            + String.format("%.2f",Extension.convertCurrency(currentCurrencyName,
                        selectedWallet.currency.toString(),
                        trans.amount as Double,
                        this))
                )
                binding.exchangeRate.text = convertedAmount
                binding.exchangeRate.visibility = View.VISIBLE
            }
        }
        else {
            binding.deleteLayout.visibility = View.GONE
            if (addTranCalendar) {
                mSelectDate = intent.getStringExtra(ConstantsHelper.Option.mSELECT_DATE).toString()
                binding.selectedDate.text = TimeConverter.dateFormatCalendarAdd(mSelectDate)
            } else {
                binding.selectedDate.text = getString(
                    R.string.blank,
                    today.toString()
                            + " "
                            + LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            + " "
                            + LocalDate.now().year.toString()
                )
            }
            if (isFromBudget) {
                val bundle = intent.getBundleExtra("bundle")
                val budgetCategory = bundle?.getSerializable("category" ) as Category
                if (budgetCategory.title != getString(R.string.all_categories)) {
                    category = budgetCategory
                    addTransVM.selectedCategory = category
                    binding.categoryName.text = Extension.getStringResourceByName(this, category.title.toString())
                    binding.categoryIcon.setImageResource(
                        Extension.getResouceId(this, category.image)
                    )
                }
                selectedWalletId = intent.getStringExtra(Enums.Extras.WALLET_ID.value).toString()
                if (selectedWalletId == "") {
                    walletList.value?.forEach {
                        if (it.isDefault) {
                            selectedWallet = it.wallet
                            selectedWalletId = it.walletId
                            currentCurrencyName = selectedWallet.currency.toString()
                            changeSelectedWalletView()
                        }
                    }
                }
                else {
                    walletList.value?.forEach {
                        if (it.walletId == selectedWalletId) {
                            selectedWallet = it.wallet
                            currentCurrencyName = selectedWallet.currency.toString()
                            changeSelectedWalletView()
                        }
                    }
                }
                showCalculator()
            }
            else {
                // init selected wallet
                walletList.value?.forEach {
                    if(io.paraga.moneytrackerdev.views.selectedWalletId == "") {
                        if (it.isDefault) {
                            selectedWallet = it.wallet
                            selectedWalletId = it.walletId
                            currentCurrencyName = selectedWallet.currency.toString()
                            changeSelectedWalletView()
                            return@forEach
                        }
                    }
                    else if (it.walletId == io.paraga.moneytrackerdev.views.selectedWalletId){
                        selectedWallet = it.wallet
                        selectedWalletId = it.walletId
                        currentCurrencyName = selectedWallet.currency.toString()
                        changeSelectedWalletView()
                        return@forEach
                    }

                }
                showCalculator()
            }

        }

        changeSelectedDateStyle()

        if (currentCurrencyName == "") {
            binding.transAmountLayout.isEnabled = false
        } else {
            binding.transAmountLayout.isEnabled = true
            binding.transAmountLayout.setOnClickListener {
                showCalculator()
            }
        }

//        if (binding.amount.text.toString() != "0") {
//            binding.amountSign.setText(R.string.negative)
//        }

        // remove sign when amount = 0
        if (binding.amount.text.toString() == "0") {
            binding.amountSign.visibility = View.GONE
        }
        else {
            binding.amountSign.visibility = View.VISIBLE

        }

//
//        Extension.changeStroke(this, binding.dateLayout, R.color.primaryGreen)
//


        binding.chooseDate.setOnClickListener {
            val chooseDateFrag = ChooseDateFrag()
            chooseDateFrag.addTrans = this
            chooseDateFrag.show(supportFragmentManager, "")
        }

        binding.chooseCategory.setOnClickListener {
            val chooseCategoryFrag = ChooseCategoryFrag(this)
            chooseCategoryFrag.addTrans = this
            chooseCategoryFrag.isExpense = (tranType == Enums.TransTypes.EXPENSE.value)
            chooseCategoryFrag.show(supportFragmentManager, "")
        }

        binding.incomeBtn.setOnClickListener {
            // set attr color for releated views
            theme.resolveAttribute(R.attr.secondaryIncomeBgColor, value, true)
            binding.layoutChip.setCardBackgroundColor(
                value.data
            )
            if (isEditTrans) {
                binding.incomeBtn.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black16
                    )
                )
            }
            else {
                theme.resolveAttribute(R.attr.tertiaryIncomeBgColor, value, true)
                binding.incomeBtn.setCardBackgroundColor(
                    value.data
                )
                theme.resolveAttribute(R.attr.incomeSelectedTextColor, value, true)
                binding.incomeText.setTextColor(value.data)
            }
            binding.expenseBtn.setCardBackgroundColor(
                0
            )
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.expenseText.setTextColor(value.data)
            theme.resolveAttribute(R.attr.secondaryIncomeBgColor, value, true)
            binding.chooseWalletLayout.setCardBackgroundColor(
                value.data
            )
            binding.deleteLayout.setCardBackgroundColor(
                value.data
            )

            // set another attr color for another bg
            theme.resolveAttribute(R.attr.incomeBgColor, value, true)
            binding.topLayout.setBackgroundColor(value.data)
//            binding.saveBtn.background.setTint(ContextCompat.getColor(this, R.color.primaryGreen))
            if (!isEditTrans) {
                if (binding.amount.text.toString() != "0") {
                    theme.resolveAttribute(R.attr.incomeButtonBgColor, value, true)
                    binding.saveBtnLayout.setCardBackgroundColor(value.data)
                    binding.saveBtn.alpha = 1F
                    binding.saveBtn.setTextColor(ContextCompat.getColor(this@AddTrans,R.color.darkPrimaryTextColor))
                }
            }
            binding.amount.setTextColor(ContextCompat.getColor(this, R.color.primaryGreen))
            binding.amountSign.setTextColor(ContextCompat.getColor(this, R.color.primaryGreen))
            binding.currencySymbol.setTextColor(ContextCompat.getColor(this, R.color.primaryGreen))
            binding.amountSign.setText(R.string.positive)
            theme.resolveAttribute(R.attr.incomeBgColor, value, true)
            changeStatusBarColor(value.data)
            tranType = Enums.TransTypes.INCOME.value

            resetCategory()
        }

        binding.expenseBtn.setOnClickListener {
            theme.resolveAttribute(R.attr.secondaryExpenseBgColor, value, true)

            binding.layoutChip.setCardBackgroundColor(
                value.data
            )
            binding.incomeBtn.setCardBackgroundColor(
                0
            )
            if (isEditTrans) {
                binding.expenseBtn.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black16
                    )
                )
            }
            else {
                theme.resolveAttribute(R.attr.tertiaryExpenseBgColor, value, true)
                binding.expenseBtn.setCardBackgroundColor(
                    value.data
                )
                theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
                binding.incomeText.setTextColor(value.data)
                theme.resolveAttribute(R.attr.expenseSelectedTextColor, value, true)
                binding.expenseText.setTextColor(
                    value.data
                )
            }
            theme.resolveAttribute(R.attr.secondaryExpenseBgColor, value, true)
            binding.chooseWalletLayout.setCardBackgroundColor(
                value.data
            )
            binding.deleteLayout.setCardBackgroundColor(
                value.data
            )
            theme.resolveAttribute(R.attr.expenseBgColor, value, true)
            binding.topLayout.setBackgroundColor(value.data)
            if (!isEditTrans) {
                if (binding.amount.text.toString() != "0") {
                    theme.resolveAttribute(R.attr.expenseButtonBgColor, value, true)
                    binding.saveBtnLayout.setCardBackgroundColor(value.data)
                    binding.saveBtn.alpha = 1F
                    binding.saveBtn.setTextColor(ContextCompat.getColor(this@AddTrans,R.color.darkPrimaryTextColor))
                }
            }
            binding.amount.setTextColor(ContextCompat.getColor(this, R.color.primaryRed))
            binding.amountSign.setTextColor(ContextCompat.getColor(this, R.color.primaryRed))
            binding.currencySymbol.setTextColor(ContextCompat.getColor(this, R.color.primaryRed))

            binding.amountSign.setText(R.string.negative)
            theme.resolveAttribute(R.attr.expenseBgColor, value, true)
            changeStatusBarColor(value.data)

            tranType = Enums.TransTypes.EXPENSE.value

            resetCategory()
        }

        if (tranType == Enums.TransTypes.EXPENSE.value) {
            binding.expenseBtn.performClick()
        }
        else {
            binding.incomeBtn.performClick()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.chooseWalletLayout.setOnClickListener {
            val chooseWalletFrag = ChooseWalletFrag()
            chooseWalletFrag.isFromMainActivity = false
            chooseWalletFrag.isFromImportActivity = false
            chooseWalletFrag.addTrans = this
            chooseWalletFrag.show(supportFragmentManager, "")
        }

        binding.saveBtnLayout.setOnClickListener {
            saveOrUpdateTrans()
            updateWidgetSmall()
        }



        binding.deleteLayout.setOnClickListener {
            DialogHelper.showPrimaryDialog(this,
                Enums.DialogType.TRANSACTION.value,
                onOkayPressed = { alertDialog ->
                    addTransVM.deleteTrans(trans.documentId.toString(),
                        this,
                        onSuccess = {

                        },
                        onFailure = {
                            alertDialog.dismiss()
                        })
                    Toast(this).showCustomToast(
                        getString(R.string.transaction_is_deleted),
                        this
                    )
                    alertDialog.dismiss()
                    finish()
                })

        }
    }

    fun enableButton() {
        if (binding.amount.text.toString() != Extension.toBigDecimal(trans.amount.toString()) || binding.remarkEditText.text.toString() != trans.remark || binding.categoryName.text.toString() != Extension.getStringResourceByName(this, trans.category.title.toString()) || selectedDate != getString(R.string.blank,
                transDate.dayOfMonth.toString() + " " + transDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " +
                        transDate.year.toString())
            || previousSelectedWalletId != selectedWalletId) {
            //validation add transaction
            if (tranType == Enums.TransTypes.EXPENSE.value) {
                theme.resolveAttribute(R.attr.expenseButtonBgColor, value, true)
                binding.saveBtnLayout.apply {
                    isEnabled = true
                    setCardBackgroundColor(value.data)
                }
                binding.saveBtn.alpha = 1F
                binding.saveBtn.setTextColor(ContextCompat.getColor(this@AddTrans,R.color.darkPrimaryTextColor))
            } else {
                theme.resolveAttribute(R.attr.incomeButtonBgColor, value, true)
                binding.saveBtnLayout.apply {
                    isEnabled = true
                    setCardBackgroundColor(value.data)
                }
                binding.saveBtn.setTextColor(ContextCompat.getColor(this@AddTrans,R.color.darkPrimaryTextColor))
                binding.saveBtn.alpha = 1F
            }

        } else {
            theme.resolveAttribute(R.attr.buttonDisableBgColor, value, true)
            binding.saveBtnLayout.apply {
                isEnabled = false
                setCardBackgroundColor(
                    value.data
                )
            }
            theme.resolveAttribute(R.attr.buttonDisabledTextColor, value, true)
            binding.saveBtn.setTextColor(value.data)
            binding.saveBtn.alpha = 0.38F
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (binding.remarkEditText.isFocused) {
            removeFocus(binding.remarkEditText, event)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun keyboardHandling() {

        keyboardLayoutListener = OnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            val height =window.decorView.height

            if(height - r.bottom > height*0.1399){
                //keyboard is open
                Log.d("imm", "opened")
                binding.amountBottomLine.layoutParams.width = binding.root.width
                (binding.saveBtnLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, 80F)
                (binding.dynamicLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
                binding.dynamicLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            } else{
//                binding.remarkEditText.clearFocus()
                //keyboard is close
//                Log.d("imm", "closed")
//                val imm =
//                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                if (imm.isAcceptingText()) {
//                    binding.amountBottomLine.layoutParams.width = 0
//                    (binding.saveBtnLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, 10F)
//                    (binding.dynamicLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = Extension.dpToPx(this@AddTrans, -40F)
//                }

            }
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

    }
    private fun changeSelectedDateStyle() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        if (isEditTrans) {
            selectedDate = getString(
                R.string.blank,
                String.format("%02d", transDate.dayOfMonth)
                        + " "
                        + transDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + " "
                        + transDate.year.toString()
            )
        }
        else if (addTranCalendar) {
            val originalDate = LocalDate.parse(mSelectDate)
            val formattedDate = originalDate.format(formatter)
            transDate = originalDate.atStartOfDay()
            selectedDate = formattedDate.toString()
        }
        else {
            selectedDate = getString(
                R.string.blank,
                today.toString()
                        + " "
                        + LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + " "
                        + LocalDate.now().year.toString()
            )
        }
        var selectedDate: LocalDate
        try {
            selectedDate = LocalDate.parse(this.selectedDate, formatter)
        }
        catch (exc: Exception) {
            selectedDate = now
        }
        now.format(formatter)
        when (selectedDate) {
            now -> {
                Extension.changeStroke(this, binding.dateLayout, R.color.primaryGreen, 1F)
                binding.selectedDate.setTextColor(ContextCompat.getColor(this, R.color.primaryGreen))
                binding.selectedDate.text = Enums.General.TODAY.value
            }
            now.plusDays(1) -> {
                Extension.changeStroke(this, binding.dateLayout, R.color.yellow, 1F)
                binding.selectedDate.setTextColor(ContextCompat.getColor(this, R.color.yellow))
                binding.selectedDate.text = Enums.General.TOMORROW.value
            }
            now.minusDays(1) -> {
                Extension.changeStroke(this, binding.dateLayout, R.color.primaryRed, 1F)
                binding.selectedDate.setTextColor(ContextCompat.getColor(this, R.color.primaryRed))
                binding.selectedDate.text = Enums.General.YESTERDAY.value
            }
            else -> {
                theme.resolveAttribute(R.attr.transDateTextColor, value, true)
                Extension.changeStroke(this, binding.dateLayout, value.data, 1F, isColorInt = true)
                binding.selectedDate.setTextColor(value.data)
                binding.selectedDate.text = binding.selectedDate.text
            }
        }
    }

    private fun resetCategory() {
        // reset only in add trans
        if (!isEditTrans && !isFromBudget) {
            if (!isCalculatorOpened) {
                binding.categoryIcon.setImageResource(R.drawable.ic_other)
                binding.categoryName.text = Extension.getStringResourceByName(this, defaultCategory.title.toString())
                addTransVM.selectedCategory = defaultCategory
            }
        }
    }
    private fun removeFocus(editText: EditText, event: MotionEvent?) {
        val v = currentFocus
        val outRect = Rect()
        editText.getGlobalVisibleRect(outRect)
        binding.saveBtn.getGlobalVisibleRect(outRect)
        if (!outRect.contains(event?.rawX?.toInt() ?: 0, event?.rawY?.toInt() ?: 0)) {
            editText.clearFocus()
            //
            // Hide keyboard
            //
            val imm =
                v!!.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)


        }
    }

    fun getWallet(onSuccess: () -> Unit) {
        if (walletList.value?.size == 0) {
            firebaseManager.initWalletList {
                onSuccess()
            }
        } else {
            onSuccess()
        }
    }

     fun saveOrUpdateTrans() {
        if (binding.amount.text.toString() == "0") {
            Toast(this).showCustomToast(getString(R.string.invalid_trans_amount), this)
        } else {

            val currentDate = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val dateInString = formatter.format(currentDate).toString().split(" ")
            val currentTime = dateInString[1]
            val amount: Number = binding.amount.text.toString().replace(",","").toDouble()
            try {
                date = "$selectedDate $currentTime"
                if (isEditTrans) {
                    trans.amount = amount
                    trans.currency = currentCurrencyName
//                    if (!(transDate.dayOfMonth == tempDate.dayOfMonth && transDate.monthValue == tempDate.monthValue
//                        && transDate.year == tempDate.year)) {
//                    }
                    trans.date = Timestamp(Date(date))
                    trans.remark = binding.remarkEditText.text.toString()
                    trans.type = tranType
                    trans.walletID = selectedWalletId
                    trans.category = addTransVM.selectedCategory

                    updateCurrentMonthYear(date)

                    addTransVM.updateTrans(trans, onSuccess = {

                    },
                        onFailure = {
                            Toast(this).showCustomToast(
                                getString(R.string.transaction_is_not_saved),
                                this
                            )
                        })
                    Toast(this).showCustomToast(
                        getString(R.string.transaction_is_saved),
                        this
                    )
                    adManager.showFullScreenAd(this)
                    finish()


                } else {
                    updateCurrentMonthYear(date)
                    addTransVM.addTrans(
                        amount = amount,
                        currency = currentCurrencyName,
                        date = date,
                        remark = binding.remarkEditText.text.toString(),
                        type = tranType,
                        walletID = selectedWalletId,
                        context = this,
                        onSuccess = {

                        },
                        onFailure = {
                            Toast(this).showCustomToast(
                                getString(R.string.transaction_is_not_added),
                                this
                            )
                        }
                    )
                    Toast(this).showCustomToast(
                        getString(R.string.transaction_is_added),
                        this
                    )
                    adManager.showFullScreenAd(this)
                    finish()
                }

            } catch (exc: Exception) {
                Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)

            }
        }
    }

    fun changeSelectedWalletView() {
        binding.walletIcon.setBackgroundResource(
            Extension.getResouceId(this, selectedWallet.symbol)
        )
        binding.walletName.text = selectedWallet.name
    }

    // Update widgetSmall
    private fun updateWidgetSmall() {
        val widgetIntent = Intent(this, SmallWidget::class.java)
        widgetIntent.action = ACTION_UPDATE_WIDGET
        sendBroadcast(widgetIntent)
    }
    private fun showCalculator() {
        calculatorFrag = CalculatorFrag()
        if (calculatorFrag.isAdded){
            return
        }
        calculatorFrag.isExpense = (tranType == Enums.TransTypes.EXPENSE.value)
        calculatorFrag.wallet = selectedWallet
        calculatorFrag.currentCurrencyName = currentCurrencyName
        if (!supportFragmentManager.isDestroyed) {
            calculatorFrag.show(supportFragmentManager, "")
        }
    }

    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }


    private fun setAd() {
        val adContainerView = findViewById<FrameLayout>(R.id.ad_view_container)
        if (!isProUser.value!!) {

            // Add test device
            val testDevices: MutableList<String> = ArrayList()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            testDevices.add("BE478A06BAE68E0CC7F507CEED161B1E")
            testDevices.add("4B922F2C530A4092877CC8571129B81C")
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)

            // Banner
            adView = AdView(this)
            adView?.adUnitId = Config.BANNER_ID
            adView?.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    adView?.visibility = View.GONE
                }
            }
            adContainerView.addView(adView)
            adManager.loadBanner(adView, this)

        } else {
            adContainerView.visibility = View.GONE
        }
    }

    fun updateCurrentMonthYear(date: String) {
        val localDate = Date(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        currentDisplayedMonth = YearMonth.of(localDate.year, localDate.month)
        currentMonthYear.postValue(getString(
            R.string.blank, localDate.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " "
                    + localDate?.year.toString()))
    }



    override fun onBackPressed() {
        super.onBackPressed()
        binding.remarkEditText.clearFocus()
    }

    override fun onDestroy() {
        if (adView != null) {
            adView?.destroy()
        }
        super.onDestroy()

    }

}