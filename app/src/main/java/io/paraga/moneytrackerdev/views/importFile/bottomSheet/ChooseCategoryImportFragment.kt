package io.paraga.moneytrackerdev.views.importFile.bottomSheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.utils.helper.OnClickedListener
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentChooseCategoryImportBinding
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.views.category.NewCategoryFrag
import io.paraga.moneytrackerdev.views.importFile.adapter.CategoryImportAdapter

class ChooseCategoryImportFragment(private val mResultBack: OnClickedListener<io.paraga.moneytrackerdev.models.Category> ?= null) : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentChooseCategoryImportBinding

    companion object {
        const val TAG = ""
    }

    var expense: Boolean = true
    private lateinit var categoryAdapter: CategoryImportAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentChooseCategoryImportBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 1)
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (incomeModel.value?.size == 0) {
            FirebaseManager(requireContext()).initCategory(
                onSuccess = {
                    setUpCategory()
                },
                onFailure = {

                }
            )
        } else {
//            Toast.makeText(this, "Already fetch", Toast.LENGTH_SHORT).show()
            setUpCategory()
        }
//        mBinding.saveBtn.setOnClickListener {
//            val newCategoryFrag = NewCategoryFrag(true)
//            val fm: FragmentManager? = activity?.supportFragmentManager
//            newCategoryFrag.apply {
//                if (fm != null) {
//                    show(fm, "")
//                }
//            }
//        }
    }
    fun setUpCategory() {
//        initRecyclerView(expenseModel.value!!, Enums.TransTypes.EXPENSE.value)
        mBinding.incomeBtn.setOnClickListener {
//            initRecyclerView(incomeModel.value!!, Enums.TransTypes.INCOME.value)

            mBinding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondaryGrey
                )
            )
            mBinding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryGreen
                )
            )
            mBinding.incomeText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            mBinding.expenseText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey
                )
            )
        }

        mBinding.expenseBtn.setOnClickListener {
            expense = false
//            initRecyclerView(expenseModel.value!!, Enums.TransTypes.EXPENSE.value)
            mBinding.incomeBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondaryGrey
                )
            )
            mBinding.expenseBtn.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryRed
                )
            )
            mBinding.expenseText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            mBinding.incomeText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey
                )
            )

        }


    }
//    private fun initRecyclerView(
//        categoryList: ArrayList<io.paraga.moneytrackerdev.models.Category>,
//        transType: Int
//    ) {
//        categoryAdapter =
//            CategoryImportAdapter(category = Category(), categoryList, transType, mListener)
//        val categoryLinearLayoutManager = LinearLayoutManager(activity)
//        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
//        mBinding.categoryRecycler.layoutManager = categoryLinearLayoutManager
//        mBinding.categoryRecycler.adapter = categoryAdapter
//        mBinding.categoryRecycler.setHasFixedSize(true)
//        categoryAdapter.notifyDataSetChanged()
//    }
    private fun getCategoryList() {
        if (incomeModel.value?.size == 0) {
            FirebaseManager(requireContext()).initCategory(
                onSuccess = {
                },
                onFailure = {

                }
            )
        } else {
//            initRecyclerView(incomeModel.value!!, Enums.TransTypes.INCOME.value)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private val mListener =
        object : OnClickedListener<io.paraga.moneytrackerdev.models.Category> {
            override fun onClicked(
                itemView: View?,
                position: Int?,
                model: io.paraga.moneytrackerdev.models.Category
            ) {
                super.onClicked(itemView, position, model)
                mResultBack?.onClicked(itemView, position, model)
            }
        }
    fun updateList() {
        if (expense) {
//            initRecyclerView(expenseModel.value!!, Enums.TransTypes.EXPENSE.value)
        }

        }
    }
