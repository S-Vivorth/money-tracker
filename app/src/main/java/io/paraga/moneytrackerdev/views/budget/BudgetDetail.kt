package io.paraga.moneytrackerdev.views.budget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.TransactionAdapter
import io.paraga.moneytrackerdev.adapters.budgetTransList
import io.paraga.moneytrackerdev.databinding.ActivityBudgetDetailBinding
import io.paraga.moneytrackerdev.models.Budget
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.wallet.EditWallet
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BudgetDetail : AppCompatActivity() {
    lateinit var transactionAdapter: TransactionAdapter
    lateinit var binding: ActivityBudgetDetailBinding
    var tempNestedList: ArrayList<NestedTransaction> = ArrayList()
    var transList: ArrayList<Transaction> = ArrayList()
    lateinit var budget: Budget
    var budgetCurrency: String = ""
    var budgetSpending: Double = 0.0

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
        binding = ActivityBudgetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.getBundleExtra("bundle")
        binding.currentMonthYear.text = intent.getStringExtra(Enums.Extras.CURRENT_MONTH_YEAR.value)
        try {
            budget = bundle?.getSerializable("budget") as Budget
        } catch (exc: Exception) {
            Log.d("exc", exc.toString())
        }
        if (budget.wallet != "") {
            walletList.value?.forEach {
                if (it.walletId == budget.wallet) {
                    budgetCurrency = it.wallet.currency.toString()
                }
            }
        } else {
            budgetCurrency = user?.defaultCurrency.toString()
        }
        nestedTransList.observeForever {
            clearData()
            initNestedTransList()
            setUpViews()
            initRecyclerView(tempNestedList)
        }

    }

    private fun setUpViews() {
        binding.horizontalCategoryIcon.setImageResource(
            Extension.getResouceId(
                applicationContext,
                budget.category.image
            )
        )

        binding.horizontalBudgetName.text = budget.category.title

        var walletName = ""
        var currencySymbol: String = ""
        if (budget.wallet == "") {
            walletName = applicationContext.getString(R.string.all_wallets)
            currencySymbol = Extension.getCurrencySymbol(user?.defaultCurrency.toString())
        } else {
            walletList.value?.forEach {
                if (it.walletId == budget.wallet) {
                    walletName = it.wallet.name.toString()
                    currencySymbol = Extension.getCurrencySymbol(it.wallet.currency.toString())
                    return@forEach
                }
            }
        }
        binding.horizontalWalletName.text = walletName
        binding.horizontalBudgetAmountSpending.text = applicationContext.getString(
            R.string.blank,
            (Extension.getCurrencySymbol(budgetCurrency) +
                    Extension.toBigDecimal(
                        String.format("%.2f", budgetSpending).replace(",", ".")
                    ) +
                    getString(R.string.of)
                    + Extension.toBigDecimal(
                String.format("%.2f", budget.amount).replace(",", ".")
            ))
        )
        val layerDrawable: LayerDrawable =
            binding.horizontalBudgetProgress.progressDrawable as LayerDrawable
        val progressLayer: Drawable =
            layerDrawable.findDrawableByLayerId(R.id.progress) as ScaleDrawable
        Extension.setColorProgressBar(
            binding.horizontalBudgetProgress,
            binding.horizontalBudgetAmountLeft,
            currencySymbol,
            budgetSpending.toInt(),
            budget.amount?.toInt() ?: 0,
            applicationContext,
            progressLayer
        )
        val budgetSpendingPercentage = ((budgetSpending / budget.amount!!.toInt()) * 100)
        binding.horizontalBudgetProgress.apply {
            progress = budgetSpendingPercentage.toInt()
            max = 100 * 100
        }

        Extension.startProgressAnimation(binding.horizontalBudgetProgress)

        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(applicationContext, CreateBudget::class.java)
            val bundle = Bundle()
            bundle.putSerializable("budget", budget as Serializable)
            intent.putExtra("bundle", bundle)
            intent.putExtra(Enums.Extras.IS_EDIT_BUDGET.value, true)
            startActivityForResult(intent, 1)
        }
    }

    fun initRecyclerView(transList: ArrayList<NestedTransaction>) {
        transactionAdapter = TransactionAdapter(
            transList,
            currency = budgetCurrency
        )
        val categoryLinearLayoutManager = LinearLayoutManager(applicationContext)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.transactionDetailRecyclerView.layoutManager = categoryLinearLayoutManager
        binding.transactionDetailRecyclerView.adapter = transactionAdapter
        binding.transactionDetailRecyclerView.setHasFixedSize(true)
        binding.transactionDetailRecyclerView.isNestedScrollingEnabled = false
    }

    private fun initNestedTransList() {
        if (nestedTransMapByMonth.containsKey(currentMonthYear.value.toString())) {
            (nestedTransMapByMonth[currentMonthYear.value.toString()] ?: ArrayList()).forEach {
                if (budget.category.title == Enums.General.ALL_CATEGORIES.value && budget.wallet == "") {
                    it.nestedTransList.forEach { trans ->
                        if (trans.type != Enums.TransTypes.INCOME.value) {
                            if (trans.currency == budgetCurrency) {
                                budgetSpending += trans.amount?.toDouble() ?: 0.0
                            } else {
                                val convertedAmount = Extension.convertCurrency(
                                    trans.currency.toString(),
                                    budgetCurrency,
                                    trans.amount!!.toDouble(),
                                    applicationContext
                                )
                                budgetSpending += convertedAmount
                            }
                            transList.add(trans)
                        }
                    }
                } else {
                    it.nestedTransList.forEach { trans ->
                        if (trans.type != Enums.TransTypes.INCOME.value) {
                            if (budget.category.title == Enums.General.ALL_CATEGORIES.value && budget.wallet == trans.walletID
                                || trans.category.title == budget.category.title && budget.wallet == trans.walletID.toString()
                                || trans.category.title == budget.category.title && budget.wallet == ""
                            ) {
                                if (trans.currency == budgetCurrency) {
                                    budgetSpending += trans.amount?.toDouble() ?: 0.0
                                } else {
                                    val convertedAmount = Extension.convertCurrency(
                                        trans.currency.toString(),
                                        budgetCurrency,
                                        trans.amount!!.toDouble(),
                                        applicationContext
                                    )
                                    budgetSpending += convertedAmount
                                }
                                transList.add(trans)
                            }
                        }
                    }

                }

            }
        }
        val transMap = transList.groupBy {
            SimpleDateFormat("MMMM yyyy").format(it.date?.toDate() ?: Date())
        }.toSortedMap()
        for ((key, value) in transMap) {
            val transMapByDay = value.groupBy {
                SimpleDateFormat("dd MMM yyyy").format(it.date?.toDate() ?: Date())
            }.toSortedMap(reverseOrder())

            for ((key, value) in transMapByDay) {
                var totalAmount: Double = 0.0
                var totalIncome = 0.0
                var totalExpense = 0.0

                value.forEach {
                    if (it.type == Enums.TransTypes.EXPENSE.value) {
                        if (it.currency == budgetCurrency) {
                            if (it.type == Enums.TransTypes.INCOME.value) {
                                totalAmount += it.amount!!.toDouble()
                                totalIncome += it.amount!!.toDouble()
                            } else {
                                totalAmount -= it.amount!!.toDouble()
                                totalExpense += it.amount!!.toDouble()
                            }
                        } else {
                            val convertedAmount = Extension.convertCurrency(
                                it.currency.toString(),
                                budgetCurrency,
                                it.amount!!.toDouble(),
                                applicationContext
                            )
                            if (it.type == Enums.TransTypes.INCOME.value) {
                                totalAmount += convertedAmount
                                totalIncome += convertedAmount
                            } else {
                                totalAmount -= convertedAmount
                                totalExpense += convertedAmount
                            }
                        }
                    }
                }
                tempNestedList.add(
                    NestedTransaction(
                        nestedTransList = ArrayList(
                            (value as ArrayList<Transaction>).sortedWith(
                                compareBy { (it.date).toString() }).reversed()
                        ),
                        date = key,
                        totalAmount = String.format("%.2f", totalAmount).replace(",", ".")
                            .toDouble(),
                        totalIncome = String.format("%.2f", totalIncome).replace(",", ".")
                            .toDouble(),
                        totalExpense = String.format("%.2f", totalExpense).replace(",", ".")
                            .toDouble(),
                    )
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val isEditBudget = data?.getBooleanExtra(Enums.Extras.IS_EDIT_BUDGET.value, false)
                if (!isEditBudget!!) {
                    finish()
                } else {
                    val bundle = data.getBundleExtra("bundle")
                    budget = bundle?.getSerializable("budget") as Budget
                    clearData()
                    initNestedTransList()
                    setUpViews()
                    initRecyclerView(tempNestedList)
                }
            }
        }
    }

    private fun clearData() {
        budgetSpending = 0.0
        tempNestedList = ArrayList()
        transList = ArrayList()
    }
}