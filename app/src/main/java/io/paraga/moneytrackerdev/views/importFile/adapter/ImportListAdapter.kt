package io.paraga.moneytrackerdev.views.importFile.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.ImportModel
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.views.importFile.activity.ImportDetailActivity

class ImportListAdapter(private val mListener: OnClickedListener<ImportModel>?=null, val list: ArrayList<ArrayList<String>> = arrayListOf(),
                        private val importFileActivity: ImportDetailActivity): RecyclerView.Adapter<ImportListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cell_import, parent, false)
        return ViewHolder(view = view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowList : ArrayList<Int> = arrayListOf()
        val listM = list[position]
        val importModel = importFileActivity.mTypeList[position]
        importFileActivity.theme.resolveAttribute(R.attr.secondaryBgColor, importFileActivity.value, true)
        holder.layoutMain.backgroundTintList = ColorStateList.valueOf(importFileActivity.value.data)
        holder.itemView.setOnClickListener {
            mListener?.onClicked(it , position, mList = listM, importModel = importModel)
        }
        for (index in 1 .. list.size) {
            rowList.add(index)
        }
        holder.textRow.text = holder.context.getString(R.string.blank,"#"+rowList[position].toString())
        if (list[position].first().toString().isNotEmpty()) {
//            importFileActivity.theme.resolveAttribute(R.attr.primaryText, importFileActivity.value, true)
//            holder.textHeader.setTextColor(importFileActivity.value.data)
            holder.textHeader.text = list[position].first().toString()
        } else {
//            importFileActivity.theme.resolveAttribute(R.attr.colorOnSurfaceVariant, importFileActivity.value, true)
//            holder.textHeader.setTextColor(importFileActivity.value.data)
            holder.textHeader.text = holder.context.getString(R.string.empty)
        }
        holder.textBody.text = list [position].toString().replace("[","").replace("]","")
        if (importFileActivity.mTypeList[position].title != holder.context.getString(R.string.ingored) && importFileActivity.mTypeList[position].icon_select != R.drawable.do_not_disturb_on.toString()) {
            holder.layoutSelect.setBackgroundResource(R.drawable.rounded_slight_stroke_8r_select)
            importFileActivity.theme.resolveAttribute(R.attr.colorOnSurfaceVariant, importFileActivity.value, true)
            holder.textBody.setTextColor(importFileActivity.value.data)
            importFileActivity.theme.resolveAttribute(R.attr.primaryText, importFileActivity.value, true)
            holder.textSelect.setTextColor(importFileActivity.value.data)
            holder.textRow.setTextColor(importFileActivity.value.data)
        } else {
            importFileActivity.theme.resolveAttribute(R.attr.colorPrimaryDarkOutLine, importFileActivity.value, true)
            holder.textRow.setTextColor(importFileActivity.value.data)
            importFileActivity.theme.resolveAttribute(R.attr.colorTextSelectImport, importFileActivity.value, true)
            holder.textSelect.setTextColor(importFileActivity.value.data)
            importFileActivity.theme.resolveAttribute(R.attr.colorTextBody, importFileActivity.value, true)
            holder.textBody.setTextColor(importFileActivity.value.data)
            holder.layoutSelect.setBackgroundResource(R.drawable.rounded_slight_stroke_8r)
        }
        holder.textSelect.text = importFileActivity.mTypeList[position].title
        holder.imgIcon.setImageResource(Extension.getResouceId(holder.itemView.context,
            importFileActivity.mTypeList[position].icon_select
        ))

    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textHeader: TextView
        var textBody: TextView
        var textSelect: TextView
        var textRow: TextView
        var layoutSelect: ConstraintLayout
        var imgIcon: ImageView
        var context: Context
        var layoutMain: ConstraintLayout
        init {
            textHeader = view.findViewById(R.id.text_header)
            textBody = view.findViewById(R.id.text_body)
            textSelect = view.findViewById(R.id.text_select)
            layoutSelect = view.findViewById(R.id.layout_select)
            textRow = view.findViewById(R.id.text_row)
            imgIcon = view.findViewById(R.id.img_icon)
            context = view.context
            layoutMain = view.findViewById(R.id.layout_main)
        }
    }
}