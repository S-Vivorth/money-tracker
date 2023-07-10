package io.paraga.moneytrackerdev.views.premium

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.PremiumAdapter
import io.paraga.moneytrackerdev.adapters.SliderAdapter
import io.paraga.moneytrackerdev.databinding.FragmentPremiumSubBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PremiumSubFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class PremiumSubFrag : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentPremiumSubBinding
    lateinit var premiumAdapter: PremiumAdapter
    var selectedPackagePosition: Int = 0
    lateinit var sliderAdapter: SliderAdapter
    var timer: Timer? = Timer()
    lateinit var timerTask: TimerTask
    var currentIndex = 0
    lateinit var packages: List<com.revenuecat.purchases.Package>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPremiumSubBinding.inflate(layoutInflater)
        view?.setBackgroundResource(android.R.color.transparent)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timerTask.cancel()
        timer?.cancel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            initViewPager()
            initRecyclerView()
            getPackages()
            binding.ctnSubBtn.setOnClickListener {
                subscribe()
            }
            binding.closeBtn.setOnClickListener {
                dismiss()
            }

            binding.restoreSubBtn.setOnClickListener {
                Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
                    override fun onError(error: PurchasesError) {
                    }

                    override fun onReceived(customerInfo: CustomerInfo) {
                        if (customerInfo.entitlements[Config.ENTITLEMENT_ID]?.isActive == true) {
                            // Grant user "pro" access
                            Preferences().getInstance().setPreviousSubscriptionStatus(
                                requireContext(), true)
                            Toast(context).showCustomToast(requireContext().getString(R.string.subscription_has_been_restored_successfully), requireActivity())
                            isProUser.value = true
                            dismiss()
                        }
                        else {
                            Toast(context).showCustomToast(requireContext().getString(R.string.failed_to_restore_the_subscription), requireActivity())
                        }
                    }

                })

            }

            changeSelectedState(
                binding.firstIndicator,
                requireContext()
            )
        }
        else {
            dismiss()
        }

    }

    private fun initViewPager() {
        sliderAdapter = SliderAdapter()
        binding.sliderViewPager.adapter = sliderAdapter
        binding.sliderViewPager.currentItem = currentIndex
        binding.sliderViewPager.setDurationScroll(600)
        binding.sliderViewPager.addOnPageChangeListener(object : OnPageChangeListener{

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("scrolled",position.toString())

            }


            override fun onPageSelected(position: Int) {
                currentIndex = position

                // clear timer
                timer?.cancel()
                timer?.purge()
                timer = null

                switchItem()
                if (currentIndex == 0) {
                    changeSelectedState(
                        binding.firstIndicator,
                        requireContext()
                    )
                } else if (currentIndex == 1) {
                    changeSelectedState(
                        binding.secondIndicator,
                        requireContext()
                    )
                } else if (currentIndex == 2){
                    changeSelectedState(
                        binding.thirdIndicator,
                        requireContext()
                    )
                }
                else if (currentIndex == 3){
                    changeSelectedState(
                        binding.forthIndicator,
                        requireContext()
                    )
                }
                else{
                    changeSelectedState(
                        binding.fifthIndicator,
                        requireContext()
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("stateChanged",state.toString())
            }

        })
        switchItem()

    }


    fun changeSelectedState(
        selectedCard: CardView,
        context: Context
    ) {


        binding.indicatorLayout.children.forEach {
            if (selectedCard == it) {
                it.layoutParams.width = Extension.dpToPx(context, 10F)
                it.layoutParams.height = Extension.dpToPx(context, 10F)
                (it as CardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.default_og
                    )
                )
            }
            else {
                it.layoutParams.width = Extension.dpToPx(context, 8F)
                it.layoutParams.height = Extension.dpToPx(context, 8F)
                (it as CardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            }
        }


    }

    fun switchItem() {
        try {
            timerTask = object : TimerTask() {
            override fun run() {

                currentIndex += 1
                if (currentIndex == 5) {
                    currentIndex = 0
                }
                requireActivity().runOnUiThread {
                    kotlin.run {
                        binding.sliderViewPager.currentItem = currentIndex
                        if (currentIndex == 0) {
                            changeSelectedState(
                                binding.firstIndicator,
                                requireContext()
                            )
                        } else if (currentIndex == 1) {
                            changeSelectedState(
                                binding.secondIndicator,
                                requireContext()
                            )
                        } else if (currentIndex == 2){
                            changeSelectedState(
                                binding.thirdIndicator,
                                requireContext()
                            )
                        }
                        else if (currentIndex == 3){
                            changeSelectedState(
                                binding.forthIndicator,
                                requireContext()
                            )
                        }
                        else{
                            changeSelectedState(
                                binding.fifthIndicator,
                                requireContext()
                            )
                        }
                    }
                }
                switchItem()
            }
        }
        timer = Timer()
        timer?.schedule(timerTask, 4000)

        }
        catch (exc: Exception) {
            Log.d("exc", exc.message.toString())
        }
    }

    private fun getPackages() {
        fun getOffers() {
            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->
                    Log.d("error", error.toString())
                },
                onSuccess = { offerings ->
                    packages = offerings.current?.availablePackages!!.reversed()
                    binding.ctnSubBtn.isEnabled = true
                }
            )
        }
        if (Purchases.isConfigured) {
            getOffers()
        }
        else {
            Purchases.configure(PurchasesConfiguration.Builder(requireContext(), Config.RC_PUB_KEY).build())
            getOffers()
        }

    }

    private fun subscribe() {
        Purchases.sharedInstance.purchaseProductWith(
            requireActivity(),
            packages[selectedPackagePosition].product,
            onError = { error, userCancelled -> /* No purchase */
                Log.d("productError", error.toString())
                Log.d("userCancelled", userCancelled.toString())

            },
            onSuccess = { product, customerInfo ->
                Log.d("productSuccess", product.toString() + "\n" + customerInfo.toString())
                if (customerInfo.entitlements[Config.ENTITLEMENT_ID]?.isActive == true) {
                    Preferences().getInstance().setPreviousSubscriptionStatus(
                        requireContext(), true)
                    Toast(context).showCustomToast(requireContext().getString(R.string.premium_feature_has_been_unlocked_successfully), requireActivity())
                    isProUser.value = true
                    dismiss()
                    // Unlock that great "pro" content
                }
            })
    }
    fun initRecyclerView() {
        premiumAdapter = PremiumAdapter(this)
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.offersRecyclerView.layoutManager = categoryLinearLayoutManager
        binding.offersRecyclerView.adapter = premiumAdapter
        binding.offersRecyclerView.setHasFixedSize(true)
        binding.offersRecyclerView.isNestedScrollingEnabled = false
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PremiumSubFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PremiumSubFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
class PresentationViewPager : ViewPager {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    fun setDurationScroll(millis: Int) {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.setAccessible(true)
            scroller.set(this, OwnScroller(context, millis))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class OwnScroller(context: Context?, durationScroll: Int) :
        Scroller(context, DecelerateInterpolator()) {
        private var durationScrollMillis = 1

        init {
            durationScrollMillis = durationScroll
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, durationScrollMillis)
        }
    }

    companion object {
        const val DEFAULT_SCROLL_DURATION = 250
        const val PRESENTATION_MODE_SCROLL_DURATION = 1000
    }
}