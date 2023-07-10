package io.paraga.moneytrackerdev.views.auth

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityForgetPwBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM


class ForgetPw : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPwBinding
    private val authVM = AuthVM()
    private var isFromPwLogin = true

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
        binding = ActivityForgetPwBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.backBtn.setOnClickListener {
            finish()
        }
        isFromPwLogin = intent.getBooleanExtra(Enums.Extras.IS_FROM_PW_LOGIN.value, true)

        if (!isFromPwLogin) {
            binding.backToLoginBtnLayout.visibility = View.GONE
        }
        binding.clearAllBtn.setOnClickListener {
            binding.email.text.clear()
        }
        binding.backToLoginBtn.setOnClickListener {
            Extension.goToNewActivity(this, LoginOption::class.java)
        }

        binding.resetPwBtn.setOnClickListener {
            val email = binding.email.text.toString()
            authVM.resetPassword(email, onSuccess = {
                Toast(this).showCustomToast(getString(R.string.reset_link_has_been_sent), this)
                Extension.goToNewActivity(this, InputEmailLogin::class.java, clearTop = true)
            }, onFailure = {
                Toast(this).showCustomToast(getString(R.string.failed_to_send_reset_link), this)
            })
        }

        binding.email.setOnFocusChangeListener { view, hasFocus ->
            Extension.changeEditTextStroke(this, hasFocus, view, binding.emailIcon, binding.crossIcon)
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val mEditText = binding.email
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
}