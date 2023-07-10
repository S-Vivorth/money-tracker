package io.paraga.moneytrackerdev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Subscription
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag

class PremiumAdapter(
    private val premiumSubFrag: PremiumSubFrag
) :
    RecyclerView.Adapter<PremiumAdapter.ViewHolder>() {

    var selectedPosition: Int = 0
    var subProductsList: ArrayList<Subscription> = ArrayList()
    init {
        subProductsList.add(
            Subscription(
                premiumSubFrag.context?.getString(R.string.yearly_price),
                premiumSubFrag.context?.getString(R.string.with_seven_days_of_free_trial),
                premiumSubFrag.context?.getString(R.string.save_twenty_five_percentage),
            ))
        subProductsList.add(
            Subscription(
                premiumSubFrag.context?.getString(R.string.monthly_price),
                premiumSubFrag.context?.getString(R.string.with_seven_days_of_free_trial),
                premiumSubFrag.context?.getString(R.string.save_twenty_five_percentage),
            ))
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var context: Context
        var price: TextView
        var duration: TextView
        var description: TextView
        var saveLayout: CardView
        init {
            context = view.context
            price = view.findViewById(R.id.price)
            duration = view.findViewById(R.id.duration)
            description = view.findViewById(R.id.description)
            saveLayout = view.findViewById(R.id.saveLayout)

        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.package_cell, parent, false)
        return ViewHolder(view = view)
    }

    @SuppressLint("NotifyDataSetChanged")

    override fun onBindViewHolder(holder: PremiumAdapter.ViewHolder, position: Int) {
        if (position == 1) {
            holder.saveLayout.visibility = View.GONE
            holder.price.text = subProductsList[position].price
            holder.duration.text = holder.context.getString(
                R.string.monthly_duration
            )
        } else {
            holder.price.text = subProductsList[position].price
            holder.duration.text = holder.context.getString(
                R.string.yearly_duration
            )
        }

        holder.itemView.setOnClickListener {
            premiumSubFrag.selectedPackagePosition = position
            if (selectedPosition >= 0) {
                notifyDataSetChanged()
            }
            selectedPosition = position
            notifyDataSetChanged()
        }
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.rounded_subscription_selected)
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.rounded_subscription)
        }
    }

    override fun getItemCount(): Int {
        return subProductsList.size
    }
}
