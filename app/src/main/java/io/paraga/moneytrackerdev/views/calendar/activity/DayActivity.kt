package io.paraga.moneytrackerdev.views.calendar.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.ActivityDayBinding
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.views.calendar.fragment.DayPagerFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDayBinding
    private val numPages = 601
    private val middleNumPages = (numPages - 1) / 2
    lateinit  var visibleCalendar: Calendar
    private var dateValue = ""

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        initDay()
    }
    private fun initViewPager() {
        val adapter = ScreenSlidePagerAdapter(supportFragmentManager)
        binding.pager.clipToPadding = false
        binding.pager.adapter = adapter
        binding.pager.currentItem = middleNumPages
        binding.pager.pageMargin = 180
        binding.pager.setPageTransformer(true, FadePageTransformer())
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
    private fun initDay() {
        dateValue = intent.getStringExtra(Constants.DATE_KEY).toString()

        val separatedDate = dateValue.split("-").toTypedArray()
        val dayInt = Integer.valueOf(separatedDate[2].replaceFirst("^0*".toRegex(), ""))
        val monthInt = Integer.valueOf(separatedDate[1].replaceFirst("^0*".toRegex(), ""))
        val yearInt = Integer.valueOf(separatedDate[0].replaceFirst("^0*".toRegex(), ""))

        // Set adapter to current day
        visibleCalendar = Calendar.getInstance()
        visibleCalendar.set(yearInt, monthInt - 1, dayInt)
        initViewPager()
    }
    private inner class ScreenSlidePagerAdapter(activity: FragmentManager) : FragmentStatePagerAdapter(activity) {

    override fun getCount(): Int  = numPages


    override fun getItem(position: Int): Fragment {
        val cal = visibleCalendar.clone() as Calendar
        cal.add(GregorianCalendar.DATE, position - middleNumPages)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val itemValue = dateFormat.format(cal.time)
        return DayPagerFragment.newInstance(dataValue = itemValue, mSelectDate = dateValue)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}
    class FadePageTransformer : ViewPager.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            if (position < -1 || position > 1) {
                view.alpha = 0f
            } else if (position <= 0 || position <= 1) {
                // Calculate alpha. Position is decimal in [-1,0] or [0,1]
                val alpha = if (position <= 0) position + 1 else 1.0f
                view.alpha = alpha
            } else  {
                view.alpha = 1f
            }
        }
    }
}