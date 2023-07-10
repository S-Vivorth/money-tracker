package io.paraga.moneytrackerdev.utils.helper

import android.content.Context
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class CalendarHelper(private val context: Context, dateValue: String) {
    private var dayInt = 1
    private var monthInt = 1
    private var yearInt = 2000

    init {
        val separatedDate = dateValue.split("-").toTypedArray()
        dayInt = separatedDate[2].replaceFirst("^0*".toRegex(), "").toInt()
        monthInt = separatedDate[1].replaceFirst("^0*".toRegex(), "").toInt()
        yearInt = separatedDate[0].replaceFirst("^0*".toRegex(), "").toInt()
    }

    // normal date
    val day: String
        get() = dayInt.toString().replaceFirst("^0*".toRegex(), "")

    val week: String
        get() = getWeek(context, dayInt, monthInt - 1, yearInt)
    val month: String
        get() {
            val monthArray = listOf<String>(
                Month.JANUARY.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.FEBRUARY.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.MARCH.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.APRIL.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.MAY.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.JUNE.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.JULY.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.AUGUST.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.SEPTEMBER.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.OCTOBER.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.NOVEMBER.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            Month.DECEMBER.getDisplayName(TextStyle.FULL, Extension.getLocale(context)),
            )
            return monthArray[monthInt - 1]
        }
    val year: String
        get() = yearInt.toString()
    private fun getWeek(context: Context, day: Int, month: Int, year: Int): String {
        var weekDay = ""
        val c = Calendar.getInstance()
        c[year, month] = day
        val dayOfWeek = c[Calendar.DAY_OF_WEEK]

        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = daysOfWeek()[2].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = daysOfWeek()[3].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = daysOfWeek()[4].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = daysOfWeek()[5].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = daysOfWeek()[6].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = daysOfWeek()[0].getDisplayName(TextStyle.FULL, Extension.getLocale(context))
        }
        return weekDay
    }

}
