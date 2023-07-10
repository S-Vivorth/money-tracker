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
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.category.ChooseCategoryFrag

class MostFrequentAdapter(private val chooseCategoryFrag: ChooseCategoryFrag,
                          private val mostFrequentList: ArrayList<Category>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var categoryName: TextView
    lateinit var categoryIcon: ImageView
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.most_frequent_cell, parent, false)
        categoryName = view.findViewById(R.id.frequentCategoryName)
        categoryIcon = view.findViewById(R.id.frequentCategoryIcon)
        context = parent.context
        return ViewHolder(view = view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemView = holder.itemView

        itemView.setOnClickListener {
            chooseCategoryFrag.addTrans.binding.categoryName.text = Extension.getStringResourceByName(context,
                mostFrequentList[position].title.toString())
            chooseCategoryFrag.addTrans.binding.categoryIcon.setImageResource(
                Extension.getResouceId(context, mostFrequentList[position].image)
            )
            chooseCategoryFrag.addTrans.addTransVM.selectedCategory = mostFrequentList[position]

            chooseCategoryFrag.dismiss()
        }
        categoryIcon.setImageResource(
            Extension.getResouceId(
                context,
                mostFrequentList.get(position).image?.replace(
                    "-",
                    "_"
                )
            )
        )
        categoryName.text = Extension.getStringResourceByName(context,mostFrequentList.get(position).title.toString())
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mostFrequentList.size
    }

}
