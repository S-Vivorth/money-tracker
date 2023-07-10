package io.paraga.moneytrackerdev.views.importFile.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.EditCategoryWalletBottomSheetFragment

class WalletImportAdapter(private val walletList: ArrayList<WalletTrans>,private val mListener: OnClickedListener<WalletTrans>,private val editCategoryWalletBottomSheetFragment: EditCategoryWalletBottomSheetFragment,private val walletID : String): RecyclerView.Adapter<WalletImportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.choose_wallet_cell_import, parent, false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.walletIcon.setImageResource(Extension.getResouceId(holder.context,walletList[position].wallet.symbol))
        holder.walletName.text = walletList[position].wallet.name
        holder.walletIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(walletList[position].wallet.color.toString()))
        holder.cardLayout.setOnClickListener {
            mListener.onClicked(itemView = holder.itemView,position = position, model = walletList[position])
            editCategoryWalletBottomSheetFragment.dialog?.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return walletList.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var walletName: TextView
        var walletIcon: ImageView
        var context: Context
//        var walletIconChildLayout: RelativeLayout
        var cardLayout: CardView
        var clickIcon: ImageView
        init {
            walletName = view.findViewById(R.id.walletName)
            walletIcon = view.findViewById(R.id.walletIcon)
            context = view.context
//            walletIconChildLayout = view.findViewById(R.id.walletIconChildLayout)
            cardLayout = view.findViewById(R.id.cardLayout)
            clickIcon = view.findViewById(R.id.walletClick)
        }
    }
}