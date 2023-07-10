package io.paraga.moneytrackerdev.models

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.time.LocalDate

@SuppressLint("SimpleDateFormat")


data class ExchangeRate  (
    var updated: String = "",
    var base: String = "",
    var results: JSONObject? = JSONObject(),
    var date: LocalDate = LocalDate.now()
)

