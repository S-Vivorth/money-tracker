package io.paraga.moneytrackerdev.views.wallet

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.FragmentColorListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ColorListFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class ColorListFrag(val parent: Any, val isEditWallet: Boolean = false) : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentColorListBinding
    lateinit var previousColorLayout: RelativeLayout
    lateinit var imageView: ImageView
    val vp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    var selectedIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        if (parent is CreateWallet) {
            parent.expandColorSelected = true
            parent.firstColorSelectedIndex = selectedIndex
        }
        super.onDismiss(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentColorListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpColorLayout()

        binding.backBtnLayout.setOnClickListener {
            dismiss()
        }

        binding.firstColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 1
        }

        binding.secondColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 2
        }

        binding.thirdColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 3
        }

        binding.forthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 4
        }

        binding.fifthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 5
        }

        binding.sixthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 6
        }

        binding.seventhColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 7
        }

        binding.eightColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 8
        }

        binding.ninthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 9
        }

        binding.tenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 10
        }

        binding.eleventhColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)
            selectedIndex = 11
        }

        binding.twelfthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 12
        }

        binding.thirteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 13
        }
        binding.fourteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 14
        }
        binding.fifteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 15
        }
        binding.sixteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 16
        }
        binding.seventeenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 17
        }
        binding.eighteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 18
        }
        binding.ninteenthColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 19
        }
        binding.twentiethColorLayout.setOnClickListener {
            selectedColor(it as RelativeLayout)

            selectedIndex = 20
        }

    }


    private fun selectedColor(colorLayout: RelativeLayout){
        if (isEditWallet) {
            (parent as EditWallet).previousColorLayout.removeView(parent.imageView)
            if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.default_og)) {
                parent.binding.secondColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.secondColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.vividCerulean)) {
                parent.binding.thirdColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.thirdColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.illuminating_emerald)) {
                parent.binding.forthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.forthColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.maximum_blue_green)) {
                parent.binding.fifthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.fifthColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.primaryPurple)) {
                parent.binding.sixthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.sixthColorLayout

            }
            else {
                parent.binding.firstColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.firstColorLayout
                parent.binding.firstColor.setCardBackgroundColor((colorLayout.children.first() as CardView).cardBackgroundColor)
            }
            previousColorLayout.removeView(imageView)

            colorLayout.addView(imageView)
            previousColorLayout = colorLayout

            //change views color
            val color = (colorLayout.children.first() as CardView).cardBackgroundColor
            parent.changeViewsColor(color)
        }
        else {
            (parent as CreateWallet).previousColorLayout.removeView(parent.imageView)
            if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.default_og)) {
                parent.binding.secondColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.secondColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.vividCerulean)) {
                parent.binding.thirdColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.thirdColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.illuminating_emerald)) {
                parent.binding.forthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.forthColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.maximum_blue_green)) {
                parent.binding.fifthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.fifthColorLayout
            }
            else if ((colorLayout.children.first() as CardView).cardBackgroundColor.defaultColor == requireContext().getColor(R.color.primaryPurple)) {
                parent.binding.sixthColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.sixthColorLayout

            }
            else {
                parent.binding.firstColorLayout.addView(parent.imageView)
                parent.previousColorLayout = parent.binding.firstColorLayout
                parent.binding.firstColor.setCardBackgroundColor((colorLayout.children.first() as CardView).cardBackgroundColor)
            }
            previousColorLayout.removeView(imageView)

            colorLayout.addView(imageView)
            previousColorLayout = colorLayout


            //change views color
            val color = (colorLayout.children.first() as CardView).cardBackgroundColor
            parent.changeViewsColor(color)
        }
        dismiss()
    }


    private fun setUpColorLayout(){
        imageView = ImageView(context)
        when (selectedIndex) {
            1 -> previousColorLayout = binding.firstColorLayout
            2 -> previousColorLayout = binding.secondColorLayout
            3 -> previousColorLayout = binding.thirdColorLayout
            4 -> previousColorLayout = binding.forthColorLayout
            5 -> previousColorLayout = binding.fifthColorLayout
            6 -> previousColorLayout = binding.sixthColorLayout
            7 -> previousColorLayout = binding.seventhColorLayout
            8 -> previousColorLayout = binding.eightColorLayout
            9 -> previousColorLayout = binding.ninthColorLayout
            10 -> previousColorLayout = binding.tenthColorLayout
            11 -> previousColorLayout = binding.eleventhColorLayout
            12 -> previousColorLayout = binding.twelfthColorLayout
            13 -> previousColorLayout = binding.thirteenthColorLayout
            14 -> previousColorLayout = binding.fourteenthColorLayout
            15 -> previousColorLayout = binding.fifteenthColorLayout
            16 -> previousColorLayout = binding.sixteenthColorLayout
            17 -> previousColorLayout = binding.seventeenthColorLayout
            18 -> previousColorLayout = binding.eighteenthColorLayout
            19 -> previousColorLayout = binding.ninteenthColorLayout
            20 -> previousColorLayout = binding.twentiethColorLayout
        }


        val id = resources.getIdentifier("check", "drawable", context?.packageName)
        vp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        imageView.layoutParams = vp
        imageView.setImageResource(id)
        imageView.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.white),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        previousColorLayout.addView(imageView)
    }

}