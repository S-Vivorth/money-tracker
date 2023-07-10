package io.paraga.moneytrackerdev.utils.helper

import android.graphics.ColorSpace.Model
import android.view.View
import io.paraga.moneytrackerdev.models.DayModel
import io.paraga.moneytrackerdev.models.ImportModel

interface OnClickedListener<T> {
    fun resultBack(day: T){}
    fun onClicked(itemView: View? = null, position: Int? = null, model: T){}
    fun onClicked(itemView: View? = null, position: Int? = null, mList: ArrayList<String>, importModel: T){}
    fun onClicked(itemView: View? = null, position: Int? = null, mType: List<T>){}
    fun onClicked(itemView: View? = null, mType: ImportModel){}
    fun returnBackPosition(position: Int?){}
}