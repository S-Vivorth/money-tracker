package io.paraga.moneytrackerdev.views.category

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.adapters.ChooseCategoryAdapter
import io.paraga.moneytrackerdev.adapters.MostFrequentAdapter
import io.paraga.moneytrackerdev.databinding.FragmentChooseCategoryBinding
import io.paraga.moneytrackerdev.networks.FirebaseManager
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.viewmodels.category.ChooseCategoryFragVM
import io.paraga.moneytrackerdev.views.transaction.AddTrans

class ChooseCategoryFrag(val activity: Activity): BottomSheetDialogFragment() {
    private lateinit var mostFrequentAdapter: MostFrequentAdapter
    private lateinit var categoryAdapter: ChooseCategoryAdapter
    lateinit var binding: FragmentChooseCategoryBinding
    lateinit var addTrans: AddTrans
    lateinit var selectedCategory: Category
    var isExpense = false

    val chooseCategoryFragVM = ChooseCategoryFragVM()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseCategoryBinding.inflate(layoutInflater)
        view?.setBackgroundResource(android.R.color.transparent)
        return binding.root


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 0.9).toInt()
        return bottomSheetDialog
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategoryList {
            initRecyclerView()
            chooseCategoryFragVM.mergeCategories.sortBy {
                it.selectedCount
            }

            binding.searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    categoryAdapter.filter(p0.toString())

                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }


        binding.crossIcon.setOnClickListener {
            dismiss()
        }
        binding.addCategoryBtn.setOnClickListener {
            val newCategoryFrag = NewCategoryFrag(isImport = false, activity = activity)
            newCategoryFrag.isFromAddTrans = true
            newCategoryFrag.chooseCategoryFrag = this
            newCategoryFrag.show(requireActivity().supportFragmentManager, "")
        }
        Log.d("categoryList", expenseModel.value.toString())

    }

    fun getCategoryList(onSuccess: () -> Unit) {
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

    fun initRecyclerView(){
        var mostFrequentList = ArrayList<io.paraga.moneytrackerdev.models.Category>()
        var counter = 0
        if (isExpense) {
            expenseModel.value?.forEach {
                if (it.selectedCount != 0) {
                    mostFrequentList.add(it)
                    counter+=1
                }
            }
            if (counter == 0) {
                try {
                    mostFrequentList = ArrayList(expenseModel.value?.subList(0,5))
                }
                catch (exc: java.lang.IndexOutOfBoundsException) {
                    mostFrequentList = ArrayList()
                }
            }
        }
        else {
            incomeModel.value?.forEach {
                if (it.selectedCount != 0) {
                    mostFrequentList.add(it)
                    counter+=1
                }
            }
            if (counter == 0) {
                try {
                    mostFrequentList = ArrayList(incomeModel.value?.subList(0,5))
                }
                catch (exc: java.lang.IndexOutOfBoundsException) {
                    mostFrequentList = ArrayList()
                }
            }
        }
        mostFrequentAdapter = MostFrequentAdapter(this, mostFrequentList)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.mostFrequentRecycler.layoutManager = linearLayoutManager
        binding.mostFrequentRecycler.adapter = mostFrequentAdapter
        binding.mostFrequentRecycler.setHasFixedSize(true)
        // to make vertical scroll recyclerview below work, otherwise it won't be able to scroll
        binding.mostFrequentRecycler.isNestedScrollingEnabled = false


        categoryAdapter = ChooseCategoryAdapter(this)
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.categoryRecycler.layoutManager = categoryLinearLayoutManager
        binding.categoryRecycler.adapter = categoryAdapter
        binding.categoryRecycler.setHasFixedSize(true)


    }
}