package io.paraga.moneytrackerdev.views.wallet

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import com.github.mikephil.charting.formatter.ValueFormatter
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.InactiveWalletAdapter
import io.paraga.moneytrackerdev.adapters.WalletAdapter
import io.paraga.moneytrackerdev.databinding.ActivityWalletBinding
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.viewmodels.wallet.WalletVM
import io.paraga.moneytrackerdev.views.isDarkTheme
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag

class Wallet : AppCompatActivity() {
    lateinit var binding: ActivityWalletBinding
    lateinit var walletAdapter: WalletAdapter
    lateinit var inactiveWalletAdapter: InactiveWalletAdapter
    val walletVM = WalletVM()
    private val value = TypedValue()

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentLocale = LocaleList.getDefault()[0]
        Log.d("currentLanguage", currentLocale.language.toString())

        walletList.observeForever {
            walletVM.totalBalance = 0.0
            walletVM.walletListConvertedBalance = ArrayList()
            walletVM.getWallet(
                onSuccess = {

                    initRecyclerView()
                    setUpWalletPieChart()
                    loadWalletPieChartData()
                    binding.totalBalance.text = getString(
                        R.string.blank,
                        Extension.getCurrencySymbol(
                            io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString()
                        ) + Extension.toBigDecimal(walletVM.totalBalance.toString())
                    )

                    binding.currencySymbol.text = Extension.getCurrencySymbol(
                        io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString()
                    )


                },
                onFailure = {

                },
                context = this
            )
        }


        binding.createWalletBtn.setOnClickListener {
            checkProUser()
        }



        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        binding.balanceBtn.setOnClickListener {
            setUpWalletPieChart()
            loadWalletPieChartData()
            binding.walletPieChart.setUsePercentValues(false)
            binding.percentageBtn.setCardBackgroundColor(Color.TRANSPARENT)
            theme.resolveAttribute(R.attr.selectedPercentageSwitchBgColor, value, true)
            binding.balanceBtn.setCardBackgroundColor(value.data)
            theme.resolveAttribute(R.attr.quaternaryBgColor, value, true)
            theme.resolveAttribute(R.attr.nextButtonTextColor, value, true)
            binding.currencySymbol.setTextColor(value.data)
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.percentageText.setTextColor(value.data)
        }

        binding.percentageBtn.setOnClickListener {
            setUpWalletPieChart()
            loadWalletPieChartData(false)
            binding.walletPieChart.setUsePercentValues(true)

            binding.balanceBtn.setCardBackgroundColor(Color.TRANSPARENT)
            theme.resolveAttribute(R.attr.selectedPercentageSwitchBgColor, value, true)
            binding.percentageBtn.setCardBackgroundColor(value.data)
            theme.resolveAttribute(R.attr.quaternaryBgColor, value, true)
            theme.resolveAttribute(R.attr.nextButtonTextColor, value, true)
            binding.percentageText.setTextColor(value.data)
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.currencySymbol.setTextColor(value.data)
        }

        val nightModeFlags: Int = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.incomeExpenseLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkQuaternaryBgColor))
            binding.balanceBtn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.darkSelectedPercentageSwitchBgColor))
        }
        else {
            binding.incomeExpenseLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.lightSecondaryBgColor))
            binding.balanceBtn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun checkProUser() {
        if (isProUser.value == true) {
            Extension.goToNewActivity(this, CreateWallet::class.java)
        }
        else {
            notProAccess()
        }
    }

    private fun initRecyclerView() {
        if(isProUser.value == false && walletList.value!!.size > 2) {
            walletAdapter =
                WalletAdapter(walletList.value?.sortedWith(compareBy (
                    {Preferences().getInstance().getChosenTwoWalletIds(this)?.contains(it.walletId) == true},
                    {it.isDefault}
                ))?.reversed() ?: listOf(), this)
        }
        else {
            walletAdapter =
                WalletAdapter(walletList.value?.toList()?.sortedWith(compareBy {
                    it.isDefault
                })?.reversed() ?: listOf(), this)
        }

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.activeWalletRecyclerView.layoutManager = linearLayoutManager
        binding.activeWalletRecyclerView.adapter = walletAdapter
        binding.activeWalletRecyclerView.setHasFixedSize(true)

//        inactiveWalletAdapter = InactiveWalletAdapter()
//        val inactiveLinearLayoutManager = LinearLayoutManager(this)
//        binding.inactiveWalletRecyclerView.layoutManager = inactiveLinearLayoutManager
//        binding.inactiveWalletRecyclerView.adapter = inactiveWalletAdapter
//        binding.inactiveWalletRecyclerView.setHasFixedSize(true)
    }


    private fun setUpWalletPieChart() {
        binding.walletPieChart.isDrawHoleEnabled = true
//        binding.walletPieChart.setUsePercentValues(true)
        binding.walletPieChart.centerText = "All Wallet"
        binding.walletPieChart.description.isEnabled = false
        binding.walletPieChart.setEntryLabelColor(R.color.black)
        binding.walletPieChart.setEntryLabelTextSize(9F)
        binding.walletPieChart.setExtraOffsets(16F, 16F, 16F, 16F)
        binding.walletPieChart.holeRadius = 70F
        binding.walletPieChart.animateY(1000, Easing.EaseOutSine)

        val legend: Legend = binding.walletPieChart.getLegend()
        legend.isEnabled = false
    }

    private fun loadWalletPieChartData(isBalance: Boolean = true) {
        val entries = ArrayList<PieEntry>()

        val colors = ArrayList<Int>()
        fun setUpEntries(onCompleted: () -> Unit) {
            var counter = 0
            for (it in walletVM.walletListConvertedBalance) {


                if (it.balance.toFloat() > 0) {
                    entries.add(PieEntry(it.balance.toFloat(), it.wallet.name.toString()))
                    colors.add(Color.parseColor(it.wallet.color.toString()))
                }
                counter += 1

                //check if loop is completed
                if (counter == walletVM.walletListConvertedBalance.size) {
                    onCompleted()
                }
            }

        }
        setUpEntries {
            val walletDataSet = PieDataSet(entries, "")

            walletDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            walletDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            walletDataSet.colors = colors
            val data = PieData(walletDataSet)
            data.setDrawValues(true)
            data.setValueTextSize(12F)
            walletDataSet.setValueTextColors(colors)
            walletDataSet.isUsingSliceColorAsValueLineColor = true
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (isBalance) {
                        val valueString = getString(
                            R.string.blank,
                            Extension.getCurrencySymbol(
                                io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString()
                            )
                        ) + Extension.toBigDecimal(value.toString())
                        return valueString
                    }
                    else{
                        // 2 digits after decimal point
                        var valueString = ""
//                        if (value > 10) {
                            valueString = getString(
                                R.string.blank,
                                "%"
                            ) + Extension.toBigDecimal(value.toString())
//                        }

                        return valueString
                    }
                }
            })
            theme.resolveAttribute(R.attr.primaryText, value, true)
            binding.walletPieChart.setEntryLabelColor(value.data)
            binding.walletPieChart.setCenterTextColor(value.data)
            theme.resolveAttribute(R.attr.bgColor, value, true)
            binding.walletPieChart.setHoleColor(value.data)
            binding.walletPieChart.data = data
        }
    }

    private fun notProAccess() {
        if (walletList.value?.size!! < 2) {
            Extension.goToNewActivity(this, CreateWallet::class.java)
        } else {
            val premiumSubFrag = PremiumSubFrag()
            premiumSubFrag.show(supportFragmentManager, "")
        }
    }





}
