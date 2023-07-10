package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Language
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.language.ChooseLanguageFrag

class LanguageAdapter(private val mainActivity: MainActivity,
private val chooseLanguageFrag: ChooseLanguageFrag,
private val languageList: ArrayList<Language>) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var languageName: TextView
        var languageDetail: TextView
        var selectedIcon: ImageView
        var context: Context

        init {
            languageName = view.findViewById(R.id.languageName)
            languageDetail = view.findViewById(R.id.languageDetail)
            selectedIcon = view.findViewById(R.id.autoTickIcon)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.language_cell, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: LanguageAdapter.ViewHolder, position: Int) {
        holder.languageName.text = languageList[position].name
        holder.languageDetail.text = languageList[position].detail
        if (!Extension.isAutoLanguage(holder.context)) {
            if (position == 0) {
                holder.selectedIcon.visibility = View.VISIBLE
            }
        }
        holder.itemView.setOnClickListener {
            Preferences().getInstance()
                .setAutoLanguage(
                    holder.context, false,
                    languageCode = languageList[position].code
                )
            holder.context.changeLanguage()
            mainActivity.onConfigurationChanged(mainActivity.resources.configuration)
            chooseLanguageFrag.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}