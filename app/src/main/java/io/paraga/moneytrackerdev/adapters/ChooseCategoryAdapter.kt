package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.category.ChooseCategoryFrag

class ChooseCategoryAdapter(private val chooseCategoryFrag: ChooseCategoryFrag) :
    RecyclerView.Adapter<ChooseCategoryAdapter.ViewHolder>() {
    private var categoryList: ArrayList<Category> = ArrayList()
    var itemsCopy: ArrayList<Category> = ArrayList()
    private var selectedPosition = -1
    var categories: HashMap<String, ArrayList<Category>> = HashMap()

    init {
        if (chooseCategoryFrag.isExpense) {
            categoryList.addAll(expenseModel.value!!)
        } else {
            categoryList.addAll(incomeModel.value!!)
        }
        itemsCopy.addAll(categoryList)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCategoryAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_cell, parent, false)
        return ViewHolder(view = view)
    }


    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ChooseCategoryAdapter.ViewHolder, position: Int) {

        val itemView = holder.itemView

        itemView.setOnClickListener {
            chooseCategoryFrag.addTrans.binding.categoryName.text = Extension.getStringResourceByName(holder.context,categoryList[position].title.toString())
            chooseCategoryFrag.addTrans.binding.categoryIcon.setImageResource(
                Extension.getResouceId(holder.context, categoryList[position].image)
            )
            chooseCategoryFrag.addTrans.addTransVM.selectedCategory = categoryList[position]
            if(chooseCategoryFrag.isExpense) {
                expenseModel.value?.find { it == categoryList[position] }?.apply {
                    selectedCount = 1
                }
            }
            else {
                incomeModel.value?.find { it == categoryList[position] }?.apply {
                    selectedCount = 1
                }
            }
            categories[Enums.DB.INCOME_FIELD.value] = incomeModel.value!!
            categories[Enums.DB.EXPENSE_FIELD.value] = expenseModel.value!!
            FirebaseManager(context = holder.context).updateCategory(categories, onSuccess = {
            })
            chooseCategoryFrag.dismiss()
        }

        // get resource by string name
        holder.categoryIcon.setImageResource(Extension.getResouceId(holder.context, categoryList[position].image))

        holder.categoryName.text = Extension.getStringResourceByName(holder.context,categoryList[position].title.toString())

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun setFilter(newList: List<Category>) {
        categoryList.clear()
        categoryList.addAll(newList)
        notifyDataSetChanged()
    }
    fun filter(text: String) {
        var text = text
        categoryList.clear()

        text = text.lowercase()
        for (item in itemsCopy) {
            if (item.title?.lowercase()?.contains(text)!!) {
                categoryList.add(item)
            }
        }
        notifyDataSetChanged()
    }

}