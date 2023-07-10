package io.paraga.moneytrackerdev.views.language

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.adapters.LanguageAdapter
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.FragmentChooseLanguageBinding
import io.paraga.moneytrackerdev.models.Language
import io.paraga.moneytrackerdev.networks.budgetList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.MainActivity
import io.paraga.moneytrackerdev.views.statistics.fragment.adapter.StatisticsAdapter
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseLanguage.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseLanguageFrag : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentChooseLanguageBinding
    lateinit var languageAdapter: LanguageAdapter
    lateinit var mainActivity: MainActivity
    var languageList: ArrayList<Language> = ArrayList()
    private val constants = Constants()
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
        binding = FragmentChooseLanguageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels * 1).toInt()
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.backBtnLayout.setOnClickListener {
            dismiss()
        }
        try {
            if (Extension.isAutoLanguage(mainActivity)) {
                languageList = ArrayList(constants.languageList)
                initRecyclerView(languageList)
            }
            else {
                val selectedLanguage = Preferences().getInstance().getLanguage(mainActivity)
                constants.languageList.forEach {
                    if (it.code == selectedLanguage) {
                        languageList.add(it)
                        return@forEach
                    }
                }
                languageList.addAll(constants.languageList)
                languageList = ArrayList(languageList.distinct())
                initRecyclerView(languageList)
                binding.autoTickIcon.visibility = View.GONE
            }
        }
        catch (_: UninitializedPropertyAccessException) {
            dismiss()
        }


        binding.autoLayout.setOnClickListener {
            Preferences().getInstance()
                .setAutoLanguage(
                    mainActivity, true,
                    languageCode = Locale.getDefault().language
                )
            mainActivity.changeLanguage()
            mainActivity.onConfigurationChanged(mainActivity.resources.configuration)
            dismiss()
        }
    }

    private fun initRecyclerView(languageList: ArrayList<Language>) {
        binding.languageRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            languageAdapter      = LanguageAdapter(mainActivity, this@ChooseLanguageFrag, languageList)
            adapter       = languageAdapter
            setHasFixedSize(true)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseLanguage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseLanguageFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}