package io.paraga.moneytrackerdev.utils.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import io.paraga.moneytrackerdev.R
import java.util.*

class LoadingHelper constructor(context: Context){
    private val dialog= Dialog(context)
    init {
        dialog.setContentView(R.layout.layout_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    fun show(isCancelable : Boolean? = null) {
        dialog.setCancelable(isCancelable ?: false)
        dialog.setCanceledOnTouchOutside(isCancelable ?: false)
        if (!dialog.isShowing)
            dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    companion object {
        private val LHs = IdentityHashMap<Context, LoadingHelper>()
        fun with(ctx: Context): LoadingHelper {
            var lh = LHs[ctx]
            if (lh == null) {
                lh = LoadingHelper(ctx)
                LHs[ctx] = lh
            }
            return lh
        }
    }
}