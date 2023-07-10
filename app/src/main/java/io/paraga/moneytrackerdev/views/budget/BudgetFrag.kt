package io.paraga.moneytrackerdev.views.budget

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.views.MainActivity
import androidx.recyclerview.widget.GridLayoutManager
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.adapters.BudgetAdapter
import io.paraga.moneytrackerdev.databinding.FragmentBudgetBinding
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.networks.budgetList
import io.paraga.moneytrackerdev.networks.nestedTransList
import io.paraga.moneytrackerdev.networks.nestedTransMapByMonth
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
class BudgetFrag : Fragment {
    private var monthYear = YearMonth.now()
    lateinit var mainActivity: MainActivity
    private lateinit var budgetAdapter: BudgetAdapter
    lateinit var binding: FragmentBudgetBinding
    lateinit var gridLayoutManager: GridLayoutManager
    private var nestedTransMapByCategoryAndWallet: HashMap<String, HashMap<String, Any>> = HashMap()

    constructor() : super()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = context as MainActivity
        setUpViews()
        initListener()
        initRecyclerView()



        budgetList.observeForever {
            initBudgetData()
            initRecyclerView()
        }

        nestedTransList.observeForever {
            initBudgetData()
            initRecyclerView()
        }
        mainActivity.goToNextOrPrevMonth()
        mainActivity.binding.layoutHeader.imageArrowNext.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.plusMonths(1)
            mainActivity.selectMonthNavigation()
            mainActivity.goToNextOrPrevMonth()
            mainActivity.setDataNavigation()
        }
        mainActivity.binding.layoutHeader.imageArrowBack.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.minusMonths(1)
            mainActivity.selectMonthNavigation()
            mainActivity.goToNextOrPrevMonth()
            mainActivity.setDataNavigation()
        }
    }
    private fun initBudgetData() {
        budgetList.value?.forEach { budget ->
            val transList: ArrayList<Transaction> = ArrayList()
            val hashMap: HashMap<String, Any> = HashMap()
            var budgetAmount = 0.0
            try {
                if (nestedTransMapByMonth.containsKey(currentMonthYear.value.toString())) {
                    (nestedTransMapByMonth[currentMonthYear.value.toString()] ?: ArrayList()).forEach {
                        if (budget.category.title == Enums.General.ALL_CATEGORIES.value && budget.wallet == "") {
                            it.nestedTransList.forEach { trans ->
                                if (trans.type != Enums.TransTypes.INCOME.value) {
                                    val wallet = Wallet()
                                    wallet.currency = user?.defaultCurrency.toString()
                                    if (trans.currency == wallet.currency) {
                                        budgetAmount += trans.amount?.toDouble() ?: 0.0
                                    }
                                    else {
                                        val convertedAmount = Extension.convertCurrency(
                                            trans.currency.toString(),
                                            wallet.currency.toString(),
                                            trans.amount!!.toDouble(),
                                            mainActivity
                                        )
                                        budgetAmount += convertedAmount
                                    }
                                    transList.add(trans)
                                }
                            }
                        }
                        else {
                            it.nestedTransList.forEach { trans ->
                                if (trans.type != Enums.TransTypes.INCOME.value) {
                                    if (budget.category.title == Enums.General.ALL_CATEGORIES.value && budget.wallet == trans.walletID
                                        || (trans.category.title == budget.category.title && budget.wallet == trans.walletID.toString())
                                        || trans.category.title == budget.category.title && budget.wallet == "") {
                                        var wallet = Wallet()
                                        if (budget.wallet == "") {
                                            wallet.currency = user?.defaultCurrency.toString()
                                        }
                                        else {
                                            walletList.value?.forEach { walletTrans ->
                                                if (walletTrans.walletId == budget.wallet) {
                                                    wallet = walletTrans.wallet
                                                    return@forEach
                                                }
                                            }
                                        }

                                        if (trans.currency == wallet.currency) {
                                            budgetAmount += trans.amount?.toDouble() ?: 0.0
                                        }
                                        else {
                                            val convertedAmount = Extension.convertCurrency(
                                                trans.currency.toString(),
                                                wallet.currency.toString(),
                                                trans.amount!!.toDouble(),
                                                mainActivity
                                            )
                                            budgetAmount += convertedAmount
                                        }
                                        transList.add(trans)
                                    }
                                }
                            }
                        }
                    }
                }

                hashMap.put(Enums.General.TOTAL_BUDGET_AMOUNT.value, budgetAmount)
                hashMap.put(Enums.General.BUDGET_TRANS_LIST.value, transList)

                // use walletId to differentiate category
                nestedTransMapByCategoryAndWallet.put(
                    (budget.category.title + budget.wallet) ?: "",
                    hashMap
                )
            }
            catch (exc: ConcurrentModificationException) {
                Thread.currentThread().interrupt()
            }

        }
        Log.d("nestedTransMapBy",nestedTransMapByCategoryAndWallet.keys.toString())
    }
    private fun setUpViews() {
        //hide layout total from mainActivity
        hideOrShowViews()

        if (mainActivity.spanCount == Enums.SpanCount.ONE.value) {
            mainActivity.binding.budgetViewType.setImageResource(R.drawable.general_grid_view)
        }
        else {
            mainActivity.binding.budgetViewType.setImageResource(R.drawable.general_view_agenda)
        }
    }
    private fun initListener() {
        mainActivity.binding.layoutCurrentMonth.setOnClickListener {
            currentDisplayedMonth = monthYear
            mainActivity.goneDatePicker()
        }

        mainActivity.binding.addBudgetBtn.setOnClickListener {
            val intent = Intent(context, CreateBudget::class.java)
            intent.putExtra(Enums.Extras.IS_EDIT_BUDGET.value, false)
            if (isProUser.value == true) {
                startActivity(intent)
            }
            else {
                if (budgetList.value?.size!! < 2) {
                    startActivity(intent)
                } else {
                    val premiumSubFrag = PremiumSubFrag()
                    premiumSubFrag.show(mainActivity.supportFragmentManager, "")
                }
            }
        }

        mainActivity.binding.budgetViewType.setOnClickListener {
            switchLayout()
            Preferences().getInstance().setBudgetViewType(mainActivity,mainActivity.spanCount)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun switchLayout() {
        if (mainActivity.spanCount == Enums.SpanCount.ONE.value) {
            mainActivity.spanCount = Enums.SpanCount.TWO.value
            mainActivity.binding.budgetViewType.setImageResource(R.drawable.general_view_agenda)
        }
        else {
            mainActivity.spanCount = Enums.SpanCount.ONE.value
            mainActivity.binding.budgetViewType.setImageResource(R.drawable.general_grid_view)
        }
        gridLayoutManager.spanCount = mainActivity.spanCount
        budgetAdapter.notifyDataSetChanged()
//        budgetAdapter.notifyItemRangeChanged(0, budgetAdapter.itemCount)
    }
    private fun initRecyclerView() {
        binding.budgetRecyclerView.apply {
            gridLayoutManager = GridLayoutManager(context, mainActivity.spanCount)
            budgetAdapter = BudgetAdapter(this@BudgetFrag ,gridLayoutManager, nestedTransMapByCategoryAndWallet)
            adapter = budgetAdapter
            layoutManager = gridLayoutManager
        }
    }
     fun hideOrShowViews() {
        mainActivity.binding.layoutPrice.visibility = View.GONE
        mainActivity.binding.chooseWalletLayout.visibility = View.GONE
        mainActivity.binding.layoutCurrentMonth.visibility = View.GONE
        mainActivity.binding.layoutSearch.visibility = View.GONE
        mainActivity.binding.textBudget.visibility = View.VISIBLE
        mainActivity.binding.budgetViewType.visibility = View.VISIBLE
        mainActivity.binding.addBudgetBtn.visibility = View.VISIBLE

    }


    override fun onResume() {
        super.onResume()
        hideOrShowViews()
    }
}