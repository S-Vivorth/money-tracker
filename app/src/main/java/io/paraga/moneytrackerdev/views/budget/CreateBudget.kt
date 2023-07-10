package io.paraga.moneytrackerdev.views.budget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.ChooseBudgetCategoryAdapter
import io.paraga.moneytrackerdev.databinding.ActivityCreateBudgetBinding
import io.paraga.moneytrackerdev.models.Budget
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.budget.BudgetVM
import io.paraga.moneytrackerdev.views.transaction.ChooseWalletFrag
import io.paraga.moneytrackerdev.views.wallet.AddInitialBalanceFrag
import org.apache.poi.ss.formula.functions.Choose
import java.io.Serializable
import java.util.ArrayList
import kotlin.properties.Delegates

class CreateBudget : AppCompatActivity() {
    lateinit var binding: ActivityCreateBudgetBinding
    private val value = TypedValue()
    private var isEditBudget by Delegates.notNull<Boolean>()
    var budgetCategory: Category = Category(
        title = "All Categories",
        image = "ic-general"
    )
    var selectedWallet: Wallet = Wallet(
        symbol = "globe",
        currency = user?.defaultCurrency
    )
    var selectedWalletId: String = ""
    var budget = Budget()
    private val budgetVM = BudgetVM()

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
        binding = ActivityCreateBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        initListeners()
    }


    private fun initListeners() {
        binding.backBtnLayout.setOnClickListener {
            finish()
        }
        binding.saveBtn.setOnClickListener {
            budget.userid = user?.userid
            budget.period = Enums.Period.MONTHLY.value
            budget.wallet = selectedWalletId
            if (isEditBudget) {
                if (budget.amount == 0.0) {
                    Toast(this).showCustomToast(getString(R.string.invalid_budget_amount), this)
                } else {
                    budgetVM.updateBudget(
                        budget,
                        onSuccess = {
                        }
                    )
                    val intent = Intent()
                    intent.putExtra(Enums.Extras.IS_EDIT_BUDGET.value, true)
                    val bundle = Bundle()
                    bundle.putSerializable("budget", budget as Serializable)
                    intent.putExtra("bundle", bundle)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else {
                if (budget.amount == 0.0) {
                    Toast(this).showCustomToast(getString(R.string.invalid_budget_amount), this)
                } else {
                    budgetVM.createBudget(
                        budget,
                        onSuccess = {
                        }
                    )
                    finish()
                }
            }

        }

        binding.budgetCategoryLayout.setOnClickListener {
            val budgetCategoryFrag = BudgetCategoryFrag()
            budgetCategoryFrag.createBudget = this
            budgetCategoryFrag.show(supportFragmentManager, "")
        }

        binding.budgetAmountLayout.setOnClickListener {
            val addInitialBalance = AddInitialBalanceFrag()
            addInitialBalance.isFromBudget = true
            addInitialBalance.fromEditWallet = false
            addInitialBalance.show(supportFragmentManager, "")
        }

        binding.budgetWalletLayout.setOnClickListener {
            val chooseWalletFrag = ChooseWalletFrag()
            chooseWalletFrag.isFromBudget = true
            chooseWalletFrag.isFromMainActivity = false
            chooseWalletFrag.isFromImportActivity = false
            chooseWalletFrag.show(supportFragmentManager, "")
        }
    }


    private fun setUpViews() {
        selectedWallet.name = getString(R.string.all_wallets)
        val nightModeFlags: Int =
            resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.budgetIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkBudgetIconColor))
            binding.amountIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkBudgetIconColor))
            binding.walletIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkBudgetIconColor))
            binding.periodIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkBudgetIconColor))
            binding.budgetForLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkBudgetTextColor
                )
            )
            binding.amountLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkBudgetTextColor
                )
            )
            binding.walletLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkBudgetTextColor
                )
            )
            binding.periodLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkBudgetTextColor
                )
            )
        } else {
            binding.budgetIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightBudgetIconColor))
            binding.amountIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightBudgetIconColor))
            binding.walletIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightBudgetIconColor))
            binding.periodIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            binding.budgetForLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.lightBudgetTextColor
                )
            )
            binding.amountLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.lightBudgetTextColor
                )
            )
            binding.walletLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.lightBudgetTextColor
                )
            )
            binding.periodLabel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.lightBudgetTextColor
                )
            )
        }

        isEditBudget = intent.getBooleanExtra(Enums.Extras.IS_EDIT_BUDGET.value, false)

        if (isEditBudget) {
            setUpEditBudget()
        }

        binding.amountBudget.text = getString(
            R.string.blank,
            selectedWallet.currency
                    + " "
                    + budget.amount
        )

        binding.categoryName.text = Extension.getStringResourceByName(this, budget.category.title.toString())
        binding.walletName.text = selectedWallet.name
    }

    private fun setUpEditBudget() {
        binding.deleteBudgetBtn.visibility = View.VISIBLE
        binding.deleteBudgetBtn.setOnClickListener {
            DialogHelper.showPrimaryDialog(
                this,
                Enums.DialogType.BUDGET.value,
                onOkayPressed = {
                    budgetVM.deleteBudget(
                        budget,
                        onSuccess = {
                        }
                    )
                    val intent = Intent()
                    intent.putExtra(Enums.Extras.IS_EDIT_BUDGET.value, false)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            )
        }
        binding.budgetTitle.setText(R.string.edit_budget)
        val bundle = intent.getBundleExtra("bundle")
        budget = bundle?.getSerializable("budget") as Budget
        if (budget.wallet != "") {
            walletList.value?.forEach {
                if (it.walletId == budget.wallet) {
                    selectedWallet = it.wallet
                    return@forEach
                }
            }
        }
        selectedWalletId = budget.wallet.toString()
    }


}