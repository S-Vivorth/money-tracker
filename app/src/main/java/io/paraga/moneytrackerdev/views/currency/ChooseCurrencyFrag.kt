package io.paraga.moneytrackerdev.views.currency

import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.adapters.CurrencyAdapter
import io.paraga.moneytrackerdev.databinding.FragmentChooseCurrencyBinding
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.transaction.CalculatorFrag
import io.paraga.moneytrackerdev.views.wallet.CreateWallet
import io.paraga.moneytrackerdev.views.wallet.EditWallet

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseCurrencyFrag.newInstance] factory method to
 * create an instance of this fragment.
 */

class ChooseCurrencyFrag() : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentChooseCurrencyBinding
    lateinit var currencyAdapter: CurrencyAdapter
    lateinit var currentCurrencyName: String
    lateinit var calculatorFrag: CalculatorFrag
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
        binding = FragmentChooseCurrencyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initRecyclerView()
        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }

        binding.currentCurrencyName.text = currentCurrencyName


        binding.currencySymbol.text = Extension.getCurrencySymbol(
            currentCurrencyName.slice(0..2)
        )

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currencyAdapter.filter(p0.toString())

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseCurrencyFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseCurrencyFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initRecyclerView(){
        if (context is EditWallet) {
            currencyAdapter = CurrencyAdapter(editWallet = context as EditWallet, chooseCurrencyFrag = this)
        }
        else if (context is CreateWallet){
            currencyAdapter = CurrencyAdapter(createWallet = context as CreateWallet, chooseCurrencyFrag = this)
        }
        else if (context is MainActivity){
            currencyAdapter = CurrencyAdapter(mainActivity = context as MainActivity, chooseCurrencyFrag = this)
        }
        else {
            currencyAdapter = CurrencyAdapter(calculatorFrag = calculatorFrag, chooseCurrencyFrag = this)
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.currencyRecyclerView.layoutManager = linearLayoutManager
        binding.currencyRecyclerView.adapter = currencyAdapter
        binding.currencyRecyclerView.setHasFixedSize(true)
        binding.currencyRecyclerView.isNestedScrollingEnabled = false

    }

}