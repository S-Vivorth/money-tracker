package io.paraga.moneytrackerdev.views.account

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityChangePwBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM
import io.paraga.moneytrackerdev.views.auth.ForgetPw


class ChangePw : AppCompatActivity() {
    private lateinit var binding: ActivityChangePwBinding
    private val authVM = AuthVM()
    private var isNewPwVisible = false
    private var isConfirmPwVisible = false
    private var value = TypedValue()

    override fun attachBaseContext(newBase: Context) {
        Extension.setTheme(newBase)
        if (!Extension.isAutoLanguage(newBase)) {
            super.attachBaseContext(ContextWrapper(newBase.changeLanguage()))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newPwRightIcon.setOnClickListener {
            changeNewPwVisibility()
        }

        binding.reEnterPasswordRightIcon.setOnClickListener {
            changeConfirmPwVisibility()
        }

        binding.currentPwRightIcon.setOnClickListener {
            binding.currentPw.text.clear()
        }

        binding.backBtnLayout.backBtn.setOnClickListener {
            finish()
        }

        binding.forgetPwBtn.setOnClickListener {
            val intent = Intent(this, ForgetPw::class.java)
            intent.putExtra(Enums.Extras.IS_FROM_PW_LOGIN.value, false)
            startActivity(intent)
        }

        binding.doneBtn.setOnClickListener {

            val currentPw = binding.currentPw.text.toString()
            val newPw = binding.newPw.text.toString()
            val confirmPw = binding.reEnterPw.text.toString()

            authVM.changePw(currentPw, newPw, confirmPw,
            onSuccess = {
                Toast(this).showCustomToast(getString(R.string.password_is_updated_successfully), this)
                finish()
            },
            onFailure = {
                Toast(this).showCustomToast(getString(R.string.failed_to_update_password), this)
            })
        }


        binding.newPw.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                Extension.changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.newPwLeftIcon,
                    binding.newPwRightIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                Extension.clearEditTextState(
                    view,
                    binding.newPwLeftIcon,
                    binding.newPwRightIcon,
                    value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                Extension.clearEditTextStrokeColor(view, value.data)
            }
        }

        binding.reEnterPw.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                Extension.changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.reEnterPwLeftIcon,
                    binding.reEnterPasswordRightIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                Extension.clearEditTextState(
                    view,
                    binding.reEnterPwLeftIcon, binding.reEnterPasswordRightIcon,
                    value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                Extension.clearEditTextStrokeColor(view, value.data)
            }
        }


        binding.currentPw.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                Extension.changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.currentPwLeftIcon,
                    binding.currentPwRightIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                Extension.clearEditTextState(
                    view,
                    binding.currentPwLeftIcon,
                    binding.currentPwRightIcon,
                            value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                Extension.clearEditTextStrokeColor(view, value.data)
            }
        }

    }


    private fun changeNewPwVisibility(){
        if (isNewPwVisible) {
            binding.newPwRightIcon.setImageResource(R.drawable.visible)
            binding.newPw.transformationMethod = PasswordTransformationMethod()
            isNewPwVisible = false
        }
        else{
            binding.newPwRightIcon.setImageResource(R.drawable.invisible)
            binding.newPw.transformationMethod = null
            isNewPwVisible = true
        }
        binding.newPw.setSelection(binding.newPw.text.length)
    }

    private fun changeConfirmPwVisibility(){
        if (isConfirmPwVisible) {
            binding.reEnterPasswordRightIcon.setImageResource(R.drawable.visible)
            binding.reEnterPw.transformationMethod = PasswordTransformationMethod()
            isConfirmPwVisible = false
        }
        else{
            binding.reEnterPasswordRightIcon.setImageResource(R.drawable.invisible)
            binding.reEnterPw.transformationMethod = null
            isConfirmPwVisible = true
        }
        binding.reEnterPw.setSelection(binding.reEnterPw.text.length)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val newPw = binding.newPw
        val reEnterPw = binding.reEnterPw
        val currentPw = binding.currentPw
//        if (event?.action === MotionEvent.ACTION_DOWN) {
        if (newPw.isFocused) {
            removeFocus(newPw, event)
        }
        if (reEnterPw.isFocused) {
            removeFocus(reEnterPw, event)

        }
        if (currentPw.isFocused) {
            removeFocus(currentPw, event)
        }


//        }
        return super.dispatchTouchEvent(event)
    }

    private fun removeFocus(editText: EditText, event: MotionEvent?) {
        val v = currentFocus
        val outRect = Rect()
        editText.getGlobalVisibleRect(outRect)
        if (!outRect.contains(event?.rawX?.toInt() ?: 0, event?.rawY?.toInt() ?: 0)) {
            editText.clearFocus()
            //
            // Hide keyboard
            //
            val imm =
                v!!.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}