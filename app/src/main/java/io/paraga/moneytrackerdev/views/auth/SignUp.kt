package io.paraga.moneytrackerdev.views.auth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivitySignUpBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.SignUpVM
import io.paraga.moneytrackerdev.views.MainActivity


class SignUp : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    val signUpVM = SignUpVM()
    private var isPwVisible = false

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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.backBtn.setOnClickListener {
            finish()
        }

        binding.clearAllBtn.setOnClickListener {
            binding.userName.text.clear()

        }

        binding.pwVisibilityBtn.setOnClickListener {
            changePwVisibility(binding.password, (it as RelativeLayout).children.first() as ImageView)
        }

        binding.reEnterPwVisibilityBtn.setOnClickListener {
            changePwVisibility(binding.reEnterPw, (it as RelativeLayout).children.first() as ImageView)
        }

        val email: String = intent.getStringExtra(Enums.Extras.EMAIL.value).toString()
        binding.emailText.text = email

        binding.signUpBtn.setOnClickListener {
            val userName = binding.userName.text.toString()
            val password = binding.password.text.toString()
            val reEnterpw = binding.reEnterPw.text.toString()
            signUpVM.signUp(email = email,
                userName = userName,
                password = password,
                reEnterPassword = reEnterpw,
                context = this,
                onSuccess = {
                    val editor =
                        getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value, MODE_PRIVATE).edit()
                    editor.putString(
                        Enums.SharePref.LOGIN_TYPE.value,
                        Enums.SharePref.EMAIL_AND_PW.value
                    )
                    editor.apply()
                    goToHomeScreen()
                    finish()
                },
                onFailure = {
                    Toast(this).showCustomToast(it, this)
                })
        }

        binding.userName.setOnFocusChangeListener { view, hasFocus ->

            Extension.changeEditTextStroke(
                this,
                hasFocus,
                view,
                binding.personIcon,
                binding.crossIcon
            )

        }

        binding.password.setOnFocusChangeListener { view, hasFocus ->

            Extension.changeEditTextStroke(
                this,
                hasFocus,
                view,
                binding.lockIcon,
                binding.visibility
            )

        }

        binding.reEnterPw.setOnFocusChangeListener { view, hasFocus ->

            Extension.changeEditTextStroke(
                this,
                hasFocus,
                view,
                binding.reEnterPwLockIcon,
                binding.reEnterPwVisibility
            )

        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val usernameText = binding.userName
        val passwordText = binding.password
        val reEnterPwText = binding.reEnterPw
//        if (event?.action === MotionEvent.ACTION_DOWN) {
        if (usernameText.isFocused) {
            removeFocus(usernameText, event)
        }
        if (passwordText.isFocused) {
            removeFocus(passwordText, event)

        }
        if (reEnterPwText.isFocused) {
            removeFocus(reEnterPwText, event)
        }


//        }
        return super.dispatchTouchEvent(event)
    }

    private fun goToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
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
    private fun changePwVisibility(editText: EditText, icon: ImageView){
        if (isPwVisible) {
            icon.setImageResource(R.drawable.visible)
            editText.transformationMethod = PasswordTransformationMethod()
            isPwVisible = false
        }
        else{
            icon.setImageResource(R.drawable.invisible)
            editText.transformationMethod = null
            isPwVisible = true
        }
        editText.setSelection(editText.text.length)
    }

}