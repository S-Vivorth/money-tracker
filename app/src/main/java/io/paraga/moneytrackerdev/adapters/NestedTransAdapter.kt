package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.viewmodels.exchangeRate
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import io.paraga.moneytrackerdev.views.transaction.TransactionFrag
import org.w3c.dom.Text
import java.util.*


class NestedTransAdapter(
    private val nestedList: NestedTransaction,
    private val currency: String) :
    RecyclerView.Adapter<NestedTransAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var transAmount: TextView
        var categoryIcon: ImageView
        var categoryName: TextView
        var context: Context
        var walletName: TextView
        var walletImg: ImageView
        var exchangeRateText: TextView
        var currentExchangeRate: String
        var remarkText: TextView
        init {
            transAmount = view.findViewById(R.id.text_trans_amount)
            categoryIcon = view.findViewById(R.id.img_category)
            categoryName = view.findViewById(R.id.text_category)
            walletName = view.findViewById(R.id.text_wallet_name)
            walletImg = view.findViewById(R.id.img_wallet)
            exchangeRateText = view.findViewById(R.id.text_exchange)
            remarkText = view.findViewById(R.id.text_remark)
            context = view.context
            try {
                currentExchangeRate = context.getString(
                    R.string.blank,
                    Extension.getCurrencyObj(
                        user?.defaultCurrency.toString()
                    ).code
                            + " "
                            + exchangeRate.results?.get(
                        Extension.getCurrencyObj(
                            user?.defaultCurrency.toString()
                        ).code
                    )
                )
            }
            catch (exc: Exception) {
                currentExchangeRate = "0.0"
                Log.d("exc", exc.toString())
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nested_trans_cell, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (nestedList.nestedTransList[position].type == Enums.TransTypes.INCOME.value) {
            holder.transAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryGreen
                )
            )
        } else {
            holder.transAmount.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    R.color.primaryRed
                )
            )
        }

        if (currency == nestedList.nestedTransList[position].currency.toString()) {
            holder.exchangeRateText.visibility = View.GONE
        }
        val convertedAmount = String.format(
            "%.2f",
            Extension.convertCurrency(
                nestedList.nestedTransList[position].currency.toString(),
                currency,
                nestedList.nestedTransList[position].amount!!.toDouble(),
                holder.context
            )
        ).replace(",",".")
        holder.transAmount.text = holder.context.getString(
            R.string.blank,
            Extension.getCurrencySymbol(
                currency
            )
            + Extension.toBigDecimal(convertedAmount)
        )
        holder.exchangeRateText.text = holder.context.getString(
            R.string.blank,
            nestedList.nestedTransList[position].currency.toString()
                    + Extension.toBigDecimal(nestedList.nestedTransList[position].amount?.toDouble().toString())
        )

        val remark = nestedList.nestedTransList[position].remark
        holder.categoryName.text = nestedList.nestedTransList[position].category.title
        holder.remarkText.text = remark
        holder.remarkText.maxLines = 3
        walletList.value?.forEach {
            if (it.walletId == nestedList.nestedTransList[position].walletID.toString()) {
                holder.walletName.text = it.wallet.name
                holder.walletImg.setBackgroundResource(
                    Extension.getResouceId(holder.context, it.wallet.symbol)
                )
                holder.walletImg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.wallet.color.toString()))
            }
        }

        holder.categoryIcon.setImageResource(
            Extension.getResouceId(
                holder.context,
                nestedList.nestedTransList[position].category.image.toString()
            )
        )
        holder.itemView.setOnClickListener {
                val selectedTrans = nestedList.nestedTransList[position]
                val milliseconds =
                    selectedTrans.date!!.seconds * 1000 + selectedTrans.date!!.nanoseconds / 1000000
                val intent = Intent(holder.context, AddTrans::class.java)
                if (currency == nestedList.nestedTransList[position].currency.toString()) {
                    intent.putExtra(Enums.Extras.AMOUNT.value, convertedAmount.toDouble())
                } else {
                    intent.putExtra(Enums.Extras.AMOUNT.value, selectedTrans.amount)
                }
                intent.putExtra(Enums.Extras.CURRENCY.value, selectedTrans.currency)
                intent.putExtra(Enums.Extras.MILLISECOND.value, milliseconds)

                intent.putExtra(Enums.Extras.REMARK.value, selectedTrans.remark)
                intent.putExtra(Enums.Extras.TYPE.value, selectedTrans.type)
                intent.putExtra(Enums.Extras.USER_ID.value, selectedTrans.userid)
                intent.putExtra(Enums.Extras.WALLET_ID.value, selectedTrans.walletID)
                intent.putExtra(Enums.Extras.DOC_ID.value, selectedTrans.documentId)
                intent.putExtra(Enums.Extras.CATEGORY_COLOR.value, selectedTrans.category.color)
                intent.putExtra(Enums.Extras.CATEGORY_IMG.value, selectedTrans.category.image)
                intent.putExtra(Enums.Extras.SELECTED_COUNT.value, selectedTrans.category.selectedCount)
                intent.putExtra(Enums.Extras.CATEGORY_TITLE.value, selectedTrans.category.title)
                intent.putExtra(Enums.Extras.IS_EDIT_TRANS.value, true)
                holder.context.startActivity(intent)
            }



    }

    override fun getItemCount(): Int {
        return nestedList.nestedTransList.size
    }
}