package io.paraga.moneytrackerdev.views.account

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import io.paraga.moneytrackerdev.R

class EnterPwDialog(context: Context) {

    private val dialog : AlertDialog
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.enter_pw_dialog,null)
        val dialog_builder = AlertDialog.Builder(context)
        dialog_builder.setView(view)


        dialog = dialog_builder.create()
    }

    fun show()
    {
        dialog.show()
    }



}