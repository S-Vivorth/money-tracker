package io.paraga.moneytrackerdev.views.calendar.fragment
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.FragmentCalendarBinding
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.TimeConverter
import io.paraga.moneytrackerdev.views.calendar.activity.DayActivity

import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.selectedWalletId
import io.paraga.moneytrackerdev.views.transaction.MonthViewContainer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import java.util.*
import kotlin.concurrent.thread



class CalendarFrag : Fragment {
    private var _binding: FragmentCalendarBinding?= null
    private val binding get() = _binding!!
    private var selectedDate: LocalDate? = LocalDate.now()
    private val today = LocalDate.now()
    private var currentDate = YearMonth.now()
    private var dayOfWeek = daysOfWeek()
    private val dateKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var transType = Enums.TransTypes.ALL.value
    val value = TypedValue()
    lateinit var mainActivity: MainActivity
    lateinit var sundayShort:String
    private val sundayFull = daysOfWeek()[0].getDisplayName(TextStyle.FULL, Locale.getDefault())
    private val mondayShort = daysOfWeek()[1].getDisplayName(TextStyle.SHORT, Locale.getDefault())
    lateinit var mondayFull: String
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    constructor() : super()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    //Nothing todo
                }
            }
        mainActivity = context as MainActivity
        sundayShort = daysOfWeek()[0].getDisplayName(TextStyle.SHORT, Extension.getLocale(mainActivity))
        mondayFull = daysOfWeek()[1].getDisplayName(java.time.format.TextStyle.FULL,Extension.getLocale(mainActivity))
        showLayout()
        thread {
            kotlin.run {
                initTransaction()
            }
        }
        goToNextOrPrevMonth()
        initListener()
    }

    fun getRefreshCalendar() {
        mainActivity.switchFrag(CalendarFrag())
    }

    fun showLayout() {
        //hide layout total from main
        mainActivity.binding.layoutPrice.visibility = View.VISIBLE
        mainActivity.binding.chooseWalletLayout.visibility = View.VISIBLE
        mainActivity.binding.layoutHeader.calendarHeader.visibility = View.VISIBLE
        mainActivity.binding.walletIcon.visibility = View.VISIBLE
        mainActivity.binding.walletName.visibility = View.VISIBLE
        mainActivity.binding.imgArrowDown.visibility = View.VISIBLE
        mainActivity.binding.layoutCurrentMonth.visibility = View.VISIBLE
        mainActivity.binding.layoutSearch.visibility = View.VISIBLE
        mainActivity.binding.textBudget.visibility = View.GONE
        mainActivity.binding.budgetViewType.visibility = View.GONE
        mainActivity.binding.addBudgetBtn.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        showLayout()
    }

    private fun initListener() {
        mainActivity.binding.layoutHeader.imageArrowNext.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.plusMonths(1)
            goToNextOrPrevMonth()
            currentDisplayedMonth?.let { it1 -> binding.calendarView.smoothScrollToMonth(it1) }
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
        mainActivity.binding.layoutHeader.imageArrowBack.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.minusMonths(1)
            goToNextOrPrevMonth()
            currentDisplayedMonth?.let { it1 -> binding.calendarView.smoothScrollToMonth(it1) }
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
        mainActivity.binding.layoutCurrentMonth.setOnClickListener {
            currentDisplayedMonth = currentDate
            mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                TextStyle.FULL, Extension.getLocale(mainActivity))
                    + " "
                    + currentDisplayedMonth?.year.toString())
            goToNextOrPrevMonth()
            mainActivity.goneDatePicker()
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
        // Scroll Calendar Next Month or Last Month
        binding.calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                binding.calendarView.smoothScrollToMonth(p1.yearMonth)
                if (p1.yearMonth.isAfter(currentDisplayedMonth)) {
                    currentDisplayedMonth = p1.yearMonth
                    currentMonthYear.value = activity?.getString(
                        R.string.blank, p1.yearMonth.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                + " "
                                + p1.yearMonth.year.toString()).toString()
                    getNestedTransList()
                }
                if(p1.yearMonth.isBefore(currentDisplayedMonth)) {
                    currentDisplayedMonth = p1.yearMonth
                    currentMonthYear.value = activity?.getString(
                        R.string.blank, p1.yearMonth.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                + " "
                                + p1.yearMonth.year.toString()).toString()
                    getNestedTransList()
                }
                goToNextOrPrevMonth()
                mainActivity.setDataNavigation()
                mainActivity.calculateData()
            }
        }
    }

    @SuppressLint("SetTextI18n")

    private fun initCalendar() {


        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(
                container: DayViewContainer,
                data: CalendarDay
            ) {
                container.textView.text = data.date.dayOfMonth.toString()
                Thread {
                    kotlin.run {
                        nestedTransList.value?.filter { nestedTransaction ->
                            TimeConverter.dateWithFormat(nestedTransaction.date) == currentMonthYear.value
                        }?.map { date ->
                            val day = TimeConverter.dateFormatCalendar(date.date)
                            var totalIncomeInCell = 0.0
                            var totalExpenseInCell = 0.0
                            val allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet()
                            val currency: String = if (selectedWallet == allWallet) {
                                user?.defaultCurrency.toString()
                            } else {
                                (selectedWallet.currency.toString())
                            }
                            if ((day == data.date.dayOfMonth.toString()) && TimeConverter.dateFormatCurrentMonth(data.date.yearMonth.toString()) == currentMonthYear.value ) {
                                date.nestedTransList.forEach {
                                    if (it.type == Constants.INCOME) {
                                        val inComeConverter = String.format(
                                            "%.2f",
                                            it.amount?.toDouble()?.let { it1 ->
                                                Extension.convertCurrency(
                                                    it.currency.toString(),
                                                    currency,
                                                    it1,
                                                    requireContext()
                                                )
                                            }
                                        ).replace(",",".").toDouble()
                                        totalIncomeInCell += inComeConverter
                                    } else {
                                        val expenseConverter = String.format(
                                            "%.2f",
                                            it.amount?.toDouble()?.let { it1 ->
                                                Extension.convertCurrency(
                                                    it.currency.toString(),
                                                    currency,
                                                    it1,
                                                    requireContext()
                                                )
                                            }
                                        ).replace(",",".").toDouble()
                                        totalExpenseInCell += expenseConverter
                                    }
                                }
                                if (totalIncomeInCell == 0.0) {
                                    container.textIncome.visibility = View.GONE
                                } else  {
                                    container.textIncome.text = activity?.getString(
                                        R.string.blank,
                                        Extension.getCurrencySymbol(
                                            selectedWallet.currency.toString()
                                        )
                                                + Extension.toBigDecimal(totalIncomeInCell.toString())
                                    )
                                }
                                if (totalExpenseInCell == 0.0) {
                                    container.textExpense.visibility = View.GONE
                                } else {
                                    container.textExpense.text = activity?.getString(
                                        R.string.blank,
                                        Extension.getCurrencySymbol(
                                            selectedWallet.currency.toString()
                                        )
                                                + Extension.toBigDecimal(totalExpenseInCell.toString())
                                    )
                                }
                                if (totalIncomeInCell - totalExpenseInCell == 0.0) {
                                    container.textTotal.visibility = View.GONE
                                } else {
                                    container.textTotal.text = activity?.getString(
                                        R.string.blank,
                                        Extension.getCurrencySymbol(
                                            selectedWallet.currency.toString()
                                        )
                                                + Extension.toBigDecimal((totalIncomeInCell - totalExpenseInCell).toString())
                                    )
                                }
                            }
                        }
                    }
                }.start()

                if (data.position != DayPosition.MonthDate) {
                    context?.theme?.resolveAttribute(R.attr.statisticsBgColor, value, true)
                    container.frame.setBackgroundColor(value.data)
                    context?.theme?.resolveAttribute(R.attr.calendarDisableCellTextColor, value, true)
                    container.textView.setTextColor(value.data)
                }
                // clicked on calendar
                container.frame.setOnClickListener {
                    if (data.position == DayPosition.MonthDate) {
                        if (selectedDate != null) {
                            selectedDate?.let {binding.calendarView.notifyDateChanged(it)}
                        }
                        selectedDate = data.date
                        selectedDate?.let { binding.calendarView.notifyDateChanged(it)}
                        // show day screen
                        val dateValue = dateKeyFormatter.format(selectedDate)
                        showDayActivity(dateValue)
                    }
                }

                // set color when clicked on cell calendar
                if (data.position == DayPosition.MonthDate) {
                    when(data.date) {
                        today -> {
                            context?.theme?.resolveAttribute(R.attr.statisticsBgColor, value, true)
                            container.dayCell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorItemCalendar))
                            container.frame.setBackgroundColor(value.data)
                        }
                        selectedDate -> {
                            context?.theme?.resolveAttribute(R.attr.calendarCellSelectedBgColor, value, true)
                            container.frame.setBackgroundColor(value.data)
                        }
                        else -> {
                            context?.theme?.resolveAttribute(R.attr.statisticsBgColor, value, true)
                            container.frame.setBackgroundColor(value.data)
                        }
                    }
                }
                setColorOnSunday(container,data)
            }
        }

        // set day like SUN MON on calendar
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library

        // init new calendar
        dayOfWeek = if (mainActivity.startDate() == mondayFull){
            daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        } else {
            daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        }
        binding.calendarView.setup(startMonth, endMonth, dayOfWeek.first())
        currentDisplayedMonth?.let { binding.calendarView.scrollToMonth(it) }
        val titlesContainer = view?.findViewById<ViewGroup>(R.id.titlesContainer)
        titlesContainer?.children
            ?.map { it as TextView }
            ?.forEachIndexed { index, textView ->
//                val name = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)[index]
                val title = dayOfWeek[index].getDisplayName(TextStyle.SHORT, Extension.getLocale(mainActivity))
                if (title == sundayShort) {
                    context?.theme?.resolveAttribute(R.attr.primaryText, value, true)
                    textView.setTextColor(value.data)
                }
                textView.text = title
            }
        // month binder
        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
            }

            override fun create(view: View) = MonthViewContainer (view)
        }
    }
    private fun setColorOnSunday(container: DayViewContainer, day: CalendarDay) {
        val dayOfWeek = day.date.dayOfWeek
        if (dayOfWeek == DayOfWeek.SUNDAY && day.position == DayPosition.MonthDate) {
            container.textView.setTextColor(
                ContextCompat.getColor(
                    mainActivity,
                    R.color.colorPublicHolidayText
                )
            )
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun initTransaction() {
        GlobalScope.launch(Dispatchers.Main) {
            nestedTransList.observeForever {
                initCalendar()
            }
            mainActivity.calculateData()
        }
    }
    private fun goToNextOrPrevMonth() {
        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(
                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(mainActivity)))
        } else {
            mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(
                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(mainActivity))
                        + " "
                        + currentDisplayedMonth?.year.toString())
        }
        currentMonthYear.value = activity?.getString(
            R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " "
                    + currentDisplayedMonth?.year.toString()).toString()
//        getNestedTransList()
    }

    private fun getNestedTransList() {
        FirebaseManager(requireContext()).getNestedTransList(
            currentMonthYear.value.toString(), selectedWalletId,
            onSuccess = {
                mainActivity.calculateData()
            })
    }

    private fun showDayActivity(dateValue: String) {
        val intent = Intent(context, DayActivity::class.java)
        intent.putExtra(Constants.DATE_KEY, dateValue)
        activityResult.launch(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
            TextStyle.FULL, Extension.getLocale(mainActivity))
                + " "
                + currentDisplayedMonth?.year.toString())
        val titlesContainer = view?.findViewById<ViewGroup>(R.id.titlesContainer)
        titlesContainer?.children
            ?.map { it as TextView }
            ?.forEachIndexed { index, textView ->
//                val name = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)[index]
                val title = dayOfWeek[index].getDisplayName(TextStyle.SHORT, Extension.getLocale(mainActivity))
                if (title == sundayShort) {
                    context?.theme?.resolveAttribute(R.attr.primaryText, value, true)
                    textView.setTextColor(value.data)
                }
                textView.text = title
            }


    }
}

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    val textIncome: TextView = view.findViewById(R.id.text_income)
    val textExpense: TextView = view.findViewById(R.id.text_expense)
    val textTotal : TextView = view.findViewById(R.id.text_total)
    val dayCell: FrameLayout = view.findViewById(R.id.dayCell)
    val frame : FrameLayout = view.findViewById(R.id.frame_item)
}
