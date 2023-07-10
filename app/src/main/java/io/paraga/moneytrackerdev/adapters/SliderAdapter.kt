package io.paraga.moneytrackerdev.adapters

import android.content.Context
import android.media.Image
import android.os.Build
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.viewpager.widget.PagerAdapter
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.utils.helper.Extension
import org.w3c.dom.Text
import kotlin.math.sign

class SliderAdapter(): PagerAdapter() {
    lateinit var img: ImageView
    lateinit var title: TextView
    lateinit var description: TextView
//    lateinit var firstIndicator: CardView
//    lateinit var secondIndicator: CardView
//    lateinit var thirdIndicator: CardView
    private val sliderList = Constants().sliderList
    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }



    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.slider_cell, container, false)
        container.addView(view)
        img = view.findViewById(R.id.sliderImg)
        title = view.findViewById(R.id.sliderTitle)
        description = view.findViewById(R.id.sliderDescription)
//        firstIndicator = view.findViewById(R.id.firstIndicator)
//        secondIndicator = view.findViewById(R.id.secondIndicator)
//        thirdIndicator = view.findViewById(R.id.thirdIndicator)
        img.setImageResource(sliderList[position].img)
        title.text = sliderList[position].title
        description.text = sliderList[position].description
        if (position == 0) {
//            changeSelectedState(firstIndicator, container.context)
        }
        else if (position == 1){
//            changeSelectedState(secondIndicator, container.context)
        }
        else {
//            changeSelectedState(thirdIndicator, container.context)
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    fun changeSelectedState(cardView: CardView, context: Context) {
        cardView.layoutParams.width = Extension.dpToPx(context, 10F)
        cardView.layoutParams.height = Extension.dpToPx(context, 10F)
        cardView.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.default_og
            ))
    }
}