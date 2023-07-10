package io.paraga.moneytrackerdev.utils.helper

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.content.ContextCompat
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Currency
import io.paraga.moneytrackerdev.viewmodels.exchangeRate
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.isDarkTheme
import okhttp3.*
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*



class Extension {
    companion object Extension {
        fun goToHomeScreen(ctx: Context, clearAll: Boolean = false) {
            val intent = Intent(ctx, MainActivity::class.java)

            if (clearAll) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
        }

        fun goToNewActivity(
            ctx: Context, newClass: Class<*>,
            clearAll: Boolean = false,
            clearTop: Boolean = false
        ) {
            val intent = Intent(ctx, newClass)

            if (clearAll) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (clearTop) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            ctx.startActivity(intent)
        }

        fun changeEditTextStroke(
            ctx: Context, hasFocus: Boolean, view: View,
            leftIcon: ImageView,
            rightIcon: ImageView
        ) {
            val backgroundGradient: GradientDrawable = view.background as GradientDrawable
            val value = TypedValue()
            ctx.theme.resolveAttribute(R.attr.primaryText, value, true)

            if (hasFocus) {
                backgroundGradient.setStroke(2, ContextCompat.getColor(ctx, R.color.splashBgColor))
                leftIcon.setColorFilter(
                    ContextCompat.getColor(ctx, R.color.splashBgColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )


                rightIcon.setColorFilter(
                    value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )


            } else {
                ctx.theme.resolveAttribute(R.attr.separatorColor, value, true)
                backgroundGradient.setStroke(
                    2,
                   value.data
                )
                ctx.theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                leftIcon.setColorFilter(
                    value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                rightIcon.setColorFilter(
                    value.data,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        fun changeEditTextState(
            ctx: Context, hasFocus: Boolean, view: View,
            leftIcon: ImageView,
            rightIcon: ImageView,
            color: Int
        ) {
            val backgroundGradient: GradientDrawable = view.background as GradientDrawable
            backgroundGradient.setStroke(2, ContextCompat.getColor(ctx, R.color.splashBgColor))
            leftIcon.setColorFilter(
                ContextCompat.getColor(ctx, R.color.splashBgColor),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            rightIcon.setColorFilter(
                color,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        fun clearEditTextState(
            view: View,
            leftIcon: ImageView,
            rightIcon: ImageView,
            color: Int
        ) {
            val backgroundGradient: GradientDrawable = view.background as GradientDrawable
            leftIcon.setColorFilter(
                color,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            rightIcon.setColorFilter(
                color,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        fun clearEditTextStrokeColor(view: View,
                                             color: Int) {
            val backgroundGradient: GradientDrawable = view.background as GradientDrawable
            backgroundGradient.setStroke(
                2,
                color
            )
        }

        fun changeStroke(ctx: Context, view: View, color: Int, width: Float = 1.5F, isColorInt: Boolean = false) {
            val backgroundGradient: GradientDrawable = view.background as GradientDrawable
            if (!isColorInt) {
                backgroundGradient.setStroke(dpToPx(ctx, width), ContextCompat.getColor(ctx, color))
            }
            else {
                backgroundGradient.setStroke(dpToPx(ctx, width), color)
            }

        }


        fun dpToPx(context: Context, dp: Float): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
        fun pxToDp(context: Context, px: Float): Int {
            return (px / context.resources.displayMetrics.density).toInt()
        }

        fun getResouceId(context: Context, name: String?, isReplace: Boolean = true): Int {
            if (name == "try") {
                return context.resources.getIdentifier(
                    name+"_turkey",
                    "drawable",
                    context.packageName
                )
            }
            if (!isReplace) {

                return context.resources.getIdentifier(
                    name,
                    "drawable",
                    context.packageName
                )
            }
            val resourceId = context.resources.getIdentifier(
                name?.replace("-","_"),
                "drawable",
                context.packageName
            )
            if (resourceId == 0) {
                // icon does not exist
                return context.resources.getIdentifier(
                    "ic_general",
                    "drawable",
                    context.packageName
                )
            }
            else {
                return resourceId
            }


        }

        fun getCurrencySymbol(currencyName: String) : String {

            return try {
                Constants.currencyJson.optJSONObject(currencyName)?.getString("symbol_native")
                    ?: ""
            } catch (ex: Exception) {
                ""
            }

        }
        fun showKeyboard(editText: EditText, isNumberInput: Boolean = false, activity: Activity) {
            if (!isNumberInput) {
                editText.transformationMethod = null
            }
            editText.postDelayed({
                editText.requestFocus()
                val imgr: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }, 1000)


        }

        fun getCurrencyObj(code: String): Currency {
            val jsonObj = Constants.currencyJson.optJSONObject(code)
            val currency = Currency(
                 symbol = jsonObj?.getString("symbol") ?: "",
                 name = jsonObj?.getString("name") ?: "",
                 symbol_native = jsonObj?.getString("symbol_native") ?: "",
                code = jsonObj?.getString("code") ?: ""
            )
            return currency

        }



        private fun getExchangeRate(ctx: Context, onSuccess: () -> Unit) {
            val pref = ctx.getSharedPreferences(Enums.SharePref.EXCHANGE_RATE.value,
                AppCompatActivity.MODE_PRIVATE
            )
            if (exchangeRate.updated == "") {
                val json = JSONObject(pref.getString(Enums.SharePref.EXCHANGE_RATE.value,"{}")!!)
                if (json == {}) {
                    initExchangeRate(ctx, onSuccess = {
                        onSuccess()
                    })
                }
                else {
                    try {
                        initExchangeRateSingleton(json)
                        onSuccess()
                    }
                    catch (exc: java.lang.Exception) {
                        onSuccess()
                    }
                }
            }
            else {
                onSuccess()
            }

        }

        fun initExchangeRateSingleton(json: JSONObject) {
            exchangeRate.base = json.getString("base")
            exchangeRate.updated = json.getString("updated")
            exchangeRate.results = json.getJSONObject("results")
            exchangeRate.date = LocalDate.parse(exchangeRate.updated, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
        fun convertCurrency(firstCurrency: String, secondCurrency: String, amount: Double, ctx: Context): Double {
            var result = 0.0
            getExchangeRate(ctx, onSuccess = {
                try {

                    if (firstCurrency == Enums.Currencies.USD.value
                        && secondCurrency != Enums.Currencies.USD.value) {
                        result = usdToOther(secondCurrency, amount)
                    }

                    if (firstCurrency != Enums.Currencies.USD.value
                        && secondCurrency == Enums.Currencies.USD.value) {
                        result = otherToUSD(firstCurrency, amount)
                    }

                    if (firstCurrency != Enums.Currencies.USD.value
                        && secondCurrency != Enums.Currencies.USD.value) {
                        result = convertOtherToOther(firstCurrency, secondCurrency, amount)
                    }

                    if (firstCurrency == secondCurrency) {
                        result = amount
                    }
                }
                catch (exc: Exception) {
                    Log.d("exchangeRateExc", exc.toString())
                    result = 0.0
                }
            })


            return result
        }

        private fun usdToOther(currency: String, amount: Double): Double {
            val rate = exchangeRate.results?.get(currency) as Double
            val result = amount * rate
            return result
        }

        private fun otherToUSD(currency: String, amount: Double): Double {
            val rate = exchangeRate.results?.get(currency) as Double
            val result = amount / rate
            return result
        }

        private fun convertOtherToOther(firstCurrency: String, secondCurrency: String, amount: Double): Double {
            val toUSD = otherToUSD(firstCurrency, amount)
            val result = usdToOther(secondCurrency, toUSD)
            return result
        }

        fun toBigDecimal(stringDouble: String): String {
            val formatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
            return formatter.format(stringDouble.toDouble())
        }


        fun toBigDecimalNo(stringDouble: String): String {
            return stringDouble.toBigDecimal().toPlainString()
        }
        fun imgUriToBitmap(uri: Uri?, context: Context): Bitmap {
            var imageStream: InputStream? = null
            try {
                imageStream = context.contentResolver.openInputStream(
                    uri!!
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return BitmapFactory.decodeStream(imageStream)
        }

        fun bitmapToImageUri(inContext: Context, inImage: Bitmap): Uri? {
            val path =
                MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null) ?: ""
            return Uri.parse(path)

        }

        fun compressBitmap(image: Bitmap, maxSize: Int): Bitmap {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        fun Context.changeLanguage(): Context {
            val locale = getLocale(this)

            val config = resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            resources.updateConfiguration(config, resources.displayMetrics)
            return createConfigurationContext(config)
        }

        fun getLocale(context: Context): Locale {
            val language = Preferences().getInstance().getLanguage(context)
            val locale: Locale = when(language) {
                "zh-rCN" -> Locale("zh", "CN")
                "zh-rTW" -> Locale("zh", "TW")
                "pt-rBR" -> Locale("pt","BR")
                else -> Locale(language)
            }
            return locale
        }

        fun isAutoLanguage(context: Context): Boolean {
            return Preferences().getInstance().getAutoLanguage(context)
        }

        fun Context.setLanguage(context: Context) {
            if (!isAutoLanguage(context)) {
                changeLanguage()
            }

        }
        private fun initExchangeRate(ctx: Context, onSuccess: () -> Unit) {
            val client = OkHttpClient()
            val request =
                Request.Builder().url(Config().fastForexUrl).get()
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("error",e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseInstance = response
                    if (responseInstance.code == 200) {
                        val json = JSONObject(responseInstance.body!!.string())
                        initExchangeRateSingleton(json)
                        Log.d("date", exchangeRate.date.month.value.toString())
                        val editor =  ctx.getSharedPreferences(Enums.SharePref.EXCHANGE_RATE.value,
                            AppCompatActivity.MODE_PRIVATE
                        ).edit()
                        editor.putString(Enums.SharePref.EXCHANGE_RATE.value, json.toString())
                        editor.apply()
                        onSuccess()
                    }

                }
            })

        }
        fun setColorProgressBar(progressBar: ProgressBar,
                                amountLeftOrOverText: TextView,
                                currencySymbol: String,
                                progress: Int,
                                max: Int,
                                context: Context,
                                progressLayer: Drawable
        ) {


            if ((progress/max.toDouble()) > 1) {
                val overSpendingAmount = progress - max
                amountLeftOrOverText.text = context.getString(
                    R.string.blank,
                    "-"
                            + currencySymbol
                            + io.paraga.moneytrackerdev.utils.helper.Extension.toBigDecimal(
                        overSpendingAmount.toString()
                    ) + " "
                            + context.getString(R.string.over_spending_budget)
                )
                amountLeftOrOverText.setTextColor( ContextCompat.getColor(context, R.color.red))
                progressLayer.setColorFilter(
                    ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            else if ((progress/max.toDouble()) >= 0.8 && (progress/max) <= 1) {
                val amountLeft = max - progress
                amountLeftOrOverText.text = context.getString(
                    R.string.blank,
                    currencySymbol
                            + io.paraga.moneytrackerdev.utils.helper.Extension.toBigDecimal(
                        amountLeft.toString()
                    )
                            + context.getString(R.string.left)
                )
                progressLayer.setColorFilter(
                    ContextCompat.getColor(context, R.color.warningBudgetProgress), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            else {
                val amountLeft = max - progress
                amountLeftOrOverText.text = context.getString(
                    R.string.blank,
                    currencySymbol
                            + io.paraga.moneytrackerdev.utils.helper.Extension.toBigDecimal(
                        amountLeft.toString()
                    )
                            + context.getString(R.string.left)
                )
                progressLayer.setColorFilter(
                    ContextCompat.getColor(context, R.color.normalBudgetProgress), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
        fun startProgressAnimation(progressBar: ProgressBar) {
            ObjectAnimator.ofInt(progressBar, "progress",progressBar.progress,
                progressBar.progress * 100 )
                .setDuration(2000)
                .start()
        }

        private fun changeTheme(isDarkTheme: Boolean, context: Context) {
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Preferences().getInstance().setDarkTheme(context, isDarkTheme)
        }

        fun setTheme(context: Context) {
            isDarkTheme = Preferences().getInstance().getDarkTheme(context)
            changeTheme(isDarkTheme, context)
        }

        fun checkNotificationPermission(context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, permission) -> {
                        Log.d("permission", "Notification is granted")
                    }

                    else -> {
                        Log.d("permission", "Notification is not granted")
                        requestPermissionLauncher.launch(permission)
                    }
                }
            }
        }

        fun getStringResourceByName(context: Context, name: String, isCategory: Boolean = true): String {
            val packageName: String = context.packageName
            val newName: String
            if (isCategory) {
                newName = Constants.defaultCategoryName[name].toString()
            }
            else {
                newName = name
            }
            val resId: Int = context.resources.getIdentifier(newName, "string", packageName)
            try {
                return context.getString(resId)
            }
            catch (exc: Exception) {
                if (isCategory) {
                    return name
                }
                else {
                    return ""
                }
            }
        }
    }
}