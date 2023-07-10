package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.views.wallet.EditWallet
import io.paraga.moneytrackerdev.views.wallet.ShareUserFrag

class WalletUserAdapter(val sharedUserList: ArrayList<User>, val walletTrans: WalletTrans,
val shareUserFrag: ShareUserFrag): RecyclerView.Adapter<WalletUserAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userName: TextView
        var userEmail: TextView
        var statusLayout: CardView
        var statusText: TextView
        var removeBtn: ImageView
        var context: Context

        init {
            userName = view.findViewById(R.id.userName)
            userEmail = view.findViewById(R.id.userEmail)
            statusLayout = view.findViewById(R.id.walletStatusLayout)
            statusText = view.findViewById(R.id.walletStatus)
            removeBtn = view.findViewById(R.id.removeBtn)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.share_user_cell, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = sharedUserList[position].displayName.toString()
        holder.userEmail.text = sharedUserList[position].email

        if (walletTrans.wallet.userid == sharedUserList[position].userid) {
            holder.removeBtn.visibility = View.GONE
        }
        else {
            if (walletTrans.wallet.userid != user?.userid) {
                holder.removeBtn.visibility = View.GONE
                holder.statusLayout.visibility = View.GONE
            }
            else {
                if (walletTrans.wallet.sharedWith?.get(sharedUserList[position].userid) == Enums.InvitationType.REJECTED.value) {
                    holder.statusText.text = holder.context.getString(R.string.rejected)
                    holder.statusText.setTextColor(ContextCompat.getColor(holder.context, R.color.primaryRed))
                    holder.statusLayout.setCardBackgroundColor(ContextCompat.getColor(holder.context, R.color.lightSeparatorColor))
                }
                else if (walletTrans.wallet.sharedWith?.get(sharedUserList[position].userid) == Enums.InvitationType.ACCEPTED.value) {
                    holder.statusLayout.visibility = View.GONE
                }
                else if (walletTrans.wallet.sharedWith?.get(sharedUserList[position].userid) == Enums.InvitationType.PENDING.value) {
                    holder.statusText.text = holder.context.getString(R.string.pending)
                    holder.statusLayout.setCardBackgroundColor(ContextCompat.getColor(holder.context, R.color.lightSeparatorColor))
                }
            }
        }

        holder.removeBtn.setOnClickListener {
            DialogHelper.showPrimaryDialog(
                holder.context,
                Enums.DialogType.REMOVE_USER.value,
                onOkayPressed = {
                    walletTrans.wallet.sharedWith?.remove(sharedUserList[position].userid)
                    if (shareUserFrag.activity is EditWallet) {
                        Thread {
                            shareUserFrag.firebaseManager.updateShareUser(walletTrans, onSuccess = {
                            })
                        }.start()

                    }
                    shareUserFrag.walletTrans.value = walletTrans
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return sharedUserList.size
    }
}