package io.paraga.moneytrackerdev.views.importFile.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.ActivityImportDetailBinding
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.ExcelHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.utils.helper.TimeConverter
import io.paraga.moneytrackerdev.views.importFile.adapter.ImportListAdapter
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.MatchDataTypeFragment
import io.paraga.moneytrackerdev.views.transaction.ChooseWalletFrag
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class ImportDetailActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityImportDetailBinding
    private  var  mAdapter : ImportListAdapter?= null
    var mTypeList: ArrayList<ImportModel> = arrayListOf()
    private var mRowListString: ArrayList<String> = arrayListOf()
    private val mListLocalDate: ArrayList<String> = arrayListOf()
    private var arrayListRemark: ArrayList<String> = arrayListOf()
    private var arrayListDate: ArrayList<String> = arrayListOf()
    private var arrayListCategory: ArrayList<String> = arrayListOf()
    private var arrayListIncome: ArrayList<String> = arrayListOf()
    private var arrayListExpense: ArrayList<String> = arrayListOf()
    private var arrayListAuto: ArrayList<String> = arrayListOf()
    private var mListPosition: ArrayList<PositionModel> = arrayListOf()
    private var import: ImportModel = ImportModel()
    var selectedWallet: Wallet = Wallet()
    private var newCategory = Category()
    var selectedIconName = "ic_defult_category"
    var selectedCategoryColor = "#BE4DE7"
//    private val defaultCategory : Category  = Category(color = "#F0831A",image = "ic-general", title = "Others")
    private val defaultCategory : Category  = Category(color = "#BE4DE7",image = "ic-general", title = "Others")
    var selectedWalletId: String = ""
    var currentCurrencyName: String = ""
    private var uri = ""
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    val dateInString = formatter.format(currentDate).toString().split(" ")
    val currentTime = dateInString[1]
    private val  dotSeparatorFormat = NumberFormat.getInstance(Locale.getDefault())
    private val  commaSeparatorFormat = NumberFormat.getInstance(Locale.ENGLISH)
    var stringNumber : Double = 0.0
    lateinit var resultNumber : Number
    val value = TypedValue()

    companion object {
        private var fileType = ""
        private const val extensionXLS = "XLS"
        private const val extensionXLXS = "XLXS"
    }

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityImportDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        // init selected wallet
        walletList.value?.forEach {
            if (it.isDefault) {
                selectedWallet = it.wallet
                selectedWalletId = it.walletId
                currentCurrencyName = selectedWallet.currency.toString()
                changeSelectedWalletView()
            }
        }
        getCategoryList {

        }
        getIntentData()
        readExcelFile(this,uri.toUri())
        initNameFileExcel()
        initListener()
        setDarkThem()

    }
    private fun setDarkThem() {
        theme.resolveAttribute(R.attr.colorTextSelectImport,value, true)
        mBinding.textDefaultValue.setTextColor(value.data)
        theme.resolveAttribute(R.attr.colorNameWallet,value, true)
        mBinding.textNameWallet.setTextColor(value.data)
        theme.resolveAttribute(R.attr.colorTextHead,value, true)
        mBinding.textWallet.setTextColor(value.data)
    }


    private fun removePrefix(s: String?, prefix: String?): String? {
        return if (s != null && prefix != null && s.startsWith(prefix)) {
            s.substring(prefix.length)
        } else s
    }

    private fun initListener() {
        mBinding.imageArrowBack.setOnClickListener {
            mAdapter?.list?.clear()
            finish()
        }
        mBinding.linearWallet.setOnClickListener {
            val chooseWalletFrag = ChooseWalletFrag()
            chooseWalletFrag.isFromMainActivity = false
            chooseWalletFrag.isFromImportActivity = true
            chooseWalletFrag.importDetailBinding = this
            chooseWalletFrag.show(supportFragmentManager, "")
        }
        mBinding.saveBtnLayout.setOnClickListener {
            if(arrayListAuto.isEmpty()) {
                if (arrayListIncome.size == arrayListExpense.size) {
                    val transactionList: ArrayList<ImportTransaction> = arrayListOf()
                    for (index in 0 until arrayListIncome.size) {
                        if (arrayListIncome[index].isEmpty()) {
                            if (arrayListExpense.isNotEmpty()) {
                                arrayListIncome[index] = "-${arrayListExpense[index]}"
                            } else {
                                arrayListIncome[index] = "-${arrayListExpense[index]}"
                            }
                        }
                        try {
                            val amount = arrayListIncome[index]
                            try {
                                resultNumber = commaSeparatorFormat.parse(amount) as Number
                                stringNumber = resultNumber.toDouble()
                            } catch (e : java.lang.Exception) {
                                resultNumber = dotSeparatorFormat.parse(amount) as Number
                                stringNumber = resultNumber.toDouble()
                            }
                            if (arrayListCategory.isEmpty()) {
                                //no need to check create new category
                                if (arrayListRemark.isEmpty()){
                                    //remark is empty
                                    if (stringNumber > 0) {
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    } else {
                                        val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                        transactionList.add(ImportTransaction(
                                            amount = amountExpense!!,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.EXPENSE,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }
                                } else {
                                    // remark isNotEmpty
                                    if (stringNumber > 0) {
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    } else {
                                        val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                        transactionList.add(ImportTransaction(
                                            amount = amountExpense!!,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.EXPENSE,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }
                                }
                            } else {
                                // need to check new create category
                                if (arrayListRemark.isEmpty()){
                                    val newListCategory: ArrayList<String> = arrayListOf()
                                    newListCategory.add(arrayListCategory[index])
                                    val valueList : ArrayList<String> = arrayListOf()
                                    newListCategory.forEach {
                                        if (it.isNotEmpty()) {
                                            valueList.add(it)
                                        }
                                    }
                                    if (stringNumber > 0) {
                                        // amount income
                                        if (arrayListCategory[index].isEmpty()) {
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    } else {
                                        // amount expense
                                        val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                        if (arrayListCategory[index].isEmpty()) {
                                            transactionList.add(ImportTransaction(
                                                amount = amountExpense!!,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = amountExpense!!,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }
                                } else {
                                    //remark isNotEmpty
                                    if (stringNumber > 0) {
                                        //amount income
                                        if (arrayListCategory[index].isEmpty()) {
                                                transactionList.add(ImportTransaction(
                                                    amount = stringNumber,
                                                    currency = selectedWallet.currency!!,
                                                    date = "",
                                                    remark = arrayListRemark[index],
                                                    type = Constants.INCOME,
                                                    walletID = selectedWalletId,
                                                    selectedWallet = selectedWallet,
                                                    newCategory = defaultCategory
                                                ))
                                            Log.i("asadsad","$transactionList")
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                    amount = stringNumber,
                                                    currency = selectedWallet.currency!!,
                                                    date = "",
                                                    remark = arrayListRemark[index],
                                                    type = Constants.INCOME,
                                                    walletID = selectedWalletId,
                                                    selectedWallet = selectedWallet,
                                                    newCategory = newCategory
                                                ))
                                            }
                                    } else {
                                        //amount expense
                                        val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                        if (arrayListCategory[index].isEmpty()){
                                            transactionList.add(ImportTransaction(
                                                amount = amountExpense!!,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                            Log.i("asadsad","123:$transactionList")
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = amountExpense!!,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }
                                }
                            }
                        }catch (e :Exception) {
                        }
                    }
                    for (i in 0 until transactionList.size) {
                        if (transactionList[i].date.isEmpty() && mListLocalDate.isNotEmpty()) {
                            transactionList[i].date = mListLocalDate[i]
                        }
                    }
                    if(mListLocalDate.isNotEmpty() && transactionList.isNotEmpty()) {
                        val i = Intent(this, ImportVerifyActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("transactionList", transactionList as Serializable)
                        i.putExtra("bundle", bundle)
                        startActivity(i)
                    } else {
                        Toast.makeText(this,"pls match the type",Toast.LENGTH_LONG).show()
                    }
                } else {
                    // check array income or expense
                    if (arrayListIncome.isNotEmpty()){
                        val transactionList: ArrayList<ImportTransaction> = arrayListOf()
                        for (index in 0 until arrayListIncome.size) {
                            try {
                                val amount = arrayListIncome[index]
                                try {
                                    resultNumber = commaSeparatorFormat.parse(amount) as Number
                                    stringNumber = resultNumber.toDouble()
                                } catch (e : java.lang.Exception) {
                                    resultNumber = dotSeparatorFormat.parse(amount) as Number
                                    stringNumber = resultNumber.toDouble()
                                }
                                if (arrayListCategory.isEmpty()){
                                    if (arrayListRemark.isEmpty()){
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }else{
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }
                                } else {
                                    // need to check create new category
                                    //amount income
                                    if (arrayListRemark.isEmpty()){
                                        if (arrayListCategory[index].isEmpty()) {
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }else{
                                        if (arrayListCategory[index].isEmpty()) {
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.INCOME,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }

                                    }
//                                    categories[Enums.DB.INCOME_FIELD.value] = incomeModel.value!!
//                                    categories[Enums.DB.EXPENSE_FIELD.value] = expenseModel.value!!
//                                    newCategoryVM.updateCategory(
//                                        categories
//                                    ) {
//                                    }
                                }
                            }catch (e :Exception) {
                            }
                        }
                        for (i in 0 until transactionList.size) {
                            if (transactionList[i].date.isEmpty()&& mListLocalDate.isNotEmpty()) {
                                transactionList[i].date = mListLocalDate[i]
                            }
                        }

                        if(mListLocalDate.isNotEmpty()&& transactionList.isNotEmpty()) {
                            val i = Intent(this, ImportVerifyActivity::class.java)
                            val bundle = Bundle()
                            bundle.putSerializable("transactionList", transactionList as Serializable)
                            i.putExtra("bundle", bundle)
                            startActivity(i)
                        } else {
                            Toast.makeText(this,"pls match the type",Toast.LENGTH_LONG).show()
                        }
                    }
                    if (arrayListExpense.isNotEmpty()){
                        val transactionList: ArrayList<ImportTransaction> = arrayListOf()
                        for (index in 0 until arrayListExpense.size) {
                            try {
                                val amount = arrayListExpense[index]
                                try {
                                    resultNumber = commaSeparatorFormat.parse(amount) as Number
                                    stringNumber = resultNumber.toDouble()
                                } catch (e : java.lang.Exception) {
                                    resultNumber = dotSeparatorFormat.parse(amount) as Number
                                    stringNumber = resultNumber.toDouble()
                                }
                                if (arrayListCategory.isEmpty()) {
                                    if (arrayListRemark.isEmpty()){
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.EXPENSE,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }else{
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.EXPENSE,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    }
                                } else {
                                    // need to check create new category
                                    //amount expense
                                    if (arrayListRemark.isEmpty()){
                                        if (arrayListCategory[index].isEmpty()) {
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }

                                    }else{
                                        if (arrayListCategory[index].isEmpty()){
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        } else {
                                            newCategory.title = arrayListCategory[index]
                                            newCategory.color = selectedCategoryColor
                                            newCategory.image = selectedIconName
                                            transactionList.add(ImportTransaction(
                                                amount = stringNumber,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }
                                }
                            }catch (e :Exception) {
                            }
                        }
                        for (i in 0 until transactionList.size) {
                            if (transactionList[i].date.isEmpty() && mListLocalDate.isNotEmpty()) {
                                transactionList[i].date = mListLocalDate[i]
                            }
                        }

                        if(mListLocalDate.isNotEmpty() && transactionList.isNotEmpty()) {
                            val i = Intent(this, ImportVerifyActivity::class.java)
                            val bundle = Bundle()
                            bundle.putSerializable("transactionList", transactionList as Serializable)
                            i.putExtra("bundle", bundle)
                            startActivity(i)
                        } else {
                            Toast.makeText(this,"pls match the type",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                // array auto isNotEmpty
                val transactionList: ArrayList<ImportTransaction> = arrayListOf()
                for (index in 0 until arrayListAuto.size) {
                    try {
                        val amount = arrayListAuto[index]
                        try {
                            resultNumber = commaSeparatorFormat.parse(amount) as Number
                            stringNumber = resultNumber.toDouble()
                        } catch (e : java.lang.Exception) {
                            resultNumber = dotSeparatorFormat.parse(amount) as Number
                            stringNumber = resultNumber.toDouble()
                        }
                        if (arrayListCategory.isEmpty()) {
                            //no need to check create new category
                            if (arrayListRemark.isEmpty()){
                                if (stringNumber > 0) {
                                    transactionList.add(ImportTransaction(
                                        amount = stringNumber,
                                        currency = selectedWallet.currency!!,
                                        date = "",
                                        remark = "",
                                        type = Constants.INCOME,
                                        walletID = selectedWalletId,
                                        selectedWallet = selectedWallet,
                                        newCategory = defaultCategory
                                    ))
                                } else {
                                    transactionList.add(ImportTransaction(
                                        amount = stringNumber,
                                        currency = selectedWallet.currency!!,
                                        date = "",
                                        remark = "",
                                        type = Constants.EXPENSE,
                                        walletID = selectedWalletId,
                                        selectedWallet = selectedWallet,
                                        newCategory = defaultCategory
                                    ))
                                }
                            }else{
                                if (stringNumber > 0) {
                                    transactionList.add(ImportTransaction(
                                        amount = stringNumber,
                                        currency = selectedWallet.currency!!,
                                        date = "",
                                        remark = arrayListRemark[index],
                                        type = Constants.INCOME,
                                        walletID = selectedWalletId,
                                        selectedWallet = selectedWallet,
                                        newCategory = defaultCategory
                                    ))
                                } else {
                                    transactionList.add(ImportTransaction(
                                        amount = stringNumber,
                                        currency = selectedWallet.currency!!,
                                        date = "",
                                        remark = arrayListRemark[index],
                                        type = Constants.EXPENSE,
                                        walletID = selectedWalletId,
                                        selectedWallet = selectedWallet,
                                        newCategory = defaultCategory
                                    ))
                                }
                            }
                        } else {
                            //need to check for create new category
                            if (arrayListRemark.isEmpty()){
                                //remark isEmpty
                                if (stringNumber > 0) {
                                    //amount income
                                    if (arrayListCategory[index].isEmpty()) {
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    } else {
                                        newCategory.title = arrayListCategory[index]
                                        newCategory.color = selectedCategoryColor
                                        newCategory.image = selectedIconName
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = "",
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = newCategory
                                        ))
                                    }
                                } else {
                                    val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                    if (arrayListCategory[index].isEmpty()) {
                                        amountExpense?.let {
                                            transactionList.add(ImportTransaction(
                                                amount = it,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = defaultCategory
                                            ))
                                        }
                                    } else {
                                        newCategory.title = arrayListCategory[index]
                                        newCategory.color = selectedCategoryColor
                                        newCategory.image = selectedIconName
                                        amountExpense?.let {
                                            transactionList.add(ImportTransaction(
                                                amount = it,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = "",
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }

                                }
                            }else{
                                //remark isNotEmpty
                                if (stringNumber > 0) {
                                    //amount income
                                    if (arrayListCategory[index].isEmpty()) {
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = defaultCategory
                                        ))
                                    } else {
                                        newCategory.title = arrayListCategory[index]
                                        newCategory.color = selectedCategoryColor
                                        newCategory.image = selectedIconName
                                        transactionList.add(ImportTransaction(
                                            amount = stringNumber,
                                            currency = selectedWallet.currency!!,
                                            date = "",
                                            remark = arrayListRemark[index],
                                            type = Constants.INCOME,
                                            walletID = selectedWalletId,
                                            selectedWallet = selectedWallet,
                                            newCategory = newCategory
                                        ))
                                    }

                                } else {
                                    //amount expense
                                    val amountExpense = removePrefix(stringNumber.toString(),"-")?.toDouble()
                                    if (arrayListCategory[index].isEmpty()) {
                                       amountExpense?.let {
                                           transactionList.add(ImportTransaction(
                                               amount = it,
                                               currency = selectedWallet.currency!!,
                                               date = "",
                                               remark = arrayListRemark[index],
                                               type = Constants.EXPENSE,
                                               walletID = selectedWalletId,
                                               selectedWallet = selectedWallet,
                                               newCategory = defaultCategory
                                           ))
                                       }
                                    } else {
                                        newCategory.title = arrayListCategory[index]
                                        newCategory.color = selectedCategoryColor
                                        newCategory.image = selectedIconName
                                        amountExpense?.let {
                                            transactionList.add(ImportTransaction(
                                                amount = it,
                                                currency = selectedWallet.currency!!,
                                                date = "",
                                                remark = arrayListRemark[index],
                                                type = Constants.EXPENSE,
                                                walletID = selectedWalletId,
                                                selectedWallet = selectedWallet,
                                                newCategory = newCategory
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                    }catch (e :Exception) {
                    }
                }
                for (i in 0 until transactionList.size) {
                    if (transactionList[i].date.isEmpty() && mListLocalDate.isNotEmpty()) {
                        transactionList[i].date = mListLocalDate[i]
                    }
                }

                if(mListLocalDate.isNotEmpty() && transactionList.isNotEmpty()) {
                    val i = Intent(this, ImportVerifyActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("transactionList", transactionList as Serializable)
                    i.putExtra("bundle", bundle)
                    startActivity(i)
                } else {
                    Toast.makeText(this,"pls match the type",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        mAdapter?.list?.clear()
    }
    @SuppressLint("Range")
    fun changeSelectedWalletView() {
        theme.resolveAttribute(R.attr.primaryText, value, true)
        mBinding.imgWallet.backgroundTintList = ColorStateList.valueOf(value.data)
        mBinding.imgWallet.setBackgroundResource(
            Extension.getResouceId(this, selectedWallet.symbol)
        )
        mBinding.imgWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedWallet.color))

        mBinding.textNameWallet.text = "${selectedWallet.name} (${selectedWallet.currency})"
    }
    private fun initNameFileExcel() {
        mBinding.textFileExcel.text = getFileName(uri.toUri())
    }
    private fun getIntentData() {
        uri = intent.getStringExtra("uri").toString()
    }
    private fun getFileName(uri: Uri): String {
        val returnCursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
    private fun initRecyclerView(arrayList: ArrayList<ArrayList<String>>) {
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ImportDetailActivity)
            LinearLayoutManager(this@ImportDetailActivity).orientation = RecyclerView.VERTICAL
            mAdapter      = ImportListAdapter(mListener,arrayList,this@ImportDetailActivity)
            adapter       = mAdapter
            setHasFixedSize(true)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private val mListener = object : OnClickedListener<ImportModel> {
        override fun onClicked(
            itemView: View?,
            position: Int?,
            mList: ArrayList<String>,
            importModel: ImportModel
        ) {
            super.onClicked(itemView, position, mList, importModel)
            import = importModel
            mRowListString = mList
            position?.let {
                mListPosition.add(
                    PositionModel(
                        position = it,
                        mList = mList
                    )
                )
            }
            position?.let {
                MatchDataTypeFragment(listener = sheetListener, position = it, mListPosition = mListPosition, mList = mTypeList,this@ImportDetailActivity).apply {
                    show(supportFragmentManager, MatchDataTypeFragment.TAG)
                }
            }
        }
    }
    private fun getCategoryList(onSuccess: () -> Unit) {
        if (incomeModel.value?.size == 0) {
            FirebaseManager(this).initCategory(
                onSuccess = {
                    onSuccess()
                },
                onFailure = {

                }
            )
        }
        else {
            onSuccess()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private val sheetListener = object : OnClickedListener<ImportModel> {
        override fun onClicked(itemView: View?, position: Int?, model: ImportModel) {
            super.onClicked(itemView, position, model)

            //BottomSheet
            mTypeList[position!!].title = model.title
            mTypeList[position].icon_select = model.icon_select
            mTypeList[position].isCheck = model.isCheck
            mTypeList[position].mSelectPosition = model.mSelectPosition

            mAdapter?.notifyItemChanged(position)
            //check Data Type
            when (mTypeList[position].title) {
                getString(R.string.ingored) -> {
                    when (mRowListString) {
                        arrayListDate -> {
                            mListLocalDate.clear()
                        }
                        arrayListRemark -> {
                            arrayListRemark.clear()
                        }
                        arrayListCategory -> {
                            arrayListCategory.clear()
                        }
                        arrayListAuto -> {
                            arrayListAuto.clear()
                        }
                        arrayListExpense -> {
                            arrayListExpense.clear()
                        }
                        arrayListIncome -> {
                            arrayListIncome.clear()
                        }
                    }
                }
                getString(R.string.date) -> {
                    mListLocalDate.clear()
                    mRowListString.forEach {
                        arrayListDate.add(it)
                        isTimeStampValid(it)
                    }
                }
                getString(R.string.remark) -> {
                    arrayListRemark.clear()
                    for (i in 0 until mRowListString.size) {
                        arrayListRemark.add(mRowListString[i])
                    }
                }
                getString(R.string.category) -> {
                    arrayListCategory.clear()
                    mRowListString.forEach {
                        arrayListCategory.add(it)
                    }
                }
                getString(R.string.amount_auto) -> {
                    arrayListAuto.clear()
                    for (i in 0 until mRowListString.size) {
                        arrayListAuto.add(mRowListString[i])
                    }
                }
                getString(R.string.amount_expense) -> {
                    arrayListExpense.clear()
                    for (i in 0 until mRowListString.size) {
                        arrayListExpense.add(mRowListString[i])
                    }
                }
                getString(R.string.amount_income) -> {
                    arrayListIncome.clear()
                    for (i in 0 until mRowListString.size) {
                        arrayListIncome.add(mRowListString[i])
                    }
                }
            }
        }
    }

    private fun isTimeStampValid(inputString: String?): Boolean {
        val dateTimeFormatterBuilder: DateTimeFormatterBuilder = DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ofPattern("" + "[yyyy-MM-dd'T'HH:mm:ss.SSSZ]" + "[yyyy-MM-dd]" + "[dd-MM-yyyy]" + "[dd/MM/yyyy]" + "[MMM dd, yyyy]" + "[yyyy/MM/dd]" + "[yyyy.MM.dd]" + "[dd/MM/yyyy]" + "[dd.MM.yyyy]" + "[MM/dd/yyyy]" + "[MM-dd-yyyy]" + "[MMM dd yyyy]" + "[MMMM dd yyyy]" + "[dd MMM yyyy]" + "[dd MMMM yyyy]" + "[yyyy MMM dd]" + "[yyyy MMMM dd]" + "[yyyy/MMM/dd]" + "[yyyy/MMMM/dd]" + "[MMM-dd-yyyy]" + "[MMMM-dd-yyyy]" + "[dd-MMM-yyyy]" + "[dd-MMMM-yyyy]" + "[yyyy-MMM-dd]" + "[yyyy-MMMM-dd]" + "[yyyy/MMM/dd]" + "[yyyy/MMMM/dd]" + "[MM-dd-yy]" + "[dd-MM-yy]" + "[yy-MM-dd]" + "[MM/dd/yy]" + "[dd/MM/yy]" + "[yy/MM/dd]" + "[MM.dd.yy]" + "[dd.MM.yy]" + "[yy.MM.dd]" + "[MM.dd.yyyy]" + "[dd.MM.yyyy]" + "[yyyy.MM.dd]" + "[MM/dd/yyyy]" + "[dd/MM/yyyy]" + "[yyyy/dd/MM]" + "[yyyy-dd-MM]" + "[yyyy/MM/dd]" + "[yyyy-dd-MM]" + "[yyyy.MM.dd]" + "[yyyy.MM.dd]" + "[yyyy-MM-dd HH:mm:ss]" + "[yyyy/MM/dd HH:mm:ss]" + "[yyyy.MM.dd HH:mm:ss]" + "[dd/MM/yyyy HH:mm:ss]" + "[dd.MM.yyyy HH:mm:ss]" + "[MM/dd/yyyy HH:mm:ss]" + "[MM-dd-yyyy HH:mm:ss]" + "[MMM dd yyyy HH:mm:ss]" + "[MMMM dd yyyy HH:mm:ss]" + "[dd MMM yyyy HH:mm:ss]" + "[dd MMMM yyyy HH:mm:ss]" + "[yyyy MMM dd HH:mm:ss]" + "[yyyy MMMM dd HH:mm:ss]" + "[yyyy/MMM/dd HH:mm:ss]" + "[yyyy/MMMM/dd HH:mm:ss]" + "[MMM-dd-yyyy HH:mm:ss]" + "[MMMM-dd-yyyy HH:mm:ss]" + "[dd-MMM-yyyy HH:mm:ss]" + "[dd-MMMM-yyyy HH:mm:ss]" + "[yyyy-MMM-dd HH:mm:ss]" + "[yyyy-MMMM-dd HH:mm:ss]" + "[yyyy/MMM/dd HH:mm:ss]" + "[yyyy/MMMM/dd HH:mm:ss]" + "[MM-dd-yy HH:mm:ss]" + "[dd-MM-yy HH:mm:ss]" + "[yy-MM-dd HH:mm:ss]" + "[MM.dd.yyyy HH:mm:ss]" + "[dd.MM.yyyy HH:mm:ss]" + "[yyyy.MM.dd HH:mm:ss]" + "[MM/dd/yyyy HH:mm:ss]" + "[dd/MM/yyyy HH:mm:ss]" + "[yyyy/dd/MM HH:mm:ss]" + "[yyyy-dd-MM HH:mm:ss]" + "[yyyy/MM/dd HH:mm:ss]" + "[yyyy-dd-MM HH:mm:ss]" + "[yyyy.MM.dd HH:mm:ss]" + "[yyyy.MM.dd HH:mm:ss]"))
        val dateTimeFormatter: DateTimeFormatter = dateTimeFormatterBuilder.toFormatter()
        return try {
            val times = LocalDate.from(dateTimeFormatter.parse(inputString)).toString()
            mListLocalDate.add(TimeConverter.dateFormatMonth(times) + " " + currentTime)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun readExcelFile(context: Context, uri: Uri?)     {
        try {
            val inStream: InputStream?
            var wb: Workbook? = null
            try {
                inStream = context.contentResolver.openInputStream(uri!!)
                wb =
                    if (fileType === extensionXLS) HSSFWorkbook(inStream) else XSSFWorkbook(inStream)
                inStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            assert(wb != null)
            val sheet1 = wb!!.getSheetAt(0)
            ExcelHelper.insertExcelToSqlite(sheet1, onSuccess = {
                for (index in 1 .. it.size) {
                    mTypeList.add(ImportModel(context.getString(R.string.ingored),R.drawable.do_not_disturb_on.toString(), isCheck = false, mSelectPosition = index))
                }
                initRecyclerView(it)
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}