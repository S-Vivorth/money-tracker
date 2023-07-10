package io.paraga.moneytrackerdev.views.category

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.adapters.NewCategoryAdapter
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.FragmentNewCategoryBinding
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.archivedCategoryModel
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.transListCopy
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Validation
import io.paraga.moneytrackerdev.viewmodels.category.NewCategoryVM
import io.paraga.moneytrackerdev.views.budget.BudgetCategoryFrag
import io.paraga.moneytrackerdev.views.importFile.bottomSheet.EditCategoryWalletBottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.set

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewCategoryFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewCategoryFrag(private val isImport: Boolean = false,private val editCategoryWalletBottomSheetFragment : EditCategoryWalletBottomSheetFragment ?= null,
val activity: Activity) : BottomSheetDialogFragment(), AdapterView.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentNewCategoryBinding
    lateinit var newCategoryAdapter: NewCategoryAdapter
    private val constant = Constants()
    var previousSelectedIndex = 0
    var selectedIconName = "ic_general"
    var selectedCategoryColor = "#BE4DE7"
    var oldTransType = Enums.TransTypes.EXPENSE.value
    var newTransType = Enums.TransTypes.EXPENSE.value
    var categories: HashMap<String, ArrayList<io.paraga.moneytrackerdev.models.Category>> = HashMap()
    private val newCategoryVM = NewCategoryVM()
    var oldCategory = io.paraga.moneytrackerdev.models.Category()
    var newCategory = io.paraga.moneytrackerdev.models.Category()
    var isEditCategory = false
    var isFromAddTrans = false
    var isFromBudget = false
    var chooseCategoryFrag = ChooseCategoryFrag(activity)
    lateinit var chooseBudgetCategoryFrag: BudgetCategoryFrag
    private val value = TypedValue()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGridView()




        binding.incomeBtn.setOnClickListener {
            changeToIncomeView()
        }

        binding.expenseBtn.setOnClickListener {
            changeToExpenseView()
        }

        binding.backBtnLayout.setOnClickListener {
            dismiss()
        }

        if (isEditCategory) {
            initEditCategory()
        }
        else {
            initNewCategory()
        }
        binding.saveBtn.setOnClickListener {
            updateCategory()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGridView() {
        newCategoryAdapter = NewCategoryAdapter(this)
        binding.categoryGridView.adapter = newCategoryAdapter
        binding.categoryGridView.isNestedScrollingEnabled = false
        binding.categoryGridView.onItemClickListener = this

        // to disable bottomsheet dismiss behaviour when gridview scroll
        binding.categoryGridView.setOnTouchListener { view, motionEvent ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            view.onTouchEvent(motionEvent)
        }
    }

    fun initEditCategory() {
        binding.newOrEditCategoryTitle.text = requireContext().getString(R.string.edit_category)
        binding.categoryNameEditText.setText(oldCategory.title)
        binding.categoryIcon.setImageResource(
            Extension.getResouceId(
                requireContext(),
                oldCategory.image
            )
        )
        if (oldTransType == Enums.TransTypes.INCOME.value) {
            binding.incomeBtn.performClick()
        }
        else {
            binding.expenseBtn.performClick()
        }
        binding.deleteCategoryBtn.visibility = View.VISIBLE

        binding.deleteCategoryBtn.setOnClickListener {
            deleteCategory()
        }
        try {
            gridItemClick(constant.categoryImgNameList.indexOf(oldCategory.image?.replace("-","_")))
        }
        catch(exc: Exception) {
            Log.d("exc", exc.toString())
        }

    }

    private fun deleteCategory() {
        DialogHelper.showPrimaryDialog(requireContext(), Enums.DialogType.CATEGORY.value,
        onOkayPressed = {
            updateCategory(isDelete = true)
        })
    }
    private fun updateCategory(isDelete: Boolean = false) {
        val categoryName = binding.categoryNameEditText.text.toString()
        if (!Validation().lengthValidation(categoryName, 1)) {
            Toast(context).showCustomToast(requireContext().getString(R.string.invalid_inputs), context as Activity)
        } else {
            newCategory.image = selectedIconName.replace("_", "-")
            newCategory.title = categoryName


            if (isDelete) {
                if (oldTransType == Enums.TransTypes.INCOME.value) {
                    incomeModel.value?.removeIf {
                        it == oldCategory
                    }
                } else {
                    expenseModel.value?.removeIf {
                        it == oldCategory
                    }
                }
                archivedCategoryModel.value?.add(oldCategory)
                Toast(context).showCustomToast(
                    categoryName + requireContext().getString(
                        R.string.is_deleted
                    ), requireActivity()
                )
            } else {
                if ((incomeModel.value!!.contains(newCategory) || expenseModel.value!!.contains(newCategory)) && (newTransType == oldTransType)) {
                    Toast(context).showCustomToast(
                        requireContext().getString(R.string.category_already_exist),
                        requireActivity()
                    )
                } else {
                    if (isEditCategory) {
                        if (oldTransType == Enums.TransTypes.INCOME.value) {
                            newCategory.selectedCount = oldCategory.selectedCount
                            newCategory.color = oldCategory.color
                            if (newTransType != oldTransType) {
                                incomeModel.value?.removeIf {
                                    it == oldCategory
                                }
                                expenseModel.value?.add(newCategory)
                            } else {

                                incomeModel.value?.find { it == oldCategory }?.apply {
                                    title = newCategory.title
                                    image = newCategory.image
                                    color = newCategory.color
                                    selectedCount = newCategory.selectedCount
                                }
                            }
                            Toast(context).showCustomToast(
                                categoryName + requireContext().getString(
                                    R.string.is_updated
                                ), requireActivity()
                            )
                        }
                        else {
                            newCategory.selectedCount = oldCategory.selectedCount
                            newCategory.color = oldCategory.color
                            if (newTransType != oldTransType) {
                                expenseModel.value?.remove(oldCategory)
                                incomeModel.value?.add(newCategory)
                            } else {
                                expenseModel.value?.find { it == oldCategory }?.apply {
                                    title = newCategory.title
                                    image = newCategory.image
                                    color = newCategory.color
                                    selectedCount = newCategory.selectedCount
                                }
                            }
                            Toast(context).showCustomToast(
                                categoryName + requireContext().getString(
                                    R.string.is_updated
                                ), requireActivity()
                            )
                        }
                    }
                    else {
                        newCategory.color = selectedCategoryColor
                        if (newTransType == Enums.TransTypes.INCOME.value) {
                            incomeModel.value?.add(
                                newCategory
                            )
                            Toast(context).showCustomToast(
                                "$categoryName " + requireContext().getString(R.string.is_created),
                                requireActivity()
                            )
                        }
                        else {
                            expenseModel.value?.add(
                                newCategory
                            )
                            Toast(context).showCustomToast(
                                "$categoryName " + requireContext().getString(R.string.is_created),
                                requireActivity()
                            )
                        }
                    }
                }
            }

            categories[Enums.DB.INCOME_FIELD.value] = incomeModel.value!!
            categories[Enums.DB.EXPENSE_FIELD.value] = expenseModel.value!!
            categories[Enums.DB.ARCHIVE.value] = archivedCategoryModel.value!!
            newCategoryVM.updateCategory(
                categories
            ) {
                Thread {
                    kotlin.run {
                        transList.value?.forEach {trans ->
                            if (newCategory.initialName == trans.category.initialName) {
                                trans.category = newCategory
                            }
                        }
                        transListCopy.postValue(transList.value)
                    }
                }.start()
            }
            if (isFromAddTrans) {
                chooseCategoryFrag.initRecyclerView()
                dismiss()
            }
            else if (isFromBudget) {
                chooseBudgetCategoryFrag.initRecyclerView()
                dismiss()
            }
            else {
                if (isImport) {
                    editCategoryWalletBottomSheetFragment?.initRecyclerViewCategory()
                    dismiss()
                } else {
                    Extension.goToNewActivity(
                        requireContext(),
                        Category::class.java,
                        clearTop = true
                    )
                }

            }



        }


    }

    fun initNewCategory() {
        Extension.showKeyboard(binding.categoryNameEditText, activity = requireActivity())
    }



    fun changeToIncomeView() {
        newTransType = Enums.TransTypes.INCOME.value
        binding.expenseBtn.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )
        binding.incomeBtn.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primaryGreen
            )
        )
        binding.incomeText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
        binding.expenseText.setTextColor(
            value.data
        )
    }

    fun changeToExpenseView() {
        newTransType = Enums.TransTypes.EXPENSE.value
        binding.incomeBtn.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )
        binding.expenseBtn.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primaryRed
            )
        )
        binding.expenseText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        context?.theme?.resolveAttribute(R.attr.unselectedIncomeExpenseTextColor, value, true)
        binding.incomeText.setTextColor(
            value.data
        )
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewCategoryFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, activity: Activity) =
            NewCategoryFrag(activity = activity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        gridItemClick(p2)
    }

    fun gridItemClick(previousSelectedIndex: Int) {
        newCategoryAdapter.previousSelectedIndex = previousSelectedIndex

        //need to refresh
        newCategoryAdapter.refresh()

        val iconName = constant.categoryList[previousSelectedIndex].image.toString()
        selectedIconName = iconName
        selectedCategoryColor = constant.categoryList[previousSelectedIndex].color.toString()
        binding.categoryIcon.setImageResource(
            Extension.getResouceId(
                requireContext(),
                constant.categoryList[previousSelectedIndex].image?.replace("-","_")
            )
        )
    }
}