package io.paraga.moneytrackerdev.utils.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.paraga.moneytrackerdev.R

object PhotoViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun imageUrl(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(url)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_wallet)
            .into(imageView)
    }
}
