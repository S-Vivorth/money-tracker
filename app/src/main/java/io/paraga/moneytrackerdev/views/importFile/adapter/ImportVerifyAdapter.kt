package io.paraga.moneytrackerdev.views.importFile.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.ImportTransaction
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.viewmodels.auth.ImportTransactionVM
import io.paraga.moneytrackerdev.viewmodels.category.NewCategoryVM
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.importFile.activity.ImportDetailActivity
import io.paraga.moneytrackerdev.views.importFile.activity.ImportVerifyActivity
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.EditCategoryWalletBottomSheetFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ImportVerifyAdapter(
    private val importVerifyActivity: ImportVerifyActivity,
    private var transactionList: ArrayList<ImportTransaction>,
    private val activity: Activity
): RecyclerView.Adapter<ImportVerifyAdapter.ViewHolder>() {
    private var walletID = ""
    private var categoryTitle = ""
    var mSelectPosition: ArrayList<Int> = arrayListOf()
    private var  mSelectType  : Number? = null
    private var newCategory = Category()
    private var selectedIconName = "ic_defult_category"
    private var selectedCategoryColor = "#BE4DE7"
    private var categories: HashMap<String, ArrayList<Category>> = HashMap()
    private val newCategoryVM = NewCategoryVM()
    private val value = TypedValue()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_verify_import, parent, false)
        return ViewHolder(view = view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.checkBox.isChecked = transactionList[position].newCheck
        if (holder.checkBox.isChecked) {
            holder.context.theme.resolveAttribute(R.attr.secondaryBgColor, value, true)
            holder.layoutCategory.setBackgroundColor(value.data)
            holder.checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.context, R.color.bgButton))
        } else {
            holder.context.theme.resolveAttribute(R.attr.bgColor, value, true)
            holder.layoutCategory.setBackgroundColor(value.data)
            holder.context.theme.resolveAttribute(R.attr.primaryText, value, true)
            holder.checkBox.buttonTintList = ColorStateList.valueOf(value.data)
        }
        //new logic
        if (importVerifyActivity.tranType != transactionList[position].type && importVerifyActivity.tranType != 2) {
            importVerifyActivity.tranType = 2
        }
        checkDisableAction(mSelectPosition,holder)

        val addTransVM = ImportTransactionVM()

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val yesterday =
            LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val tomorrow =
            LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var transDate = transactionList[position].date
        transDate = when (transDate) {
            today -> {
                holder.context.getString(R.string.today) + ", " + SimpleDateFormat("dd").format(SimpleDateFormat("dd MMMM yyyy").parse(transactionList[position].date))
            }
            yesterday -> {
                holder.context.getString(R.string.yesterday) + ", " + SimpleDateFormat("dd").format(SimpleDateFormat("dd MMMM yyyy").parse(transactionList[position].date))
            }
            tomorrow -> {
                holder.context.getString(R.string.tomorrow) + ", " + SimpleDateFormat("dd").format(SimpleDateFormat("dd MMMM yyyy").parse(transactionList[position].date))
            }
            else -> {
                SimpleDateFormat("EEEE, dd MMMM yyyy").format(SimpleDateFormat("dd MMMM yyyy").parse(transactionList[position].date))
            }
        }
        holder.ic_category.setImageResource(
            Extension.getResouceId(
                holder.context,
                transactionList[position].newCategory.image.toString()
            )
        )
        holder.textDate.text = transDate
        holder.textWallet.text = transactionList[position].selectedWallet.name
        holder.textBody.text = transactionList[position].remark
        if (transactionList[position].type == Constants.INCOME) {
            holder.textAmount.text = "+${transactionList[position].amount}"
            holder.textAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryGreen
                )
            )
        }else {
            holder.textAmount.text = "-${transactionList[position].amount}"
            holder.textAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryRed
                )
            )
        }
        if (transactionList[position].type == Constants.INCOME) {
            var counter = 0
            incomeModel.value?.forEach { income ->
                if (transactionList[position].newCategory.title != income.title) {
                    counter += 1
                }
            }
            if (counter == incomeModel.value?.size) {
                holder.cardNew.visibility = View.VISIBLE
                newCategory.title = transactionList[position].newCategory.title
                newCategory.color = selectedCategoryColor
                newCategory.image = selectedIconName
//                incomeModel.value?.add(newCategory)
            } else {
                incomeModel.value?.find { it.title == transactionList[position].newCategory.title }?.apply {
                    newCategory.title = title
                    newCategory.image =image
                    newCategory.color = color
                    selectedCount = newCategory.selectedCount
                    holder.cardNew.visibility = View.GONE
                }
            }
        }
        if (transactionList[position].type == Constants.EXPENSE){
            var counter = 0
            expenseModel.value?.forEach { expense ->
                if (transactionList[position].newCategory.title != expense.title) {
                    counter += 1
                }
            }
            if (counter == expenseModel.value?.size) {
                holder.cardNew.visibility = View.VISIBLE
                newCategory.title = transactionList[position].newCategory.title
                newCategory.color = selectedCategoryColor
                newCategory.image = selectedIconName
//                expenseModel.value?.add(newCategory)
            } else {
                expenseModel.value?.find { it.title == transactionList[position].newCategory.title }?.apply {
                    newCategory.title = title
                    newCategory.image =image
                    newCategory.color = color
                    selectedCount = newCategory.selectedCount
                    holder.cardNew.visibility = View.GONE
                }
            }
        }

        holder.textCategory.text = transactionList[position].newCategory.title
        var counter = Preferences().getInstance().getCountImport(importVerifyActivity)
        importVerifyActivity.mBinding.saveBtnLayout.setOnClickListener {
            DialogHelper.showPrimaryDialog(context = importVerifyActivity,
                Enums.DialogType.IMPORT.value,
                onOkayPressed = { alertDialog ->
                    importVerifyActivity.showLoading(false)
                    var counters = 0
                    if (transactionList[position].type == Constants.EXPENSE) {
                        expenseModel.value?.forEach { expense ->
                            if (transactionList[position].newCategory.title != expense.title) {
                                counters += 1
                            }
                        }
                        if (counters == expenseModel.value?.size) {
                            expenseModel.value?.add(newCategory)
                        }
                    } else {
                        incomeModel.value?.forEach { income ->
                            if (transactionList[position].newCategory.title != income.title) {
                                counters += 1
                            }
                        }
                        if(counters == incomeModel.value?.size) {
                            incomeModel.value?.add(newCategory)
                        }
                    }


                    Thread {
                        kotlin.run {
                            categories[Enums.DB.INCOME_FIELD.value] = incomeModel.value!!
                            categories[Enums.DB.EXPENSE_FIELD.value] = expenseModel.value!!
                            newCategoryVM.updateCategory(
                                    categories
                                ) {
                                }
                            addTransVM.addTrans(
                                importTransactionList = transactionList,
                                context = holder.context,
                                onSuccess = {
                                    importVerifyActivity.hideLoading()
                                    counter += 1
                                    Toast.makeText(importVerifyActivity,"Import Success",Toast.LENGTH_LONG).show()
                                    Preferences().setCountImport(importVerifyActivity,counter)
                                    holder.context.startActivity(Intent(holder.context,MainActivity::class.java))
                                    holder.importDetailActivity.finish()
                                    importVerifyActivity.finish()
                                },
                                onFailure = {
                                    Toast(holder.context).showCustomToast(
                                        holder.context.getString(R.string.transaction_is_not_added),
                                        holder.context as Activity
                                    )
                                }
                            )
                        }
                    }.start()
                    alertDialog.dismiss()
                })
        }
        if (mSelectPosition.size == transactionList.size) {
            importVerifyActivity.mBinding.textSelectAll.text = holder.context.getString(R.string.de_select_all)
            importVerifyActivity.mBinding.selectBtnLayout.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    transactionList[index].newCheck = false
                    mSelectPosition.clear()
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        } else {
            // logic select all transaction
            importVerifyActivity.mBinding.textSelectAll.text = holder.context.getString(R.string.select_all)
            importVerifyActivity.mBinding.selectBtnLayout.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.EXPENSE || transactionList[index].type == Constants.INCOME) {
                        transactionList[index].newCheck = false
                        mSelectPosition.clear()
                    }
                }
                for (index in 0 until transactionList.size) {
                    transactionList[index].newCheck = true
                    mSelectPosition.add(index)
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        }

        //logic expense
        if (mSelectPosition.size == importVerifyActivity.mSelectPositionExpense.size) {
            importVerifyActivity.mBinding.textSelectAllExpense.text = holder.context.getString(R.string.de_select_all)
            importVerifyActivity.mBinding.selectBtnLayoutExpense.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.EXPENSE) {
                        transactionList[index].newCheck = false
                        mSelectPosition.clear()
                    }
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        } else {
            importVerifyActivity.mBinding.textSelectAllExpense.text = holder.context.getString(R.string.select_all_expense)
            importVerifyActivity.mBinding.selectBtnLayoutExpense.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.INCOME) {
                        transactionList[index].newCheck = false
                        mSelectPosition.clear()
                    }
                }
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.EXPENSE) {
                        transactionList[index].newCheck = true
                        mSelectPosition.add(index)
                    }
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        }
        //logic income
        if (mSelectPosition.size == importVerifyActivity.mSelectPositionIncome.size) {
            importVerifyActivity.mBinding.textSelectAllIncome.text = holder.context.getString(R.string.de_select_all)
            importVerifyActivity.mBinding.selectBtnLayoutIncome.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.INCOME) {
                        transactionList[index].newCheck = false
                        mSelectPosition.clear()
                    }
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        } else {
            importVerifyActivity.mBinding.textSelectAllIncome.text = holder.context.getString(R.string.select_all_income)
            importVerifyActivity.mBinding.selectBtnLayoutIncome.setOnClickListener {
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.EXPENSE) {
                        transactionList[index].newCheck = false
                        mSelectPosition.clear()
                    }
                }
                for (index in 0 until transactionList.size) {
                    if (transactionList[index].type == Constants.INCOME) {
                        transactionList[index].newCheck = true
                        mSelectPosition.add(index)
                    }
                }
                checkDisableAction(mSelectPosition,holder)
                isCheckSelect(mSelectPosition)
                notifyDataSetChanged()
            }
        }



        importVerifyActivity.mBinding.selectCategory.setOnClickListener {
            if (mSelectType == Constants.INCOME) {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.INCOME, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,0, activity).apply {
                    show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            } else {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.EXPENSE, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,0, activity).apply {
                    show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            }
        }
        importVerifyActivity.mBinding.selectWallet.setOnClickListener {
            if (transactionList[position].type == Constants.INCOME) {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.INCOME, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,1, activity).apply {
                    show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            } else {
                EditCategoryWalletBottomSheetFragment(typeCategory = Constants.EXPENSE, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,1, activity).apply {
                    show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                }
            }
        }
        //logic remove list
        importVerifyActivity.mBinding.selectDelete.setOnClickListener {
            DialogHelper.showPrimaryDialog(context = importVerifyActivity,
                Enums.DialogType.TRANSACTION.value,
                onOkayPressed = { alertDialog ->
                    var index = 0
                    val transListIterator = transactionList.iterator()
                    while (transListIterator.hasNext()) {
                        transListIterator.next()
                        if (mSelectPosition.contains(index)) {
                            transListIterator.remove()
                        }
                        index += 1
                    }
                    mSelectPosition.clear()
                    isCheckSelect(mSelectPosition)
                    importVerifyActivity.isCheck(isCheck = false)
                    importVerifyActivity.isCheckType = false
                    notifyDataSetChanged()
                    Toast(importVerifyActivity).showCustomToast(
                        importVerifyActivity.getString(R.string.transaction_is_deleted),
                        importVerifyActivity
                    )
                    alertDialog.dismiss()
                })
        }
        holder.layoutCategory.setOnClickListener {
            if(importVerifyActivity.isCheckType) {
                if (mSelectPosition.contains(position)) {
                    mSelectPosition.removeIf { it == position }
                    transactionList[position].newCheck = false
                } else {
                    if (mSelectPosition.isEmpty()) {
                        mSelectPosition.add(position)
                        transactionList[position].newCheck = true
                        mSelectType = transactionList[position].type
                    } else {
                        if (mSelectType == transactionList[position].type) {
                            mSelectPosition.add(position)
                            transactionList[position].newCheck = true
                        } else {
                            Toast.makeText(holder.context,"Can not select income and expense at the same time",Toast.LENGTH_LONG).show()
                        }
                    }
                }
                holder.checkBox.isChecked = transactionList[position].newCheck
                if (holder.checkBox.isChecked) {
                    holder.context.theme.resolveAttribute(R.attr.secondaryBgColor, value, true)
                    holder.layoutCategory.setBackgroundColor(value.data)
                    holder.checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.context, R.color.bgButton))
                } else {
                    holder.context.theme.resolveAttribute(R.attr.bgColor, value, true)
                    holder.layoutCategory.setBackgroundColor(value.data)
                    holder.context.theme.resolveAttribute(R.attr.primaryText, value, true)
                    holder.checkBox.buttonTintList = ColorStateList.valueOf(value.data)
                }
                isCheckSelect(mSelectPosition)
                checkDisableAction(mSelectPosition,holder)
            } else {
                mSelectPosition.add(holder.adapterPosition)
                categoryTitle = transactionList[position].newCategory.title.toString()
                walletID = transactionList[position].walletID
                if (transactionList[position].type == Constants.INCOME) {
                    EditCategoryWalletBottomSheetFragment(typeCategory = Constants.INCOME, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,0, activity).apply {
                        show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                    }
                } else {
                    EditCategoryWalletBottomSheetFragment(typeCategory = Constants.EXPENSE, walletListenerBack = walletListener, categoryListenerBack = categoryListener, walletID = walletID, categoryTitle = categoryTitle,0, activity).apply {
                        show(importVerifyActivity.supportFragmentManager, EditCategoryWalletBottomSheetFragment.TAG)
                    }
                }
            }
        }

        if (importVerifyActivity.isCheckType) {
            holder.categoryIcon.visibility = View.VISIBLE
            holder.checkBox.visibility = View.VISIBLE

        } else {
            holder.categoryIcon.visibility = View.VISIBLE
            holder.checkBox.visibility = View.GONE
        }

        if (importVerifyActivity.isCheckClearList){
            if (mSelectPosition.isNotEmpty()) {
                mSelectPosition.map {
                    transactionList[it].newCheck = false
                }
                mSelectPosition.clear()
                isCheckSelect(mSelectPosition)
            }
        }
    }

    fun filterList(filterList: ArrayList<ImportTransaction>) {
        transactionList = filterList
        notifyDataSetChanged()
    }
    private fun isCheckSelect(mSelect: ArrayList<Int>) {
        if (mSelect.size == 0) {
            importVerifyActivity.mBinding.textSelect.text = "0"
        } else {
            importVerifyActivity.mBinding.textSelect.text = mSelect.size.toString()
        }
    }
    private val categoryListener = object : OnClickedListener<Category> {
        override fun onClicked(itemView: View?, position: Int?, model: Category) {
            super.onClicked(itemView, position, model)
            position?.let {
                walletID = transactionList[it].walletID
            }
            mSelectPosition.forEach { position ->
                transactionList[position].newCategory = model
                transactionList[position].newCheck = false
                notifyItemChanged(position)
            }
            mSelectPosition.clear()
            isCheckSelect(mSelectPosition)
            importVerifyActivity.isCheck(isCheck = false)
            importVerifyActivity.isCheckType = false
            notifyDataSetChanged()
        }
    }
    private fun checkDisableAction(mSelectPosition: ArrayList<Int>,holder: ViewHolder) {
        //logic enable action bulk action
        if (mSelectPosition.isNotEmpty()) {
            importVerifyActivity.mBinding.selectCategory.isEnabled = true
            importVerifyActivity.theme.resolveAttribute(R.attr.iconCategory, value, true)
            importVerifyActivity.mBinding.selectCategory.setColorFilter(value.data, android.graphics.PorterDuff.Mode.SRC_IN)
            importVerifyActivity.mBinding.selectWallet.isEnabled = true
            importVerifyActivity.mBinding.selectWallet.setColorFilter(value.data, android.graphics.PorterDuff.Mode.SRC_IN)
            importVerifyActivity.mBinding.selectDelete.isEnabled = true
            importVerifyActivity.mBinding.selectDelete.setColorFilter(ContextCompat.getColor(holder.context, R.color.primaryRed), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            importVerifyActivity.mBinding.selectCategory.isEnabled = false
            importVerifyActivity.mBinding.selectCategory.setColorFilter(ContextCompat.getColor(holder.context, R.color.darkDisableToggleStrokeColor), android.graphics.PorterDuff.Mode.SRC_IN)
            importVerifyActivity.mBinding.selectWallet.isEnabled = false
            importVerifyActivity.mBinding.selectWallet.setColorFilter(ContextCompat.getColor(holder.context, R.color.darkDisableToggleStrokeColor), android.graphics.PorterDuff.Mode.SRC_IN)
            importVerifyActivity.mBinding.selectDelete.isEnabled = false
            importVerifyActivity.mBinding.selectDelete.setColorFilter(ContextCompat.getColor(holder.context, R.color.darkDisableToggleStrokeColor), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }
    private val walletListener = object : OnClickedListener<WalletTrans> {
        override fun onClicked(itemView: View?, position: Int?, model: WalletTrans) {
            super.onClicked(itemView, position, model)
            walletID = model.walletId
            mSelectPosition.forEach { position ->
                transactionList[position].selectedWallet = model.wallet
                transactionList[position].walletID = model.walletId
                transactionList[position].newCheck = false
                notifyItemChanged(position)
            }
            mSelectPosition.clear()
            isCheckSelect(mSelectPosition)
            importVerifyActivity.isCheck(isCheck = false)
            importVerifyActivity.isCheckType = false
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        return transactionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        transactionList.clear()
        notifyDataSetChanged()
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textCategory: TextView
        var textBody: TextView
        var textDate: TextView
        var textAmount: TextView
        var textMonth: TextView
        var textWallet: TextView
        var context: Context
        var ic_category : ImageView
        var importDetailActivity: ImportDetailActivity
        var layoutCategory : ConstraintLayout
        var checkBox : CheckBox
        var categoryIcon : CardView
        var cardNew : CardView
        init {
            textCategory = view.findViewById(R.id.text_category)
            textBody = view.findViewById(R.id.text_body)
            textDate = view.findViewById(R.id.text_day)
            textAmount = view.findViewById(R.id.text_amount)
            textMonth = view.findViewById(R.id.text_month)
            textWallet = view.findViewById(R.id.text_wallet)
            ic_category = view.findViewById(R.id.ic_category)
            context = view.context
            checkBox = view.findViewById(R.id.checkbox)
            categoryIcon = view.findViewById(R.id.categoryIconLayout)
            importDetailActivity = ImportDetailActivity()
            layoutCategory = view.findViewById(R.id.layout_category)
            cardNew = view.findViewById(R.id.card_text_new)
        }
    }
}