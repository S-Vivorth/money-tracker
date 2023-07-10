package io.paraga.moneytrackerdev.views.budget

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.adapters.ChooseBudgetCategoryAdapter
import io.paraga.moneytrackerdev.databinding.FragmentBudgetCategoryBinding
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.category.NewCategoryFrag

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BudgetCategoryFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class BudgetCategoryFrag : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBudgetCategoryBinding
    private lateinit var chooseBudgetCategoryAdapter: ChooseBudgetCategoryAdapter
    lateinit var createBudget: CreateBudget
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
        binding = FragmentBudgetCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.categoryIcon.setImageResource(Extension.getResouceId(requireContext(), createBudget.budget.category.image))
            binding.categoryName.text = Extension.getStringResourceByName(createBudget, createBudget.budget.category.title.toString())
        }
        catch (_: UninitializedPropertyAccessException) {
            dismiss()
        }


        initRecyclerView()
        binding.addCategoryBtn.setOnClickListener {
            val newCategoryFrag = NewCategoryFrag(isImport = false, activity = createBudget)
            newCategoryFrag.isFromBudget = true
            newCategoryFrag.chooseBudgetCategoryFrag = this
            newCategoryFrag.show(requireActivity().supportFragmentManager, "")
        }
        binding.backBtnLayout.setOnClickListener {
            dismiss()
        }
    }

    fun initRecyclerView() {
        binding.budgetCategoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            chooseBudgetCategoryAdapter = ChooseBudgetCategoryAdapter(this@BudgetCategoryFrag)
            adapter       = chooseBudgetCategoryAdapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BudgetCategoryFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BudgetCategoryFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}