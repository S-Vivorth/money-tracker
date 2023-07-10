package io.paraga.moneytrackerdev.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.material.color.DynamicColors
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.core.daysOfWeek
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.constants.ConstantsHelper
import io.paraga.moneytrackerdev.databinding.ActivityMainBinding
import io.paraga.moneytrackerdev.models.DayModel
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.services.LocalNotificationService
import io.paraga.moneytrackerdev.utils.helper.*
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.setBackGround
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.MainActivityVM
import io.paraga.moneytrackerdev.viewmodels.auth
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM
import io.paraga.moneytrackerdev.viewmodels.budget.BudgetVM
import io.paraga.moneytrackerdev.viewmodels.wallet.WalletVM
import io.paraga.moneytrackerdev.views.account.Profile
import io.paraga.moneytrackerdev.views.auth.LoginOption
import io.paraga.moneytrackerdev.views.bottomSheet.BottomSheetStartDateFragment
import io.paraga.moneytrackerdev.views.bottomSheet.BottomSheetStartDateFragment.Companion.TAG
import io.paraga.moneytrackerdev.views.budget.BudgetFrag
import io.paraga.moneytrackerdev.views.calendar.fragment.CalendarFrag
import io.paraga.moneytrackerdev.views.category.Category
import io.paraga.moneytrackerdev.views.currency.ChooseCurrencyFrag
import io.paraga.moneytrackerdev.views.importFile.activity.ImportDetailActivity
import io.paraga.moneytrackerdev.views.language.ChooseLanguageFrag
import io.paraga.moneytrackerdev.views.notification.Notification
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import io.paraga.moneytrackerdev.views.search.activity.SearchActivity
import io.paraga.moneytrackerdev.views.statistics.fragment.StatisticsFrag
import io.paraga.moneytrackerdev.views.transaction.AddTrans
import io.paraga.moneytrackerdev.views.transaction.ChooseWalletFrag
import io.paraga.moneytrackerdev.views.transaction.TransactionFrag
import io.paraga.moneytrackerdev.views.wallet.CreateWallet
import io.paraga.moneytrackerdev.views.wallet.Wallet
import io.paraga.moneytrackerdev.widget.SmallWidget
import kotlinx.coroutines.selects.select
import okhttp3.internal.concurrent.Task

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.ConcurrentModificationException
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.hypot

var currentMonthYear: MutableLiveData<String> = MutableLiveData("")
var selectedWalletId: String = ""
var adManager = AdManager()
var isThemeInitialized = false
var isThemeValueInitialized = false
var isThemeInitializeCounter = 0
var currentFragment = Enums.FragmentType.TRANS.value
var currentDisplayedMonth: YearMonth? = YearMonth.now()
var isSwitchTheme = false
class MainActivity() : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var filePicker: ActivityResultLauncher<Intent>? = null
    var transFrag =  TransactionFrag()
    var calendarFrag = CalendarFrag()
    var budgetFrag = BudgetFrag()
    var statisticFrag = StatisticsFrag()
    var previousFrag: Fragment? = null
    var getTransListThread: Thread? = null
    var calculateDataThread: Thread? = null
    companion object {
        init {
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
            )
        }

        private const val PERMISSION_REQUEST_MEMORY_ACCESS = 0
        private var fileType = ""
        private const val extensionXLS = "XLS"
        private const val extensionXLXS = "XLXS"
    }

    private val mainActivityVM = MainActivityVM()
    private val authVM = AuthVM()
    val fragManager: FragmentManager = supportFragmentManager
    val firebaseManager = FirebaseManager(this)
    var allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet(
        name = "All Wallet",
        symbol = "globe"
    )
    var currentCurrencyName: String = ""
    var isRefresh = false
    var isStatic = false
    private val today = LocalDate.now()
    var transType = Enums.TransTypes.ALL.value
    val value = TypedValue()
    private val defaultImportCounter = 3
    private var textCounter = 0
    var spanCount = Enums.SpanCount.TWO.value
    private val mList: ArrayList<String> = arrayListOf()
    private val notificationManager: NotificationManager = NotificationManager()
    private var notificationActivityLaucher: ActivityResultLauncher<Intent>? = null
    private var walletVM = WalletVM()
    private var budgetVM = BudgetVM()

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DynamicColors.applyToActivitiesIfAvailable(application)
        LocalNotificationService.createNotificationChannel(this)
        LocalNotificationService.initNoUseAppNotification(this)
        NotificationManagerCompat.from(this)
            .cancel(intent.getIntExtra(Enums.Extras.NOTIFICATION_ID.value, 0))
//        Preferences().getInstance().setAutoLanguage(applicationContext, false, languageCode = "es")
//        Log.d("defaultLanguage", Locale.getDefault().language)
//        Extension.changeLanguage(applicationContext)
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        if (info.versionCode != Preferences().getCurrentVersion(this)) {
            Preferences().setCurrentVersion(this,info.versionCode)
            resources.getStringArray(R.array.what_new).map {
                mList.add(it)
            }
            DialogHelper.showWhatNewDialog(context = this,mList)
        }
        filePicker = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent1 = result.data
                val uri = intent1!!.data
                startActivity(Intent(this, ImportDetailActivity::class.java).apply {
                    putExtra("uri", uri.toString())
                })
            }
        }
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("permission", "Notification permission is granted after request")
            } else {
                Log.d("permission", "Notification permission is not granted after request")
            }
        }
        Extension.checkNotificationPermission(
            this,
            requestPermissionLauncher
        )

        changeSelectedWalletView(selectedWallet)
        goneViewNavigation()
        initSwitchThemeValue()
        checkPopupCondition()

        // in Activity's onCreate() for instance
        val window: Window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // fetch exchange rate
        val fetchCondition = mainActivityVM.getFetchCondition(this)
        if (!fetchCondition) {
            Thread {
                kotlin.run {
                    mainActivityVM.getExhangeRate(this) {
                    }
                }
            }.start()

        }
//        initUser()
//        Thread {
//            kotlin.run {
//                getCategoryList()
//            }
//        }.start()
        setDataNavigation()
        // set layout above system navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { v, insets ->
            val systemBarInsets =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBarInsets.bottom)
            insets
        }

        Thread {
            kotlin.run {
                initSubscriptionStatus()
                setAd()
                if (user == null) {
                    firebaseManager.initUser {
                        getData()
                        notificationManager.getCurrentToken()
//                        notificationManager.sendNotification(
//                            user,
//                            "From Android",
//                            "No message",
//                            "ic"
//                        )
                        firebaseManager.getNotificationList {
                            runOnUiThread {
                                kotlin.run {
                                    setNotificationNumber()
                                }
                            }
                        }
                    }
                }
                else {
                    getData()
                    notificationManager.firebaseManager = firebaseManager
                    notificationManager.getCurrentToken()
                    firebaseManager.getNotificationList {
                        runOnUiThread {
                            kotlin.run {
                                setNotificationNumber()
                            }
                        }
                    }
                }
            }
        }.start()

        notificationList.observeForever {
            setNotificationNumber()
        }

//         set default fragment
//        if (savedInstanceState == null) {
//            switchFrag(transFrag)
//            binding.trans.setBackgroundResource(R.drawable.rounded_bottombar_item)
//            binding.trans.children.forEach { child ->
//                if (child is ImageView) {
//                    child.setColorFilter(
//                        ContextCompat.getColor(this, R.color.default_og),
//                        android.graphics.PorterDuff.Mode.SRC_IN
//                    )
//                } else if (child is TextView) {
//                    child.setTextColor(getColor(R.color.default_og))
//                }
//            }
//        }

        isProUser.observeForever {
            checkProUser()
            checkProUserImport()
        }
        currentMonthYear.observeForever {
            goToNextOrPrevYear(false)
        }
        setBudgetViewType()
        setCurrentDate()
        initListener()
        binding.trans.performClick()
        setSelectedFrag()
    }

    private fun getData(isRefresh: Boolean = false) {
        if (walletList.value?.size == 0 || isRefresh) {
            firebaseManager.initWalletList {
                walletVM.chooseTwoWallets(this)
                firebaseManager.initBudget(onSuccess = {
                    budgetVM.chooseTwoBudgets(this)
                },
                    onFailure = {})
                getCategoryList(onSuccess = {
                    if (nestedTransList.value?.size == 0 && FirebaseAuth.getInstance().currentUser != null) {
                        firebaseManager.initTransaction(onSuccess = {
                            currentMonthYear.postValue(getString(
                                R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                                    TextStyle.FULL, Locale.getDefault()
                                )
                                        + " "
                                        + currentDisplayedMonth?.year.toString()
                            ))
                            allWallet.currency = user?.defaultCurrency
                            runOnUiThread {
                                kotlin.run {
                                    binding.mainCurrency.text = user?.defaultCurrency
                                    binding.progressbar.visibility = View.GONE
                                }
                            }
                            observeSelectedWalletChange()
                        },
                            onFailure = {
                                runOnUiThread {
                                    kotlin.run {
                                        binding.progressbar.visibility = View.GONE
                                        Toast(this).showCustomToast(it, this)
                                    }
                                }
                            }
                        )
                    } else {
                        runOnUiThread {
                            kotlin.run {
                                binding.mainCurrency.text = user?.defaultCurrency
                            }
                        }
                        calculateData()
                    }
                },
                onFailure = {
                })
            }
        }
        else {
            calculateData()
            runOnUiThread {
                kotlin.run {
                    binding.progressbar.visibility = View.GONE
                }
            }
        }
    }
    private fun checkProUserImport() {
        val counter = Preferences().getInstance().getCountImport(this)
        if (isProUser.value!!) {
            binding.cardImport.visibility = View.GONE
        } else {
            binding.cardImport.visibility = View.VISIBLE
            textCounter = defaultImportCounter - counter
            if (textCounter >= 0 ) {
                binding.textCount.text = textCounter.toString()
            } else if (textCounter < 0) {
                binding.textCount.text = "0"
            }
        }
    }

    private fun setNotificationNumber(isClicked: Boolean? = false) {
        if (isClicked == true) {
            Preferences().getInstance().setLatestNumbOfNotif(this, notificationList.value?.size ?: 0)
        }
        val latestNumbOfNotif = Preferences().getInstance().getLatestNumbOfNotif(this)
        if (latestNumbOfNotif != notificationList.value?.size && ((notificationList.value?.size ?:0) - latestNumbOfNotif) > 0)  {
            binding.numberOfNotification.visibility = View.VISIBLE
            binding.numberOfNotification.text =  ((notificationList.value?.size ?:0) - latestNumbOfNotif).toString()
        }
        else {
            binding.numberOfNotification.visibility = View.GONE
        }

    }

    fun showSwitchThemeAnimation() {
        val w = binding.drawer.measuredWidth
        val h = binding.drawer.measuredHeight

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.drawer.draw(canvas)


        val finalRadius = hypot(w.toFloat(), h.toFloat())


        val anim = ViewAnimationUtils.createCircularReveal(
            binding.drawer,
            w / 2,
            h / 2,
            0f,
            finalRadius.toFloat()
        )
        anim.duration = 1000L

        anim.start()


    }

    private fun setSelectedFrag() {
        when (currentFragment) {
            Enums.FragmentType.TRANS.value -> setSelected(binding.trans)
            Enums.FragmentType.CALENDAR.value -> setSelected(binding.calendar)
            Enums.FragmentType.BUDGET.value -> setSelected(binding.budget)
            Enums.FragmentType.STATISTICS.value -> setSelected(binding.statistics)
        }
    }

    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            Snackbar.make(
                binding.relate, R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE
            ).setAction("OK") { requestStoragePermission() }
                .show()
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_MEMORY_ACCESS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker()
            } else {
                Snackbar.make(
                    binding.relate, R.string.storage_access_denied,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun chooseFile() {
        try {
            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE)

            if (fileType == extensionXLS) fileIntent.type =
                "application/vnd.ms-excel" else fileIntent.type =
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            filePicker?.launch(fileIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_MEMORY_ACCESS
            )
        } else {
            Snackbar.make(binding.relate, R.string.storage_unavailable, Snackbar.LENGTH_SHORT)
                .show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_MEMORY_ACCESS
            )
        }
    }

    private fun openFilePicker() {
        try {
//            if (checkPermission()) {
            chooseFile()
//            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setCurrentDate() {
        binding.textCurrentMonth.text = TimeConverter.dateFormat(today.toString())
    }

    private fun goneViewNavigation() {
        binding.datePickerNavigation.imageArrowBack.visibility = View.INVISIBLE
        binding.datePickerNavigation.imageArrowNext.visibility = View.INVISIBLE
    }

    private fun showViewNavigation() {
        binding.datePickerNavigation.imageArrowBack.visibility = View.VISIBLE
        binding.datePickerNavigation.imageArrowNext.visibility = View.VISIBLE
    }

    private fun changeProUserView() {
        binding.upgradeText.visibility = View.GONE
        binding.upgradeBtn.visibility = View.GONE
        binding.proTextLayout.visibility = View.VISIBLE
        binding.proLayout.layoutParams.height = Extension.dpToPx(this, 90F)
        theme.resolveAttribute(R.attr.bgColor, value, true)
        binding.proLayoutBg.setBackgroundColor(value.data)
        binding.proLayoutSeparator.visibility = View.VISIBLE
//        binding.proLayout.visibility = View.GONE
    }

    private fun checkProUser() {
        if (isProUser.value!!) {
//            Toast(this).showCustomToast("You are signed in as a Pro user.",this)
            changeProUserView()

        }
        else {
//            Toast(this).showCustomToast("Not a pro user",this)
        }
    }

    private fun initUser() {
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                currentUser = it.currentUser!!
                mainActivityVM.getUser(context = this, onSuccess = {
                    user = it
                    allWallet = io.paraga.moneytrackerdev.models.Wallet(
                        name = "All Wallet",
                        symbol = "globe",
                        currency = user?.defaultCurrency
                    )
                },
                    onFailure = {
                        goToLogin()
                    })
            }
        }
    }

    private val mListener = object : OnClickedListener<DayModel> {
        override fun resultBack(day: DayModel) {
            super.resultBack(day)
            if (isRefresh) {
                switchFrag(calendarFrag)
            }
            binding.textDate.text = day.day
        }
    }


    fun observeSelectedWalletChange() {
        walletList.observeForever {
            var counter = 0
            walletList.value?.forEach {
                if (selectedWalletId == it.walletId) {
                    selectedWallet = it.wallet
                    runOnUiThread {
                        kotlin.run {
                            changeSelectedWalletView(selectedWallet)
                        }
                    }
                    getNestedTransList()
                    counter += 1
                    return@forEach
                }
            }
            if (counter == 0) {
                selectedWallet = allWallet
                selectedWalletId = ""
                runOnUiThread {
                    kotlin.run {
                        changeSelectedWalletView(selectedWallet)
                    }
                }
                getNestedTransList()
            }
        }
    }

    fun getWallet(onSuccess: () -> Unit) {

        if (walletList.value?.size == 0) {
            firebaseManager.initWalletList {
                onSuccess()
            }
        } else {
            onSuccess()
        }

    }

    fun startDate(): String {
        val mondayFull =
            daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext))
        val sundayFull =
            daysOfWeek()[0].getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext))
        return if (binding.textDate.text.toString() == mondayFull) {
            mondayFull
        } else {
            sundayFull
        }
    }

    fun setDataNavigation() {
        binding.datePickerNavigation.textSelectDate.text = getString(
            R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                TextStyle.FULL, Extension.getLocale(applicationContext)
            )
        )
        binding.textJan.text =
            Month.JANUARY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textFeb.text =
            Month.FEBRUARY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textMar.text =
            Month.MARCH.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textApr.text =
            Month.APRIL.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textMay.text =
            Month.MAY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textJun.text =
            Month.JUNE.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textJul.text =
            Month.JULY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textAug.text =
            Month.AUGUST.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textSep.text =
            Month.SEPTEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textOct.text =
            Month.OCTOBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textNov.text =
            Month.NOVEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textDec.text =
            Month.DECEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))

        binding.textSelectDateNavigation.text =
            getString(R.string.blank, currentDisplayedMonth?.year.toString())
        when (getString(
            R.string.blank,
            binding.datePickerNavigation.textSelectDate.text.toString()
        )) {
            Month.JANUARY.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = true)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.FEBRUARY.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = true)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.MARCH.getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext)) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = true)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.APRIL.getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext)) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = true)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.MAY.getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext)) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = true)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.JUNE.getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext)) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = true)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.JULY.getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext)) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = true)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.AUGUST.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = true)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.SEPTEMBER.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = true)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.OCTOBER.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = true)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.NOVEMBER.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = true)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            }
            Month.DECEMBER.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            ) -> {
                CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
                CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
                CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
                CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
                CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
                CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
                CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
                CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
                CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
                CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
                CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
                CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = true)
            }
        }
    }

    fun goneDatePicker() {
        binding.datePicker.visibility = View.GONE
        selectMonthNavigation()
    }


    private fun showDatePicker() {
        binding.datePicker.visibility = View.VISIBLE
    }

    @SuppressLint("SuspiciousIndentation", "ClickableViewAccessibility")
    private fun initListener() {
        notificationActivityLaucher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                changeSelectedWalletView(selectedWallet)
            }
        }
        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.bgButton))
        transFrag.mainActivity = this
        val nightModeFlags: Int =
            resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#0000001f"))
            binding.proIcon.setColorFilter(Color.BLACK)
            binding.upgradeBtnText.setTextColor(Color.BLACK)
        } else {
            binding.proIcon.setColorFilter(Color.WHITE)
            binding.upgradeBtnText.setTextColor(Color.WHITE)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            calculateDataThread?.interrupt()
            getData(isRefresh = true)
            binding.swipeRefreshLayout.isRefreshing = false
            Log.d("refreshed", "fired")
        }

        initWeekStart()

        val navigationAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val navigationAnimFadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        binding.add.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    //view.performClick()
                    binding.addIcon.startAnimation(scaleDown)
                    Extension.goToNewActivity(this, AddTrans::class.java)
                }
                MotionEvent.ACTION_UP -> {
                    binding.addIcon.startAnimation(scaleUp)
                }
            }
            return@OnTouchListener false
        })
        binding.upgradeBtn.setOnClickListener {
            binding.drawer.closeDrawers()
            val premiumSubFrag = PremiumSubFrag()
            premiumSubFrag.show(supportFragmentManager, "")

        }
        binding.mainCurrencyLayout.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.mainCurrencyLayout.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.mainCurrencyLayout.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    currentCurrencyName = getString(
                        R.string.blank,
                        user?.defaultCurrency + " - " +
                                Extension.getCurrencyObj(user?.defaultCurrency.toString()).name
                    )
                    val chooseCurrencyFrag = ChooseCurrencyFrag()
                    chooseCurrencyFrag.currentCurrencyName = currentCurrencyName
                    chooseCurrencyFrag.show(supportFragmentManager, "")
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.mainCurrencyLayout.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }

        binding.categoryLayout.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener onTouch(view as CardView, motionEvent, Category::class.java)
        }

        binding.accountLayout.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener onTouch(view as CardView, motionEvent, Profile::class.java)
        }

        binding.walletLayout.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener onTouch(view as CardView, motionEvent, Wallet::class.java)
        }

        binding.notificationLayout.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener onTouch(view as CardView, motionEvent, Notification::class.java)
        }

        binding.drawerBtn.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        binding.changeLanguageLayout.setOnTouchListener { view, motionEvent ->

            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.changeLanguageLayout.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.changeLanguageLayout.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    val chooseLanguageFrag = ChooseLanguageFrag()
                    chooseLanguageFrag.mainActivity = this@MainActivity
                    chooseLanguageFrag.show(supportFragmentManager, "")
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.changeLanguageLayout.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }

        binding.layoutImport.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.layoutImport.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.layoutImport.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    //Action open dialog
                    fileType = extensionXLXS
                    if (isProUser.value!!) {
                        openFilePicker()
                    } else {
                        if (textCounter == 0) {
                            //show premium screen
                            showPremiumSubFrag()
                        } else if (textCounter > 0){
                            openFilePicker()
                        }
                    }
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.layoutImport.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }

        binding.layoutReview.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.layoutReview.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.layoutReview.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    reviewApp()
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.layoutReview.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }


        binding.layoutShare.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.layoutShare.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.layoutShare.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    shareApp()
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.layoutShare.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }


        binding.layoutPrivacy.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.layoutPrivacy.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.layoutPrivacy.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    binding.drawer.closeDrawers()
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(ConstantsHelper.KeyUri.PRIVACY_LINK)
                    startActivity(openURL)
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.layoutPrivacy.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }

        binding.layoutAbout.setOnClickListener {
            binding.drawer.closeDrawers()
        }
        binding.changeWeekStart.setOnTouchListener { _, motionEvent ->
            theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.changeWeekStart.setCardBackgroundColor(
                        value.data
                    )

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.changeWeekStart.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    BottomSheetStartDateFragment(
                        listener = mListener,
                        binding.textDate.text.toString()
                    ).apply {
                        show(supportFragmentManager, TAG)
                    }
                    binding.drawer.closeDrawers()
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                else -> {
                    binding.changeWeekStart.setCardBackgroundColor(
                        Color.TRANSPARENT
                    )
                    return@setOnTouchListener false
                }
            }
        }
        binding.trans.setOnClickListener {
            isRefresh = false
            isStatic = false
            currentFragment = Enums.FragmentType.TRANS.value
            switchFrag(transFrag)
            transFrag.showLayout()
            setSelected(it)
            if (binding.datePicker.visibility == View.VISIBLE) {
                goneDatePicker()
            }
        }
        setSelected(binding.trans)
        binding.calendar.setOnClickListener {
            isRefresh = true
            isStatic = false
            currentFragment = Enums.FragmentType.CALENDAR.value
            if (calendarFrag.isAdded) {
                calendarFrag.showLayout()
            }
            switchFrag(calendarFrag)
            setSelected(it)
            if (binding.datePicker.visibility == View.VISIBLE) {
                goneDatePicker()
            }
        }
        binding.budget.setOnClickListener {
            isRefresh = false
            isStatic = false
            currentFragment = Enums.FragmentType.BUDGET.value
            if (budgetFrag.isAdded) {
                budgetFrag.hideOrShowViews()
            }
            switchFrag(budgetFrag)
            setSelected(it)
            if (binding.datePicker.visibility == View.VISIBLE) {
                goneDatePicker()
            }
        }
        binding.statistics.setOnClickListener {
            isRefresh = false
            isStatic = true
            currentFragment = Enums.FragmentType.STATISTICS.value
            if (statisticFrag.isAdded) {
                statisticFrag.hideLayout()
            }
            switchFrag(statisticFrag)
            setSelected(it)
            if (binding.datePicker.visibility == View.VISIBLE) {
                goneDatePicker()
            }
        }
        binding.datePickerNavigation.textSelectDate.setOnClickListener {
            binding.datePicker.startAnimation(navigationAnimFadeOut)
            goneDatePicker()
        }

        //ChooseWallet
        binding.chooseWalletLayout.setOnClickListener {
            val chooseWalletFrag = ChooseWalletFrag()
            chooseWalletFrag.mainActivity = this
            chooseWalletFrag.show(supportFragmentManager, "")
            calculateData()
        }
        binding.layoutHeader.textSelectDate.setOnClickListener {
            binding.datePicker.startAnimation(navigationAnimFadeIn)
            showDatePicker()
        }
        binding.layoutTran.setOnClickListener {
            binding.datePicker.startAnimation(navigationAnimFadeOut)
            goneDatePicker()
//            showViewNavigation()
        }
        binding.cardJan.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = true)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(1)
            selectMonthNavigation()
        }
        binding.cardFeb.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = true)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(2)
            selectMonthNavigation()
        }
        binding.cardMar.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = true)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(3)
            selectMonthNavigation()
        }
        binding.cardApr.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = true)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(4)
            selectMonthNavigation()
        }
        binding.cardMay.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = true)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(5)
            selectMonthNavigation()
        }
        binding.cardJun.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = true)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(6)
            selectMonthNavigation()
        }
        binding.cardJul.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = true)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(7)
            selectMonthNavigation()
        }
        binding.cardAug.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = true)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(8)
            selectMonthNavigation()
        }
        binding.cardSep.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = true)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(9)
            selectMonthNavigation()
        }
        binding.cardOct.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = true)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(10)
            selectMonthNavigation()
        }
        binding.cardNov.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = true)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = false)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(11)
            selectMonthNavigation()
        }
        binding.cardDec.setOnClickListener {
            CardView(this).setBackGround(binding.cardJan, binding.textJan, isCheck = false)
            CardView(this).setBackGround(binding.cardFeb, binding.textFeb, isCheck = false)
            CardView(this).setBackGround(binding.cardMar, binding.textMar, isCheck = false)
            CardView(this).setBackGround(binding.cardApr, binding.textApr, isCheck = false)
            CardView(this).setBackGround(binding.cardMay, binding.textMay, isCheck = false)
            CardView(this).setBackGround(binding.cardJun, binding.textJun, isCheck = false)
            CardView(this).setBackGround(binding.cardJul, binding.textJul, isCheck = false)
            CardView(this).setBackGround(binding.cardAug, binding.textAug, isCheck = false)
            CardView(this).setBackGround(binding.cardSep, binding.textSep, isCheck = false)
            CardView(this).setBackGround(binding.cardOct, binding.textOct, isCheck = false)
            CardView(this).setBackGround(binding.cardNov, binding.textNov, isCheck = false)
            CardView(this).setBackGround(binding.cardDec, binding.textDec, isCheck = true)
            goneNavigation()
            currentDisplayedMonth = currentDisplayedMonth?.withMonth(12)
            selectMonthNavigation()
        }
        binding.imageArrowNextNavigation.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.plusYears(1)
            goToNextOrPrevYear()
        }
        binding.imageArrowBackNavigation.setOnClickListener {
            currentDisplayedMonth = currentDisplayedMonth?.minusYears(1)
            goToNextOrPrevYear()
        }
        binding.layoutSearch.setOnClickListener {
            goneDatePicker()
            startActivity(Intent(this, SearchActivity::class.java))
        }

        initSwitchThemeStyle()
        val trackBgGradient: GradientDrawable =
            binding.themeSwitch.trackDrawable as GradientDrawable

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            theme.resolveAttribute(R.attr.themeSwitchColor, value, true)
            if (isChecked) {
                trackBgGradient.color = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.lightThemeSwitchColor
                    )
                )
                trackBgGradient.setStroke(
                    Extension.dpToPx(this, 2F),
                    ContextCompat.getColor(this, R.color.darkThemeSwitchColor)
                )
                binding.themeSwitch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                theme.resolveAttribute(R.attr.disableToggleStrokeColor, value, true)
                trackBgGradient.setStroke(Extension.dpToPx(this, 2F), value.data)
                trackBgGradient.color = ColorStateList.valueOf(Color.WHITE)
                binding.themeSwitch.thumbTintList = ColorStateList.valueOf(value.data)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                changeTheme(isChecked)
            }, 500)
        }
    }

    private fun changeTheme(isDarkTheme: Boolean ) {
//        isThemeInitialized = if (isDarkTheme) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            false
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            true
//        }
        Preferences().getInstance().setDarkTheme(this, isDarkTheme)
        Extension.setTheme(this)
    }

    private fun initSwitchThemeStyle() {
        val trackBgGradient: GradientDrawable =
            binding.themeSwitch.trackDrawable as GradientDrawable
        val nightModeFlags = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            theme.resolveAttribute(R.attr.themeSwitchColor, value, true)
            trackBgGradient.color =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkThemeSwitchColor))
            trackBgGradient.setStroke(
                Extension.dpToPx(this, 2F),
                ContextCompat.getColor(this, R.color.darkThemeSwitchColor)
            )
            binding.themeSwitch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
        } else {
            if (binding.themeSwitch.isChecked) {
                trackBgGradient.color = ColorStateList.valueOf(value.data)
            }
            trackBgGradient.color =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            theme.resolveAttribute(R.attr.disableToggleStrokeColor, value, true)
            trackBgGradient.setStroke(Extension.dpToPx(this, 2F), value.data)
            theme.resolveAttribute(R.attr.disableThumbTintColor, value, true)
            binding.themeSwitch.thumbTintList = ColorStateList.valueOf(value.data)
        }
    }

    private fun initWeekStart() {
        if (Preferences().getInstance()
                .getWeekStart(applicationContext) == Enums.General.MONDAY.value
        ) {
            binding.textDate.text = daysOfWeek()[1].getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            )
        } else {
            binding.textDate.text = daysOfWeek()[0].getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            )
        }
    }

    private fun initSwitchThemeValue() {
        if (!isThemeValueInitialized) {
            binding.themeSwitch.isChecked = Preferences().getInstance().getDarkTheme(this)
            isThemeInitializeCounter += 1
            if (isThemeInitializeCounter == 2) {
                isThemeValueInitialized = true
            }
        }
        else {
            binding.themeSwitch.isChecked = !Preferences().getInstance().getDarkTheme(this)
        }

    }

    private fun shareApp() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, "Money Tracker")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            ConstantsHelper.KeyUri.SHARE_LINK_APP
        )
        intent.type = "text/plain"
        startActivity(intent)
    }

    private fun reviewApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(ConstantsHelper.KeyUri.SHARE_LINK_APP)
        startActivity(intent)
    }

    private fun goneNavigation() {
        val navigationAnimFadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        Handler(Looper.myLooper()!!).postDelayed({
            binding.datePicker.startAnimation(navigationAnimFadeOut)
            goneDatePicker()
        }, 200)
    }

    // next more condition for select next month or prev month
    fun selectMonthNavigation() {
        binding.datePickerNavigation.textSelectDate.text = getString(
            R.string.blank,
            currentDisplayedMonth?.month?.getDisplayName(
                TextStyle.FULL,
                Extension.getLocale(applicationContext)
            )
        )
        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
            )
        } else {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
                        + " "
                        + currentDisplayedMonth?.year.toString()
            )
        }
        currentMonthYear.value = getString(
            R.string.blank,
            currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " "
                    + currentDisplayedMonth?.year.toString()
        )
        if (currentFragment == Enums.FragmentType.BUDGET.value) {
            getNestedTransByMonth()
        } else {
            getNestedTransList()
        }

    }

    private fun goToNextOrPrevYear(isChangeCurrentMonthYear: Boolean = true) {
        binding.textSelectDateNavigation.text = getString(
            R.string.blank, currentDisplayedMonth?.year.toString()
        )
        if (isChangeCurrentMonthYear) {
            goToNextOrPrevMonth()
        }
        else {
            goToNextOrPrevMonth(false)
        }
    }

    fun goToNextOrPrevMonth(isChangeCurrentMonthYear: Boolean = true) {
        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
            )
        } else {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
                        + " "
                        + currentDisplayedMonth?.year.toString()
            )
        }
        if (isChangeCurrentMonthYear) {
            currentMonthYear.value = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " "
                        + currentDisplayedMonth?.year.toString()
            )
        }

        // close to load data when change year in date picker
        //        getNestedTransList()
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
     fun calculateData() {
        try {
            initCalculateDataThread()
            calculateDataThread?.start()
        }
        catch (exc: Exception) {
            Log.d("exc", exc.toString())
        }


//        // value income expense total
//        var allTotalAmount = 0.0
//        var totalIncome = 0.0
//        var totalExpense = 0.0
//        var convertedAmount: Double
//        var convertedIncome: Double
//        var convertedExpense: Double
//        val currency: String = if (selectedWallet == allWallet) {
//            user?.defaultCurrency.toString()
//        } else {
//            (selectedWallet.currency.toString())
//        }
//        nestedTransList.value?.filter {
//            TimeConverter.dateFormatMonthYear(it.date) == (currentDisplayedMonth?.month?.getDisplayName(
//                TextStyle.FULL,
//                Locale.getDefault()
//            ) + " " + currentDisplayedMonth?.year.toString())
//        }
//            ?.forEach { date ->
//                convertedIncome = String.format(
//                    "%.2f",
//                    date.totalIncome.toDouble().let { it1 ->
//                        Extension.convertCurrency(
//                            currency,
//                            currency,
//                            it1,
//                            this
//                        )
//                    }
//                ).replace(",",".").toDouble()
//                convertedExpense = String.format(
//                    "%.2f",
//                    date.totalExpense.toDouble().let { it1 ->
//                        Extension.convertCurrency(
//                            currency,
//                            currency,
//                            it1,
//                            this
//                        )
//                    }
//                ).replace(",",".").toDouble()
//                convertedAmount = String.format(
//                    "%.2f",
//                    date.totalAmount.toDouble().let { it1 ->
//                        Extension.convertCurrency(
//                            currency,
//                            currency,
//                            it1,
//                            this
//                        )
//                    }
//                ).replace(",",".").toDouble()
//                allTotalAmount += convertedAmount
//                totalIncome += convertedIncome
//                totalExpense += convertedExpense
//            }
//        val totalIncomeText = Extension.toBigDecimal(totalIncome.toString())
//        val totalExpenseText = Extension.toBigDecimal(totalExpense.toString())
//        val totalAmountText = Extension.toBigDecimal(allTotalAmount.toString())
//        val currencySymbol = Extension.getCurrencySymbol(
//            selectedWallet.currency.toString()
//        )
//        this.runOnUiThread {
//            kotlin.run {
//                binding.textValueTotal.text = getString(
//                    R.string.blank,
//                    currencySymbol
//                            + totalAmountText
//                )
//                binding.textValueIncome.text = getString(
//                    R.string.blank,
//                    currencySymbol
//                            + totalIncomeText
//                )
//                binding.textValueExpense.text = getString(
//                    R.string.blank,
//                    currencySymbol
//                            + totalExpenseText
//                )
//            }
//        }


    }
    fun initCalculateDataThread() {
        calculateDataThread = Thread {
            kotlin.run {
                try {
                    // value income expense total
                    var allTotalAmount = 0.0
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    var convertedAmount: Double
                    var convertedIncome: Double
                    var convertedExpense: Double
                    val currency: String = if (selectedWallet == allWallet) {
                        user?.defaultCurrency.toString()
                    } else {
                        (selectedWallet.currency.toString())
                    }

                    nestedTransList.value?.filter { TimeConverter.dateFormatMonthYear(it.date) == (currentDisplayedMonth?.month?.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    ) + " " + currentDisplayedMonth?.year.toString()) }
                        ?.forEach { date ->

                            convertedIncome = String.format(
                                "%.2f",
                                date.totalIncome.toDouble().let { it1 ->
                                    Extension.convertCurrency(
                                        currency,
                                        currency,
                                        it1,
                                        this
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
                                        this
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
                                        this
                                    )
                                }
                            ).replace(",",".").toDouble()
                            allTotalAmount += convertedAmount
                            totalIncome += convertedIncome
                            totalExpense += convertedExpense
                        }
                    val totalIncomeText = Extension.toBigDecimal(totalIncome.toString())
                    val totalExpenseText = Extension.toBigDecimal(totalExpense.toString())
                    val totalAmountText = Extension.toBigDecimal(allTotalAmount.toString())
                    val currencySymbol = Extension.getCurrencySymbol(
                        selectedWallet.currency.toString()
                    )
                    this.runOnUiThread {
                        kotlin.run {
                            binding.textValueTotal.text = getString(
                                R.string.blank,
                                currencySymbol
                                        + totalAmountText
                            )
                            binding.textValueIncome.text = getString(
                                R.string.blank,
                                currencySymbol
                                        + totalIncomeText
                            )
                            binding.textValueExpense.text = getString(
                                R.string.blank,
                                currencySymbol
                                        + totalExpenseText
                            )
                        }
                    }
                }
                catch (exc: ConcurrentModificationException) {
                    // to stop current thread from running
                    Thread.currentThread().interrupt()
                }
            }
        }
    }
    private fun getCategoryList(onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (incomeModel.value?.size == 0) {
            Thread {
                FirebaseManager(this).initCategory(
                    onSuccess = {
                        onSuccess()
                    },
                    onFailure = {
                        onFailure()
                    }
                )
            }.start()
        }
        else {
            onSuccess()
        }
    }

    fun getNestedTransList() {
        Log.d("selectedWalletID", selectedWalletId)
        Log.d("selectedWalletID", currentMonthYear.value.toString())
        try {

            Thread {
                firebaseManager.getNestedTransList(
                    currentMonthYear.value.toString(), selectedWalletId,
                    onSuccess = {
                        calculateData()
                        firebaseManager.initTransListByTransType(
                            transType,
                            onCompleted = {
                                runOnUiThread {
                                    kotlin.run {
                                        updateWidgetSmall()
                                    }
                                }
                            }
                        )
                    })
            }.start()
        }
        catch (exc: Exception) {
            Log.d("exc", exc.toString())
        }

    }

    fun initTransListThread() {
        getTransListThread = Thread {
            kotlin.run {
                firebaseManager.getNestedTransList(
                    currentMonthYear.value.toString(), selectedWalletId,
                    onSuccess = {
                        calculateData()
                        firebaseManager.initTransListByTransType(
                            transType,
                            onCompleted = {
                                runOnUiThread {
                                    kotlin.run {
                                        updateWidgetSmall()
                                    }
                                }
                            }
                        )
                    })
            }
        }
    }
    fun getNestedTransByMonth() {
        Thread {
            kotlin.run {
                firebaseManager.getNestedTransList(
                    currentMonthYear.value.toString(), selectedWalletId,
                    onSuccess = {
                    })
            }
        }.start()
    }


    // Update widgetSmall
    private fun updateWidgetSmall() {
        val widgetIntent = Intent(this, SmallWidget::class.java)
        widgetIntent.action = Constants.ACTION_UPDATE_WIDGET
        sendBroadcast(widgetIntent)
    }

    private fun getSafeSubstring(s: String, maxLength: Int): String {
        if (!TextUtils.isEmpty(s)) {
            if (s.length >= maxLength) {
                return "${s.substring(0, maxLength)}..."
            }
        }
        return s
    }

    @SuppressLint("ClickableViewAccessibility")
    fun changeSelectedWalletView(wallet: io.paraga.moneytrackerdev.models.Wallet) {
        if (user != null) {
            allWallet.currency = user?.defaultCurrency
        }
        binding.walletIcon.setBackgroundResource(
            Extension.getResouceId(this, wallet.symbol)
        )
        binding.walletName.text = getSafeSubstring(wallet.name.toString(), 16)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_drawer, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.signOutBtn -> {
                Toast(this).showCustomToast(getString(R.string.logged_out_successfully), this)
                authVM.signOut(this)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    fun switchFrag(fragment: Fragment) {

        val fragTrans: FragmentTransaction = supportFragmentManager.beginTransaction()

        // pop old fragment to front without load it again
        if (fragment.isAdded) {
            if (fragment is TransactionFrag){
                supportFragmentManager.popBackStack("trans", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            else if (fragment is CalendarFrag){
                supportFragmentManager.popBackStack("calendar", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            else if (fragment is BudgetFrag){
                supportFragmentManager.popBackStack("budget", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            else {
                supportFragmentManager.popBackStack("statistics", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        else {
            if (previousFrag != null) {
                if (previousFrag is TransactionFrag) {
                    fragTrans.addToBackStack("trans")
                }
                else if (previousFrag is CalendarFrag){
                    fragTrans.addToBackStack("calendar")
                }
                else if (previousFrag is BudgetFrag) {
                    fragTrans.addToBackStack("budget")
                }
                else {
                    fragTrans.addToBackStack("statistics")
                }
            }
            fragTrans.add(R.id.frameLayout, fragment)
        }

        fragTrans.commit()
        previousFrag = fragment

    }

    //    private fun switchFrag(fragment: Fragment, tag: String? = "") {
////        val backStateName = fragment.javaClass.name
//        val manager = supportFragmentManager
//        val fragmentPopped = manager.popBackStackImmediate(tag, 0)
//        if (!fragmentPopped && manager.findFragmentByTag(tag) == null) { //fragment not in back stack, create it.
//            val ft = manager.beginTransaction()
//            ft.replace(R.id.frameLayout, fragment, tag)
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            ft.addToBackStack(tag)
//            ft.commit()
//            Toast.makeText(this, "created", Toast.LENGTH_SHORT).show()
//        }
//    }
    fun onTouch(card: CardView, motionEvent: MotionEvent, newClass: Class<*>): Boolean {
        theme.resolveAttribute(R.attr.bottomNavigationItemBgColor, value, true)
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                card.setCardBackgroundColor(
                    value.data
                )
                return true
            }
            MotionEvent.ACTION_UP -> {
                binding.drawer.closeDrawers()
                if(newClass == Notification::class.java) {
                    setNotificationNumber(isClicked = true)
                    notificationActivityLaucher!!.launch(Intent(this, newClass))
                }
                else {
                    Extension.goToNewActivity(this, newClass)
                }
                card.setCardBackgroundColor(
                    Color.TRANSPARENT
                )
                return false
            }
            MotionEvent.ACTION_MOVE -> {

                return true
            }
            else -> {
                card.setCardBackgroundColor(
                    Color.TRANSPARENT
                )
                return false
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginOption::class.java))
    }

    override fun onResume() {
        super.onResume()
        // Rate
        if (Utils.isInternetAvailable(this)) {
            RateHelper().showRateDialogIfNecessary(this)
        }
        // remove bg color
        binding.accountLayout.setCardBackgroundColor(
            Color.TRANSPARENT
        )
        binding.walletLayout.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.categoryLayout.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.mainCurrencyLayout.setCardBackgroundColor(
            Color.TRANSPARENT
        )
        binding.changeLanguageLayout.setCardBackgroundColor(
            Color.TRANSPARENT
        )
        binding.changeWeekStart.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.layoutTheme.setCardBackgroundColor(
            Color.TRANSPARENT
        )
        binding.layoutImport.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.layoutReview.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.layoutShare.setCardBackgroundColor(
            Color.TRANSPARENT
        )

        binding.layoutPrivacy.setCardBackgroundColor(
            Color.TRANSPARENT
        )
        binding.drawer.closeDrawer(GravityCompat.START)

    }

    private fun checkPopupCondition() {
        val popUpDate: LocalDate
        val now = LocalDate.now()
        val dayDiff: Int
        val isFirstOpen = Preferences().getInstance().getFirstOpenApp(this)
        if (isFirstOpen && Preferences().getInstance().getPopUpDate(this) == "") {
            Preferences().getInstance().addPopUpDate(this, now.toString())
        } else {
            popUpDate = LocalDate.parse(Preferences().getInstance().getPopUpDate(this))
            dayDiff = ChronoUnit.DAYS.between(now, popUpDate).toInt().absoluteValue
            if ((isFirstOpen && dayDiff >= 1) || (!isFirstOpen && dayDiff >= 7)) {
                showPremiumSubFrag()
                Preferences().getInstance().addFirstOpenApp(this, false)
                Preferences().getInstance().addPopUpDate(this, now.toString())
            }
        }
    }

    private fun showPremiumSubFrag() {
        val premiumSubFrag = PremiumSubFrag()
        premiumSubFrag.show(supportFragmentManager, "")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        } else {
            if (binding.datePicker.visibility == View.VISIBLE) {
                goneDatePicker()
            } else {
                // close the app but still save history on back press
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        }
    }

    private fun setSelected(selectedView: View) {
        binding.bottomBarLayout.children.forEach { menuItem ->
            if (menuItem == selectedView) {
                menuItem.setBackgroundResource(R.drawable.rounded_bottombar_item)
                theme.resolveAttribute(R.attr.bottomNavigationItemColor, value, true)
                (menuItem as LinearLayout).children.forEach { child ->
                    if (child is ImageView) {
                        if (menuItem.id == binding.trans.id) {
                            child.setImageResource(R.drawable.ballot_on)
                        } else if (menuItem.id == binding.calendar.id) {
                            child.setImageResource(R.drawable.calendar_on)
                        } else if (menuItem.id == binding.budget.id) {
                            child.setImageResource(R.drawable.budget_on)
                        } else if (menuItem.id == binding.statistics.id) {
                            child.setImageResource(R.drawable.statistics_on)
                        }
                        child.setColorFilter(
                            value.data,
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    } else if (child is TextView) {
                        child.setTextColor(value.data)
                    }

                }
            } else {
                (menuItem as LinearLayout).children.forEach { child ->
                    if (child.id != binding.addIcon.id) {
                        if (child is ImageView) {
                            if (menuItem.id == binding.trans.id) {
                                child.setImageResource(R.drawable.ballot)
                            } else if (menuItem.id == binding.calendar.id) {
                                child.setImageResource(R.drawable.calendar)
                            } else if (menuItem.id == binding.budget.id) {
                                child.setImageResource(R.drawable.flag)
                            } else if (menuItem.id == binding.statistics.id) {
                                child.setImageResource(R.drawable.statistics)
                            }
                            child.setColorFilter(
                                ContextCompat.getColor(this, R.color.tertiaryBlack),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        } else if (child is TextView) {
                            child.setTextColor(getColor(R.color.tertiaryBlack))
                        }
                    }
                }
                menuItem.setBackgroundResource(0)
            }
        }
    }

    fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(
            this,
            ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        )
    }

    private fun setAd() {
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }
        if (!isProUser.value!!) {

            // Add test device
            val testDevices: MutableList<String> = ArrayList()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            testDevices.add("B5382995D1D8AA7E8B34363F0A071983")
            testDevices.add("0668AB12DFB9D5F03E0102157874858B")
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)


            // Interstitial
            runOnUiThread {
                kotlin.run {
                    adManager.loadFullScreenAd(this)
                }
            }
        }
    }

    private fun initSubscriptionStatus() {
        Purchases.configure(PurchasesConfiguration.Builder(this, Config.RC_PUB_KEY).build())
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onError(error: PurchasesError) {
            }

            override fun onReceived(customerInfo: CustomerInfo) {
                if (customerInfo.entitlements[Config.ENTITLEMENT_ID]?.isActive == true) {
                    // Grant user "pro" access
                    isProUser.postValue(true)
                }
                else {
                    if (Preferences().getInstance().getPreviousSubscriptionStatus(this@MainActivity)) {
                        DialogHelper.showSubscriptionExpiredDialog(this@MainActivity, onOkayPressed = {
                            showPremiumSubFrag()
                        })
                        Preferences().setPreviousSubscriptionStatus(this@MainActivity, false)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun recreate() {
        super.recreate()
    }

    private fun initRecyclerView() {
//        drawerAdapter = DrawerAdapter()
//        val linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.orientation = RecyclerView.VERTICAL
//        binding.drawerRecyclerView.layoutManager = linearLayoutManager
//        binding.drawerRecyclerView.adapter = drawerAdapter
//        binding.drawerRecyclerView.setHasFixedSize(true)
//        binding.drawerRecyclerView.bringToFront()
//        binding.drawer.requestLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.transText.setText(R.string.trans)
        binding.calendarText.setText(R.string.calendar)
        binding.budgetText.setText(R.string.budget)
        binding.statisticsText.setText(R.string.statistics)

        binding.incomeText.setText(R.string.income)
        binding.expenseText.setText(R.string.expense)
        binding.totalText.setText(R.string.total)

        binding.upgradeText.setText(R.string.upgrade_to_unlock)
        binding.upgradeBtnText.setText(R.string.upgrade_now)
        binding.accountText.setText(R.string.account)
        binding.changeLanguageText.setText(R.string.change_language)
        binding.weekStartAtText.setText(R.string.week_start_at)
        binding.darkThemeText.setText(R.string.dark_theme)
        binding.categoryText.setText(R.string.category)
        binding.walletText.setText(R.string.wallet)
        binding.mainCurrencyText.setText(R.string.main_currency)
        binding.importText.setText(R.string.import_label)
        binding.reviewText.setText(R.string.review)
        binding.shareText.setText(R.string.share)
        binding.privacyText.setText(R.string.privacy)

        if (LocalDate.now().year.toString() == currentDisplayedMonth?.year.toString()) {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
            )
        } else {
            binding.layoutHeader.textSelectDate.text = getString(
                R.string.blank,
                currentDisplayedMonth?.month?.getDisplayName(
                    TextStyle.FULL,
                    Extension.getLocale(applicationContext)
                )
                        + " "
                        + currentDisplayedMonth?.year.toString()
            )
        }

        binding.datePickerNavigation.textSelectDate.text = getString(
            R.string.blank, currentDisplayedMonth?.month?.getDisplayName(
                TextStyle.FULL, Extension.getLocale(applicationContext)
            )
        )
        binding.textJan.text =
            Month.JANUARY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textFeb.text =
            Month.FEBRUARY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textMar.text =
            Month.MARCH.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textApr.text =
            Month.APRIL.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textMay.text =
            Month.MAY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textJun.text =
            Month.JUNE.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textJul.text =
            Month.JULY.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textAug.text =
            Month.AUGUST.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textSep.text =
            Month.SEPTEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textOct.text =
            Month.OCTOBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textNov.text =
            Month.NOVEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textDec.text =
            Month.DECEMBER.getDisplayName(TextStyle.SHORT, Extension.getLocale(applicationContext))
        binding.textSelectDateNavigation.text =
            getString(R.string.blank, currentDisplayedMonth?.year.toString())

        val mondayFull =
            daysOfWeek()[1].getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext))
        val sundayFull =
            daysOfWeek()[0].getDisplayName(TextStyle.FULL, Extension.getLocale(applicationContext))
        if (binding.textDate.text.toString() == mondayFull) {
            binding.textDate.text = mondayFull
        } else {
            binding.textDate.text = sundayFull
        }
    }

    fun setBudgetViewType() {
        spanCount = Preferences().getInstance().getBudgetViewType(this)
    }

}