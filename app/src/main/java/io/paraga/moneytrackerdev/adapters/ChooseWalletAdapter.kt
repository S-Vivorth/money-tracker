package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.budget.CreateBudget
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.selectedWalletId
import io.paraga.moneytrackerdev.views.transaction.ChooseWalletFrag



class ChooseWalletAdapter(private val chooseWalletFrag: ChooseWalletFrag) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var walletIcon: ImageView
    lateinit var walletName: TextView
    private lateinit var walletCell: RelativeLayout
    lateinit var context: Context
    private val addTrans =   chooseWalletFrag.addTrans
    private val mainActivity = chooseWalletFrag.mainActivity
    private val importDetailActivity = chooseWalletFrag.importDetailBinding
    private lateinit var walletTransList: List<WalletTrans>
    val value = TypedValue()
    init {
        if (isProUser.value == false && walletList.value!!.size > 2) {
            walletTransList = walletList.value!!.filter { Preferences().getInstance().getChosenTwoWalletIds(chooseWalletFrag.requireContext())?.contains(it.walletId) == true}
        }
        else {
            walletTransList = walletList.value!!
        }
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.choose_wallet_cell, parent, false)
        walletIcon = view.findViewById(R.id.walletIcon)
        walletName = view.findViewById(R.id.walletName)
        context = parent.context
        walletCell = view.findViewById(R.id.walletCell)
        return ViewHolder(view)
    }


    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (chooseWalletFrag.isFromMainActivity) {

            if (position == 0) {

                walletIcon.setBackgroundResource(
                    Extension.getResouceId(context, Enums.ResourceName.GLOBE.value)
                )
                context.theme.resolveAttribute(R.attr.primaryText, value, true)
                walletIcon.backgroundTintList = ColorStateList.valueOf(value.data)
                walletName.text = context.getString(
                    R.string.all_wallet
                )
                if (selectedWalletId == ""){
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true)

                    walletCell.setBackgroundColor(value.data)
                }
            }
            else{
                if (selectedWallet == walletTransList[position-1].wallet) {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true)
                    walletCell.setBackgroundColor(value.data)

                }

                walletIcon.setBackgroundResource(
                    Extension.getResouceId(context, walletTransList[position-1].wallet.symbol)
                )
                walletIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(walletTransList[position-1].wallet.color))
                walletName.text = walletTransList[position-1].wallet.name
            }

        }
        else if (chooseWalletFrag.isFromBudget) {
            if (position == 0) {
                walletIcon.setBackgroundResource(
                    Extension.getResouceId(context, Enums.ResourceName.GLOBE.value)
                )
                context.theme.resolveAttribute(R.attr.primaryText, value, true)
                walletIcon.backgroundTintList = ColorStateList.valueOf(value.data)
                walletName.text = context.getString(
                    R.string.all_wallet
                )
                if ((chooseWalletFrag.context as CreateBudget).selectedWalletId == "") {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true)
                    walletCell.setBackgroundColor(value.data)
                }

            }
            else {
                if ((chooseWalletFrag.context as CreateBudget).selectedWallet == walletTransList[position-1].wallet){
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true)
                    walletCell.setBackgroundColor(value.data)
                }
                walletIcon.setBackgroundResource(
                    Extension.getResouceId(context, walletTransList[position-1].wallet.symbol)
                )
                walletIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(walletTransList[position-1].wallet.color))
                walletName.text = walletTransList[position-1].wallet.name

            }
        }
        else{
            if (addTrans.selectedWallet == walletTransList[position].wallet){
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true)
                walletCell.setBackgroundColor(value.data)
            }
            walletIcon.setBackgroundResource(
                Extension.getResouceId(context, walletTransList[position].wallet.symbol)
            )
            walletIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(walletTransList[position].wallet.color))
            walletName.text = walletTransList[position].wallet.name
        }


        holder.itemView.setOnClickListener {
            if (chooseWalletFrag.isFromMainActivity) {
                if (position == 0) {
                    selectedWallet = mainActivity.allWallet
                    mainActivity.changeSelectedWalletView(
                        wallet = mainActivity.allWallet
                    )
                    selectedWalletId = ""
                }
                else {
                    selectedWallet = walletTransList[position-1].wallet
                    selectedWalletId = walletTransList[position-1].walletId
                    mainActivity.changeSelectedWalletView(
                        wallet = walletTransList[position-1].wallet
                    )
                }
                Preferences().getInstance().setSelectedWalletId(mainActivity, selectedWalletId)
                mainActivity.getNestedTransList()
            } else if (chooseWalletFrag.isFromImportActivity) {
                // import screen
                importDetailActivity.selectedWallet = walletTransList[position].wallet
                importDetailActivity.selectedWalletId = walletTransList[position].walletId
                importDetailActivity.changeSelectedWalletView()
            }
            else if (chooseWalletFrag.isFromBudget) {
                if (position == 0) {
                    (chooseWalletFrag.context as CreateBudget).apply {
                        selectedWallet = mainActivity.allWallet
                        selectedWallet.currency = user?.defaultCurrency.toString()
                        binding.walletName.text = selectedWallet.name
                        selectedWalletId = ""
                        binding.amountBudget.text = getString(
                            R.string.blank,
                            selectedWallet.currency
                                    + " "
                                    + budget.amount
                        )
                    }
                }
                else {
                    (chooseWalletFrag.context as CreateBudget).apply {
                        selectedWallet = walletTransList[position-1].wallet
                        binding.walletName.text = walletTransList[position-1].wallet.name
                        binding.amountBudget.text = getString(
                            R.string.blank,
                            selectedWallet.currency
                                    + " "
                                    + budget.amount
                        )
                        selectedWalletId = walletTransList[position-1].walletId
                    }
                }

            }
            else{

                // add trans screen
                if (addTrans.selectedWallet != walletTransList[position].wallet) {
                    if (!addTrans.isEditTrans) {
                        addTrans.currentCurrencyName = walletTransList[position].wallet.currency.toString()
                        addTrans.binding.currencySymbol.text = Extension.getCurrencySymbol(
                            addTrans.currentCurrencyName
                        )
                    }
                    if (walletTransList[position].wallet.currency == addTrans.currentCurrencyName) {
                        addTrans.binding.exchangeRate.visibility = View.GONE
                    }
                    else {
                        val convertedAmount = addTrans.getString(
                            R.string.blank,
                            walletTransList[position].wallet.currency!!
                                    + " "
                                    + Extension.toBigDecimal(
                                Extension.convertCurrency(
                                    addTrans.currentCurrencyName,
                                    walletTransList[position].wallet.currency!!,
                                    addTrans.binding.amount.text.toString().replace(",", "")
                                        .toDouble(),
                                    context
                                ).toString()
                            )
                        )
                        addTrans.binding.exchangeRate.text = convertedAmount

                        addTrans.binding.exchangeRate.visibility = View.VISIBLE

                    }

                    addTrans.selectedWallet = walletTransList[position].wallet
                    addTrans.selectedWalletId = walletTransList[position].walletId
                    addTrans.changeSelectedWalletView()
                    addTrans.enableButton()
                }

            }

            Handler(Looper.myLooper()!!).postDelayed({
                chooseWalletFrag.dismiss()
            }, 500)
        }
    }

    override fun getItemCount(): Int {
        return if (chooseWalletFrag.isFromMainActivity || chooseWalletFrag.isFromBudget) {
            walletTransList.size + 1
        } else {
            walletTransList.size
        }
    }
}