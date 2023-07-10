package io.paraga.moneytrackerdev.utils.helper

import android.widget.TextView
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindTextAddress")
    fun bindValueText(view: TextView, data: ArrayList<String>) {
        data.forEach {
        }
    }
}

