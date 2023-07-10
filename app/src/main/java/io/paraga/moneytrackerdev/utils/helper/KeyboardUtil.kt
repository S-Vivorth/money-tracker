package io.paraga.moneytrackerdev.utils.helper

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardUtil {

    private var imm: InputMethodManager? = null
    private var mView: View? = null
    private var index = -1

    fun showKeyboardWithFocus(activity: Activity, view: View) {
        view.requestFocus()
        index +=1
        imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, index)
        mView = view
    }
    fun showKeyboard(context: Context, view: View) {
        if (view.requestFocus()) {
            imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

    }
    fun hideKeyboardWithFocus(activity: Activity){
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}