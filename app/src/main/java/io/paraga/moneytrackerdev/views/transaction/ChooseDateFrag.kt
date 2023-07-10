package io.paraga.moneytrackerdev.views.transaction

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.databinding.FragmentChooseDateBinding
import io.paraga.moneytrackerdev.utils.helper.Extension
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import io.paraga.moneytrackerdev.R
import java.util.*


class ChooseDateFrag : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseDateBinding
    lateinit var addTrans: AddTrans
    var currentDisplayedMonth = YearMonth.now()
    private val todays = LocalDate.now()
    private var selectedDate: LocalDate? = LocalDate.now()
    val today = LocalDate.now().dayOfMonth
    val yesterday = LocalDate.now().minusDays(1).dayOfMonth
    val tomorrow = LocalDate.now().plusDays(1).dayOfMonth
    val value = TypedValue()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseDateBinding.inflate(layoutInflater)
        view?.setBackgroundResource(android.R.color.transparent)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 0.8).toInt()
        return bottomSheetDialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        binding.todayLayout.setOnClickListener {
            Extension.changeStroke(requireContext(), it, R.color.green)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.tomorrowLayout, value.data, isColorInt = true)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.yesterdayLayout, value.data, isColorInt = true)
            if (selectedDate != null) {
                binding.calendarView.notifyDateChanged(selectedDate!!)
            }
            selectedDate = LocalDate.now()
            binding.calendarView.smoothScrollToDate(LocalDate.now())
            binding.calendarView.notifyDateChanged(selectedDate!!)
            setDay(today.toString())
        }

        binding.yesterdayLayout.setOnClickListener {
            Extension.changeStroke(requireContext(), it, R.color.redBgColor)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.todayLayout, color=value.data, isColorInt = true)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.tomorrowLayout, color=value.data, isColorInt = true)
            if (selectedDate != null) {
                binding.calendarView.notifyDateChanged(selectedDate!!)
            }
            selectedDate = LocalDate.now().minusDays(1)
            binding.calendarView.smoothScrollToDate(LocalDate.now().minusDays(1))
            binding.calendarView.notifyDateChanged(selectedDate!!)
            setDay(yesterday.toString())

        }

        binding.tomorrowLayout.setOnClickListener {
            Extension.changeStroke(requireContext(), it, R.color.yellow)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.todayLayout, value.data, isColorInt = true)
            addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
            Extension.changeStroke(requireContext(), binding.yesterdayLayout, color=value.data, isColorInt = true)
            if (selectedDate != null) {
                binding.calendarView.notifyDateChanged(selectedDate!!)
            }
            selectedDate = LocalDate.now().plusDays(1)
            binding.calendarView.smoothScrollToDate(LocalDate.now().plusDays(1))
            binding.calendarView.notifyDateChanged(selectedDate!!)
            setDay(tomorrow.toString())
        }




        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            @SuppressLint("ResourceAsColor")
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()





                container.dayCell.setOnClickListener {
                    if (data.date == LocalDate.now()) {
                        binding.todayLayout.performClick()
                    }
                    else if (data.date == LocalDate.now().minusDays(1)) {
                        binding.yesterdayLayout.performClick()
                    }
                    else if (data.date == LocalDate.now().plusDays(1)) {
                        binding.tomorrowLayout.performClick()
                    }
                    else {
                        clearDateStroke()
                    }

                    if (data.position == DayPosition.MonthDate) {
                        binding.selectedDate.text = getString(
                            R.string.blank,
                            data.date.dayOfMonth.toString()
                                    + " "
                                    + data.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                    + " "
                                    + data.date.year.toString()
                        )
                        if (selectedDate != null) {
                            binding.calendarView.notifyDateChanged(selectedDate!!)
                        }

                        selectedDate = data.date

                        binding.calendarView.notifyDateChanged(selectedDate!!)
                    }
                }
                if (selectedDate == data.date) {
                    addTrans.theme.resolveAttribute(R.attr.selectedDateBgColor, value, true)
                    container.dayCell.background.setTint(value.data)
                    addTrans.theme.resolveAttribute(R.attr.selectedDateTextColor, value, true)
                    container.textView.setTextColor(value.data)
                }else if (todays == data.date) {
                    addTrans.theme.resolveAttribute(R.attr.defaultTodayBgColor, value, true)
                    container.dayCell.background.setTint(value.data)
                    addTrans.theme.resolveAttribute(R.attr.primaryText, value, true)
                    container.textView.setTextColor(value.data)
                }
                else {
                    container.dayCell.background.setTint(Color.TRANSPARENT)
                    addTrans.theme.resolveAttribute(R.attr.primaryText, value, true)
                    container.textView.setTextColor(value.data)
                }
                if (data.position != DayPosition.MonthDate) {
                    addTrans.theme.resolveAttribute(R.attr.calendarDisableCellTextColor, value, true)
                    container.textView.setTextColor(value.data)
                }
                binding.doneBtn.setOnClickListener {
                    addTrans.selectedDate = getString(
                        R.string.blank,
                        String.format("%02d", selectedDate?.dayOfMonth)
                                + " "
                                + selectedDate?.month?.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                + " "
                                + selectedDate?.year.toString()
                    )
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                    var selectedDate: LocalDate
                    try {
                        selectedDate = LocalDate.parse(addTrans.selectedDate, formatter)
                    }
                    catch (exc: Exception) {
                        selectedDate = addTrans.now
                    }
                    if (selectedDate == addTrans.now) {
                        Extension.changeStroke(requireContext(), addTrans.binding.dateLayout, R.color.primaryGreen, 1F)
                        addTrans.binding.selectedDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryGreen))
                        addTrans.binding.selectedDate.text = Enums.General.TODAY.value
                    }
                    else if (selectedDate == addTrans.now.plusDays(1)) {
                        Extension.changeStroke(requireContext(), addTrans.binding.dateLayout, R.color.yellow, 1F)
                        addTrans.binding.selectedDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                        addTrans.binding.selectedDate.text = Enums.General.TOMORROW.value
                    }
                    else if (selectedDate == addTrans.now.minusDays(1)) {
                        Extension.changeStroke(requireContext(), addTrans.binding.dateLayout, R.color.primaryRed, 1F)
                        addTrans.binding.selectedDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryRed))
                        addTrans.binding.selectedDate.text = Enums.General.YESTERDAY.value
                    }
                    else {
                        context?.theme?.resolveAttribute(R.attr.transDateTextColor, value, true)
                        Extension.changeStroke(requireContext(), addTrans.binding.dateLayout,  value.data, 1F, isColorInt = true)
                        addTrans.binding.selectedDate.setTextColor(value.data)
                        addTrans.binding.selectedDate.text = binding.selectedDate.text
                    }

                    addTrans.transDate = selectedDate?.atStartOfDay() ?: LocalDateTime.now()
                    dismiss()
                }
            }
        }



        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)

        // set selected date and move to correct date
        selectedDate = addTrans.transDate.toLocalDate()
        binding.calendarView.scrollToDate(addTrans.transDate.toLocalDate())
        when (selectedDate) {
            LocalDate.now() -> {
                binding.todayLayout.performClick()
            }
            LocalDate.now().minusDays(1) -> {
                binding.yesterdayLayout.performClick()
            }
            LocalDate.now().plusDays(1) -> {
                binding.tomorrowLayout.performClick()
            }
        }
        setDay(addTrans.transDate.toLocalDate().dayOfMonth.toString())
//        if (addTrans.isEditTrans) {
//            selectedDate = addTrans.transDate.toLocalDate()
//            binding.calendarView.scrollToDate(addTrans.transDate.toLocalDate())
//            when (selectedDate) {
//                LocalDate.now() -> {
//                    binding.todayLayout.performClick()
//                }
//                LocalDate.now().minusDays(1) -> {
//                    binding.yesterdayLayout.performClick()
//                }
//                LocalDate.now().plusDays(1) -> {
//                    binding.tomorrowLayout.performClick()
//                }
//            }
//            setDay(addTrans.transDate.toLocalDate().dayOfMonth.toString())
//        }
//        else {
//            selectedDate = addTrans.transDate.toLocalDate()
//            binding.calendarView.scrollToDate(selectedDate!!)
//            when (selectedDate) {
//                LocalDate.now() -> {
//                    binding.todayLayout.performClick()
//                }
//                LocalDate.now().minusDays(1) -> {
//                    binding.yesterdayLayout.performClick()
//                }
//                LocalDate.now().plusDays(1) -> {
//                    binding.tomorrowLayout.performClick()
//                }
//            }
//            setDay(selectedDate!!.dayOfMonth.toString())
//        }
        binding.calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                currentDisplayedMonth = p1.yearMonth
                goToNextOrPrevMonth()
            }

        }
        binding.currentDisplayedMonth.text = getString(
            R.string.blank,
            currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " "
                    + currentMonth.year.toString()
        )
        val titlesContainer: ViewGroup = binding.titlesContainer
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek()[index]
                val title = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                textView.text = title

            }


        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Remember that the header is reused so this will be called for each month.
                // However, the first day of the week will not change so no need to bind
                // the same view every time it is reused.
//                val titlesContainer = findViewById<ViewGroup>(R.id.titlesContainer)
//                titlesContainer.children
//                    .map { it as TextView }
//                    .forEachIndexed { index, textView ->
//                        val dayOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.THURSDAY)[index]
//                        val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
//                        textView.text = title
//                    }
//                if (container.titlesContianer.tag == null) {
//                    container.titlesContianer.tag = data.yearMonth
//                    container.titlesContianer.children.map { it as TextView }
//                        .forEachIndexed { index, textView ->
//                            val dayOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.THURSDAY)[index]
//                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
//                            textView.text = title
//                            // In the code above, we use the same `daysOfWeek` list
//                            // that was created when we set up the calendar.
//                            // However, we can also get the `daysOfWeek` list from the month data:
//                            // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
//                            // Alternatively, you can get the value for this specific index:
//                            // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
//                        }
//                }
            }
        }
        binding.forwardBtn.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth.plusMonths(1)
            goToNextOrPrevMonth()
        }

        binding.backwardBtn.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth.minusMonths(1)
            goToNextOrPrevMonth()
        }


    }

    override fun onResume() {
        super.onResume()
    }


    fun goToNextOrPrevMonth() {
        binding.calendarView.scrollToMonth(currentDisplayedMonth)
        binding.currentDisplayedMonth.text = getString(
            R.string.blank,
            currentDisplayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " "
                    + currentDisplayedMonth.year.toString()
        )
    }
    fun setDay(day: String){
        if (addTrans.isEditTrans) {
            binding.selectedDate.text = getString(
                R.string.blank,
                selectedDate?.dayOfMonth.toString()
                        + " "
                        + selectedDate?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        + " "
                        + LocalDate.now().year.toString()
            )
        }
        else {
            binding.selectedDate.text = getString(
                R.string.blank,
                day
                        + " "
                        + LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        + " "
                        + LocalDate.now().year.toString()
            )
        }

    }

    fun clearDateStroke(clearToday: Boolean = true) {
        addTrans.theme.resolveAttribute(R.attr.bgColor, value, true)
        if (clearToday) {
            Extension.changeStroke(requireContext(), binding.todayLayout, value.data, isColorInt = true)
        }
        Extension.changeStroke(requireContext(), binding.tomorrowLayout, value.data, isColorInt = true)
        Extension.changeStroke(requireContext(), binding.yesterdayLayout, value.data, isColorInt = true)
    }
}

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    val dayCell = view.findViewById<CardView>(R.id.dayCell)
    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}

class MonthViewContainer(view: View) : ViewContainer(view) {
    // Alternatively, you can add an ID to the container layout and use findViewById()
    val titlesContianer = view as ViewGroup
}