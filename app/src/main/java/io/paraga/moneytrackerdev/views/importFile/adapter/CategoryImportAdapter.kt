package io.paraga.moneytrackerdev.views.importFile.adapter

import android.content.Context
import android.provider.SyncStateContract.Constants
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.EditCategoryWalletBottomSheetFragment

class CategoryImportAdapter(private val mListener: OnClickedListener<Category>,private val editCategoryWalletBottomSheetFragment: EditCategoryWalletBottomSheetFragment,private val titleCategory:String) : RecyclerView.Adapter<CategoryImportAdapter.ViewHolder>() {
    private val categoryList: ArrayList<Category> = arrayListOf()
    init {
        if (editCategoryWalletBottomSheetFragment.typeCategory == 1) {
            categoryList.addAll(expenseModel.value!!)
        } else {
            categoryList.addAll(incomeModel.value!!)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_import_cell, parent, false)
        return ViewHolder(view = view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryIcon.setImageResource(Extension.getResouceId(holder.context, categoryList[position].image))
        holder.categoryName.text = Extension.getStringResourceByName(holder.context, categoryList[position].title.toString())
//        if (categoryList[position].title == titleCategory) {
//            holder.clickIcon.visibility = View.VISIBLE
//        }
        holder.itemView.setOnClickListener {
            mListener.onClicked(itemView = holder.itemView,position = position, model = categoryList[position])
            editCategoryWalletBottomSheetFragment.dialog?.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryName: TextView
        var categoryIcon: ImageView
        var context: Context
        var clickIcon: ImageView
        init {
            categoryName = view.findViewById(R.id.categoryName)
            categoryIcon = view.findViewById(R.id.categoryIcon)
            context = view.context
            clickIcon = view.findViewById(R.id.img_arrow_down)
        }
    }
}