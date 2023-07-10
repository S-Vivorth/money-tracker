package io.paraga.moneytrackerdev.views.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.CategoryAdapter
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.databinding.ActivityCategoryBinding
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.category.ChooseCategoryFragVM


class Category : AppCompatActivity() {
    lateinit var binding: ActivityCategoryBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val chooseCategoryFragVM = ChooseCategoryFragVM()
    private val value = TypedValue()
    private var isNewType = 1

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
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isNewType = intent.getIntExtra("isType",1)
        if (isNewType == 0) {
            binding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
            binding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryGreen
                )
            )
            binding.incomeText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.expenseText.setTextColor(
                value.data
            )
        } else {
            binding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
            binding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryRed
                )
            )
            binding.expenseText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.incomeText.setTextColor(
                value.data
            )
        }
        if (incomeModel.value?.size == 0) {
            FirebaseManager(this).initCategory(
                onSuccess = {
                    setUpCategory()
                },
                onFailure = {}
            )
        }
        else {
            setUpCategory()
        }

        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        binding.addNewBtn.setOnClickListener {
            val newCategoryFrag = NewCategoryFrag(isImport = false, activity = this)
            newCategoryFrag.show(supportFragmentManager, "")
        }

        binding.resetCategoryBtn.setOnClickListener {
            resetCategory()
        }
    }

    private fun resetCategory() {
        DialogHelper.showPrimaryDialog(this, Enums.DialogType.RESET_CATEGORY.value,
            onOkayPressed = {
                FirebaseManager(this).resetCategory(onSuccess = {
                },
                    onFailure = {})
                setUpCategory()
                Toast(this).showCustomToast(
                    getString(
                        R.string.category_has_been_reset_successfully
                    ), this
                )
                it.dismiss()
            })

    }

    private fun setUpCategory() {
        if (isNewType == 1) {
            expenseModel.observeForever {
                initRecyclerView(expenseModel.value!!, Enums.TransTypes.EXPENSE.value)
            }
        } else {
            incomeModel.observeForever {
                initRecyclerView(incomeModel.value!!, Enums.TransTypes.INCOME.value)
            }
        }
        binding.incomeBtn.setOnClickListener {
            incomeModel.observeForever {
                initRecyclerView(incomeModel.value!!, Enums.TransTypes.INCOME.value)
            }

            binding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
            binding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryGreen
                )
            )
            binding.incomeText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.expenseText.setTextColor(
                value.data
            )
        }

        binding.expenseBtn.setOnClickListener {
            initRecyclerView(expenseModel.value!!, Enums.TransTypes.EXPENSE.value)
            binding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
            binding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryRed
                )
            )
            binding.expenseText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            theme.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
            binding.incomeText.setTextColor(
                value.data
            )

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(categoryList: ArrayList<Category>, transType: Int) {
        categoryAdapter = CategoryAdapter(this, categoryList, transType)
        val categoryLinearLayoutManager = LinearLayoutManager(this)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.categoryRecycler.layoutManager = categoryLinearLayoutManager
        binding.categoryRecycler.adapter = categoryAdapter
        binding.categoryRecycler.setHasFixedSize(true)
        categoryAdapter.notifyDataSetChanged()

    }
}