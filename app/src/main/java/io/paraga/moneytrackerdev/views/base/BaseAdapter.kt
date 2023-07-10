package io.paraga.moneytrackerdev.views.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

abstract class BaseAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mList: MutableList<Any> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: MutableList<Any>) {
        if (list.size == 0) return
        for (item in list) {
            val index = mList.indexOf(item)
            if (index >= 0) {
                if (mList[index].toString() != item.toString()) {
                    mList[index] = item
                    notifyItemChanged(index,1)
                }
            } else {
                mList.add(item)
                notifyItemInserted(mList.size-1)
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun select() {

//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

        // This keeps track of the currently selected position
        var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
            if (newPos in mList.indices) {
                notifyItemChanged(oldPos)
                notifyItemChanged(newPos)
            }
        }
    }

}