package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.annotation.RequiresApi
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.category.NewCategoryFrag

class NewCategoryAdapter(val newCategoryFrag: NewCategoryFrag) : BaseAdapter() {
    private val categoryList = Constants().categoryList
    lateinit var categoryIcon: ImageView
    var previousSelectedIndex = 0
    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(p0: Int): Any {
        return categoryList.get(p0)
    }


    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    fun refresh() {
        notifyDataSetChanged()
    }


    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view =  LayoutInflater.from(p2?.context).inflate(R.layout.new_category_cell, p2, false)
        categoryIcon = view.findViewById(R.id.categoryIcon)
        categoryIcon.setImageResource(Extension.getResouceId(p2?.context!!, categoryList[p0].image?.replace("-","_")))

        if (p0 == previousSelectedIndex) {
            view?.setBackgroundResource(R.drawable.rounded_wallet_icon_layout)
        }
//        view.setOnClickListener {
//            Log.d("clicked", p2.size.toString())
////            p2[p0].setBackgroundResource(R.drawable.rounded_wallet_icon_layout)
////
////            p2[previousSelectedIndex].setBackgroundResource(0)
////            previousSelectedIndex = p0
//        }
        return view
    }
}
