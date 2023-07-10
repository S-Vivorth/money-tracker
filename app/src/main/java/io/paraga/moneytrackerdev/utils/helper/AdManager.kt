package io.paraga.moneytrackerdev.utils.helper

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.views.isProUser


class AdManager {
    private var mInterstitialAd: InterstitialAd? = null
    fun loadFullScreenAd(context: Context) {
        if (!isProUser.value!!) {
            val adRequest: AdRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, Config.INTERSTITIAL_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded( interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.d("ad_test", "The ad was onAdLoaded.")
                        mInterstitialAd = interstitialAd
                        interstitialAd.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    Log.d("ad_test", "The ad was dismissed.")
                                    mInterstitialAd = null
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    // Called when fullscreen content failed to show.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    Log.d("ad_test", "The ad failed to show.")
                                    mInterstitialAd = null
                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    mInterstitialAd = null
                                    Log.d("ad_test", "The ad was shown.")
                                }
                            }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        mInterstitialAd = null
                        Log.d("ad_test", "The ad was onAdFailedToLoad.")
                    }
                }
            )
        }
    }
    fun loadBanner(adView: AdView?, context: Context) {
        val adRequest: AdRequest = AdRequest.Builder().build()
        val adSize: AdSize = AdManager().getAdSize(context as Activity)
        adView?.layoutParams?.height = adSize.getHeightInPixels(context)
        adView?.setAdSize(adSize)
        adView?.loadAd(adRequest)
    }
    fun showFullScreenAd(context: Context?) {
        if (!isProUser.value!!) {
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show((context as Activity?)!!)
            }
            else {
                Log.d("failedAds", "")
            }
        }
    }
    fun getAdSize(activity: Activity): AdSize {
        val display: Display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}