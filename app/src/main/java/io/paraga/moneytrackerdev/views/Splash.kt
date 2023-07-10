package io.paraga.moneytrackerdev.views

import android.content.Context
import android.content.ContextWrapper
import io.paraga.moneytrackerdev.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.databinding.ActivitySplashBinding
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.viewmodels.SplashScreenVM
import io.paraga.moneytrackerdev.views.auth.LoginOption
import java.util.*

var isProUser: MutableLiveData<Boolean> = MutableLiveData(false)
var isDarkTheme = false
class Splash : AppCompatActivity() {
    val splashScreenVM: SplashScreenVM = SplashScreenVM()
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        disableDarkMode()
        setTheme()
        changeStatusBarColor()

        // fetch exchange rate
        val fetchCondition = splashScreenVM.getFetchCondition(this)

        if (!fetchCondition) {
            splashScreenVM.getExhangeRate(this) {
            }
        }
        else {
//            Toast.makeText(this, "Already fetched the exchange rate", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initSubscriptionStatus() {
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onError(error: PurchasesError) {
            }

            override fun onReceived(customerInfo: CustomerInfo) {
                if (customerInfo.entitlements[Config.ENTITLEMENT_ID]?.isActive == true) {
                    // Grant user "pro" access
                    isProUser.value = true
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        // hide navigation bar
        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions


        Timer().schedule(object : TimerTask() {
            override fun run() {
                splashScreenVM.checkAuth(onSuccess = {
                    goToHomeScreen()
                    finish()
                }, onFailed = {
                    val intent = Intent(this@Splash, LoginOption::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                })
            }
        }, 2000)

    }
    private fun changeTheme(isDarkTheme: Boolean ) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        Preferences().getInstance().setDarkTheme(this, isDarkTheme)
    }

    private fun setTheme() {
        isDarkTheme = Preferences().getInstance().getDarkTheme(this)
        changeTheme(isDarkTheme)
    }
    private fun changeStatusBarColor(){
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.splashBgColor))
    }

    private fun goToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}