package io.paraga.moneytrackerdev.views.transaction

import android.content.Context
import android.content.res.Configuration
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.TransactionAdapter
import io.paraga.moneytrackerdev.databinding.FragmentTransactionBinding
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.selectedWalletId
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.immutableListOf
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class TransactionFrag : Fragment() {
    lateinit var textView: TextView
    lateinit var binding: FragmentTransactionBinding
    lateinit var transactionAdapter: TransactionAdapter
    lateinit var mainActivity: MainActivity
    private var currentDate = YearMonth.now()
    lateinit var popupWindow: PopupWindow
    lateinit var allTransBtn: RelativeLayout
    lateinit var expenseBtn: RelativeLayout
    lateinit var incomeBtn: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread {
            kotlin.run {
                initTransaction()
            }
        }
        showLayout()
        goToNextOrPrevMonth()
        initListener()
        setUpPopUpWindow()
        allTransBtn.setOnClickListener {
            binding.transText.text = (allTransBtn.children.first() as TextView).text.toString()
            popupWindow.dismiss()
//            Thread {
//                kotlin.run {
                    mainActivity.transType = Enums.TransTypes.ALL.value
                    mainActivity.firebaseManager.initTransListByTransType(
                        mainActivity.transType
                        , onCompleted = {})
//                }
//            }.start()

        }

        expenseBtn.setOnClickListener {
            binding.transText.text = (expenseBtn.children.first() as TextView).text.toString()
            popupWindow.dismiss()
//            Thread {
//                kotlin.run {
                    mainActivity.transType = Enums.TransTypes.EXPENSE.value
                    mainActivity.firebaseManager.initTransListByTransType(
                        mainActivity.transType
                        , onCompleted = {})
//                }
//            }.start()

        }

        incomeBtn.setOnClickListener {
            binding.transText.text = (incomeBtn.children.first() as TextView).text.toString()
            popupWindow.dismiss()
//            Thread {
//                kotlin.run {
                    mainActivity.transType = Enums.TransTypes.INCOME.value

                    mainActivity.firebaseManager.initTransListByTransType(
                        mainActivity.transType
                        , onCompleted = {})
//                }
//            }.start()

        }
        binding.transDropdown.setOnClickListener {
            popupWindow.showAsDropDown(it, Extension.dpToPx(requireContext(), -54F), Extension.dpToPx(requireContext(), 10F))
        }

        if (mainActivity.transType == Enums.TransTypes.INCOME.value) {
            incomeBtn.performClick()
        }
        else if (mainActivity.transType == Enums.TransTypes.EXPENSE.value) {
            expenseBtn.performClick()
        }
        else {
            allTransBtn.performClick()
        }


    }

    fun getRefreshFragment() {
        mainActivity.switchFrag(TransactionFrag())
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun initTransaction() {
        mainActivity.runOnUiThread {
            nestedTransList.observeForever {

                when (mainActivity.transType) {
                    Enums.TransTypes.INCOME.value -> {
                        incomeBtn.performClick()
                    }
                    Enums.TransTypes.EXPENSE.value -> {
                        expenseBtn.performClick()
                    }
                    else -> {
                        initRecyclerView(ArrayList(nestedTransList.value!!))
                        mainActivity.calculateData()
                    }
                }

            }
            incomeTransList.observeForever {
                if (mainActivity.transType == Enums.TransTypes.INCOME.value) {
                    initRecyclerView(incomeTransList.value ?: ArrayList())
                    mainActivity.calculateData()
                }
                else {
                    initRecyclerView(ArrayList(nestedTransList.value!!))
                    mainActivity.calculateData()
                }
            }
            expenseTransList.observeForever {
                if (mainActivity.transType == Enums.TransTypes.EXPENSE.value) {
                    initRecyclerView(expenseTransList.value ?: ArrayList())
                    mainActivity.calculateData()
                }
                else {
                    initRecyclerView(ArrayList(nestedTransList.value!!))
                    mainActivity.calculateData()
                }
            }
        }
    }
    fun showLayout() {
        //hide layout total from mainActivity
        mainActivity.binding.layoutPrice.visibility = View.VISIBLE
        mainActivity.binding.chooseWalletLayout.visibility = View.VISIBLE
        mainActivity.binding.layoutHeader.calendarHeader.visibility = View.VISIBLE
        mainActivity.binding.walletIcon.visibility = View.VISIBLE
        mainActivity.binding.walletName.visibility = View.VISIBLE
        mainActivity.binding.imgArrowDown.visibility = View.VISIBLE
        mainActivity.binding.layoutCurrentMonth.visibility = View.VISIBLE
        mainActivity.binding.layoutSearch.visibility = View.VISIBLE
        mainActivity.binding.textBudget.visibility = View.GONE
        mainActivity.binding.budgetViewType.visibility = View.GONE
        mainActivity.binding.addBudgetBtn.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        showLayout()
    }

    private fun initListener() {
        mainActivity.theme.resolveAttribute(R.attr.bgColor, mainActivity.value, true)
        binding.transDropdown.background.setColorFilter(mainActivity.value.data, PorterDuff.Mode.SRC_ATOP)

        mainActivity.binding.layoutHeader.imageArrowNext.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.plusMonths(1)
            goToNextOrPrevMonth()
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
        mainActivity.binding.layoutHeader.imageArrowBack.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.minusMonths(1)
            goToNextOrPrevMonth()
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
        mainActivity.binding.layoutCurrentMonth.setOnClickListener {
            currentDisplayedMonth = currentDate
            mainActivity.goneDatePicker()
            mainActivity.binding.layoutHeader.textSelectDate.text = getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                TextStyle.FULL, Extension.getLocale(mainActivity))
                    + " "
                    + currentDisplayedMonth?.year.toString())
            goToNextOrPrevMonth()
            mainActivity.selectMonthNavigation()
            mainActivity.setDataNavigation()
        }
    }
    fun goToNextOrPrevMonth() {
        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(
                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(mainActivity)))
        } else {
            mainActivity.binding.layoutHeader.textSelectDate.text = activity?.getString(
                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(mainActivity))
                        + " "
                        + currentDisplayedMonth?.year.toString())
        }
        currentMonthYear.value = activity?.getString(
            R.string.blank, currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Extension.getLocale(mainActivity))
                    + " "
                    + currentDisplayedMonth?.year.toString()).toString()
//        getNestedTransList()
    }
    private fun getNestedTransList() {
        FirebaseManager(requireContext()).getNestedTransList(
            currentMonthYear.value.toString(), selectedWalletId,
            onSuccess = {
                mainActivity.calculateData()
            })
    }

    fun initRecyclerView(transList: ArrayList<NestedTransaction> ?= null) {
        try {
            if (transList?.size == 0 && !mainActivity.binding.progressbar.isVisible) {
                binding.noDataLayout.visibility = View.VISIBLE
            }
            else {
                binding.noDataLayout.visibility = View.GONE
            }
            var newTransList = transList
            // add one item to the beginning
            newTransList = ArrayList(newTransList?.asReversed())
            newTransList.add(NestedTransaction(ArrayList()))
            newTransList = ArrayList(newTransList.asReversed())
            if (selectedWallet == mainActivity.allWallet) {
                transactionAdapter = TransactionAdapter(
                    newTransList,
                    this,
                    user?.defaultCurrency.toString()
                )
            } else {
                transactionAdapter = TransactionAdapter(
                    newTransList,
                    this,
                    (selectedWallet.currency.toString())
                )
            }

            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
            binding.mainRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.mainRecyclerView.adapter = transactionAdapter
            binding.mainRecyclerView.setHasFixedSize(true)
            binding.mainRecyclerView.isNestedScrollingEnabled = true
        }
        catch (exc: NoSuchElementException) {
            Log.d("exc", exc.toString())
        }

    }

    fun setUpPopUpWindow() {
        val inflater: LayoutInflater =
            activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (mainActivity.transType == Enums.TransTypes.ALL.value) {
            binding.transText.text = getString(R.string.all_trans)
        }
        else if (mainActivity.transType == Enums.TransTypes.INCOME.value) {
            binding.transText.text = getString(R.string.income)
        }
        else {
            binding.transText.text = getString(R.string.expense)
        }

        binding.noTransText.setText(R.string.no_transaction)
        binding.tapToAddTransText.setText(R.string.tap_to_add)

        transactionAdapter.notifyDataSetChanged()
    }
}
