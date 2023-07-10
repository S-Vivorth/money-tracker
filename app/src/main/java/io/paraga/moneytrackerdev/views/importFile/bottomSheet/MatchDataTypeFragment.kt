package io.paraga.moneytrackerdev.views.importFile.bottomSheet

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentMatchDataTypeBinding
import io.paraga.moneytrackerdev.models.ImportModel
import io.paraga.moneytrackerdev.models.PositionModel
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.views.importFile.activity.ImportDetailActivity

class MatchDataTypeFragment(
    private val listener: OnClickedListener<ImportModel>,
    private val position: Int,
    private var mListPosition: ArrayList<PositionModel> = arrayListOf(),
    val mList: ArrayList<ImportModel>,
    private val importDetailActivity: ImportDetailActivity
) : BottomSheetDialogFragment() {
    val value = TypedValue()

    private lateinit var mBinding: FragmentMatchDataTypeBinding
    companion object {
        const val TAG = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMatchDataTypeBinding.inflate(inflater,container,false)
        return mBinding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1)
        return bottomSheetDialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLister()
    }
    private fun initLister() {
        mList.forEach {
            if (it.isCheck && it.title == getString(R.string.date)) {
                mBinding.layoutDate.isEnabled = false
                mBinding.layoutDate.isClickable = false
                mBinding.checkboxDate.isChecked = it.isCheck
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textDate.setTextColor(value.data)
                mBinding.imgDate.setColorFilter(value.data, android.graphics.PorterDuff.Mode.MULTIPLY)
                mBinding.layoutDate.setBackgroundResource(R.drawable.rounded_drawer_while_select)
                mBinding.checkboxDate.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))

            } else {
                mBinding.layoutDate.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.date),R.drawable.calendar_month.toString(),true,1)
                    listener.onClicked(itemView = mBinding.layoutDate, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
            if (it.isCheck && it.title == getString(R.string.remark)) {
                mBinding.layoutRemark.isEnabled = false
                mBinding.layoutRemark.isClickable = false
                mBinding.checkboxRemark.isChecked = it.isCheck
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textRemark.setTextColor(value.data)
                mBinding.imgRemark.setColorFilter(value.data, android.graphics.PorterDuff.Mode.MULTIPLY)
                mBinding.layoutRemark.setBackgroundResource(R.drawable.rounded_drawer_while_select)
                mBinding.checkboxRemark.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))
            } else {
                mBinding.layoutRemark.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.remark),R.drawable.description.toString(),true,2)
                    listener.onClicked(itemView = mBinding.layoutRemark, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
            if (it.isCheck && it.title == getString(R.string.category)) {
                mBinding.layoutCategory.isEnabled = false
                mBinding.layoutCategory.isClickable = false
                mBinding.checkboxCategory.isChecked = true
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textCategory.setTextColor(value.data)
                mBinding.imgCategory.setColorFilter(value.data, android.graphics.PorterDuff.Mode.MULTIPLY)
                mBinding.layoutCategory.setBackgroundResource(R.drawable.rounded_drawer_while_select)
                mBinding.checkboxCategory.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))
            } else {
                mBinding.layoutCategory.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.category),R.drawable.bookmarks.toString(),true,3)
                    listener.onClicked(itemView = mBinding.layoutCategory, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
            if (it.isCheck && it.title == getString(R.string.amount_auto)) {
                mBinding.layoutAmountAuto.isEnabled = false
                mBinding.layoutAmountAuto.isClickable = false
                mBinding.checkboxAmountAuto.isChecked = true
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textAmountAuto.setTextColor(value.data)
                mBinding.checkboxCategory.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))
                //expense
                mBinding.layoutAmountExpense.isEnabled = false
                mBinding.layoutAmountExpense.isClickable = false
//                mBinding.textAmountExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
                mBinding.textAmountExpense.setTextColor(value.data)

                //income
                mBinding.layoutAmountIncome.isEnabled = false
                mBinding.layoutAmountIncome.isClickable = false
                mBinding.textAmountIncome.setTextColor(value.data)
//                mBinding.textAmountIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
            } else {
                mBinding.layoutAmountAuto.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.amount_auto),R.drawable.hdr_auto.toString(),true,4)
                    listener.onClicked(itemView = mBinding.layoutAmountAuto, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
            if (it.isCheck && it.title == getString(R.string.amount_expense)) {
                mBinding.layoutAmountExpense.isEnabled = false
                mBinding.layoutAmountExpense.isClickable = false
                mBinding.checkboxAmountExpense.isChecked = it.isCheck
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textAmountExpense.setTextColor(value.data)
                mBinding.checkboxAmountExpense.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))

                //amount auto
                mBinding.layoutAmountAuto.isEnabled = false
                mBinding.layoutAmountAuto.isClickable = false
                mBinding.textAmountAuto.setTextColor(value.data)


            } else {
                mBinding.layoutAmountExpense.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.amount_expense),R.drawable.arrow_circle_up.toString(),true,5)
                    listener.onClicked(itemView = mBinding.layoutAmountExpense, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
            if (it.isCheck && it.title == getString(R.string.amount_income)) {
                mBinding.layoutAmountIncome.isEnabled = false
                mBinding.layoutAmountIncome.isClickable = false
                mBinding.checkboxAmountIncome.isChecked = it.isCheck
                importDetailActivity.theme.resolveAttribute(R.attr.colorTextBottomSheet,value, true)
                mBinding.textAmountIncome.setTextColor(value.data)
                mBinding.checkboxAmountIncome.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_alpha6))
                //amount auto
                mBinding.layoutAmountAuto.isEnabled = false
                mBinding.layoutAmountAuto.isClickable = false
                mBinding.textAmountAuto.setTextColor(value.data)
            } else {
                mBinding.layoutAmountIncome.setOnClickListener {
                    val importModel = ImportModel(getString(R.string.amount_income),R.drawable.arrow_circle_down.toString(),true,6)
                    listener.onClicked(itemView = mBinding.layoutAmountIncome, position = position, model = importModel)
                    dialog?.dismiss()
                }
            }
        }
        mBinding.layoutIng.setOnClickListener {
            val importModel = ImportModel(getString(R.string.ingored),R.drawable.do_not_disturb_on.toString(),true,0)
            listener.onClicked(itemView = mBinding.layoutIng, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutDate.setOnClickListener {
            val importModel = ImportModel(getString(R.string.date),R.drawable.calendar_month.toString(),true,1)
            listener.onClicked(itemView = mBinding.layoutDate, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutRemark.setOnClickListener {
            val importModel = ImportModel(getString(R.string.remark),R.drawable.description.toString(),true,2)
            listener.onClicked(itemView = mBinding.layoutRemark, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutWallet.setOnClickListener {
            val importModel = ImportModel(getString(R.string.wallet),R.drawable.account_balance_wallet.toString(),true,3)
            listener.onClicked(itemView = mBinding.layoutWallet, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutCategory.setOnClickListener {
            val importModel = ImportModel(getString(R.string.category),R.drawable.bookmarks.toString(),true,3)
            listener.onClicked(itemView = mBinding.layoutCategory, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutAmountAuto.setOnClickListener {
            val importModel = ImportModel(getString(R.string.amount_auto),R.drawable.hdr_auto.toString(),true,4)
            listener.onClicked(itemView = mBinding.layoutAmountAuto, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutAmountExpense.setOnClickListener {
            val importModel = ImportModel(getString(R.string.amount_expense),R.drawable.arrow_circle_up.toString(),true,5)
            listener.onClicked(itemView = mBinding.layoutAmountExpense, position = position, model = importModel)
            dialog?.dismiss()
        }
        mBinding.layoutAmountIncome.setOnClickListener {
            val importModel = ImportModel(getString(R.string.amount_income),R.drawable.arrow_circle_down.toString(),true,6)
            listener.onClicked(itemView = mBinding.layoutAmountIncome, position = position, model = importModel)
            dialog?.dismiss()
        }
    }
}