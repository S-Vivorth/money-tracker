package io.paraga.moneytrackerdev.views.importFile.bottomSheet

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentEditCategoryWalletBottomSheetBinding
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.utils.helper.Utils.toPx
import io.paraga.moneytrackerdev.viewmodels.category.ChooseCategoryFragVM
import io.paraga.moneytrackerdev.views.category.NewCategoryFrag
import io.paraga.moneytrackerdev.views.importFile.adapter.CategoryImportAdapter
import io.paraga.moneytrackerdev.views.importFile.adapter.WalletImportAdapter
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.premium.PremiumSubFrag
import io.paraga.moneytrackerdev.views.wallet.CreateWallet

class EditCategoryWalletBottomSheetFragment(val typeCategory: Int ?= 0,
                                            private val walletListenerBack: OnClickedListener<WalletTrans> ?= null,
                                            private val categoryListenerBack : OnClickedListener<Category> ?= null,
                                            private val walletID: String ?= null,
                                            private val categoryTitle: String ?= null,
                                            private val typeCategoryWallet: Int ?= null,
                                            private val activity: Activity) : BottomSheetDialogFragment() {
    private var mBinding: FragmentEditCategoryWalletBottomSheetBinding ?= null
    private lateinit var categoryAdapter: CategoryImportAdapter
    private lateinit var walletImportAdapter: WalletImportAdapter
    var counter = 0

    companion object{
        const val TAG = ""
    }
    private val chooseCategoryFragVM = ChooseCategoryFragVM()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentEditCategoryWalletBottomSheetBinding.inflate(inflater,container,false)
        return mBinding!!.root
    }
    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val bottomSheet: View? = dialog?.findViewById(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            view?.post {
                val parent = requireView().parent as View
                val params = parent.layoutParams as CoordinatorLayout.LayoutParams
                params.topMargin = 20.toPx().toInt()
                val behavior = params.behavior
                val bottomSheetBehavior = behavior as BottomSheetBehavior<*>
                bottomSheetBehavior.peekHeight = requireView().measuredHeight
                (bottomSheet?.parent as View).setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeCategoryWallet.let {
            if (it != null) {
                checkTypeDataRecyclerView(it)
            }
        }
        if (typeCategoryWallet != null) {
            counter = typeCategoryWallet
        }
        if (typeCategoryWallet == 0) {
            getCategory()
        } else {
            initRecyclerViewWallet()
        }
        initListener()
    }

    private fun getCategory() {
        getCategoryList {
            initRecyclerViewCategory()
        }
    }

    private fun checkTypeDataRecyclerView(counter: Int) {
        if (counter == 0) {
            mBinding?.editCategory?.setCardBackgroundColor(Color.parseColor("#FFC767"))
            mBinding?.category?.setTextColor(ContextCompat.getColor(requireContext(),R.color.neutral))
            mBinding?.editWallet?.setCardBackgroundColor(Color.parseColor("#F1F0F0"))
            mBinding?.wallet?.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkDisableToggleStrokeColor))
        }else {
            mBinding?.editWallet?.setCardBackgroundColor(Color.parseColor("#FFC767"))
            mBinding?.wallet?.setTextColor(ContextCompat.getColor(requireContext(),R.color.neutral))
            mBinding?.editCategory?.setCardBackgroundColor(Color.parseColor("#F1F0F0"))
            mBinding?.category?.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkDisableToggleStrokeColor))
        }
    }
    private fun initListener() {
        mBinding?.editCategory?.setOnClickListener {
            counter = 0
            getCategory()
            checkTypeDataRecyclerView(0)
        }
        mBinding?.editWallet?.setOnClickListener {
            counter = 1
            initRecyclerViewWallet()
            checkTypeDataRecyclerView(1)
        }
        mBinding?.backBtnLayout?.setOnClickListener {
            dialog?.dismiss()
        }
        mBinding?.createWalletBtn?.setOnClickListener {
            if (counter == 0) {
                val newCategoryFrag = NewCategoryFrag(isImport = true,this, activity)
                newCategoryFrag.show(requireActivity().supportFragmentManager, "")
            } else {
                checkProUser()
            }
        }
    }

    private fun checkProUser() {
        if (isProUser.value == true) {
            Extension.goToNewActivity(requireActivity(), CreateWallet::class.java)
        }
        else {
            notProAccess()
        }
    }
    private fun notProAccess() {
        if (walletList.value?.size!! < 2) {
            Extension.goToNewActivity(requireActivity(), CreateWallet::class.java)
        } else {
            val premiumSubFrag = PremiumSubFrag()
            premiumSubFrag.show(requireActivity().supportFragmentManager, "")
        }
    }
    private fun getCategoryList(onSuccess: () -> Unit) {
        if (incomeModel.value?.size == 0) {
            FirebaseManager(requireContext()).initCategory(
                onSuccess = {
                    onSuccess()
                },
                onFailure = {

                }
            )
        }
        else {
            onSuccess()
        }
    }
    fun initRecyclerViewCategory() {
        mBinding?.recyclerViewCategoryWallet?.apply {
            layoutManager = LinearLayoutManager(activity)
            LinearLayoutManager(activity).orientation = RecyclerView.VERTICAL
            categoryAdapter      = CategoryImportAdapter(mListener = categoryListener , editCategoryWalletBottomSheetFragment = this@EditCategoryWalletBottomSheetFragment, titleCategory = categoryTitle!! )
            adapter       = categoryAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
    }

    private val categoryListener = object : OnClickedListener<Category> {
        override fun onClicked(itemView: View?, position: Int?, model: Category) {
            super.onClicked(itemView, position, model)
            categoryListenerBack?.onClicked(itemView = itemView, position = position , model = model)
        }
    }
     private fun initRecyclerViewWallet() {
        mBinding?.recyclerViewCategoryWallet?.apply {
            layoutManager = LinearLayoutManager(activity)
            LinearLayoutManager(activity).orientation = RecyclerView.VERTICAL
            walletImportAdapter      = walletList.value?.let { WalletImportAdapter(it,walletListener,this@EditCategoryWalletBottomSheetFragment, walletID = walletID!!) }!!
            adapter       = walletImportAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false

        }
    }

    private val walletListener = object : OnClickedListener<WalletTrans> {
        override fun onClicked(itemView: View?, position: Int?, model: WalletTrans) {
            super.onClicked(itemView, position, model)
            walletListenerBack?.onClicked(itemView = itemView,position = position, model = model)
        }
    }
}