package io.paraga.moneytrackerdev.views.bottomSheet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.daysOfWeek
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentBottomSheetStartDateBinding
import io.paraga.moneytrackerdev.models.DayModel
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.MainActivity
import java.time.format.TextStyle
import java.util.*

class BottomSheetStartDateFragment(private val listener: OnClickedListener<DayModel>, private val day: String) : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetStartDateBinding?= null
    private val binding get() = _binding!!
    companion object {
        const val TAG = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetStartDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        initListener()
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListener() {
        binding.textMonday.text = daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(requireContext()))
        binding.textSunday.text = daysOfWeek()[0].getDisplayName(TextStyle.FULL, Extension.getLocale(requireContext()))

        binding.appBar.backBtnLayout.setOnClickListener {
            dialog?.dismiss()
        }
        binding.layoutMonday.setOnClickListener {
            listener.resultBack(DayModel(binding.textMonday.text.toString()))
            binding.layoutMonday.setBackgroundResource(R.drawable.rounded_drawer)
            binding.layoutSunday.setBackgroundResource(R.drawable.rounded_drawer_while)
            Preferences().getInstance().setWeekStart(requireContext(), Enums.General.MONDAY.value)
            Handler(Looper.myLooper()!!).postDelayed({
                dialog?.dismiss()
            }, 500)

        }
        binding.layoutSunday.setOnClickListener {
            listener.resultBack(DayModel(binding.textSunday.text.toString()))
            binding.layoutSunday.setBackgroundResource(R.drawable.rounded_drawer)
            binding.layoutMonday.setBackgroundResource(R.drawable.rounded_drawer_while)
            Preferences().getInstance().setWeekStart(requireContext(), Enums.General.SUNDAY.value)
            Handler(Looper.myLooper()!!).postDelayed({
                dialog?.dismiss()
            }, 500)
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData() {
        if (day == daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(requireContext()))) {
            binding.textMonday.text = day
            binding.layoutMonday.setBackgroundResource(R.drawable.rounded_drawer)
        } else {
            binding.textSunday.text = day
            binding.layoutSunday.setBackgroundResource(R.drawable.rounded_drawer)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.textMonday.text = daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(requireContext()))
        binding.textSunday.text = daysOfWeek()[0].getDisplayName(TextStyle.FULL, Extension.getLocale(requireContext()))
    }
}