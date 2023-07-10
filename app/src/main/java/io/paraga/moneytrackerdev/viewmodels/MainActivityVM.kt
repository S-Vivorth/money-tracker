package io.paraga.moneytrackerdev.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.User
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivityVM: ViewModel() {

    val firestore = FirebaseFirestore.getInstance()



    fun getUser(context: Context, onSuccess: (User) -> Unit, onFailure: (String) -> Unit) {
        firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .document(auth.uid.toString()).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val user = it.result.toObject(User::class.java) as User
                        onSuccess(user)
                    } catch (ex: Exception) {
                        onFailure(context.getString(R.string.something_went_wrong))
                    }
                }
                else{
                    onFailure(context.getString(R.string.something_went_wrong))
                }
            }
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

    fun initExchangeRate(json: JSONObject) {
        exchangeRate.base = json.getString("base")
        exchangeRate.updated = json.getString("updated")
        exchangeRate.results = json.getJSONObject("results")
        exchangeRate.date = LocalDate.parse(exchangeRate.updated, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}