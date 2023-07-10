package io.paraga.moneytrackerdev.adapters

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Budget
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.networks.budgetList
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.budget.BudgetDetail
import io.paraga.moneytrackerdev.views.budget.BudgetFrag
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import io.paraga.moneytrackerdev.views.wallet.EditWallet
import org.w3c.dom.Text
import java.io.Serializable
import java.lang.Math.abs

var budgetTransList: ArrayList<Transaction> = ArrayList()

class BudgetAdapter(
    private val budgetFrag: BudgetFrag,
    private val gridLayoutManager: GridLayoutManager,
    private val nestedTransMapByCategoryAndWallet: HashMap<String, HashMap<String, Any>>
) :
    RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {


    private val value = TypedValue()
    init {

    }

    class ViewHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
        lateinit var budgetName: TextView
        lateinit var budgetWalletName: TextView
        lateinit var budgetSpendingAmount: TextView
        lateinit var budgetAmount: TextView
        lateinit var budgetAmountLeft: TextView
        lateinit var circularBudgetProgressBar: ProgressBar
        lateinit var budgetIcon: ImageView
        lateinit var addTransBtn: CardView
        lateinit var horizontalBudgetName: TextView
        lateinit var horizontalBudgetWalletName: TextView
        lateinit var horizontalBudgetSpendingAmount: TextView
        lateinit var horizontalBudgetAmountLeft: TextView
        lateinit var horizontalBudgetProgressBar: ProgressBar
        lateinit var horizontalBudgetIcon: ImageView
        lateinit var horizontalAddTransBtn: CardView
        lateinit var context: Context

        init {
            if (viewType == Enums.SpanCount.ONE.value) {
                horizontalBudgetName = view.findViewById(R.id.horizontalBudgetName)
                horizontalBudgetWalletName = view.findViewById(R.id.horizontalWalletName)
                horizontalBudgetSpendingAmount =
                    view.findViewById(R.id.horizontalBudgetAmountSpending)
                horizontalBudgetAmountLeft = view.findViewById(R.id.horizontalBudgetAmountLeft)
                horizontalBudgetProgressBar = view.findViewById(R.id.horizontalBudgetProgress)
                horizontalBudgetIcon = view.findViewById(R.id.horizontalCategoryIcon)
                horizontalAddTransBtn = view.findViewById(R.id.horizontalAddTransBtn)
            } else {
                budgetName = view.findViewById(R.id.budgetName)
                budgetWalletName = view.findViewById(R.id.walletName)
                budgetSpendingAmount = view.findViewById(R.id.budgetSpendingAmount)
                budgetAmount = view.findViewById(R.id.budgetAmount)
                budgetAmountLeft = view.findViewById(R.id.budgetAmountLeft)
                circularBudgetProgressBar = view.findViewById(R.id.circularBudgetProgress)
                budgetIcon = view.findViewById(R.id.categoryIcon)
                addTransBtn = view.findViewById(R.id.addTransBtn)
            }
            context = view.context
        }
    }

    override fun getItemViewType(position: Int): Int {
        val spanCount = gridLayoutManager.spanCount
        if (spanCount == Enums.SpanCount.ONE.value) {
            return Enums.SpanCount.ONE.value
        } else {
            return Enums.SpanCount.TWO.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetAdapter.ViewHolder {
        val view: View
        if (viewType == Enums.SpanCount.ONE.value) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.budget_horizontal_cell, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.budget_grid_cell, parent, false)
        }
        return ViewHolder(view, viewType)
    }

    override fun getItemCount(): Int {
        if (budgetList.value?.size == 0) {
            budgetFrag.binding.noDataLayout.visibility = View.VISIBLE
        }
        else {
            budgetFrag.binding.noDataLayout.visibility = View.GONE
        }
        return budgetList.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: BudgetAdapter.ViewHolder, position: Int) {

        val budgetSpending: Double = (nestedTransMapByCategoryAndWallet[
                budgetList.value?.get(position)?.category?.title + budgetList.value?.get(position)?.wallet.toString()
        ]?.get(Enums.General.TOTAL_BUDGET_AMOUNT.value) ?: 0.0) as Double

        val budgetSpendingPercentage =
            (budgetSpending / budgetList.value!![position].amount!!.toInt()) * 100
        if (gridLayoutManager.spanCount == Enums.SpanCount.ONE.value) {

            holder.horizontalBudgetName.text = budgetList.value!![position].category.title
            var wallet = Wallet()
            var budgetCurrency: String = ""
            if (budgetList.value!![position].wallet.toString().isEmpty()) {
                wallet.name = holder.context.getString(R.string.all_wallets)
                budgetCurrency = user?.defaultCurrency.toString()
            } else {
                walletList.value?.forEach {
                    if (it.walletId == budgetList.value!![position].wallet) {
                        wallet = it.wallet
                        budgetCurrency = it.wallet.currency.toString()
                        return@forEach
                    }
                }
            }

            holder.horizontalBudgetWalletName.text = wallet.name
            holder.horizontalBudgetSpendingAmount.text = holder.context.getString(
                R.string.blank,
                (Extension.getCurrencySymbol(budgetCurrency) +
                        Extension.toBigDecimal(
                            String.format("%.2f", budgetSpending).replace(",", ".")
                        ) +
                        holder.context.getString(R.string.of)
                        + Extension.toBigDecimal(
                    String.format("%.2f", budgetList.value!![position].amount).replace(",", ".")
                ))
            )
            val layerDrawable: LayerDrawable =
                holder.horizontalBudgetProgressBar.progressDrawable as LayerDrawable
            val progressLayer: Drawable =
                layerDrawable.findDrawableByLayerId(R.id.progress) as ScaleDrawable
            setColorProgressBar(
                holder.horizontalBudgetProgressBar,
                holder.horizontalBudgetAmountLeft,
                Extension.getCurrencySymbol(budgetCurrency),
                budgetSpending.toInt(),
                budgetList.value!![position].amount!!.toInt(),
                holder.context,
                progressLayer
            )
            holder.horizontalBudgetProgressBar.apply {
                progress = budgetSpendingPercentage.toInt()
                max = 100 * 100
            }
            startProgressAnimation(holder.horizontalBudgetProgressBar)
            holder.horizontalBudgetIcon.setImageResource(
                Extension.getResouceId(
                    holder.context,
                    budgetList.value!![position].category.image
                )
            )
            holder.horizontalAddTransBtn.setOnClickListener {
                goToAddTrans(holder,
                    budgetList.value!![position].category,
                    budgetList.value!![position].wallet.toString())
            }
        } else {
            holder.budgetName.text = budgetList.value!![position].category.title
            var wallet = Wallet()
            var budgetCurrency: String = ""

            if (budgetList.value!![position].wallet.toString().isEmpty()) {
                wallet.name = holder.context.getString(R.string.all_wallets)
                budgetCurrency = user?.defaultCurrency.toString()
            } else {
                walletList.value?.forEach {
                    if (it.walletId == budgetList.value!![position].wallet) {
                        wallet = it.wallet
                        budgetCurrency = it.wallet.currency.toString()
                        return@forEach
                    }
                }
            }

            holder.budgetSpendingAmount.text = holder.context.getString(
                R.string.blank,
                Extension.getCurrencySymbol(budgetCurrency) +
                        Extension.toBigDecimal(
                            String.format("%.2f", budgetSpending).replace(",", ".")
                        )
            )
            holder.budgetWalletName.text = wallet.name
            holder.budgetAmount.text = Extension.toBigDecimal(
                String.format("%.2f", budgetList.value!![position].amount).replace(",", ".")
            )
            val layerDrawable: LayerDrawable =
                holder.circularBudgetProgressBar.progressDrawable as LayerDrawable
            val progressLayer: Drawable = layerDrawable.findDrawableByLayerId(R.id.progress)

            setColorProgressBar(
                holder.circularBudgetProgressBar,
                holder.budgetAmountLeft,
                Extension.getCurrencySymbol(budgetCurrency),
                budgetSpending.toInt(),
                budgetList.value!![position].amount!!.toInt(),
                holder.context,
                progressLayer
            )
            holder.circularBudgetProgressBar.apply {
                progress = budgetSpendingPercentage.toInt()
                max = 100 * 100
            }

            startProgressAnimation(holder.circularBudgetProgressBar)

            holder.budgetIcon.setImageResource(
                Extension.getResouceId(
                    holder.context,
                    budgetList.value!![position].category.image
                )
            )


            holder.addTransBtn.setOnClickListener {
                goToAddTrans(holder,
                    budgetList.value!![position].category,
                    budgetList.value!![position].wallet.toString())
            }
        }
        if (isProUser.value == false && budgetList.value!!.size > 2) {
            if (Preferences().getInstance().getChosenTwoBudgetIds(holder.context)?.contains(
                    budgetList.value?.get(position)?.documentId.toString()) == false) {
                (holder.itemView as CardView).alpha = 0.6F
                holder.itemView.setOnClickListener {
                    val premiumSubFrag = PremiumSubFrag()
                    premiumSubFrag.show(budgetFrag.mainActivity.supportFragmentManager, "")
                }
            }
            else {
                holder.itemView.setOnClickListener {
                    val transList = nestedTransMapByCategoryAndWallet[
                            budgetList.value!![position].category.title + budgetList.value?.get(position)?.wallet.toString()
                    ]!![Enums.General.BUDGET_TRANS_LIST.value] as ArrayList<Transaction>
                    goToBudgetDetail(holder, transList, budgetList.value!![position])
                }
            }
        }
        else {
            holder.itemView.setOnClickListener {
                val transList = nestedTransMapByCategoryAndWallet[
                        budgetList.value!![position].category.title + budgetList.value?.get(position)?.wallet.toString()
                ]!![Enums.General.BUDGET_TRANS_LIST.value] as ArrayList<Transaction>
                goToBudgetDetail(holder, transList, budgetList.value!![position])
            }
        }

    }

    private fun goToBudgetDetail(
        holder: ViewHolder,
        transList: ArrayList<Transaction>,
        budget: Budget
    ) {
        budgetTransList = transList
        val intent = Intent(holder.context, BudgetDetail::class.java)
        val bundle = Bundle()
        bundle.putSerializable("budget", budget as Serializable)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Enums.Extras.CURRENT_MONTH_YEAR.value, budgetFrag.mainActivity.binding.layoutHeader.textSelectDate.text)
        holder.context.startActivity(intent)
    }

    private fun goToAddTrans(holder: ViewHolder,
        category: Category,
        walletId: String) {
        val intent: Intent = Intent(holder.context, AddTrans::class.java)
        val bundle = Bundle()
        bundle.putSerializable("category", category)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Enums.Extras.WALLET_ID.value, walletId)
        intent.putExtra(Enums.Extras.IS_FROM_BUDGET.value, true)
        holder.context.startActivity(intent)
    }
    fun setColorProgressBar(
        progressBar: ProgressBar,
        amountLeftOrOverText: TextView,
        currencySymbol: String,
        progress: Int,
        max: Int,
        context: Context,
        progressLayer: Drawable
    ) {


        if ((progress / max.toDouble()) > 1) {
            val overSpendingAmount = (progress - max)
            Log.d("overSpendingg", overSpendingAmount.toString())
            amountLeftOrOverText.text = context.getString(
                R.string.blank,
                "-"
                        + currencySymbol
                        + Extension.toBigDecimal(
                    overSpendingAmount.toString()
                ) + " "
                        + context.getString(R.string.over_spending_budget)
            )
            amountLeftOrOverText.setTextColor(ContextCompat.getColor(context, R.color.red))
            progressLayer.setColorFilter(
                ContextCompat.getColor(context, R.color.red),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if ((progress / max.toDouble()) >= 0.8 && (progress / max) <= 1) {
            val amountLeft = max - progress
            amountLeftOrOverText.text = context.getString(
                R.string.blank,
                currencySymbol
                        + Extension.toBigDecimal(
                    amountLeft.toString()
                )
                        + context.getString(R.string.left)
            )
            progressLayer.setColorFilter(
                ContextCompat.getColor(context, R.color.warningBudgetProgress),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            val amountLeft = max - progress
            amountLeftOrOverText.text = context.getString(
                R.string.blank,
                currencySymbol
                        + Extension.toBigDecimal(
                    amountLeft.toString()
                )
                        + context.getString(R.string.left)
            )
            progressLayer.setColorFilter(
                ContextCompat.getColor(context, R.color.normalBudgetProgress),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun startProgressAnimation(progressBar: ProgressBar) {
        ObjectAnimator.ofInt(
            progressBar, "progress", progressBar.progress,
            progressBar.progress * 100
        )
            .setDuration(2000)
            .start()
    }
}
