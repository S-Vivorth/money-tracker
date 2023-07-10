package io.paraga.moneytrackerdev.views.statistics.fragment.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.statistics.model.StatisticsModel
import java.text.DecimalFormat

class StatisticsAdapter: RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {
    private var mList : ArrayList<StatisticsModel> = arrayListOf()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textTitle: TextView
        var textPrice: TextView
        var textPercent: TextView
        var cardView: CardView
        init {
            textPercent = view.findViewById(R.id.text_percent)
            textTitle = view.findViewById(R.id.text_title)
            cardView = view.findViewById(R.id.card_percent)
            textPrice = view.findViewById(R.id.text_price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.statistics_item, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("Range", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var totalAmount = 0f
        var percents = 0f
        mList.forEach {
            totalAmount += it.percent ?: 0f
            if (it.title == mList[position].title) {
                percents = it.percent!!
            }
        }
        holder.textPrice.text = Extension.getCurrencySymbol(
            selectedWallet.currency.toString()) + " " + Extension.toBigDecimal(mList[position].percent.toString())
        holder.textTitle.text = mList[position].title ?: ""
        holder.cardView.setCardBackgroundColor(Color.parseColor(mList[position].color ?: "#FFB028"))
        val percent = percents.times(100).div(totalAmount)
        holder.textPercent.text = DecimalFormat("0").format(percent) + "%"
    }

    override fun getItemCount(): Int {
        return mList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }
    fun addItems(list: ArrayList<StatisticsModel>) {
        if (list.size == 0) return
        for (item in list) {
            val index = mList.indexOf(item)
            if (index >= 0) {
                if (mList[index].toString() != item.toString()) {
                    mList[index] = item
                    notifyItemChanged(index,1)
                }
            } else {
                mList.add(item)
                notifyItemInserted(mList.size-1)
            }
        }
    }
}