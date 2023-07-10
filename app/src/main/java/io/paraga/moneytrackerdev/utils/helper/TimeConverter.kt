package io.paraga.moneytrackerdev.utils.helper

import android.annotation.SuppressLint
import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
@SuppressLint("SimpleDateFormat")
object TimeConverter {
    fun dateWithFormat(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("dd MMM yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("MMMM yyyy")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun dateFormatCalendar(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("dd MMM yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("d")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    //MMM
    fun dateFormatMonth(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    //MMM YYYY
    fun dateFormatMonthYear(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("dd MMM yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("MMMM yyyy")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun dateFormat(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("d")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun formatMonth(dateString: String?, context: Context): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("MMMM", Extension.getLocale(context))
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("MMM")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun dateFormatDay(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("d")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("dd")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun dateFormatCurrentMonth(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("yyyy-MM")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("MMMM yyyy")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
    fun dateFormatCalendarAdd(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date?
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            return ""
        }
        value?.let {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy")
            dateFormatter.timeZone = TimeZone.getDefault()
            return dateFormatter.format(value)
        }
        return ""
    }
}