package io.paraga.moneytrackerdev.views.adapterDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.utils.helper.Extension

class DialogAdapter(private val mList: ArrayList<String>): RecyclerView.Adapter<DialogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cell_what_new, parent, false)
        return ViewHolder(view = view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgDone.setImageResource(
            Extension.getResouceId(holder.itemView.context,
            R.drawable.done.toString()))
        holder.textWhatNew.text = mList[position]
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgDone: ImageView
        var textWhatNew: TextView

        init {
            imgDone = view.findViewById(R.id.img_done)
            textWhatNew = view.findViewById(R.id.text_what_new)

        }
    }

}