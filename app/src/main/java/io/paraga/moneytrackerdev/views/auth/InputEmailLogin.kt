package io.paraga.moneytrackerdev.views.auth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityInputEmailLoginBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.InputEmailVM


class InputEmailLogin : AppCompatActivity() {
    private lateinit var binding: ActivityInputEmailLoginBinding
    private val inputEmailVM = InputEmailVM()
    private val value = TypedValue()

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
        binding = ActivityInputEmailLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.backBtn.setOnClickListener {
            finish()
        }
        binding.clearAllBtn.setOnClickListener {
            binding.emailText.text.clear()
        }

        binding.ctnEmailLoginBtn.setOnClickListener {
            val email:String = binding.emailText.text.toString().trim()

            inputEmailVM.checkEmailExist(email, context = this, onSuccess = {
                if (it) {
                    goToInputPw(email)
                }
                else {
                    goToSignUp(email)
                }
            },
            onFailed = {
                Toast(this).showCustomToast(it, this)
            })
        }

        binding.emailText.setOnFocusChangeListener { view, hasFocus ->
            Extension.changeEditTextStroke(this, hasFocus, view, binding.emailIcon, binding.crossIcon)
//            if (hasFocus) {
//                val backgroundGradient: GradientDrawable = view.background as GradientDrawable
//                backgroundGradient.setStroke(2, ContextCompat.getColor(this, R.color.splashBgColor))
//            }
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val mEditText = binding.emailText
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
    private fun goToInputPw(email: String){
        val intent = Intent(this, InputPwLogin::class.java)
        intent.putExtra(Enums.Extras.EMAIL.value, email)
        startActivity(intent)
    }

    private fun goToSignUp(email: String){
        val intent = Intent(this, SignUp::class.java)
        intent.putExtra(Enums.Extras.EMAIL.value, email)
        startActivity(intent)
    }

}