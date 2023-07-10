package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.budget.BudgetCategoryFrag

class ChooseBudgetCategoryAdapter(private val budgetCategoryFrag: BudgetCategoryFrag):
    RecyclerView.Adapter<ChooseBudgetCategoryAdapter.ViewHolder>() {

    private var categoryList: ArrayList<Category> = ArrayList()
    private val allCategory = Category(
        image = "ic-general"
    )

    init {
        allCategory.title = Extension.getStringResourceByName(budgetCategoryFrag.createBudget, Enums.General.ALL_CATEGORIES.value)
        if (budgetCategoryFrag.createBudget.budget.category.title != allCategory.title &&
            budgetCategoryFrag.createBudget.budget.category.image !=  allCategory.image) {
            categoryList.add(allCategory)
        }
        categoryList.addAll(expenseModel.value!!)
        categoryList.addAll(incomeModel.value!!)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var categoryName: TextView
        lateinit var categoryIcon: ImageView
        lateinit var context: Context

        init {
            categoryName = view.findViewById(R.id.categoryName)
            categoryIcon = view.findViewById(R.id.categoryIcon)
            context = view.context

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseBudgetCategoryAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_cell, parent, false)
        return ViewHolder(view = view)
    }
    override fun onBindViewHolder(holder: ChooseBudgetCategoryAdapter.ViewHolder, position: Int) {
        holder.categoryIcon.setImageResource(Extension.getResouceId(holder.context, categoryList[position].image))
        holder.categoryName.text = Extension.getStringResourceByName(holder.context, categoryList[position].title.toString())
        holder.itemView.setOnClickListener {
            budgetCategoryFrag.createBudget.
            binding.categoryName.text = Extension.getStringResourceByName(holder.context, categoryList[position].title.toString())
            budgetCategoryFrag.createBudget.budget.category.image = categoryList[position].image
            budgetCategoryFrag.createBudget.budget.category.title = categoryList[position].title
            budgetCategoryFrag.createBudget.budget.category.color = categoryList[position].color
            budgetCategoryFrag.dismiss()
        }
    }
    override fun getItemCount(): Int {
        return categoryList.size
    }
}
