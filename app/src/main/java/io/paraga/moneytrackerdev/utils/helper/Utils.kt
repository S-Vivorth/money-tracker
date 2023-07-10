package io.paraga.moneytrackerdev.utils.helper

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.text.format.DateFormat
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import java.util.*

object Utils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
    fun Number.toPx() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
    fun getScreenWidth(activity: Activity): Float {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        return outMetrics.widthPixels.toFloat()
    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 1000) {
        val transition: Transition  = Slide(Gravity.TOP)
//        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }
    fun convertDate(dateInMilliseconds: Long, dateFormat: String?): String? {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString()
    }

    fun getDaysBetweenDates(start: Date?, end: Date?): Int {
        val sDate = Calendar.getInstance()
        sDate.time = start
        val eDate = Calendar.getInstance()
        eDate.time = end

        // Get the represented date in milliseconds
        val milis1 = sDate.timeInMillis
        val milis2 = eDate.timeInMillis

        // Calculate difference in milliseconds
        val diff = Math.abs(milis2 - milis1)
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } else {
            false
        }
    }
}