package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.networks.expenseTransList
import io.paraga.moneytrackerdev.networks.incomeTransList
import io.paraga.moneytrackerdev.networks.nestedTransList
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.transaction.TransactionFrag
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(
    var transList: ArrayList<NestedTransaction>,
    val transactionFrag: TransactionFrag? = null,
    val currency: String
) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    val yesterday =
        LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    val tomorrow =
        LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

    class ViewHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
         lateinit var nestedRecyclerView: RecyclerView
         lateinit var transDate: TextView
         lateinit var cellLayout: LinearLayout
//         var dayInNumber: TextView
         lateinit var day: TextView
         lateinit var expandableLayout: RelativeLayout
         lateinit var totalAmount: TextView
         lateinit var context: Context
         lateinit var dropdownBtn: ImageView
        lateinit var popupWindow: PopupWindow
        lateinit var allTransBtn: RelativeLayout
        lateinit var expenseBtn: RelativeLayout
        lateinit var incomeBtn: RelativeLayout
        lateinit var nestedTransAdapter: NestedTransAdapter
        lateinit var transText: TextView
        lateinit var transDropdown: LinearLayout
        init {
            context = view.context
            if (viewType == Enums.ViewType.TRANS_DROP_DOWN.value) {
                transDropdown = view.findViewById(R.id.transDropdown)
                transText = view.findViewById(R.id.transText)
                setUpPopUpWindow(view.context)
            }
            else {
                nestedRecyclerView = view.findViewById(R.id.nestedRecyclerView)
                cellLayout = view.findViewById(R.id.cellLayout)
                transDate = view.findViewById(R.id.transDate)
                expandableLayout = view.findViewById(R.id.expandable_layout)
//            dayInNumber = view.findViewById(R.id.dayInNumber)
                day = view.findViewById(R.id.day)
                totalAmount = view.findViewById(R.id.totalAmount)
                dropdownBtn = view.findViewById(R.id.dropdownBtn)
                nestedRecyclerView.layoutManager = LinearLayoutManager(context)
                nestedRecyclerView.setHasFixedSize(true)
                nestedRecyclerView.isNestedScrollingEnabled = false
            }

        }
        fun setUpPopUpWindow(context: Context) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.trans_filter_dropdown, null)
            allTransBtn = view.findViewById(R.id.allTransLayout)
            expenseBtn = view.findViewById(R.id.expenseLayout)
            incomeBtn = view.findViewById(R.id.incomeLayout)
            popupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View
        if (viewType == Enums.ViewType.TRANS_DROP_DOWN.value) {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.trans_type_drop_down_cell, parent, false)
        }
        else {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.main_trans_cell, parent, false)
        }

        return ViewHolder(view, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && transactionFrag != null) {
            return Enums.ViewType.TRANS_DROP_DOWN.value
        }
        else {
            return Enums.ViewType.TRANS_CARD.value
        }
    }
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        holder.transDate.text = transList[position].nestedTransList[0].amount.toString()
        if (position == 0 && transactionFrag != null) {
            holder.transText.text = transactionFrag.binding.transText.text
            initDropDownListeners(holder)
        }
        else {
            var transDate = transList[position].date
            if (transDate == today) {
                transDate = holder.context.getString(R.string.today)
                holder.day.text =
                    SimpleDateFormat("dd MMM, yyyy", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            } else if (transDate == yesterday) {
                transDate = holder.context.getString(R.string.yesterday)
                holder.day.text =
                    SimpleDateFormat("dd MMM, yyyy", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            } else if (transDate == tomorrow) {
                transDate = holder.context.getString(R.string.tomorrow)
                holder.day.text =
                    SimpleDateFormat("dd MMM, yyyy", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            } else {
                transDate =
                    SimpleDateFormat("dd MMM, yyyy", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
                holder.day.text =
                    SimpleDateFormat("EEEE", Extension.getLocale(holder.context)).format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)
            }

            holder.transDate.text = transDate
//        holder.dayInNumber.text =
//            SimpleDateFormat("dd").format(SimpleDateFormat("dd MMM yyyy").parse(transList[position].date)!!)

            if (transList[position].totalAmount == transList[position].totalAmount.toInt().toDouble()) {
                holder.totalAmount.text = holder.context.getString(
                    R.string.blank,
                    Extension.getCurrencySymbol(
                        currency
                    ) + Extension.toBigDecimal(transList[position].totalAmount.toInt().toString())
                )
            } else {
                holder.totalAmount.text = holder.context.getString(
                    R.string.blank,
                    Extension.getCurrencySymbol(
                        currency
                    )
                            + Extension.toBigDecimal(transList[position].totalAmount.toString())
                )
            }

            val isExpandable = transList[position].isExpanded

            if (isExpandable) {
                holder.expandableLayout.fadeVisibility(View.VISIBLE)
                holder.dropdownBtn.setImageResource(R.drawable.expand_less)

            } else {
                holder.dropdownBtn.setImageResource(R.drawable.arrow_down)
                holder.expandableLayout.fadeVisibility(View.GONE)
            }
            holder.nestedTransAdapter = NestedTransAdapter(transList[position], currency)
            holder.nestedRecyclerView.adapter = holder.nestedTransAdapter
            holder.cellLayout.setOnClickListener {
                transList[position].isExpanded = !transList[position].isExpanded
                holder.expandableLayout.fadeVisibility(View.VISIBLE)
                holder.dropdownBtn.setImageResource(R.drawable.expand_less)
                notifyItemChanged(position)
            }
            if (transactionFrag != null) {
                if (transList.size != 0) {
                    transactionFrag.binding.noDataLayout.visibility = View.GONE
                }
                else {
                    transactionFrag.binding.noDataLayout.visibility = View.VISIBLE
                }
            }

        }


    }


    override fun getItemCount(): Int {
        return transList.size
    }

    fun initDropDownListeners(holder: ViewHolder) {
        if (transactionFrag != null) {
            transactionFrag.mainActivity.theme.resolveAttribute(R.attr.bgColor, transactionFrag.mainActivity.value, true)
            holder.transDropdown.background.setColorFilter(transactionFrag.mainActivity.value.data, PorterDuff.Mode.SRC_ATOP)
            holder.allTransBtn.setOnClickListener {
                holder.popupWindow.dismiss()
                transactionFrag.allTransBtn.performClick()
            }

            holder.expenseBtn.setOnClickListener {
                holder.popupWindow.dismiss()
                transactionFrag.expenseBtn.performClick()
            }

            holder.incomeBtn.setOnClickListener {
                holder.popupWindow.dismiss()
                transactionFrag.incomeBtn.performClick()
            }

            holder.transDropdown.setOnClickListener {
                holder.popupWindow.showAsDropDown(it, Extension.dpToPx(holder.context, -54F), Extension.dpToPx(holder.context, 10F))
            }
        }




    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 1000) {
//        val transition: Transition = Slide(Gravity.BOTTOM)
//        transition.duration = duration
//        transition.addTarget(this)
//        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }


}