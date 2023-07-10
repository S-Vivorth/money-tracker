package io.paraga.moneytrackerdev.views.wallet

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.firebase.Timestamp
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityEditWalletBinding
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.wallet.EditWalletVM
import io.paraga.moneytrackerdev.views.currency.ChooseCurrencyFrag
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*


class EditWallet : AppCompatActivity() {
    lateinit var binding: ActivityEditWalletBinding
    lateinit var previousColorLayout: RelativeLayout
    lateinit var imageView: ImageView
    val chooseWalletIconFrag = ChooseWalletIconFrag()
    val vp = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    private var expandColorSelected = false
    private val colorListFrag = ColorListFrag(this as Any, isEditWallet = true)
    lateinit var walletList: ArrayList<WalletTrans>
    lateinit var wallet: io.paraga.moneytrackerdev.models.Wallet
    lateinit var currentWalletTrans: WalletTrans
    var position: Int = 0
    lateinit var currentCurrencyName: String
    lateinit var selectedColor: String
    var transType: Int = Enums.TransTypes.INCOME.value
    lateinit var transAmount: Number
    lateinit var balance: Number
    var isBalanceChanged: Boolean = false
    private lateinit var editWalletVM: EditWalletVM
    var isDefaultWallet: Boolean = false
    var isCurrencyChange: Boolean = false
    val value = TypedValue()

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editWalletVM = EditWalletVM(this)
        val bundle = intent.getBundleExtra("bundle")
        walletList = bundle?.getSerializable("walletList") as ArrayList<WalletTrans>
        position = intent.getIntExtra("position", 0)
        walletList[position].wallet.createdTime =
            Timestamp(Date(intent.getLongExtra(Enums.Extras.MILLISECOND.value, 0)))
        wallet = walletList[position].wallet
        currentWalletTrans = walletList[position]
        setUpView()
        setUpColorLayout()
        colorListFrag.selectedIndex = setColorIndex()
        changeViewsColor(ColorStateList.valueOf(Color.parseColor(walletList[position].wallet.color.toString())))
        currentCurrencyName = getString(
            R.string.blank,
            wallet.currency.toString() + " - " +
                    Extension.getCurrencyObj(wallet.currency.toString()).name
        )
        binding.backBtnLayout.setOnClickListener {
            finish()
        }

        // disable default wallet switch
        if (walletList[position].isDefault) {
            binding.defaultWalletSwitch.isEnabled = false
        }

        if (wallet.userid != user?.userid) {
            binding.deleteWalletBtn.text = getString(R.string.leave_wallet)
        }
        binding.walletIconLayout.setOnClickListener {
            val chooseWalletIconFrag = ChooseWalletIconFrag()
            chooseWalletIconFrag.currentIconLayout =
                walletList[position].wallet.symbol?.replace("-", "_")!!

            chooseWalletIconFrag.show(supportFragmentManager, "")
        }

        binding.initialBalanceLayout.setOnClickListener {
            val addInitialBalanceFrag = AddInitialBalanceFrag()
            addInitialBalanceFrag.fromEditWallet = true
            addInitialBalanceFrag.show(supportFragmentManager, "")
        }

        binding.sharedWithLayout.setOnClickListener {
//            Toast(this).showCustomToast(getString(R.string.coming_soon), this)
            val shareUserFrag = ShareUserFrag(this)
            shareUserFrag.show(supportFragmentManager, "")
        }

        binding.chooseCurrencyLayout.setOnClickListener {
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
                colorListFrag.selectedIndex = setColorIndex()
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



        binding.deleteWalletBtn.setOnClickListener {
            if (!walletList[position].isDefault) {
                if (wallet.userid != user?.userid) {
                    DialogHelper.showSecondaryDialog(
                        this,
                        getString(R.string.leave_wallet),
                        getString(R.string.are_you_sure_you_want_to_leave_this_wallet),
                        getString(R.string.leave),
                        onOkayPressed = { alertDialog ->
                            deleteWallet(alertDialog)
                        }
                    )
                } else {
                    DialogHelper.showPrimaryDialog(this, Enums.DialogType.WALLET.value,
                        onOkayPressed = { alertDialog ->
                            deleteWallet(alertDialog)
                        })
                }


            } else {
                Toast(this).showCustomToast(
                    getString(R.string.default_wallet_cannot_be_deleted),
                    this
                )

            }

        }

        binding.saveBtn.setOnClickListener {
            walletList[position].wallet.color = selectedColor
            walletList[position].wallet.name = binding.walletName.text.toString()
            editWalletVM.updateWallet(
                walletId = walletList[position].walletId,
                walletList[position].wallet
            ) {
            }
            if (isBalanceChanged) {
                val currentDate = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val dateInString = formatter.format(currentDate).toString().split(" ")
                val currentTime = dateInString[1]

                editWalletVM.addTrans(amount = transAmount,
                    currency = Extension.getCurrencyObj(wallet.currency.toString()).code,
                    date = getString(
                        R.string.blank,
                        LocalDate.now().dayOfMonth.toString()
                                + " "
                                + LocalDate.now().month.getDisplayName(
                            TextStyle.SHORT,
                            Locale.ENGLISH
                        )
                                + " "
                                + LocalDate.now().year.toString()
                    ) + " " + currentTime,
                    remark = Enums.General.ADJUST_BALANCE.value,
                    type = transType,
                    walletID = walletList[position].walletId,
                    onSuccess = {
                    },
                    onFailure = {

                    })
                if (!walletList[position].isDefault && isDefaultWallet) {
                    editWalletVM.setDefaultWallet(walletList[position].walletId,
                        onSuccess = {
                        },
                        onFailure = {
                        })
                }
                Toast(this).showCustomToast("Wallet " + getString(R.string.is_saved), this)
                Extension.goToNewActivity(
                    this,
                    Wallet::class.java,
                    clearTop = true
                )
            } else {
                if (!walletList[position].isDefault && isDefaultWallet) {
                    editWalletVM.setDefaultWallet(walletList[position].walletId,
                        onSuccess = {
                        },
                        onFailure = {
                        })
                }
                Toast(this).showCustomToast("Wallet " + getString(R.string.is_saved), this)
                Extension.goToNewActivity(this, Wallet::class.java, clearTop = true)
            }
        }

    }

    private fun selectedColor(colorLayout: RelativeLayout) {
        previousColorLayout.removeView(imageView)
        changeViewsColor((colorLayout.children.first() as CardView).cardBackgroundColor)
        colorLayout.addView(imageView)
        previousColorLayout = colorLayout
    }

    private fun setUpView() {
        val nightModeFlags: Int =
            resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.walletIconChildLayout.alpha = 0.18F
            binding.initialBalanceRightLayout.alpha = 0.18F
            binding.sharedWithRightLayout.alpha = 0.18F
        }
        binding.walletName.setText(walletList[position].wallet.name)
        binding.walletIcon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(wallet.color.toString()))
        binding.walletIcon.setBackgroundResource(
            Extension.getResouceId(this, wallet.symbol)
        )
        binding.defaultWalletSwitch.isChecked = walletList[position].isDefault
        isDefaultWallet = walletList[position].isDefault
        balance = walletList[position].balance
        binding.balanceText.text = getString(
            R.string.blank,
            Extension.getCurrencySymbol(
                wallet.currency.toString()
            ) + String.format("%.2f", walletList[position].balance.toDouble())
        )
        binding.currency.text = getString(
            R.string.blank,
            "${wallet.currency.toString()} - " +
                    Extension.getCurrencyObj(wallet.currency.toString()).name
        )
        val shareUsers =
            currentWalletTrans.wallet.sharedWith?.values?.filter { it == Enums.InvitationType.ACCEPTED.value }
        binding.numberOfSharedUser.text = getString(
            R.string.blank,
            (((shareUsers?.size)
                ?: 0) + 1).toString()
                    + " "
                    + getString(R.string.users)
        )

        // not allow share user to change the default
        if (wallet.userid != user?.userid) {
            binding.defaultWalletSwitch.isEnabled = false
        }
    }

    private fun deleteWallet(alertDialog: android.app.AlertDialog) {
        editWalletVM.deleteWallet(walletList[position],
            onSuccess = {
            },
            onFailure = {
            })
        alertDialog.dismiss()
        walletList[position].wallet.name?.let { walletName ->
            Toast(this).showCustomToast(
                getString(R.string.wallet_is_delete),
                this
            )
            Extension.goToNewActivity(this, Wallet::class.java, clearTop = true)
        }
    }

    private fun setUpColorLayout() {
//        currentColorStateList = (binding.firstColorLayout.children.first() as CardView).cardBackgroundColor
        imageView = ImageView(this)
        if (Color.parseColor(
                wallet.color.toString()
            ) == getColor(R.color.default_og)
        ) {
            binding.secondColorLayout.addView(imageView)
            previousColorLayout = binding.secondColorLayout
        } else if (Color.parseColor(
                wallet.color.toString()
            ) == getColor(R.color.vividCerulean)
        ) {
            binding.thirdColorLayout.addView(imageView)
            previousColorLayout = binding.thirdColorLayout
        } else if (Color.parseColor(
                wallet.color.toString()
            ) == getColor(R.color.illuminating_emerald)
        ) {
            binding.forthColorLayout.addView(imageView)
            previousColorLayout = binding.forthColorLayout
        } else if (Color.parseColor(
                wallet.color.toString()
            ) == getColor(R.color.maximum_blue_green)
        ) {
            binding.fifthColorLayout.addView(imageView)
            previousColorLayout = binding.fifthColorLayout
        } else if (Color.parseColor(
                wallet.color.toString()
            ) == getColor(R.color.primaryPurple)
        ) {
            binding.sixthColorLayout.addView(imageView)
            previousColorLayout = binding.sixthColorLayout

        } else {
            binding.firstColorLayout.addView(imageView)
            previousColorLayout = binding.firstColorLayout
            binding.firstColor.setCardBackgroundColor(
                Color.parseColor(
                    wallet.color.toString()
                )
            )
        }

        val id = resources.getIdentifier("check", "drawable", packageName)
        vp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        imageView.layoutParams = vp
        imageView.setImageResource(id)
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    fun setColorIndex(): Int {
        return when (ColorStateList.valueOf(Color.parseColor(wallet.color.toString()))) {
            getColorSL(R.color.primaryBlue) -> 1
            getColorSL(R.color.default_og) -> 2
            getColorSL(R.color.vividCerulean) -> 3
            getColorSL(R.color.illuminating_emerald) -> 4
            getColorSL(R.color.maximum_blue_green) -> 5
            getColorSL(R.color.primaryPurple) -> 6
            getColorSL(R.color.primaryRoyalFuchsia) -> 7
            getColorSL(R.color.rose_pink) -> 8
            getColorSL(R.color.orchid) -> 9
            getColorSL(R.color.fire_engine_red) -> 10
            getColorSL(R.color.red_salsa) -> 11
            getColorSL(R.color.fiery_rose) -> 12
            getColorSL(R.color.windsor_tan) -> 13
            getColorSL(R.color.metallic_bronze) -> 14
            getColorSL(R.color.ecru) -> 15
            getColorSL(R.color.palatinate_blue) -> 16
            getColorSL(R.color.earth_yellow) -> 17
            getColorSL(R.color.kiwi) -> 18
            getColorSL(R.color.primaryCitron) -> 19
            getColorSL(R.color.urobilin) -> 20
            else -> 1
        }
    }

    private fun getColorSL(color: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    fun changeViewsColor(color: ColorStateList) {

        selectedColor = String.format("#%06X", 0xFFFFFF and color.defaultColor)
        binding.saveBtn.backgroundTintList = color
        binding.walletIcon.backgroundTintList = color
        binding.walletIconChildLayout.backgroundTintList = color
        binding.balanceIconLayout.backgroundTintList = color
        binding.initialBalanceRightLayout.backgroundTintList = color
        binding.balanceText.setTextColor(color)
        binding.currencyIconLayout.backgroundTintList = color
        binding.shareWithIconLayout.backgroundTintList = color
        binding.sharedWithRightLayout.backgroundTintList = color
        binding.numberOfSharedUser.setTextColor(color)
        binding.setDefaultIconLayout.backgroundTintList = color
//        val trackBgGradient: GradientDrawable =
//            binding.defaultWalletSwitch.trackDrawable as GradientDrawable
        val nightModeFlags: Int =
            resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
        binding.defaultWalletSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.defaultWalletSwitch.trackTintList = color
                binding.defaultWalletSwitch.trackDecorationTintList = color
                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            } else {
                theme.resolveAttribute(R.attr.disableToggleFillColor, value, true)
                binding.defaultWalletSwitch.trackTintList =  ColorStateList.valueOf(value.data)
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableToggleStrokeColor))

                    binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            R.color.darkDisableThumbTintColor
                        )
                    )
                } else {
                    binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableToggleStrokeColor))
                    binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            R.color.lightDisableThumbTintColor
                        )
                    )
                }

            }
            isDefaultWallet = isChecked
        }
        if (binding.defaultWalletSwitch.isChecked) {
            binding.defaultWalletSwitch.trackTintList = color
            binding.defaultWalletSwitch.trackDecorationTintList = color
            binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
        } else {
            theme.resolveAttribute(R.attr.disableToggleFillColor, value, true)
            binding.defaultWalletSwitch.trackTintList =  ColorStateList.valueOf(value.data)
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkDisableToggleStrokeColor))

                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.darkDisableThumbTintColor
                    )
                )
            } else {
                binding.defaultWalletSwitch.trackDecorationTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightDisableToggleStrokeColor))
                binding.defaultWalletSwitch.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.lightDisableThumbTintColor
                    )
                )
            }
        }
    }

    private fun setIconIndex(): Int {

        return when (walletList[position].wallet.symbol?.replace("-", "_")) {
            "ic_all_wallet" -> 1
            "account_balance_wallet" -> 2
            "ic_wallet_1" -> 3
            "toll" -> 4
            "credit_card" -> 5
            "payments" -> 6
            "monetization_on" -> 7
            "savings" -> 8
            "savings_1" -> 9
            "group" -> 10
            "diversity_3" -> 11
            "diversity_1" -> 12
            "family_restroom" -> 13
            "face" -> 14
            "face_6" -> 15
            "face_4" -> 16
            "face_3" -> 17
            "face_2" -> 18
            "business_center" -> 19
            "account_balance" -> 20
            "work" -> 21
            "home_work" -> 22
            "storefront" -> 23
            "leaderboard" -> 24
            "insert_chart" -> 25
            "whatshot" -> 26
            "school" -> 27
            "local_library" -> 28
            "folder" -> 29
            "new_releases" -> 30
            "favorite" -> 31
            "label" -> 32
            "park" -> 33
            else -> 2
        }
    }


}