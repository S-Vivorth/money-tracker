package io.paraga.moneytrackerdev.views.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.search.activity.SearchActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SearchTransactionAdapter(
    var transList: ArrayList<NestedTransaction>,
    private val searchActivity: SearchActivity
) :
    RecyclerView.Adapter<SearchTransactionAdapter.ViewHolder>() {
    val docID : ArrayList<String> = arrayListOf()
    var  mSelectType  : Number? = null
    var nestedTransList: ArrayList<Transaction> = arrayListOf()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nestedRecyclerView: RecyclerView
        var transDate: TextView
        var cellLayout: LinearLayout
        var day: TextView
        var context: Context
        lateinit var nestedTransAdapter: SearchNestedTransAdapter
        init {
            nestedRecyclerView = view.findViewById(R.id.nestedRecyclerView)
            cellLayout = view.findViewById(R.id.cellLayout)
            transDate = view.findViewById(R.id.transDate)
            day = view.findViewById(R.id.day)
            context = view.context
            nestedRecyclerView.layoutManager = LinearLayoutManager(context)
            nestedRecyclerView.setHasFixedSize(true)
            nestedRecyclerView.isNestedScrollingEnabled = false
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_trans_cell, parent, false)

        return ViewHolder(view)
    }
    fun filterList(filterList: ArrayList<NestedTransaction>) {
        transList = filterList
        notifyDataSetChanged()
    }
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        val yesterday =
            LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        val tomorrow =
            LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        var transDate = transList[position].date
        transDate = when (transDate) {
            today -> {
                holder.context.getString(R.string.today) + ", " + SimpleDateFormat("dd MMMM yyyy").format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            }
            yesterday -> {
                holder.context.getString(R.string.yesterday) + ", " + SimpleDateFormat("dd MMMM yyyy").format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            }
            tomorrow -> {
                holder.context.getString(R.string.tomorrow) + ", " + SimpleDateFormat("dd MMMM yyyy").format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            }
            else -> {
                SimpleDateFormat("EEEE, dd MMMM yyyy", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            }
        }

        holder.transDate.text = transDate
//        holder.day.text =
//            SimpleDateFormat("MMM yyyy").format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
        holder.nestedTransAdapter = SearchNestedTransAdapter(nestedList = transList[position], searchActivity = searchActivity, searchTransactionAdapter = this)
        holder.nestedRecyclerView.adapter = holder.nestedTransAdapter
    }
    override fun getItemCount(): Int {
        return transList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        transList.clear()
        notifyDataSetChanged()
    }
}