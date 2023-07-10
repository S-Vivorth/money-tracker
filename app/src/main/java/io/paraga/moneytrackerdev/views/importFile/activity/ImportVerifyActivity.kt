package io.paraga.moneytrackerdev.views.importFile.activity

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.ActivityImportVerifyBinding
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.LoadingHelper
import io.paraga.moneytrackerdev.views.importFile.adapter.ImportVerifyAdapter
import java.util.*


class ImportVerifyActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityImportVerifyBinding
    private var loadingHelper: LoadingHelper? = null
    private  var  mAdapter : ImportVerifyAdapter?= null
    private var transactionList: ArrayList<ImportTransaction> = arrayListOf()
    var mSelectPositionIncome : ArrayList<ImportTransaction> = ArrayList()
    var mSelectPositionExpense : ArrayList<ImportTransaction> = ArrayList()

    var isCheckType = false
    var isCheckClearList = false
    lateinit var tranType : Number

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
        mBinding = ActivityImportVerifyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //gone view clear text
        mBinding.icClearText.visibility = View.GONE
        val bundle = intent.getBundleExtra("bundle")
        transactionList = bundle?.getSerializable("transactionList") as ArrayList<ImportTransaction>
        tranType = transactionList[0].type
        transactionList.forEach {
            if (it.type == Constants.INCOME){
                mSelectPositionIncome.add(it)
            }
            if (it.type == Constants.EXPENSE) {
                mSelectPositionExpense.add(it)
            }
        }
        initRecyclerView(transactionList)
        initListener()

    }
    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredList: ArrayList<ImportTransaction> = ArrayList()
        for (item in transactionList) {
                if (item.newCategory.title?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true || item.remark.lowercase(
                        Locale.ROOT).contains(text.lowercase(Locale.ROOT)) || item.amount.toString()
                        .lowercase(
                            Locale.ROOT).contains(text.lowercase(Locale.ROOT))
                ) {
                    filteredList.add(item)
                }

        }
        if (filteredList.isEmpty()) {
            mAdapter?.clear()
        } else {
            mAdapter?.filterList(filteredList)
        }
    }

    private fun initListener() {
        mBinding.imageArrowBack.setOnClickListener {
            DialogHelper.showPrimaryDialog(this, Enums.DialogType.RETURN_PREVIOUS.value,
                onOkayPressed = {
                    transactionList.clear()
                    finish()
                }
            )
        }
        mBinding.icClearText.setOnClickListener {
            mBinding.editTextSearch.text.clear()
            initRecyclerView(transactionList)
        }
        mBinding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            if (text?.isEmpty() == true) {
                mBinding.icClearText.visibility = View.GONE
                initRecyclerView(transactionList)
            } else {
                mBinding.icClearText.visibility = View.VISIBLE
                filter(text.toString())
            }
        }
        mBinding.bulkAction.setOnClickListener {
            isCheck(isCheck = true)
            isCheckType = true
            isCheckClearList = false
            if (tranType == 0 || tranType == 1) {
                mBinding.selectBtnLayout.visibility = View.VISIBLE
                mBinding.selectBtnLayoutIncome.visibility = View.GONE
                mBinding.selectBtnLayoutExpense.visibility = View.GONE
            } else {
                mBinding.selectBtnLayout.visibility = View.GONE
                mBinding.selectBtnLayoutIncome.visibility = View.VISIBLE
                mBinding.selectBtnLayoutExpense.visibility = View.VISIBLE
            }
            mAdapter?.notifyDataSetChanged()
        }
        mBinding.imgClose.setOnClickListener {
            isCheck(isCheck = false)
            isCheckType = false
            isCheckClearList = true
            mBinding.selectBtnLayout.visibility = View.GONE
            mBinding.selectBtnLayoutIncome.visibility = View.GONE
            mBinding.selectBtnLayoutExpense.visibility = View.GONE
            mAdapter?.notifyDataSetChanged()
        }
    }
    fun isCheck(isCheck : Boolean) {
        if (isCheck) {
            //Gone
            mBinding.imageArrowBack.visibility = View.GONE
            mBinding.textImport.visibility = View.GONE
            mBinding.bulkAction.visibility = View.GONE
            mBinding.saveBtnLayout.visibility = View.GONE
            //Show
            mBinding.imgClose.visibility = View.VISIBLE
            mBinding.textSelect.visibility = View.VISIBLE
            mBinding.selectCategory.visibility = View.VISIBLE
            mBinding.selectWallet.visibility = View.VISIBLE
            mBinding.selectDelete.visibility = View.VISIBLE
            mBinding.selectBtnLayout.visibility = View.VISIBLE
        } else {
            //Show
            mBinding.imageArrowBack.visibility = View.VISIBLE
            mBinding.textImport.visibility = View.VISIBLE
            mBinding.bulkAction.visibility = View.VISIBLE
            mBinding.saveBtnLayout.visibility = View.VISIBLE
            //Gone
            mBinding.imgClose.visibility = View.GONE
            mBinding.textSelect.visibility = View.GONE
            mBinding.selectCategory.visibility = View.GONE
            mBinding.selectWallet.visibility = View.GONE
            mBinding.selectDelete.visibility = View.GONE
            mBinding.selectBtnLayout.visibility = View.GONE
        }
    }

    fun showLoading(isCancelable: Boolean? = null) {
        loadingHelper = LoadingHelper(this)
        loadingHelper?.show(isCancelable)
    }

    fun hideLoading() {
        loadingHelper?.dismiss()
        loadingHelper = null
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        DialogHelper.showPrimaryDialog(this, Enums.DialogType.RETURN_PREVIOUS.value,
            onOkayPressed = {
                super.onBackPressed()
                transactionList.clear()
                finish()
                }
            )
    }
    private fun initRecyclerView(transactionList: ArrayList<ImportTransaction>) {
        mBinding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ImportVerifyActivity)
            LinearLayoutManager(this@ImportVerifyActivity).orientation = RecyclerView.VERTICAL
            mAdapter      = ImportVerifyAdapter(this@ImportVerifyActivity,transactionList, this@ImportVerifyActivity)
            adapter       = mAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
    }
}