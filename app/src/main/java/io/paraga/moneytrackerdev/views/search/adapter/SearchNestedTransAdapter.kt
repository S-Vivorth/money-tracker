package io.paraga.moneytrackerdev.views.search.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.nestedTransList
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.KeyboardUtil
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.viewmodels.exchangeRate
import io.paraga.moneytrackerdev.viewmodels.transaction.AddTransVM
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.EditCategoryWalletBottomSheetFragment
import io.paraga.moneytrackerdev.views.search.activity.SearchActivity
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import java.util.Date

class SearchNestedTransAdapter(
    private val nestedList: NestedTransaction,
    private val searchActivity: SearchActivity,
    private val searchTransactionAdapter: SearchTransactionAdapter
) :
    RecyclerView.Adapter<SearchNestedTransAdapter.ViewHolder>() {
    private val value = TypedValue()
    private var walletID = ""
    private var categoryTitle = ""
    val addTransVM = AddTransVM()
    private var trans: ArrayList<Transaction> = arrayListOf()



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var transName: TextView
        var transAmount: TextView
        var categoryIcon: ImageView
        var categoryName: TextView
        var context: Context
        var walletName: TextView
        var walletIcon: ImageView
        var layoutCategory : ConstraintLayout
        var checkBox : CheckBox
        var exchangeRateText: TextView
        private var currentExchangeRate: String

        init {
            transName = view.findViewById(R.id.text_remark)
            transAmount = view.findViewById(R.id.text_trans_amount)
            categoryIcon = view.findViewById(R.id.img_category)
            categoryName = view.findViewById(R.id.text_category)
            walletName = view.findViewById(R.id.text_wallet_name)
            walletIcon = view.findViewById(R.id.img_wallet)
            layoutCategory = view.findViewById(R.id.detailItem)
            checkBox = view.findViewById(R.id.checkbox)
            exchangeRateText = view.findViewById(R.id.text_exchange)
            context = view.context
            currentExchangeRate = context.getString(
                R.string.blank,
                Extension.getCurrencyObj(
                    user?.defaultCurrency.toString()
                ).code
                + " "
                + exchangeRate.results?.get(
                    Extension.getCurrencyObj(
                        user?.defaultCurrency.toString()
                    ).code
                )

            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nested_trans_cell, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //new logic
        checkDisableAction(searchTransactionAdapter.docID,holder)
        isCheckSelect(searchTransactionAdapter.docID)
        holder.checkBox.isChecked = nestedList.nestedTransList[position].isCheck == true
        val allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet()
        holder.context.theme.resolveAttribute(R.attr.quaternaryBgColor, value, true)
        holder.itemView.setBackgroundColor(value.data)
        holder.checkBox.isChecked = nestedList.nestedTransList[position].isCheck == true
        if (nestedList.nestedTransList[position].type == Enums.TransTypes.INCOME.value) {
            holder.transAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryGreen
                )
            )
        } else {
            holder.transAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryRed
                )
            )
        }
        walletList.value?.forEach {
            if (it.walletId == nestedList.nestedTransList[position].walletID.toString()) {
                holder.walletName.text = it.wallet.name
                holder.walletIcon.setBackgroundResource(
                    Extension.getResouceId(holder.context, it.wallet.symbol)
                )
                holder.walletIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.wallet.color.toString()))
            }
        }
        val currency: String = if (selectedWallet == allWallet) {
            user?.defaultCurrency.toString()
        } else {
            (selectedWallet.currency.toString())
        }

        if (currency == nestedList.nestedTransList[position].currency.toString()) {
            holder.exchangeRateText.visibility = View.GONE
        }
        val convertedAmount = String.format(
            "%.2f",
            Extension.convertCurrency(
                nestedList.nestedTransList[position].currency.toString(),
                currency,
                nestedList.nestedTransList[position].amount!!.toDouble(),
                holder.context
            )
        ).replace(",",".")
        holder.transAmount.text = holder.context.getString(
            R.string.blank,
            Extension.getCurrencySymbol(
                selectedWallet.currency.toString()
            )
            + convertedAmount
        )
        holder.exchangeRateText.text = holder.context.getString(
            R.string.blank,
            nestedList.nestedTransList[position].currency.toString()
                    + String.format("%.2f", nestedList.nestedTransList[position].amount?.toDouble())
        )
        if (searchActivity.isCheckType) {
            holder.categoryIcon.visibility = View.VISIBLE
            holder.checkBox.visibility = View.VISIBLE
        } else {
            holder.categoryIcon.visibility = View.VISIBLE
            holder.checkBox.visibility = View.GONE
        }
        if (searchActivity.isCheckClearList){
            nestedList.nestedTransList[position].isCheck = false
            searchTransactionAdapter.docID.clear()
            isCheckSelect(searchTransactionAdapter.docID)
        }
        val remark = nestedList.nestedTransList[position].remark
        holder.transName.text = if (remark == "") nestedList.nestedTransList[position].category.title else remark
        holder.categoryName.text = nestedList.nestedTransList[position].category.title
        holder.categoryIcon.setImageResource(Extension.getResouceId(holder.context, nestedList.nestedTransList[position].category.image.toString()))
        searchActivity.binding.selectCategory.setOnClickListener {
            categoryTitle = nestedList.nestedTransList[position].category.title.toString()
            walletID = nestedList.nestedTransList[position].walletID.toString()
            if (nestedList.nestedTransList[position].type == Constants.INCOME) {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.INCOME, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,0, searchActivity).apply {
                    show(searchActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            } else {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.EXPENSE, walletListenerBack = walletListener, categoryListenerBack = categoryListener,walletID = walletID, categoryTitle = categoryTitle,0, searchActivity).apply {
                    show(searchActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            }
        }
        searchActivity.binding.selectWallet.setOnClickListener {
            categoryTitle = nestedList.nestedTransList[position].category.title.toString()
            walletID = nestedList.nestedTransList[position].walletID.toString()
            if (nestedList.nestedTransList[position].type == Constants.INCOME) {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.INCOME, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,1, searchActivity).apply {
                    show(searchActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            } else {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.EXPENSE, walletListenerBack = walletListener, categoryListenerBack = categoryListener,walletID = walletID, categoryTitle = categoryTitle,1, searchActivity).apply {
                    show(searchActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            }
        }
        holder.itemView.setOnClickListener {
            if (searchActivity.isCheckType) {
                if (searchTransactionAdapter.docID.contains(nestedList.nestedTransList[position].documentId)) {
                    searchTransactionAdapter.docID.removeIf { it == nestedList.nestedTransList[position].documentId }
                    searchTransactionAdapter.nestedTransList.removeIf { it == nestedList.nestedTransList[position] }
                    nestedList.nestedTransList[position].isCheck = false
                } else {
                    if (searchTransactionAdapter.docID.isEmpty()) {
                        searchTransactionAdapter.docID.add(nestedList.nestedTransList[position].documentId!!)
                        searchTransactionAdapter.nestedTransList.add(nestedList.nestedTransList[position])
                        nestedList.nestedTransList[position].isCheck = true
                        searchTransactionAdapter.mSelectType = nestedList.nestedTransList[position].type
                    } else {
                        if (searchTransactionAdapter.mSelectType == nestedList.nestedTransList[position].type) {
                            searchTransactionAdapter.docID.add(nestedList.nestedTransList[position].documentId!!)
                            searchTransactionAdapter.nestedTransList.add(nestedList.nestedTransList[position])
                            nestedList.nestedTransList[position].isCheck = true
                        }
                        else {
                            Toast.makeText(holder.context,"Can not select income and expense at the same time",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
                holder.checkBox.isChecked = nestedList.nestedTransList[position].isCheck == true
                if (holder.checkBox.isChecked) {
                    holder.context.theme.resolveAttribute(R.attr.itemSelect, value, true)
                    holder.layoutCategory.setBackgroundColor(value.data)
                    holder.checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.context, R.color.bgButton))
                } else {
                    holder.layoutCategory.setBackgroundColor(holder.context.getColor(R.color.trans))
                    holder.context.theme.resolveAttribute(R.attr.primaryText, value, true)
                    holder.checkBox.buttonTintList = ColorStateList.valueOf(value.data)
                }
                isCheckSelect(searchTransactionAdapter.docID)
                checkDisableAction(searchTransactionAdapter.docID,holder)
                //logic remove list
                searchActivity.binding.selectDelete.setOnClickListener {
                    DialogHelper.showPrimaryDialog(context = searchActivity,
                    Enums.DialogType.TRANSACTION.value,
                    onOkayPressed = { alertDialog ->
                        addTransVM.deleteTransBatch(
                            searchTransactionAdapter.docID,
                        searchActivity,
                        onSuccess = {
                            searchActivity.filter(searchActivity.textSearch)
                        },
                        onFailure = {
                            alertDialog.dismiss()
                        })
                    Toast(searchActivity).showCustomToast(
                        searchActivity.getString(R.string.transaction_is_deleted),
                        searchActivity
                                )
                        searchTransactionAdapter.docID.clear()
                        isCheckSelect(searchTransactionAdapter.docID)
                        searchActivity.isCheck(isCheck = false)
                        //show view
                        searchActivity.binding.bulkAction.visibility = View.VISIBLE
                        searchActivity.isCheckType = false
                    Toast(searchActivity).showCustomToast(
                        searchActivity.getString(R.string.transaction_is_deleted),
                        searchActivity
                    )

                    alertDialog.dismiss()
                })
                }
            } else {
                val selectedTrans = nestedList.nestedTransList[position]
                val milliseconds =
                    selectedTrans.date!!.seconds * 1000 + selectedTrans.date!!.nanoseconds / 1000000
                val intent = Intent(holder.context, AddTrans::class.java)
                if (currency == nestedList.nestedTransList[position].currency.toString()) {
                    intent.putExtra("amount", convertedAmount.toDouble())
                } else {
                    intent.putExtra("amount", selectedTrans.amount)
                }
                intent.putExtra("currency", selectedTrans.currency)
                intent.putExtra("milliseconds", milliseconds)

                intent.putExtra("remark", selectedTrans.remark)
                intent.putExtra("type", selectedTrans.type)
                intent.putExtra("userid", selectedTrans.userid)
                intent.putExtra("walletID", selectedTrans.walletID)
                intent.putExtra("documentId", selectedTrans.documentId)
                intent.putExtra("categoryColor", selectedTrans.category.color)
                intent.putExtra("categoryImage", selectedTrans.category.image)
                intent.putExtra("selectedCount", selectedTrans.category.selectedCount)
                intent.putExtra("categoryTitle", selectedTrans.category.title)
                intent.putExtra("isEditTrans", true)
                holder.context.startActivity(intent)
            }
        }

    }
    private val categoryListener = object : OnClickedListener<Category> {
        override fun onClicked(itemView: View?, position: Int?, model: Category) {
            super.onClicked(itemView, position, model)
            searchTransactionAdapter.docID.map { new ->
                searchTransactionAdapter.nestedTransList.map { old ->
                    if (new == old.documentId) {
                        trans.add(
                            Transaction(
                                amount = old.amount,
                                category = model,
                                currency = old.currency,
                                date = old.date,
                                remark = old.remark,
                                type = old.type,
                                walletID = old.walletID,
                                documentId = old.documentId,
                                isCheck = false,
                            )
                        )
                    }
                }
            }
            addTransVM.updateTranBatch(trans,
                onSuccess = {
                    searchActivity.filter(searchActivity.textSearch)
                    Toast(searchActivity).showCustomToast(
                        searchActivity.getString(R.string.transaction_is_saved),
                        searchActivity
                    )
            },
                onFailure = {
                    Toast(searchActivity).showCustomToast(
                        searchActivity.getString(R.string.transaction_is_not_saved),
                        searchActivity
                    )
                })
            KeyboardUtil().hideKeyboardWithFocus(searchActivity)
            searchTransactionAdapter.docID.clear()
            isCheckSelect(searchTransactionAdapter.docID)
            searchActivity.isCheck(isCheck = false)
            //show view
            searchActivity.binding.bulkAction.visibility = View.VISIBLE
            searchActivity.isCheckType = false
        }
    }
    private val walletListener = object : OnClickedListener<WalletTrans> {
        override fun onClicked(itemView: View?, position: Int?, model: WalletTrans) {
            super.onClicked(itemView, position, model)
            walletID = model.walletId
            searchTransactionAdapter.docID.map { new ->
                searchTransactionAdapter.nestedTransList.map { old ->
                    if (new == old.documentId) {
                        trans.add(
                            Transaction(
                                amount = old.amount,
                                category = old.category,
                                currency = old.currency,
                                date = old.date,
                                remark = old.remark,
                                type = old.type,
                                walletID = model.walletId,
                                documentId = old.documentId,
                                isCheck = false,
                            )
                        )
                    }
                }
            }
            addTransVM.updateTranBatch(trans, onSuccess = {
                Toast(searchActivity).showCustomToast(
                    searchActivity.getString(R.string.transaction_is_saved),
                    searchActivity
                )
                searchActivity.filter(searchActivity.textSearch)
            },
                onFailure = {
                    Toast(searchActivity).showCustomToast(
                        searchActivity.getString(R.string.transaction_is_not_saved),
                        searchActivity
                    )
                })
            KeyboardUtil().hideKeyboardWithFocus(searchActivity)
            searchTransactionAdapter.docID.clear()
            isCheckSelect(searchTransactionAdapter.docID)
            searchActivity.isCheck(isCheck = false)
            //show view
            searchActivity.binding.bulkAction.visibility = View.VISIBLE
            searchActivity.isCheckType = false
        }
    }
    private fun checkDisableAction(mSelectPosition: ArrayList<String>,holder: ViewHolder) {
        //logic enable action bulk action
        if (mSelectPosition.isNotEmpty()) {
            searchActivity.binding.selectCategory.isEnabled = true
            searchActivity.theme.resolveAttribute(R.attr.iconCategory, value, true)
            searchActivity.binding.selectCategory.setColorFilter(value.data, android.graphics.PorterDuff.Mode.SRC_IN)
            searchActivity.binding.selectWallet.isEnabled = true
            searchActivity.binding.selectWallet.setColorFilter(value.data, android.graphics.PorterDuff.Mode.SRC_IN)
            searchActivity.binding.selectDelete.isEnabled = true
            searchActivity.binding.selectDelete.setColorFilter(ContextCompat.getColor(holder.context, R.color.primaryRed), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            searchActivity.binding.selectCategory.isEnabled = false
            searchActivity.binding.selectCategory.setColorFilter(ContextCompat.getColor(holder.context, R.color.darkDisableToggleStrokeColor), android.graphics.PorterDuff.Mode.SRC_IN)
            searchActivity.binding.selectWallet.isEnabled = false
            searchActivity.binding.selectWallet.setColorFilter(ContextCompat.getColor(holder.context, R.color.darkDisableToggleStrokeColor), android.graphics.PorterDuff.Mode.SRC_IN)
            searchActivity.binding.selectDelete.isEnabled = false
            searchActivity.binding.selectDelete.setColorFilter(ContextCompat.getColor(holder.context, R.color.disableColorRed), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }
    private fun isCheckSelect(mSelect: ArrayList<String>) {
        if (mSelect.size == 0) {
            searchActivity.binding.textSelect.text = "0"
        } else {
            searchActivity.binding.textSelect.text = mSelect.size.toString()
        }
    }

    override fun getItemCount(): Int {
        return nestedList.nestedTransList.size
    }
}