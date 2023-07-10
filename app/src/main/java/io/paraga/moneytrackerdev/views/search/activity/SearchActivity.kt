package io.paraga.moneytrackerdev.views.search.activity

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.databinding.ActivitySearchBinding
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.allNestedTransList
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.KeyboardUtil
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.search.adapter.SearchTransactionAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.thread


class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    private  var searchTransactionAdapter : SearchTransactionAdapter ?= null
    private val nestedList: ArrayList<NestedTransaction> = ArrayList()
    var isCheckType = false
    var isCheckClearList = false
    var textSearch = ""
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

    override fun onResume() {
        super.onResume()
        KeyboardUtil().showKeyboard(this, binding.editTextSearch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thread {
            kotlin.run {
                initTransaction()
            }
        }
        binding.editTextSearch.doOnTextChanged { text, s1, s2, s3 ->
            if (text?.isEmpty() == true) {
                initRecyclerView(nestedList)
                isCheckText(type = true)
            } else {
                textSearch = text.toString()
                filter(text.toString())
                isCheckText(type = false)
            }
        }
        initListener()
    }
    private fun isCheckText(type : Boolean) {
        if (type) {
            binding.bulkAction.visibility = View.GONE
            binding.icClearText.visibility = View.GONE
        } else {
            binding.icClearText.visibility = View.VISIBLE
            if (binding.imgClose.visibility == View.VISIBLE) {
                binding.bulkAction.visibility = View.GONE
            } else {
                binding.bulkAction.visibility = View.VISIBLE
            }
        }
    }
    fun isCheck(isCheck : Boolean) {
        if (isCheck) {
            //Gone
            binding.imageArrowBack.visibility = View.GONE
            binding.textImport.visibility = View.GONE
            //show
            binding.imgClose.visibility = View.VISIBLE
            binding.textSelect.visibility = View.VISIBLE
            binding.selectCategory.visibility = View.VISIBLE
            binding.selectWallet.visibility = View.VISIBLE
            binding.selectDelete.visibility = View.VISIBLE
        } else {
            //Show
            binding.imageArrowBack.visibility = View.VISIBLE
            binding.textImport.visibility = View.VISIBLE
            //Gone
            binding.imgClose.visibility = View.GONE
            binding.textSelect.visibility = View.GONE
            binding.selectCategory.visibility = View.GONE
            binding.selectWallet.visibility = View.GONE
            binding.selectDelete.visibility = View.GONE
        }
    }
    private fun initRecyclerView(transList: ArrayList<NestedTransaction>) {
        searchTransactionAdapter = SearchTransactionAdapter(
            transList,this
        )
        val categoryLinearLayoutManager = LinearLayoutManager(this)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.searchRecyclerView.layoutManager = categoryLinearLayoutManager
        binding.searchRecyclerView.adapter = searchTransactionAdapter
        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.isNestedScrollingEnabled = false
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initTransaction() {
        GlobalScope.launch(Dispatchers.Main) {
            allNestedTransList.observeForever {
                initRecyclerView(nestedList)
            }
        }
    }

    fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredList: ArrayList<NestedTransaction> = ArrayList()
        for (item in allNestedTransList.value!!) {
            val filteredNestedList: ArrayList<Transaction> = ArrayList()

            item.nestedTransList.forEach {
                if (it.category.title?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true || it.remark?.lowercase(
                        Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true || it.amount?.toString()?.lowercase(
                        Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true
                    ) {
                    if (isProUser.value == false && walletList.value!!.size > 2 // filter inactive wallet trans
                                && Preferences().getInstance().getChosenTwoWalletIds(this)?.contains(it.walletID) == true
                        || walletList.value!!.size <= 2 || isProUser.value == true) {
                        filteredNestedList.add(
                            Transaction(
                                amount = it.amount,
                                category = it.category,
                                createdTime = it.createdTime,
                                currency = it.currency,
                                date = it.date,
                                remark = it.remark,
                                type = it.type,
                                userid = it.userid,
                                walletID = it.walletID,
                                documentId = it.documentId
                            )
                        )
                    }
                }
            }
            filteredList.add(
                NestedTransaction(
                    nestedTransList = filteredNestedList,
                    totalAmount = item.totalAmount,
                    totalExpense = item.totalExpense,
                    totalIncome = item.totalIncome,
                    date = item.date,
                    totalAmountByCategoryMap = item.totalAmountByCategoryMap
                )
            )
        }
        if (filteredList.isNotEmpty()) {
            val filtered : ArrayList<NestedTransaction> = ArrayList()
            val filteredValueEmpty : ArrayList<NestedTransaction> = ArrayList()

            filtered.addAll(filteredList.sortedBy { it.date })
            filtered.map {
                if (it.nestedTransList.isNotEmpty()) {
                    filteredValueEmpty.add(it)
                }
            }
            searchTransactionAdapter?.filterList(filteredValueEmpty)
        } else {
            searchTransactionAdapter?.clear()
        }
//        searchTransactionAdapter?.filterList(filteredList.sortedBy { it.date } as ArrayList<NestedTransaction>)

    }
    private fun initListener() {
        binding.imageArrowBack.setOnClickListener {
            KeyboardUtil().hideKeyboardWithFocus(this)
            isCheckType = false
            finish()
        }
        binding.imgClose.setOnClickListener {
            KeyboardUtil().showKeyboard(this,binding.editTextSearch)
            binding.bulkAction.visibility = View.VISIBLE
            isCheck(isCheck = false)
            isCheckType = false
            isCheckClearList = true
            searchTransactionAdapter?.notifyDataSetChanged()
        }
        binding.icClearText.setOnClickListener {
            KeyboardUtil().showKeyboard(this,binding.editTextSearch)
            binding.editTextSearch.text.clear()
            binding.bulkAction.visibility = View.GONE
            binding.icClearText.visibility = View.GONE
            isCheck(isCheck = false)
            isCheckType = false
            isCheckClearList = true
            initRecyclerView(nestedList)
            searchTransactionAdapter?.notifyDataSetChanged()
        }
        binding.bulkAction.setOnClickListener {
            KeyboardUtil().hideKeyboardWithFocus(this)
            binding.bulkAction.visibility = View.GONE
            isCheck(isCheck = true)
            isCheckType = true
            isCheckClearList = false
            searchTransactionAdapter?.notifyDataSetChanged()
        }
    }

}