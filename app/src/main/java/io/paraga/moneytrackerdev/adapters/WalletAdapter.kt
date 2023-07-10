package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.wallet.ChooseActiveWalletFrag
import io.paraga.moneytrackerdev.views.wallet.EditWallet
import java.io.Serializable
import java.lang.String


class WalletAdapter (private val walletList: List<WalletTrans>,private val activity: AppCompatActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    lateinit var context: Context
    lateinit var dropdownBtn: ImageView
    lateinit var popupWindow: PopupWindow
    lateinit var balance: TextView
    lateinit var walletName: TextView
    lateinit var walletIconChildLayout: RelativeLayout
    lateinit var walletIcon: ImageView
    lateinit var defaultLayout: CardView
    lateinit var numberOfSharedUser: TextView
    lateinit var walletCellBgColor: CardView
    lateinit var lockedLayout: CardView
    lateinit var sharedUserLayout: CardView
    lateinit var rootLayout:RelativeLayout
    val value = TypedValue()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.wallet_cell, parent, false)
        context = parent.context
        dropdownBtn = view.findViewById(R.id.dropdownBtn)
        balance = view.findViewById(R.id.balance)
        walletName = view.findViewById(R.id.walletName)
        walletIconChildLayout = view.findViewById(R.id.walletIconChildLayout)
        walletIcon = view.findViewById(R.id.walletIcon)
        defaultLayout = view.findViewById(R.id.defaultLayout)
        numberOfSharedUser = view.findViewById(R.id.numberOfSharedUser)
        walletCellBgColor = view.findViewById(R.id.walletCellBgColor)
        lockedLayout = view.findViewById(R.id.lockedLayout)
        sharedUserLayout = view.findViewById(R.id.sharedUserLayout)
        rootLayout = view.findViewById(R.id.rootLayout)
        setUpPopUpWindow()

        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        if (isProUser.value == false && walletList.size > 2) {
            if (Preferences().getInstance().getChosenTwoWalletIds(context)?.contains(walletList[position].walletId) == false) {
                context.theme.resolveAttribute(R.attr.surface, value, true)
                walletCellBgColor.setCardBackgroundColor(value.data)
                lockedLayout.visibility = View.VISIBLE
                defaultLayout.visibility = View.GONE
                sharedUserLayout.visibility = View.GONE
                rootLayout.alpha = 0.8F
                itemView.setOnClickListener {
                    val chooseActiveWalletFrag = ChooseActiveWalletFrag(activity)
                    chooseActiveWalletFrag.show(activity.supportFragmentManager, "")
                }
            }
            else {
                itemView.setOnClickListener {
                    goToEditWallet(position)
                }
            }
        }
        else {
            itemView.setOnClickListener {
                goToEditWallet(position)
            }
        }
        dropdownBtn.setOnClickListener {
            goToEditWallet(position)
//            popupWindow.showAsDropDown(it, -220, 20)
        }
        val color = Color.parseColor(walletList[position].wallet.color.toString())
        walletName.text = walletList[position].wallet.name
        walletIcon.setBackgroundResource(
            Extension.getResouceId(context, walletList[position].wallet.symbol)
        )
        val sharedUser = walletList[position].wallet.sharedWith?.values?.filter { it == Enums.InvitationType.ACCEPTED.value}
        numberOfSharedUser.text = context.getString(
            R.string.blank,
            ((sharedUser?.size
                ?: 0) + 1).toString()
                    + " "
                    + context.getString(R.string.users)
        )
        balance.text = context.getString(
            R.string.blank,
            Extension.getCurrencySymbol(
                walletList[position].wallet.currency.toString()
            ) + Extension.toBigDecimal(walletList[position].balance.toString())
        )
        balance.setTextColor(color)
        walletIconChildLayout.setBackgroundColor(color)
        if (walletList[position].isDefault) {
            defaultLayout.visibility = View.VISIBLE
        }

    }

    fun goToEditWallet(position: Int) {
        val intent = Intent(context, EditWallet::class.java)
        val bundle = Bundle()
        bundle.putSerializable("walletList", walletList as Serializable)

        intent.putExtra("bundle", bundle)
        intent.putExtra("position", position)
        val milliSeconds = walletList[position].wallet.createdTime!!.seconds * 1000 +
                walletList[position].wallet.createdTime!!.nanoseconds / 1000000
        intent.putExtra(Enums.Extras.MILLISECOND.value, milliSeconds)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    fun setUpPopUpWindow() {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.wallet_drop_down_layout, null)


        popupWindow = PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
    }

}