package io.paraga.moneytrackerdev.utils.helper

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import io.paraga.moneytrackerdev.R

object CustomToastHelper {
    fun Toast.showCustomToast(message: String, activity: Activity)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.custom_toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        val nightModeFlags: Int = activity.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        val bg = layout.findViewById<RelativeLayout>(R.id.button_click_parent)
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            bg.setBackgroundColor(ContextCompat.getColor(activity,R.color.darkToastBgColor))
            textView.setTextColor(ContextCompat.getColor(activity,R.color.darkPrimaryTextColor))
        }
        else {
            bg.setBackgroundColor(ContextCompat.getColor(activity,R.color.lightToastBgColor))
            textView.setTextColor(ContextCompat.getColor(activity,R.color.lightPrimaryTextColor))
        }
        this.apply {
            this@showCustomToast.setGravity(Gravity.BOTTOM, 0, 250)
            this@showCustomToast.duration = Toast.LENGTH_LONG
            this@showCustomToast.view = layout
            this@showCustomToast.show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun CardView.setBackGround(cardView: CardView, textView: TextView, isCheck: Boolean = false)
    {
        val value = TypedValue()
        if (isCheck) {
            cardView.setBackgroundResource(R.drawable.round_month_cell)
            textView.setTextColor(context.getColor(R.color.bgButton))
        } else {
            cardView.setBackgroundResource(R.drawable.non_backgroud)
            context.theme.resolveAttribute(R.attr.primaryText, value, true)
            textView.setTextColor(value.data)
        }
    }
}
