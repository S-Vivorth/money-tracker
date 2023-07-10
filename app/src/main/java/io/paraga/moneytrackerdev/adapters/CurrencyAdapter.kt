package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.networks.selectedWallet
import io.paraga.moneytrackerdev.views.currency.ChooseCurrencyFrag
import io.paraga.moneytrackerdev.views.statistics.fragment.StatisticsFrag
import io.paraga.moneytrackerdev.views.transaction.CalculatorFrag
import io.paraga.moneytrackerdev.views.wallet.CreateWallet
import io.paraga.moneytrackerdev.views.wallet.EditWallet
import org.json.JSONArray
import org.json.JSONObject

class CurrencyAdapter(
    private var editWallet: EditWallet? = EditWallet(),
    private var createWallet: CreateWallet? = CreateWallet(),
    private var mainActivity: MainActivity? = MainActivity(),
    private var calculatorFrag: CalculatorFrag? = CalculatorFrag(),
    val chooseCurrencyFrag: ChooseCurrencyFrag
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {


    var currencyJson = JSONObject(Constants.currencyList)
    var keys = currencyJson.names()
    var currencyList: ArrayList<String> = ArrayList()
    var itemsCopy: JSONObject = JSONObject()
    private var statisticsFrag: StatisticsFrag? = mainActivity?.let { StatisticsFrag() }
    lateinit var copyKeys: JSONArray
    init {
        itemsCopy = currencyJson
        copyKeys = itemsCopy.names() as JSONArray
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var currencyName: TextView
        lateinit var currencySymbol: TextView
        lateinit var flagIcon: ImageView
        lateinit var context: Context
        init {

            currencyName = view.findViewById(R.id.currencyName)
            currencySymbol = view.findViewById(R.id.currencySymbol)
            flagIcon = view.findViewById(R.id.flagIcon)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.currency_cell, parent, false)


        return ViewHolder(view = view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.currencyName.text =
            holder.context.getString(R.string.blank,
                (currencyJson.get(keys!!.getString(position)) as JSONObject)["code"] as String
            + " - " + (currencyJson.get(keys.getString(position)) as JSONObject)["name"] as String)
        holder.currencySymbol.text =
            (currencyJson.get(keys.getString(position)) as JSONObject)["symbol_native"] as String
        try {
            holder.flagIcon.setImageResource(
                Extension.getResouceId(
                    holder.context,
                    ((keys[position]) as String).lowercase(),
                    isReplace = false
                )
            )
        } catch (ex: Exception) {
            Log.d("exc", ex.toString())
        }

        holder.itemView.setOnClickListener {
            val selectedCurrency = holder.currencyName.text.toString().slice(0..2)
            if (chooseCurrencyFrag.context is EditWallet) {
                editWallet?.binding?.currency?.text = holder.currencyName.text

                editWallet?.balance = Extension.convertCurrency(editWallet?.currentCurrencyName!!.slice(0..2), selectedCurrency, editWallet?.balance!!.toDouble(), holder.context)
                editWallet?.binding?.balanceText?.text = editWallet!!.getString(
                    R.string.blank,
                    Extension.getCurrencySymbol(
                        selectedCurrency
                    ) + String.format("%.2f",editWallet?.balance).replace(",",".")
                )
//                val convertedCurrentWalletAmount = Extension.convertCurrency(editWallet?.currentWalletTrans?.wallet?.currency!!.slice(0..2), selectedCurrency, editWallet?.currentWalletTrans?.balance!!.toDouble())
//                if (editWallet?.balance?.toDouble()!! > convertedCurrentWalletAmount) {
//                    editWallet?.transAmount = editWallet?.balance?.toDouble()!! - convertedCurrentWalletAmount
//                }
//                else if (editWallet?.balance?.toDouble()!! < convertedCurrentWalletAmount) {
//                    editWallet?.transAmount = convertedCurrentWalletAmount - editWallet?.balance?.toDouble()!!
//                }
//                else {
//                    editWallet?.transAmount = 0
//                }
//                editWallet?.isBalanceChanged = true
                editWallet?.currentCurrencyName = holder.currencyName.text.toString()
                editWallet?.wallet?.currency = selectedCurrency
                chooseCurrencyFrag.dismiss()
            }
            else if (chooseCurrencyFrag.context is CreateWallet){
                createWallet?.binding?.currency?.text = holder.currencyName.text
                createWallet?.currentCurrencyName = holder.currencyName.text.toString()
                createWallet?.wallet?.currency = holder.currencyName.text.toString().slice(0..2)
                createWallet?.binding?.initialBalanceText?.text = createWallet!!.getString(
                    R.string.blank,
                    Extension.getCurrencySymbol(
                        selectedCurrency
                    ) + String.format("%.2f",createWallet?.transAmount?.toDouble())
                )
                chooseCurrencyFrag.dismiss()
            }
            else if (chooseCurrencyFrag.context is MainActivity){
                mainActivity?.binding?.mainCurrency?.text = selectedCurrency
//                Extension.changeCurrency(user?.defaultCurrency.toString(), selectedCurrency)
                user?.defaultCurrency = selectedCurrency
                if (selectedWallet == mainActivity?.allWallet) {
                    selectedWallet.currency = selectedCurrency
                }
                mainActivity?.allWallet?.currency = selectedCurrency


                //update main currency
                mainActivity?.firebaseManager?.updateMainCurrency(selectedCurrency,
                onSuccess = {
                    //current fragment
//                    if (mainActivity?.isTransaction == true) {
//                        (mainActivity?.fragManager?.findFragmentById(R.id.frameLayout) as TransactionFrag).transactionAdapter.notifyDataSetChanged()
//                    } else if (mainActivity?.isRefresh == true) {
//                        (mainActivity?.fragManager?.findFragmentById(R.id.frameLayout) as CalendarFrag).getRefreshCalendar()
//                        mainActivity?.getNestedTransList()
//                    } else if (mainActivity?.isStatic == true) {
//                        (mainActivity?.fragManager?.findFragmentById(R.id.frameLayout) as StatisticsFrag).getRefreshFragment()
//                        mainActivity?.getNestedTransList()
//                    } else {
//                    }

                },
                onFailure = {

                })
                if (mainActivity?.isStatic == true) {
                    statisticsFrag?.getRefreshFragment()
                }
                mainActivity?.calculateData()
                Toast(mainActivity).showCustomToast(holder.context.getString(R.string.main_currency_is_updated_successfully), mainActivity as Activity)
                mainActivity?.getNestedTransList()
                Handler(Looper.myLooper()!!).postDelayed({
                    chooseCurrencyFrag.dismiss()
                }, 500)

            }
            else {
                calculatorFrag?.binding?.selectedCurrencyName?.text = selectedCurrency
                calculatorFrag?.currentCurrencyName = selectedCurrency
                calculatorFrag?.addTrans?.currentCurrencyName = selectedCurrency
                calculatorFrag?.addTrans?.binding?.currencySymbol?.text =
                    Extension.getCurrencySymbol(
                        selectedCurrency
                    )
                val editor =  calculatorFrag?.context?.getSharedPreferences(
                    Enums.SharePref.RECENT_CURRENCY.value,
                    AppCompatActivity.MODE_PRIVATE
                )?.edit()
                val pref = calculatorFrag?.context?.getSharedPreferences(Enums.SharePref.RECENT_CURRENCY.value,
                    AppCompatActivity.MODE_PRIVATE
                )
                var currency = pref?.getString(Enums.SharePref.RECENT_CURRENCY.value, "")

                if (!ArrayList(currency?.split(",")!!).contains(selectedCurrency)) {
                    if ((ArrayList(currency.split(",")).size == 4)) {
                        currency += ",$selectedCurrency"
                        currency = currency.slice(4..currency.length-1)
                        editor?.putString(Enums.SharePref.RECENT_CURRENCY.value, currency)
                        editor?.apply()
                        calculatorFrag?.recentCurrencyList = ArrayList(currency.split(",").asReversed())
                        calculatorFrag?.recentCurrencyAdapter?.notifyDataSetChanged()
                    }
                    else {
                        currency += ",$selectedCurrency"
                        editor?.putString(Enums.SharePref.RECENT_CURRENCY.value, currency)
                        editor?.apply()
                        calculatorFrag?.recentCurrencyList = ArrayList(currency.split(",").asReversed())
                        calculatorFrag?.recentCurrencyAdapter?.notifyDataSetChanged()
                    }

                }
                chooseCurrencyFrag.dismiss()

            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return currencyJson.length()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        var text = text
        currencyJson = JSONObject()

        text = text.lowercase()
        for (index in 0..itemsCopy.length()-1) {
            if (copyKeys.get(index).toString().lowercase().contains(text)
                || (itemsCopy.get(copyKeys.get(index).toString()) as JSONObject)["name"].toString()
                    .lowercase().contains(text) ) {
                val key = copyKeys.get(index).toString()
                val value = itemsCopy.getString(key)
                currencyJson.apply {
                    put(key, JSONObject(value))
                }

            }
        }
        keys = currencyJson.names()
        notifyDataSetChanged()
    }
}