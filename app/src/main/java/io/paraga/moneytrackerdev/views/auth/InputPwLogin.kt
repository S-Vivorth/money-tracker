package io.paraga.moneytrackerdev.views.auth

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityInputPwLoginBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM

class InputPwLogin : AppCompatActivity() {
    lateinit var binding: ActivityInputPwLoginBinding
    val authVM = AuthVM()
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
        binding = ActivityInputPwLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backBtn.backBtn.setOnClickListener {
            finish()
        }

        binding.pwVisibilityBtn.setOnClickListener {
            changePwVisibility()
        }

        val email: String = intent.getStringExtra(Enums.Extras.EMAIL.value).toString()
        binding.emailText.text = email

        binding.forgetPwBtn.setOnClickListener {
            Extension.goToNewActivity(this, ForgetPw::class.java)
        }

        binding.loginBtn.setOnClickListener {
            val password = binding.password.text.toString()
            authVM.logInWithPw(email,password,
                context = this,
            onSuccess = {
                Extension.goToHomeScreen(this, clearAll = true)
                val editor = getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value, MODE_PRIVATE).edit()
                editor.putString(Enums.SharePref.LOGIN_TYPE.value, Enums.SharePref.EMAIL_AND_PW.value)
                editor.apply()
                finish()
            },
            onFailure = {
                Toast(this).showCustomToast(it, this)
            })
        }

        binding.password.setOnFocusChangeListener { view, hasFocus ->
            Extension.changeEditTextStroke(this, hasFocus, view, binding.lockIcon, binding.visibilityIcon)

        }

    }
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val mEditText = binding.password
//        if (event?.action === MotionEvent.ACTION_DOWN) {
        val v = currentFocus
        if (mEditText.isFocused) {
            val outRect = Rect()
            mEditText.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event?.rawX?.toInt() ?: 0, event?.rawY?.toInt() ?: 0)) {
                mEditText.clearFocus()
                //
                // Hide keyboard
                //
                val imm =
                    v!!.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
//        }
        return super.dispatchTouchEvent(event)
    }
    private fun changePwVisibility(){
        if (isPwVisible) {
            binding.visibilityIcon.setImageResource(R.drawable.visible)
            binding.password.transformationMethod = PasswordTransformationMethod()
            isPwVisible = false
        }
        else{
            binding.visibilityIcon.setImageResource(R.drawable.invisible)
            binding.password.transformationMethod = null
            isPwVisible = true
        }
        binding.password.setSelection(binding.password.text.length)

    }
}
