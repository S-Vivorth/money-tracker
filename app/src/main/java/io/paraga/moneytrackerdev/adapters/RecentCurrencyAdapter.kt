package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.transaction.CalculatorFrag


class RecentCurrencyAdapter(val calculatorFrag: CalculatorFrag, var selectedPosition: Int,
var wallet:Wallet): RecyclerView.Adapter<RecentCurrencyAdapter.ViewHolder>() {
    val value = TypedValue()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var context: Context
        lateinit var currencyName: TextView
        init {
            context = view.context
            currencyName = view.findViewById(R.id.currencyName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentCurrencyAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_currency_cell, parent, false)
        return ViewHolder(view = view)

    }

    override fun onBindViewHolder(holder: RecentCurrencyAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.currencyName.text = calculatorFrag.recentCurrencyList[position]
//        if (calculatorFrag.recentCurrencyList[position] == wallet.currency.toString()) {
//            holder.itemView.setBackgroundResource(
//                R.drawable.rounded_btn
//            )
//        }
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = position
            notifyItemChanged(selectedPosition)

        }

        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(
                R.drawable.selected_recent_currency
            )
            val selectedCurrency = calculatorFrag.recentCurrencyList[position]
            calculatorFrag.binding.selectedCurrencyName.text = selectedCurrency
            holder.context.theme.resolveAttribute(R.attr.primaryText, value, true)
            holder.currencyName.setTextColor(value.data)
            calculatorFrag.currentCurrencyName = selectedCurrency
            calculatorFrag.addTrans.currentCurrencyName = selectedCurrency
            calculatorFrag.addTrans.binding.currencySymbol.text =
                Extension.getCurrencySymbol(
                    selectedCurrency
                )
        }
        else {
            holder.itemView.setBackgroundResource(0)
            holder.currencyName.setTextColor(Color.GRAY)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return calculatorFrag.recentCurrencyList.size
    }
}