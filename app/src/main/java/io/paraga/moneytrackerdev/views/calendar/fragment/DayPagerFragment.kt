package io.paraga.moneytrackerdev.views.calendar.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.ConstantsHelper
import io.paraga.moneytrackerdev.databinding.FragmentDayPagerBinding
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.networks.nestedTransList
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.utils.helper.CalendarHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.TimeConverter
import io.paraga.moneytrackerdev.views.calendar.adapter.CalendarDetailAdapter
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread



class DayPagerFragment : Fragment() {

    private var _binding: FragmentDayPagerBinding?= null
    private val binding get() = _binding!!
    private var mDateValue = ""
    private var mSelectDate = ""
    private var mCalendar = ""
    private var mList: NestedTransaction?= null
    lateinit var calendarDetailAdapter: CalendarDetailAdapter
    var isCanClick = true
    val value = TypedValue()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDateValue = arguments?.getString(ConstantsHelper.Option.dateValue).toString()
        mSelectDate = arguments?.getString(ConstantsHelper.Option.mSELECT_DATE).toString()
        initStyles()
        initViewDay()
        thread {
            kotlin.run {
                initTransaction()
            }
        }
        initListener()
    }
    private fun initListener() {
        binding.imgClose.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        binding.buttonAddTransaction.setOnClickListener {
            if (!isCanClick) return@setOnClickListener
            setCannotClick()
            activityResult.launch(Intent(context, AddTrans::class.java).apply {
                putExtra(ConstantsHelper.Option.mSELECT_DATE,mSelectDate)
                putExtra(ConstantsHelper.Option.add_tran_calendar,true)
            })
        }
        binding.mainContainer.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

//        val nightModeFlags = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
//
//        when (nightModeFlags) {
//            Configuration.UI_MODE_NIGHT_YES ->  binding.cardPager.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.darkBgColor))
//            Configuration.UI_MODE_NIGHT_NO ->  binding.cardPager.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
//            Configuration.UI_MODE_NIGHT_UNDEFINED ->  binding.cardPager.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
//        }

    }
    private fun initViewDay() {
        val day = CalendarHelper(context = requireContext(), dateValue = mDateValue)
        binding.textDayInt.text = day.day
        binding.textDay.text = day.week
        binding.textMonth.text = day.month
        mCalendar = TimeConverter.dateFormatDay(day.day) + " " + TimeConverter.formatMonth(day.month, requireContext()) + " " + day.year
        binding.textYear.text = day.year
    }
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                initTransaction()
            }
        }

    private fun initStyles() {
        val nightModeFlags: Int = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.textDay.setTextColor(Color.WHITE)
            binding.textDayInt.setTextColor(Color.WHITE)
            binding.textValueTotal.setTextColor(Color.WHITE)
            binding.closeBtn.setColorFilter(Color.WHITE)
            binding.buttonAddTransaction.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(),
                R.color.darkYellowBtnBgColor))

            binding.cardPager.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.darkBgColor))
        }
        else {
            binding.cardPager.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.white))
        }

    }
    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        val mFilterList = nestedTransList.value?.filter { it.date == mCalendar }
        mFilterList?.toMutableList()?.clear()
        mFilterList?.forEach {
            mList = it
            binding.textValueTotal.text = context?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()) + Extension.toBigDecimal(it.totalAmount.toString()))
            binding.textValueIncome.text = context?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()) + Extension.toBigDecimal(it.totalIncome.toString()))
            binding.textValueExpense.text = context?.getString(R.string.blank, Extension.getCurrencySymbol(selectedWallet.currency.toString()) + Extension.toBigDecimal(it.totalExpense.toString()))
        }
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        calendarDetailAdapter = CalendarDetailAdapter()
        mList?.let { list ->
            calendarDetailAdapter.clear()
            list.nestedTransList.let { calendarDetailAdapter.addItems(ArrayList(it)) }
        }
        binding.recyclerView.layoutManager = categoryLinearLayoutManager
        binding.recyclerView.adapter = calendarDetailAdapter
        binding.recyclerView.setHasFixedSize(true)
        if (mFilterList?.isEmpty() == true) {
            binding.recyclerView.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noDataLayout.visibility = View.GONE
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun initTransaction() {
        GlobalScope.launch(Dispatchers.Main) {
            nestedTransList.observeForever {
                initRecyclerView()
            }
        }
    }
    private fun setCannotClick() {
        isCanClick = false
        Handler(Looper.getMainLooper()).postDelayed({
            isCanClick = true
        },1000)
    }
    companion object {
        @JvmStatic
        fun newInstance(dataValue: String,mSelectDate: String) = DayPagerFragment().apply {
            arguments= Bundle().apply {
                putString(ConstantsHelper.Option.dateValue,dataValue)
                putString(ConstantsHelper.Option.mSELECT_DATE,mSelectDate)
            }
        }
    }
}