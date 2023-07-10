package io.paraga.moneytrackerdev.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants.Companion.ACTION_EXPENSE
import io.paraga.moneytrackerdev.constants.Constants.Companion.ACTION_INCOME
import io.paraga.moneytrackerdev.constants.Constants.Companion.ACTION_MAIN
import io.paraga.moneytrackerdev.constants.Constants.Companion.ACTION_UPDATE_WIDGET
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.TimeConverter
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.selectedWalletId
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.ConcurrentModificationException


/**
 * Implementation of App Widget functionality.
 */
class SmallWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    fun getWallet(onSuccess: () -> Unit, context: Context) {
        if (walletList.value?.size == 0) {
            FirebaseManager(context = context).initWalletList {
                onSuccess()
            }
        } else {
            onSuccess()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_UPDATE_WIDGET == intent.action) {
            Log.d("AppWidgetSmall", "ACTION_UPDATE_WIDGET")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)

            val remoteViews = RemoteViews(context.packageName, R.layout.small_widget)
            setDataValue(context, remoteViews)
            appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, remoteViews)
        }
        else if (ACTION_INCOME == intent.action) {
            val intent1 = Intent()
            intent1.setClassName(context.packageName, AddTrans::class.java.name).apply {
                putExtra("widget",0)
            }
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            getWallet(onSuccess = {
                context.startActivity(intent1)
            },context)
        } else if (ACTION_EXPENSE == intent.action) {
            val intent1 = Intent()
            intent1.setClassName(context.packageName, AddTrans::class.java.name).apply {
                putExtra("widget",1)
            }
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            getWallet(onSuccess = {
                context.startActivity(intent1)
            },context)
        } else if (ACTION_MAIN == intent.action) {
            val intent1 = Intent()
            intent1.setClassName(context.packageName, MainActivity::class.java.name)
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent1)
        }
        super.onReceive(context, intent)
    }

}


@SuppressLint("UnspecifiedImmutableFlag")
fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent {
    val intent = Intent(context, SmallWidget::class.java)
    intent.action = action
    val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(
            context,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    } else {
        PendingIntent.getBroadcast(
            context,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    return pendingIntent
}

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d("AppWidgetSmall", "ACTION_UPDATE_APP_WIDGET")
        val remoteViews = RemoteViews(context.packageName, R.layout.small_widget)
        if (walletList.value?.size == 0) {
            FirebaseManager(context).initWalletList {
                FirebaseManager(context).initTransaction(onSuccess = {
                    getNestedTransList(context, remoteViews)
                    val currentDisplayedMonth = YearMonth.now()
                    // Today
                    val widgetTextMonth = context.getString(
                        R.string.blank, currentDisplayedMonth.month.getDisplayName(
                            TextStyle.FULL, Locale.getDefault()
                        )
                    )
                    val widgetTextYear =
                        context.getString(R.string.blank, currentDisplayedMonth.year.toString())
                    // Construct the RemoteViews object
                    remoteViews.setTextViewText(R.id.text_widget_month, widgetTextMonth)
                    remoteViews.setTextViewText(R.id.text_widget_year, widgetTextYear)
//    //setValue
//    setDataValue(context,remoteViews)

                    // Click on main layout and goto activity
                    initListener(context, remoteViews)
                    // Instruct the widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                },
                    onFailure = {
                        Toast(context).showCustomToast(it, context as Activity)
                    }
                )
            }
        }
    }
    private fun getNestedTransList(context: Context,remoteViews: RemoteViews) {
        val currentDisplayedMonth: YearMonth? = YearMonth.now()
        currentMonthYear.value = context.getString(R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
            TextStyle.FULL, Locale.getDefault())
                + " "
                + currentDisplayedMonth?.year.toString())
        FirebaseManager(context = context).getNestedTransList(
            currentMonthYear.value.toString(), selectedWalletId,
                onSuccess = {
                    setDataValue(context, remoteViews)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val thisAppWidgetComponentName = ComponentName(context.packageName, context.javaClass.name)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
                    appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, remoteViews)
                })
        }

    private fun initListener(context: Context, views: RemoteViews) {
        views.setOnClickPendingIntent(
            R.id.image_income,
            getPendingSelfIntent(
                context,
                ACTION_INCOME
            )
        )
        views.setOnClickPendingIntent(
            R.id.image_expense,
            getPendingSelfIntent(
                context,
                ACTION_EXPENSE
            )
        )
        views.setOnClickPendingIntent(
            R.id.midWidget,
            getPendingSelfIntent(
                context,
                ACTION_MAIN
            )
        )

    }

    private fun setDataValue(context: Context, views: RemoteViews) {
        try {
            // value income expense total
            val allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet()
            var allTotalAmount = 0.0
            var totalIncome = 0.0
            var totalExpense = 0.0
            var convertedAmount: Double
            var convertedIncome: Double
            var convertedExpense: Double
            var currentDisplayedMonth = YearMonth.now()
            val currency: String = if (selectedWallet == allWallet) {
                user?.defaultCurrency.toString()
            } else {
                (selectedWallet.currency.toString())
            }
            nestedTransList.value?.filter {
                TimeConverter.dateFormatMonthYear(it.date) == (currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                ) + " " + currentDisplayedMonth?.year.toString())
            }?.forEach { date ->
                convertedIncome = String.format(
                    "%.2f",
                    date.totalIncome.toDouble().let { it1 ->
                        Extension.convertCurrency(
                            currency,
                            currency,
                            it1,
                            context
                        )
                    }
                ).replace(",",".").toDouble()
                convertedExpense = String.format(
                    "%.2f",
                    date.totalExpense.toDouble().let { it1 ->
                        Extension.convertCurrency(
                            currency,
                            currency,
                            it1,
                            context
                        )
                    }
                ).replace(",",".").toDouble()
                convertedAmount = String.format(
                    "%.2f",
                    date.totalAmount.toDouble().let { it1 ->
                        Extension.convertCurrency(
                            currency,
                            currency,
                            it1,
                            context
                        )
                    }
                ).replace(",",".").toDouble()
                allTotalAmount += convertedAmount
                totalIncome += convertedIncome
                totalExpense += convertedExpense
            }
            val valueIncome = context.getString(
                R.string.blank,
                Extension.getCurrencySymbol(
                    selectedWallet.currency.toString()
                )
                        + Extension.toBigDecimal(totalIncome.toString())
            )
            views.setTextViewText(R.id.text_value_income, valueIncome)

            val valueExpense = context.getString(
                R.string.blank,
                Extension.getCurrencySymbol(
                    selectedWallet.currency.toString()
                )
                        + Extension.toBigDecimal(totalExpense.toString())
            )
            views.setTextViewText(R.id.text_value_expense, valueExpense)
            val valueBalance = context.getString(
                R.string.blank,
                Extension.getCurrencySymbol(
                    selectedWallet.currency.toString()
                )
                        + Extension.toBigDecimal(allTotalAmount.toString())
            )
            views.setTextViewText(R.id.text_value_balance, valueBalance)

            // date
            currentDisplayedMonth = YearMonth.now()
            // Today
            val widgetTextMonth = context.getString(
                R.string.blank, currentDisplayedMonth.month.getDisplayName(
                    TextStyle.FULL, Locale.getDefault()
                )
            )
            val widgetTextYear =
                context.getString(R.string.blank, currentDisplayedMonth.year.toString())
            // Construct the RemoteViews object
            views.setTextViewText(R.id.text_widget_month, widgetTextMonth)
            views.setTextViewText(R.id.text_widget_year, widgetTextYear)
        }
        catch (exc: ConcurrentModificationException) {
            Thread.currentThread().interrupt()
        }


    }


