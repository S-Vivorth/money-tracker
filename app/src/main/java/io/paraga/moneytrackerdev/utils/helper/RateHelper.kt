package io.paraga.moneytrackerdev.utils.helper

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.ConstantsHelper
import java.util.*

class RateHelper() {
    companion object {
        // Dialog will show after x day set in this variable (dialog already show but user click no)
        const val DATE_DURATION_TO_SHOW_DIALOG: Short = 3
        // Dialog will show after x times set in this variable (first time show dialog)
        const val TIME_TO_SHOW_DIALOG_FIRST_TIME: Short = 5
    }

    fun showRateDialogIfNecessary(context: Context?) {
        if (Preferences().getInstance().isAlreadyDisplayedRateDialog(context!!)) {
            if (!Preferences().getInstance().isRated(context)) {
                val currentDate = Date(System.currentTimeMillis())
                val oldDate = getDateWhenRateDialogShow(context)
                var differenceInDay = -2
                if (oldDate != null) {
                    differenceInDay = Utils.getDaysBetweenDates(oldDate, currentDate)
                }
                if (differenceInDay >= DATE_DURATION_TO_SHOW_DIALOG) {
                    showDialog(context)
                }
            }
        } else {
            Preferences().getInstance().addCounterForRate(context, 1)
            if (Preferences().getInstance()
                    .getCounterForRate(context) >= TIME_TO_SHOW_DIALOG_FIRST_TIME
            ) {
                Preferences().getInstance().setCounterForRate(context, 0)
                Preferences().getInstance().setAlreadyDisplayedRateDialog(context, true)
                showDialog(context)
            }
        }
    }

     private fun showDialog(context: Context) {
        Preferences().getInstance().setDateWhenRateDialogShow(context, System.currentTimeMillis())
         val  builder = MaterialAlertDialogBuilder(context,R.style.CustomAlertDialog).create()
         val view = LayoutInflater.from(context).inflate(R.layout.rate_reviews_layout,null)
         val rateUs = view.findViewById<AppCompatButton>(R.id.rate_us)
         val unLike = view.findViewById<AppCompatButton>(R.id.do_not_like)
         val imgClose = view.findViewById<ImageView>(R.id.closeBtn)
         val imm: InputMethodManager? =
             context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
         rateUs.setOnClickListener {
             Preferences().getInstance().setRate(context, true)
             val browserIntent = Intent(
                 Intent.ACTION_VIEW,
                 Uri.parse(
                     ConstantsHelper.KeyUri.SHARE_LINK_APP
                 )
             )
             context.startActivity(browserIntent)
             builder.dismiss()
        }
         imgClose.setOnClickListener {
             builder.dismiss()
         }
         unLike.setOnClickListener {
             builder.dismiss()
         }
         val back = ColorDrawable(Color.TRANSPARENT)
         val inset = InsetDrawable(back, 40)
         builder.window?.setBackgroundDrawable(inset)
         builder.setView(view)
         builder.setCancelable(false)
         builder.show()
    }

    private fun getDateWhenRateDialogShow(context: Context): Date? {
        val l: Long = Preferences().getInstance().getDateWhenRateDialogShow(context)
        return if (l == 0L) {
            null
        } else {
            Date(l)
        }
    }

}