package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.category.NewCategoryFrag


class CategoryAdapter(
    private val category: io.paraga.moneytrackerdev.views.category.Category,
    val categoryList: ArrayList<Category>,
    val transType: Int,
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryName: TextView
        var categoryIcon: ImageView
        var context: Context
        init {
            categoryName = view.findViewById(R.id.categoryName)
            categoryIcon = view.findViewById(R.id.categoryIcon)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_cell, parent, false)
        return ViewHolder(view = view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryIcon.setImageResource(Extension.getResouceId(holder.context, categoryList[position].image))

        holder.categoryName.text = Extension.getStringResourceByName(holder.context,categoryList[position].title.toString())
        holder.itemView.setOnClickListener {
            val newCategoryFrag = NewCategoryFrag(isImport = false, activity = category)
            newCategoryFrag.oldCategory.apply {
                title = categoryList[position].title
                color = categoryList[position].color
                image = categoryList[position].image
                selectedCount = categoryList[position].selectedCount
                initialName = categoryList[position].initialName
            }
            newCategoryFrag.newCategory.apply {
                title = categoryList[position].title
                color = categoryList[position].color
                image = categoryList[position].image
                selectedCount = categoryList[position].selectedCount
                initialName = categoryList[position].initialName
            }
            newCategoryFrag.isEditCategory = true
            newCategoryFrag.oldTransType = transType

            newCategoryFrag.show(category.supportFragmentManager, "")
            
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}