package io.paraga.moneytrackerdev.utils.helper

import android.content.Context
import android.content.SharedPreferences
import io.paraga.moneytrackerdev.Enums
import java.util.Locale

class Preferences {
    private var instance: Preferences? = null
    lateinit var pref: SharedPreferences
    private val PREFERENCE_NAME = "Preferences"



    fun getInstance(): Preferences {
        if (instance == null) {
            instance = Preferences()
        }
        return instance as Preferences
    }


    fun addFirstOpenApp(context: Context, isFirstOpenApp: Boolean) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.IS_FIRST_OPEN.value, isFirstOpenApp)
        editor.apply()
    }

    fun getFirstOpenApp(context: Context): Boolean {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getBoolean(Enums.SharePref.IS_FIRST_OPEN.value, true)
    }

    fun addPopUpDate(context: Context, date: String) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Enums.SharePref.POP_UP_DATE.value, date)
        editor.apply()
    }

    fun getPopUpDate(context: Context): String{
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getString(Enums.SharePref.POP_UP_DATE.value, "") ?: ""
    }

    fun setSelectedWalletId(context: Context, selectedWalletId: String) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Enums.SharePref.SELECTED_WALLET_ID.value, selectedWalletId)
        editor.apply()
    }

    fun getSelectedWalletId(context: Context): String {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getString(Enums.SharePref.SELECTED_WALLET_ID.value, "") ?: ""
    }


    fun setDarkTheme(context: Context, isDarkTheme: Boolean) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.THEME.value, isDarkTheme)
        editor.apply()
    }

    fun getDarkTheme(context: Context): Boolean {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getBoolean(Enums.SharePref.THEME.value, false)
    }
    fun setCountImport(context: Context,counter: Int) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putInt(Enums.SharePref.IMPORT_COUNT.value, counter)
        editor.apply()
    }
    fun getCountImport(context: Context): Int {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getInt(Enums.SharePref.IMPORT_COUNT.value, 0)
    }
    fun setCurrentVersion(context: Context,version: Int) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putInt(Enums.SharePref.CURRENT_VERSION.value, version)
        editor.apply()
    }
    fun getCurrentVersion(context: Context): Int {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getInt(Enums.SharePref.CURRENT_VERSION.value, 0)
    }
    //Rate Preference
    fun isAlreadyDisplayedRateDialog(context: Context): Boolean {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getBoolean("AlreadyDisplayedRateDialog", false)
    }
    fun isRated(context: Context): Boolean {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getBoolean("Rated", false)
    }
    fun getDateWhenRateDialogShow(context: Context): Long {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getLong("DateWhenRateDialogShow", 0)
    }
    fun addCounterForRate(context: Context, numAdd: Int) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val i = pref.getInt("CounterForRate", 0)
        val editor = pref.edit()
        editor.putInt("CounterForRate", i + numAdd)
        editor.apply()
    }
    fun getCounterForRate(context: Context): Int {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getInt("CounterForRate", 0)
    }
    fun setCounterForRate(context: Context, l: Int) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putInt("CounterForRate", l)
        editor.apply()
    }
    fun setAlreadyDisplayedRateDialog(context: Context, b: Boolean) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("AlreadyDisplayedRateDialog", b)
        editor.apply()
    }

    fun setRate(context: Context, b: Boolean) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("Rated", b)
        editor.apply()
    }
    fun setDateWhenRateDialogShow(context: Context, l: Long) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putLong("DateWhenRateDialogShow", l)
        editor.apply()
    }

    // Calendar cell height
    fun getCellHeight(context: Context): Int {
        pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return pref.getInt("CellHeight", 0)
    }

    fun setCellHeight(context: Context, cellHeight: Int) {
        pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("CellHeight", cellHeight)
        editor.apply()
    }
    fun setItemInput(context: Context, itemValue: Int) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putInt("ItemInput", itemValue)
        editor.apply()
    }

    fun setAutoLanguage(context: Context, isAutoLanguage: Boolean, languageCode: String) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.AUTO_LANGUAGE.value, isAutoLanguage)
        editor.putString(Enums.SharePref.LANGUAGE_CODE.value, languageCode)
        editor.apply()
    }

    fun getAutoLanguage(context: Context): Boolean {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getBoolean(Enums.SharePref.AUTO_LANGUAGE.value, true)
    }

    fun getLanguage(context: Context): String {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return pref.getString(Enums.SharePref.LANGUAGE_CODE.value, Locale.getDefault().language).toString()
    }

    private fun initPref(context: Context) {
        pref = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
    fun setWeekStart(context: Context, startDay: String){
        initPref(context)
        val editor = pref.edit()
        editor.putString(Enums.SharePref.WEEK_START.value, startDay)
        editor.apply()
    }

    fun getWeekStart(context: Context): String {
        initPref(context)
        return pref.getString(Enums.SharePref.WEEK_START.value, Enums.General.MONDAY.value).toString()
    }

    fun setLatestNumbOfNotif(context: Context, numbOfNotif: Int){
        initPref(context)
        val editor = pref.edit()
        editor.putInt(Enums.SharePref.LATEST_NUMBER_OF_NOTIFICATION.value, numbOfNotif)
        editor.apply()
    }

    fun getLatestNumbOfNotif(context: Context): Int {
        initPref(context)
        return pref.getInt(Enums.SharePref.LATEST_NUMBER_OF_NOTIFICATION.value, 0)
    }

    fun setBudgetViewType(context: Context, viewType: Int) {
        initPref(context)
        val editor = pref.edit()
        editor.putInt(Enums.SharePref.BUDGET_VIEW_TYPE.value, viewType)
        editor.apply()
    }

    fun getBudgetViewType(context: Context): Int {
        initPref(context)
        return pref.getInt(Enums.SharePref.BUDGET_VIEW_TYPE.value, Enums.SpanCount.TWO.value)
    }

    fun setPreviousSubscriptionStatus(context: Context, isSubscribe: Boolean) {
        initPref(context)
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.PREVIOUS_SUBSCRIPTION_STATUS.value, isSubscribe)
        editor.apply()
    }

    fun getPreviousSubscriptionStatus(context: Context): Boolean {
        initPref(context)
        return pref.getBoolean(Enums.SharePref.PREVIOUS_SUBSCRIPTION_STATUS.value, false)
    }

    fun setChosenTwoWalletIds(context: Context, walletIds: MutableSet<String>) {
        initPref(context)
        val editor = pref.edit()
        editor.putStringSet(Enums.SharePref.CHOSEN_TWO_WALLET_IDS.value, walletIds)
        editor.apply()
    }

    fun getChosenTwoWalletIds(context: Context): MutableSet<String>? {
        initPref(context)
        val set = mutableSetOf<String>()
        return pref.getStringSet(Enums.SharePref.CHOSEN_TWO_WALLET_IDS.value,set)
    }

    fun setTwoWalletIdsChooseStatus(context: Context, isChosen: Boolean) {
        initPref(context)
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.TWO_WALLET_IDS_CHOOSE_STATUS.value, isChosen)
        editor.apply()
    }

    fun getTwoWalletIdsChooseStatus(context: Context): Boolean {
        initPref(context)
        return pref.getBoolean(Enums.SharePref.TWO_WALLET_IDS_CHOOSE_STATUS.value, false)
    }

    fun setChosenTwoBudgetIds(context: Context, budgetIds: MutableSet<String>) {
        initPref(context)
        val editor = pref.edit()
        editor.putStringSet(Enums.SharePref.CHOSEN_TWO_BUDGET_IDS.value, budgetIds)
        editor.apply()
    }

    fun getChosenTwoBudgetIds(context: Context): MutableSet<String>? {
        initPref(context)
        val set = mutableSetOf<String>()
        return pref.getStringSet(Enums.SharePref.CHOSEN_TWO_BUDGET_IDS.value,set)
    }

    fun setTwoBudgetIdsChooseStatus(context: Context, isChosen: Boolean) {
        initPref(context)
        val editor = pref.edit()
        editor.putBoolean(Enums.SharePref.TWO_BUDGET_IDS_CHOOSE_STATUS.value, isChosen)
        editor.apply()
    }

    fun getTwoBudgetIdsChooseStatus(context: Context): Boolean {
        initPref(context)
        return pref.getBoolean(Enums.SharePref.TWO_BUDGET_IDS_CHOOSE_STATUS.value, false)
    }


}