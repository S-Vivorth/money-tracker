package io.paraga.moneytrackerdev.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.ExchangeRate
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.utils.helper.Extension
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//singleton

var auth: FirebaseAuth = FirebaseAuth.getInstance()

var exchangeRate: ExchangeRate = ExchangeRate()

class SplashScreenVM: ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
    fun checkAuth(onSuccess: () -> Unit, onFailed: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null ) {
             onSuccess()
        }
        else{
            onFailed()
        }
    }



    fun getExhangeRate(ctx: Context, onSuccess: () -> Unit) {
        val client = OkHttpClient()
        val request =
            Request.Builder().url(Config().fastForexUrl).get()
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseInstance = response
                if (responseInstance.code == 200) {
                    val json = JSONObject(responseInstance.body!!.string())
                    initExchangeRate(json)
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


    fun getFetchCondition(ctx: Context): Boolean {
        val pref = ctx.getSharedPreferences(Enums.SharePref.EXCHANGE_RATE.value,
            AppCompatActivity.MODE_PRIVATE
        )
        if (pref.getString(Enums.SharePref.EXCHANGE_RATE.value, "") == "") {
            return false
        }
        val json = JSONObject(pref.getString(Enums.SharePref.EXCHANGE_RATE.value,"{}")!!)
        initExchangeRate(json)
        if (exchangeRate.date.month.value < LocalDate.now().month.value
            || exchangeRate.date.year < LocalDate.now().year) {
            return false
        }
        return true
    }


    fun initExchangeRate(json: JSONObject) {
        exchangeRate.base = json.getString("base")
        exchangeRate.updated = json.getString("updated")
        exchangeRate.results = json.getJSONObject("results")
        exchangeRate.date = LocalDate.parse(exchangeRate.updated, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
//    fun getFetchCondition() : Boolean{
//        val rates: ExchangeRate
//        try {
//            rates = getExhangeRate()
//        }
//        catch (ex: Exception) {
//            return false
//        }
//
//        if (rates.date.month < LocalDate.now().month || rates.date.year < LocalDate.now().year ) {
//            return false
//        }
//        return true
//    }


}