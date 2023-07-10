package io.paraga.moneytrackerdev.views.wallet

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.firebase.Timestamp
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.NotificationManager
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityCreateWalletBinding
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.wallet.CreateWalletVM
import io.paraga.moneytrackerdev.views.currency.ChooseCurrencyFrag
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class CreateWallet : AppCompatActivity() {
    lateinit var binding: ActivityCreateWalletBinding
    lateinit var previousColorLayout: RelativeLayout
    lateinit var imageView: ImageView
    val vp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    var expandColorSelected = false
    private val colorListFrag = ColorListFrag(this as Any)
    lateinit var walletList: ArrayList<WalletTrans>
    lateinit var wallet: io.paraga.moneytrackerdev.models.Wallet
    var position: Int = 0
    lateinit var currentCurrencyName: String
    lateinit var selectedColor: String
    var transType: Int = Enums.TransTypes.INCOME.value
    var transAmount: Number = 0.0
    lateinit var balance: Number
    var isBalanceChanged: Boolean = false
    var selectedIconName = Enums.General.IC_ALL_WALLET.value
    var isDefaultWallet = false
    lateinit var walletName: String
    val createWalletVM = CreateWalletVM()
    var firstColorSelectedIndex = 1
    val value = TypedValue()
    lateinit var firebaseManager: FirebaseManager
    lateinit var notificationManager: NotificationManager
    var sharedUserList: ArrayList<User> = ArrayList()
    //    lateinit var currentColorStateList: ColorStateList

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseManager = FirebaseManager(this)
        notificationManager = NotificationManager()
        initWallet()
        setUpColorLayout()

//        if (binding.defaultWalletSwitch.isChecked) {
//            binding.defaultWalletSwitch.trackDrawable.setTintList(currentColorStateList)
//        }
//        else {
//            binding.defaultWalletSwitch.trackDrawable.setTintList(ColorStateList.valueOf(Color.TRANSPARENT))
//        }
        val nightModeFlags: Int = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.walletIconChildLayout.alpha = 0.18F
            binding.initialBalanceRightLayout.alpha = 0.18F
            binding.sharedWithRightLayout.alpha = 0.18F
        }
        walletName = binding.walletNameEdittext.text.toString()
        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        showKeyboard()

        binding.walletIconLayout.setOnClickListener {
            val chooseWalletIconFrag = ChooseWalletIconFrag()
            chooseWalletIconFrag.currentIconLayout = selectedIconName
            chooseWalletIconFrag.show(supportFragmentManager, "")
        }

        binding.initialBalanceLayout.setOnClickListener {
            val addInitialBalanceFrag = AddInitialBalanceFrag()
            addInitialBalanceFrag.show(supportFragmentManager, "")
        }

        binding.sharedWithLayout.setOnClickListener {
//            Toast(this).showCustomToast(getString(R.string.coming_soon), this)
            val shareUserFrag = ShareUserFrag(this)
            shareUserFrag.show(supportFragmentManager, "")
        }

        currentCurrencyName = getString(
            R.string.blank,
            io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString() + " - " +
                    Extension.getCurrencyObj(io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString()).name
        )



        binding.chooseCurrencyLayout.setOnClickListener {
            currentCurrencyName = getString(
                R.string.blank,
                wallet.currency.toString() + " - " +
                        Extension.getCurrencyObj(wallet.currency.toString()).name
            )
            val chooseCurrencyFrag = ChooseCurrencyFrag()
            chooseCurrencyFrag.currentCurrencyName = currentCurrencyName
            chooseCurrencyFrag.show(supportFragmentManager, "")
        }

        binding.expandColorBtn.setOnClickListener {
            colorListFrag.show(supportFragmentManager, "")
        }

        binding.firstColor.setOnClickListener {
            selectedColor(binding.firstColorLayout)
            if (!expandColorSelected) {
                colorListFrag.selectedIndex = 1
            }
            else {
                colorListFrag.selectedIndex = firstColorSelectedIndex
            }

        }
        binding.secondColor.setOnClickListener {
            selectedColor(binding.secondColorLayout)
            colorListFrag.selectedIndex = 2
        }

        binding.thirdColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            colorListFrag.selectedIndex = 3
        }
        binding.forthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            colorListFrag.selectedIndex = 4

        }
        binding.fifthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            colorListFrag.selectedIndex = 5

        }
        binding.sixthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            colorListFrag.selectedIndex = 6

        }

        binding.defaultWalletSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            isDefaultWallet = isChecked
        }

        binding.firstColor.performClick()

        binding.saveBtn.setOnClickListener {
            binding.saveBtn.isEnabled = false
            walletName = binding.walletNameEdittext.text.toString()
            val currentDate = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val dateInString = formatter.format(currentDate).toString().split(" ")
            val currentTime = dateInString[1]
            wallet.name = walletName
            wallet.color = selectedColor
            val trans = Transaction()
            trans.amount = transAmount
            trans.currency = Extension.getCurrencyObj(wallet.currency.toString()).code
            trans.date = Timestamp(
                Date(
                    getString(
                        R.string.blank,
                        LocalDate.now().dayOfMonth.toString()
                                + " "
                                + LocalDate.now().month.getDisplayName(
                            TextStyle.SHORT,
                            Locale.ENGLISH
                        )
                                + " "
                                + LocalDate.now().year.toString()
                    ) + " " + currentTime
                )
            )
            trans.remark = Enums.General.ADJUST_BALANCE.value
            trans.type = transType
            createWalletVM.createWallet(
                wallet,
                isDefaultWallet,
                trans,
                context = this,
                onSuccess = { walletId ->

                    Thread {
                        if (sharedUserList.size > 0) {
                            sharedUserList.forEach {
                                val walletTrans = WalletTrans()
                                walletTrans.wallet = wallet
                                walletTrans.walletId = walletId
                                notificationManager.sendWalletInvitation(
                                    walletTrans,
                                    it,
                                    getString(R.string.wallet_invitation),
                                    getString(
                                        R.string.blank,
                                        getString(R.string.you_were_invited_to_join_a_wallet)
                                                + " "
                                                + wallet.name
                                                + " "
                                                + getString(R.string.from)
                                                + " "
                                                + user?.email
                                    ),
                                    "",
                                    false
                                )
                            }
                        }
                    }.start()
                },
                onFailed = {
                    binding.saveBtn.isEnabled = true
                    Toast(this).showCustomToast(it, this)

                })

            Handler(Looper.getMainLooper()).postDelayed({
                Toast(this).showCustomToast(  getString(R.string.wallet_is_added), this)
                Extension.goToNewActivity(
                    this,
                    Wallet::class.java,
                    clearTop = true
                )
                finish()
            }, 1000)

        }
    }

    private fun showKeyboard() {
        binding.walletNameEdittext.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun selectedColor(colorLayout: RelativeLayout) {

        previousColorLayout.removeView(imageView)
        changeViewsColor((colorLayout.children.first() as CardView).cardBackgroundColor)
        colorLayout.addView(imageView)
        previousColorLayout = colorLayout

    }

    private fun setUpColorLayout() {
//        currentColorStateList = (binding.firstColorLayout.children.first() as CardView).cardBackgroundColor
        imageView = ImageView(this)
        previousColorLayout = binding.firstColorLayout
        val id = resources.getIdentifier("check", "drawable", packageName)
        vp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        imageView.layoutParams = vp
        imageView.setImageResource(id)
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        previousColorLayout.addView(imageView)
        selectedColor = java.lang.String.format(
            "#%06X",
            0xFFFFFF and binding.firstColor.cardBackgroundColor.defaultColor
        )
    }

    fun changeViewsColor(color: ColorStateList) {

        binding.saveBtn.backgroundTintList = color
        binding.walletIcon.backgroundTintList = color
        binding.walletIconChildLayout.backgroundTintList = color
        binding.balanceIconLayout.backgroundTintList = color
        binding.initialBalanceRightLayout.backgroundTintList = color
        binding.initialBalanceText.setTextColor(color)
        binding.currencyIconLayout.backgroundTintList = color
        binding.shareWithIconLayout.backgroundTintList = color
        binding.sharedWithRightLayout.backgroundTintList = color
        binding.sharedWithText.setTextColor(color)
        binding.setDefaultIconLayout.backgroundTintList = color
//        val trackBgGradient: GradientDrawable = binding.defaultWalletSwitch.trackDrawable as GradientDrawable
        val nightModeFlags: Int = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        binding.defaultWalletSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.defaultWalletSwitch.trackTintList = color
                binding.defaultWalletSwitch.trackDecorationTintList = color
                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            }
            else {
                theme.resolveAttribute(R.attr.disableToggleFillColor, value, true)
                binding.defaultWalletSwitch.trackTintList =  ColorStateList.valueOf(value.data)
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableToggleStrokeColor))
                    binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableThumbTintColor))
                }
                else {
                    binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableToggleStrokeColor))
                    binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableThumbTintColor))
                }

            }
        }
        if (binding.defaultWalletSwitch.isChecked) {
            binding.defaultWalletSwitch.trackTintList = color
            binding.defaultWalletSwitch.trackDecorationTintList = color
            binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
        }
        else {
            theme.resolveAttribute(R.attr.disableToggleFillColor, value, true)
            binding.defaultWalletSwitch.trackTintList =  ColorStateList.valueOf(value.data)
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableToggleStrokeColor))
                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableThumbTintColor))
            }
            else {
                binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableToggleStrokeColor))
                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableThumbTintColor))
            }
        }



        // set selected color to hex format
        selectedColor = java.lang.String.format("#%06X", 0xFFFFFF and color.defaultColor)
//        currentColorStateList = color


    }



    private fun initWallet() {
        wallet = io.paraga.moneytrackerdev.models.Wallet()
        wallet.symbol = Enums.General.IC_ALL_WALLET.value
        wallet.color = java.lang.String.format(
            "#%06X",
            0xFFFFFF and binding.firstColor.cardBackgroundColor.defaultColor
        )
        wallet.currency = io.paraga.moneytrackerdev.networks.user?.defaultCurrency
        wallet.userid = io.paraga.moneytrackerdev.networks.user?.userid
        binding.currency.text = getString(
            R.string.blank,
            io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString() + " - " +
                    Extension.getCurrencyObj(io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString()).name
        )
        binding.initialBalanceText.text = getString(
            R.string.blank,
            Extension.getCurrencySymbol(io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString())
                    + transAmount
        )
    }
}