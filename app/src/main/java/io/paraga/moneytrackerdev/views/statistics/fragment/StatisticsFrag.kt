package io.paraga.moneytrackerdev.views.statistics.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.FragmentStatisticsBinding
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.TimeConverter
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.statistics.fragment.adapter.StatisticsAdapter
import io.paraga.moneytrackerdev.views.statistics.model.StatisticsModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.ConcurrentModificationException
import kotlin.concurrent.thread


class StatisticsFrag : Fragment() {

    private var _binding: FragmentStatisticsBinding?= null
    private val binding get() = _binding!!
    private var isExpense = true//use for check data percent or balance when user clicked change income or expense
    private var isPercent = false
    private lateinit var mAdapter: StatisticsAdapter
    private var currentDate = YearMonth.now()
    private val value = TypedValue()
    private var mainActivity: MainActivity ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentStatisticsBinding.inflate(inflater,container,false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLayout()
        initRecyclerView()
        thread {
            kotlin.run {
                initTransaction()
            }
        }
        initListener()
        if (isExpense) {
            binding.expenseBtn.performClick()
        } else {
            binding.incomeBtn.performClick()
        }
        goToNextOrPrevMonth()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun initTransaction() {
        GlobalScope.launch(Dispatchers.Main) {
            nestedTransList.observeForever {
                binding.textMoney.text = activity?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()))
                if (isExpense) {
                    binding.expenseBtn.performClick()
                } else {
                    binding.incomeBtn.performClick()
                }
            }
        }
    }
    fun getRefreshFragment() {
        mainActivity?.switchFrag(StatisticsFrag())
    }
    fun hideLayout() {
        //hide layout total from mainActivity
        mainActivity?.binding?.layoutPrice?.visibility = View.GONE
        mainActivity?.binding?.chooseWalletLayout?.visibility = View.VISIBLE
        mainActivity?.binding?.layoutHeader?.calendarHeader?.visibility = View.VISIBLE
        mainActivity?.binding?.walletIcon?.visibility = View.VISIBLE
        mainActivity?.binding?.walletName?.visibility = View.VISIBLE
        mainActivity?.binding?.imgArrowDown?.visibility = View.VISIBLE
        mainActivity?.binding?.layoutCurrentMonth?.visibility = View.VISIBLE
        mainActivity?.binding?.layoutSearch?.visibility = View.VISIBLE
        mainActivity?.binding?.textBudget?.visibility = View.GONE
        mainActivity?.binding?.budgetViewType?.visibility = View.GONE
        mainActivity?.binding?.addBudgetBtn?.visibility = View.GONE
    }

    private fun goToNextOrPrevMonth() {
        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            mainActivity?.let {
                it.binding.layoutHeader.textSelectDate.text = activity?.getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(it)))
            }
        } else {
            mainActivity?.let {
                it.binding.layoutHeader.textSelectDate.text = activity?.getString(
                    R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(it))
                            + " "
                            + currentDisplayedMonth?.year.toString())
            }
        }
        mainActivity?.let {
            currentMonthYear.value = activity?.getString(
                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " "
                        + currentDisplayedMonth?.year.toString()).toString()
        }
    }

    private fun initListener() {
        mainActivity?.binding?.layoutHeader?.imageArrowNext?.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.plusMonths(1)
            goToNextOrPrevMonth()
            if (isExpense) {
                setDataRecyclerViewExpense()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = true)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = true)
                    binding.walletPieChart.setUsePercentValues(false)
                }
            } else {
                setDataRecyclerViewIncome()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = false)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = false)
                    binding.walletPieChart.setUsePercentValues(false)
                }
            }
            mainActivity?.selectMonthNavigation()
            mainActivity?.setDataNavigation()
        }
        mainActivity?.binding?.layoutHeader?.imageArrowBack?.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.minusMonths(1)
            goToNextOrPrevMonth()
            if (!isExpense) {
                setDataRecyclerViewIncome()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = false)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = false)
                }
            } else {
                setDataRecyclerViewExpense()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = true)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = true)
                }
            }
            mainActivity?.selectMonthNavigation()
            mainActivity?.setDataNavigation()
        }
        mainActivity?.binding?.layoutCurrentMonth?.setOnClickListener {
            currentDisplayedMonth = currentDate
            mainActivity?.goneDatePicker()
            mainActivity?.let {
                it.binding.layoutHeader.textSelectDate.text = activity?.getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL, Extension.getLocale(it))
                        + " "
                        + currentDisplayedMonth?.year.toString())
            }

            goToNextOrPrevMonth()
            if (isExpense) {
                setDataRecyclerViewExpense()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = true)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = true)
                }
            } else {
                setDataRecyclerViewIncome()
                if (isPercent) {
                    initPiChartExpense(isBalance = false, isExpense = false)
                    binding.walletPieChart.setUsePercentValues(true)
                } else {
                    initPiChartExpense(isBalance = true, isExpense = false)
                }
            }
            mainActivity?.selectMonthNavigation()
            mainActivity?.setDataNavigation()
        }
        binding.balanceBtn.setOnClickListener {
            isPercent = false
            if (isExpense) {
                initPiChartExpense(isBalance = true,isExpense = true)
            } else {
                initPiChartExpense(isBalance = true, isExpense = false)
            }
            binding.walletPieChart.setUsePercentValues(false)
            binding.percentageBtn.setCardBackgroundColor(Color.TRANSPARENT)

            context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.textPercent.setTextColor(value.data)
            context?.theme?.resolveAttribute(R.attr.nextButtonTextColor, value, true)
            binding.textMoney.setTextColor(value.data)
            context?.theme?.resolveAttribute(R.attr.quaternaryBgColor, value, true)
            binding.balanceBtn.setCardBackgroundColor(value.data)
        }
        binding.percentageBtn.setOnClickListener {
            isPercent = true
            if (isExpense) {
                initPiChartExpense(isBalance = false,isExpense = true)
            } else {
                initPiChartExpense(isBalance = false, isExpense = false)
            }
            binding.walletPieChart.setUsePercentValues(true)
            context?.theme?.resolveAttribute(R.attr.nextButtonTextColor, value, true)
            binding.textPercent.setTextColor(value.data)
            context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.textMoney.setTextColor(value.data)
            binding.balanceBtn.setCardBackgroundColor(Color.TRANSPARENT)

            context?.theme?.resolveAttribute(R.attr.quaternaryBgColor, value, true)
            binding.percentageBtn.setCardBackgroundColor(value.data)
        }
        binding.incomeBtn.setOnClickListener {
            isExpense = false
            setDataRecyclerViewIncome()
            if (isPercent) {
                initPiChartExpense(isBalance = false, isExpense = false)
                binding.walletPieChart.setUsePercentValues(true)
            } else {
                initPiChartExpense(isBalance = true, isExpense = false)
            }


            binding.expenseBtn.setCardBackgroundColor(Color.TRANSPARENT)
            mainActivity?.let {
                binding.incomeBtn.setCardBackgroundColor(ContextCompat.getColor(it, R.color.light_green))
            }
            mainActivity?.let {
                binding.incomeText.setTextColor(ContextCompat.getColor(it, R.color.tertiaryGreen))
            }
            context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.expenseText.setTextColor(value.data)
        }
        binding.expenseBtn.setOnClickListener {
            isExpense = true
            setDataRecyclerViewExpense()
            if (isPercent) {
                initPiChartExpense(isBalance = false, isExpense = true)
                binding.walletPieChart.setUsePercentValues(true)
            } else {
                initPiChartExpense(isBalance = true, isExpense = true)
                binding.walletPieChart.setUsePercentValues(false)
            }

            binding.incomeBtn.setCardBackgroundColor(Color.TRANSPARENT)
            mainActivity?.let {
                binding.expenseBtn.setCardBackgroundColor(ContextCompat.getColor(it,R.color.primaryRed))
            }
            mainActivity?.let {
                binding.expenseText.setTextColor(ContextCompat.getColor(it, R.color.tertiaryRedBgColor))
            }

            context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.incomeText.setTextColor(value.data)
        }
    }

    override fun onResume() {
        super.onResume()
        hideLayout()
    }

    private fun initPiChartExpense(isBalance: Boolean = true,isExpense: Boolean = true) {
        try {
            val entries: ArrayList<PieEntry> = ArrayList()
            val colors: ArrayList<Int> = ArrayList()
            val staticList: ArrayList<StatisticsModel> = ArrayList()
            val transactionList: ArrayList<Transaction> = ArrayList()
            @SuppressLint("SuspiciousIndentation")
            fun setUpEntries(onCompleted: () -> Unit) {
                var counter = 0
                if (isExpense) {
                    //isExpense == true value is expense
                    val mListDate = nestedTransList.value?.filter { nestedTransaction ->
                        TimeConverter.dateWithFormat(nestedTransaction.date) == currentMonthYear.value
                    }
                    mListDate?.forEach { nestedTransaction ->
                        transactionList.addAll(nestedTransaction.nestedTransList.filter { it.type == Constants.EXPENSE })
                    }
                    transactionList.forEach { expense ->
                        totalAmountByCategory.map { title ->
                            if (expense.category.title == title.key) {
                                title.value.forEach {
                                    if (it.key == "expense") {
                                        val price = DecimalFormat("0.00").format(it.value)
                                        staticList.add(
                                            StatisticsModel(expense.category.title.toString(),
                                                price.replace(",",".").toFloat(),expense.category.color.toString())
                                        )
                                    }
                                }
                            }
                        }
                        counter += 1
                        //check if loop is completed
                        if (counter == transactionList.size) {
                            onCompleted()
                        }
                    }
                } else {
                    //isExpense == false value is income
                    val mListDate = nestedTransList.value?.filter { nestedTransaction ->
                        TimeConverter.dateWithFormat(nestedTransaction.date) == currentMonthYear.value
                    }
                    mListDate?.forEach { nestedTransaction ->
                        transactionList.addAll(nestedTransaction.nestedTransList.filter { it.type == Constants.INCOME })
                    }
                    transactionList.forEach { expense ->
                        totalAmountByCategory.map { title ->
                            if (expense.category.title == title.key) {
                                title.value.forEach {
                                    if (it.key == "income") {
                                        val price = DecimalFormat("0.00").format(it.value)
                                        staticList.add(
                                            StatisticsModel(expense.category.title.toString(),
                                                price.replace(",",".").toFloat(),expense.category.color.toString())
                                        )
                                    }
                                }
                            }
                        }
                        counter += 1
                        //check if loop is completed
                        if (counter == transactionList.size) {
                            onCompleted()
                        }
                    }
                }
            }
            setUpEntries {
                val mList = staticList.distinct()
                showLayoutEmpty(staticList)
                var totalAmount = 0f
                mList.forEach {
                    totalAmount += it.percent ?: 0f
                    entries.add(PieEntry(it.percent ?: 0f, it.title.toString()))
                    try {
                        colors.add(Color.parseColor(it.color.toString()))
                    }
                    catch (exc: Exception) {
                        colors.add(Color.parseColor("#F0831A"))
                    }
                }

                val dataSet = PieDataSet(entries, "Mobile OS")
                binding.walletPieChart.isDrawHoleEnabled = true
                val valueString = context?.getString(
                    R.string.blank,
                    " "
                ) + Extension.toBigDecimal(totalAmount.toString())
                binding.walletPieChart.centerText = activity?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()) + valueString)
                binding.walletPieChart.description.isEnabled = false

                dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                dataSet.colors = colors
                binding.walletPieChart.setEntryLabelTextSize(8F)
                binding.walletPieChart.setExtraOffsets(10F, 16F, 16F, 10F)
                binding.walletPieChart.holeRadius = 70F
                binding.walletPieChart.animateY(1000, Easing.EaseOutSine)

                val legend: Legend = binding.walletPieChart.legend
                legend.isEnabled = false
                val data = PieData(dataSet)
                data.setValueTextSize(12F)
                data.setDrawValues(true)
                data.setValueFormatter(PercentFormatter())
                dataSet.setValueTextColors(colors)
                dataSet.isUsingSliceColorAsValueLineColor = true
                data.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (isBalance) {
                            getString(
                                R.string.blank,
                                activity?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()))
                            ) + Extension.toBigDecimal(value.toString())
                        } else {
                            // 2 digits after decimal point
                            getString(
                                R.string.blank,
                                "%"
                            ) + String.format("%.2f",value)
                        }
                    }
                })
                context?.theme?.resolveAttribute(R.attr.primaryText, value, true)
                binding.walletPieChart.setEntryLabelColor(value.data)
                binding.walletPieChart.setCenterTextColor(value.data)
                context?.theme?.resolveAttribute(R.attr.statisticsBgColor, value, true)
                binding.walletPieChart.setHoleColor(value.data)
                binding.walletPieChart.data = data
                binding.walletPieChart.highlightValues(null)
                binding.walletPieChart.invalidate()
            }
        }
        catch (exc: ConcurrentModificationException) {
            Thread.currentThread().interrupt()
        }

    }
    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter      = StatisticsAdapter()
            adapter       = mAdapter
            setHasFixedSize(true)
        }
    }
    private fun setDataRecyclerViewExpense() {
        mAdapter.clear()
        val staticList: ArrayList<StatisticsModel> = ArrayList()
        val staticListBySort: ArrayList<StatisticsModel> = ArrayList()
        try {
            nestedTransList.value?.filter { TimeConverter.dateWithFormat(it.date) == currentMonthYear.value }
                ?.forEach { nestedTransaction ->
                    val mListExpense = nestedTransaction.nestedTransList.filter { it.type == Constants.EXPENSE }
                    totalAmountByCategory.forEach{ key ->
                        mListExpense.forEach { transaction ->
                            if (transaction.category.title == key.key) {
                                key.value.forEach{
                                    if (it.key == "expense") {
                                        val price = DecimalFormat("0.00").format(it.value)
                                        staticList.add(
                                            StatisticsModel(key.key.toString(),
                                                price.replace(",",".").toFloat(),transaction.category.color.toString())
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            staticListBySort.addAll(staticList.sortedByDescending { it.percent })
            showLayoutEmpty(staticList)
            mAdapter.addItems(staticListBySort)
        }
        catch (exc: ConcurrentModificationException) {
            Thread.currentThread().interrupt()
        }


    }
    private fun showLayoutEmpty(staticList: ArrayList<StatisticsModel>) {
        if (staticList.isEmpty()) {
            binding.layoutBody.visibility = View.GONE
            binding.textNoData.visibility = View.VISIBLE
            binding.iconNoData.visibility = View.VISIBLE
        } else {
            binding.layoutBody.visibility = View.VISIBLE
            binding.textNoData.visibility = View.GONE
            binding.iconNoData.visibility = View.GONE
        }
    }
    private fun setDataRecyclerViewIncome() {
        try {
            mAdapter.clear()
            val staticList: ArrayList<StatisticsModel> = ArrayList()
            val staticListBySort: ArrayList<StatisticsModel> = ArrayList()
            nestedTransList.value?.filter { TimeConverter.dateWithFormat(it.date) == currentMonthYear.value }
                ?.forEach { nestedTransaction ->
                    val mListIncome = nestedTransaction.nestedTransList.filter { it.type == Constants.INCOME }
                    totalAmountByCategory.forEach{ map ->
                        mListIncome.forEach { transaction ->
                            if (map.key == transaction.category.title) {
                                map.value.forEach{
                                    if (it.key == "income") {
                                        val price = DecimalFormat("0.00").format(it.value)
                                        staticList.add(
                                            StatisticsModel(map.key.toString(),
                                                price.replace(",",".").toFloat(),transaction.category.color.toString()))
                                    }
                                }
                            }
                        }
                    }
                }
            staticListBySort.addAll(staticList.sortedByDescending { it.percent })
            showLayoutEmpty(staticList)
            mAdapter.addItems(staticListBySort)
        }
        catch (exc: ConcurrentModificationException) {
            Thread.currentThread().interrupt()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.incomeText.setText(R.string.income)
        binding.expenseText.setText(R.string.expense)
    }
}