package io.paraga.moneytrackerdev.views.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.transaction.AddTrans

class CalendarDetailAdapter :
    RecyclerView.Adapter<CalendarDetailAdapter.ViewHolder>() {
    private var nestedList : ArrayList<Transaction> = arrayListOf()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         var textTitle: TextView
         var textName: TextView
         var textRemark: TextView
         var textValue: TextView
         var context: Context
         var textRiels : TextView
         var imgCategory : ImageView
         val detailItem: ConstraintLayout
         var imgWallet : ImageView
//         val layoutTextName: RelativeLayout
        init {
            textName = view.findViewById(R.id.text_wallet_name)
//            layoutTextName = view.findViewById(R.id.layout_text_name)
            textTitle = view.findViewById(R.id.text_category)
            textRemark = view.findViewById(R.id.text_remark)
            textValue = view.findViewById(R.id.text_trans_amount)
            imgCategory = view.findViewById(R.id.img_category)
            imgWallet = view.findViewById(R.id.img_wallet)
            context = view.context
            textRiels = view.findViewById(R.id.text_exchange)
            detailItem = view.findViewById(R.id.detailItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_detail_item, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nightModeFlags: Int = holder.context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
//        val backgroundGradient: GradientDrawable = holder.layoutTextName.background as GradientDrawable
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            holder.textTitle.setTextColor(holder.context.getColor(R.color.darkPrimaryTextColor))
            holder.textName.setTextColor(holder.context.getColor(R.color.darkSecondaryGreyTextColor))
            holder.textRemark.setTextColor(holder.context.getColor(R.color.darkSecondaryGreyTextColor))
            holder.textRiels.setTextColor(holder.context.getColor(R.color.darkSecondaryGreyTextColor))
//            backgroundGradient.setStroke(2, ContextCompat.getColor(holder.context, R.color.darkSeparatorColor))
        }
        else {
            holder.textTitle.setTextColor(holder.context.getColor(R.color.lightTitleTextColor))
            holder.textRiels.setTextColor(holder.context.getColor(R.color.lightSecondaryGreyTextColor))
//            backgroundGradient.setStroke(2, ContextCompat.getColor(holder.context, R.color.lightSeparatorColor))
        }


        val allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet()
        if (nestedList[position].type == Constants.INCOME) {
            holder.textValue.setTextColor(holder.context.getColor(R.color.primaryGreen))
        } else {
            holder.textValue.setTextColor(holder.context.getColor(R.color.primaryRed))
        }
        val remark = nestedList[position].remark
        holder.textRemark.text = remark
        holder.textTitle.text = nestedList[position].category.title
        holder.imgCategory.setImageResource(
            Extension.getResouceId(
                holder.context,
                nestedList[position].category.image.toString()
            )
        )

        val currency: String = if (selectedWallet ==  allWallet) {
        user?.defaultCurrency.toString()
        } else {
        (selectedWallet.currency.toString())
         }
            if (currency == nestedList[position].currency.toString()) {
                holder.textRiels.visibility = View.GONE
            }
            val convertedAmount = String.format(
                "%.2f",
                Extension.convertCurrency(
                    nestedList[position].currency.toString(),
                    currency,
                    nestedList[position].amount!!.toDouble(),
                    holder.context
                )
            ).replace(",",".")

            holder.textValue.text = holder.context.getString(
                R.string.blank,
                Extension.getCurrencySymbol(
                    selectedWallet.currency.toString()
                )
                        + Extension.toBigDecimal(convertedAmount)
            )
            holder.textRiels.text = holder.context.getString(
                R.string.blank,Extension.toBigDecimal(nestedList[position].amount?.toDouble().toString()) + nestedList[position].currency.toString()
            )

            holder.detailItem.setOnClickListener {
                val selectedTrans = nestedList[position]
                val milliseconds =
                    selectedTrans.date!!.seconds * 1000 + selectedTrans.date!!.nanoseconds / 1000000
                val intent = Intent(holder.context, AddTrans::class.java)
                if (currency == nestedList[position].currency.toString()) {
                    intent.putExtra("amount", convertedAmount.toDouble())
                } else {
                    intent.putExtra("amount", selectedTrans.amount)
                }
                intent.putExtra("currency", selectedTrans.currency)
                intent.putExtra("milliseconds", milliseconds)

                intent.putExtra("remark", selectedTrans.remark)
                intent.putExtra("type", selectedTrans.type)
                intent.putExtra("userid", selectedTrans.userid)
                intent.putExtra("walletID", selectedTrans.walletID)
                intent.putExtra("documentId", selectedTrans.documentId)
                intent.putExtra("categoryColor", selectedTrans.category.color)
                intent.putExtra("categoryImage", selectedTrans.category.image)
                intent.putExtra("selectedCount", selectedTrans.category.selectedCount)
                intent.putExtra("categoryTitle", selectedTrans.category.title)
                intent.putExtra("isEditTrans", true)
                holder.context.startActivity(intent)
            }
        walletList.value?.forEach {
            if (it.walletId == nestedList[position].walletID.toString()) {
                holder.textName.text = it.wallet.name
                holder.imgWallet.setBackgroundResource(
                    Extension.getResouceId(holder.context, it.wallet.symbol)
                )
                holder.imgWallet.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.wallet.color.toString()))
            }
        }
    }
    override fun getItemCount(): Int {
        return nestedList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        nestedList.clear()
        notifyDataSetChanged()
    }

    fun addItems(list: ArrayList<Transaction>) {
        if (list.size == 0) return
        for (item in list) {
            val index = nestedList.indexOf(item)
            if (index >= 0) {
                if (nestedList[index].toString() != item.toString()) {
                    nestedList[index] = item
                    notifyItemChanged(index,1)
                }
            } else {
                nestedList.add(item)
                notifyItemInserted(nestedList.size-1)
            }
        }
    }
}