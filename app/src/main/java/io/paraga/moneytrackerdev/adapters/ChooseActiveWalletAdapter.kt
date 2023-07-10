package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.wallet.ChooseActiveWalletFrag
import java.lang.String

class ChooseActiveWalletAdapter(private val activity: AppCompatActivity, val chooseActiveWalletFrag: ChooseActiveWalletFrag): RecyclerView.Adapter<ChooseActiveWalletAdapter.ViewHolder>() {



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var walletIcon: ImageView
        lateinit var walletName: TextView
        lateinit var context: Context
        lateinit var checkBox: CheckBox
        lateinit var walletCell: RelativeLayout

        init {
            walletIcon = view.findViewById(R.id.walletIcon)
            walletName = view.findViewById(R.id.walletName)
            context = view.context
            checkBox = view.findViewById(R.id.checkBox)
            walletCell = view.findViewById(R.id.walletCell)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseActiveWalletAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.choose_wallet_cell, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return walletList.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ChooseActiveWalletAdapter.ViewHolder, position: Int) {
        holder.checkBox.visibility = View.VISIBLE
        val color = Color.parseColor(walletList.value!![position].wallet.color.toString())
        holder.walletIcon.setBackgroundResource(
            Extension.getResouceId(holder.context, walletList.value!![position].wallet.symbol)
        )
        holder.walletIcon.backgroundTintList = ColorStateList.valueOf(color)
        holder.walletName.text = walletList.value!![position].wallet.name
        holder.checkBox.isChecked = chooseActiveWalletFrag.activeWalletIds.contains(walletList.value!![position].walletId) == true

        holder.itemView.setOnClickListener {
            Log.d("clickk", chooseActiveWalletFrag.activeWalletIds.toString())
            if (walletList.value?.get(position)?.isDefault == true) {

            }
            else {
                chooseActiveWalletFrag.activeWalletIds.removeIf {
                    it != walletList.value?.find { it.isDefault }?.walletId
                }
                if (!(holder.checkBox.isChecked)) {
                    chooseActiveWalletFrag.activeWalletIds.add(walletList.value!![position].walletId)
                }
            }
            notifyDataSetChanged()
        }
    }
}