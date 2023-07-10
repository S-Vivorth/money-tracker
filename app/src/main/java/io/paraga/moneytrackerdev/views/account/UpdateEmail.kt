package io.paraga.moneytrackerdev.views.account

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.databinding.ActivityUpdateEmailBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM
import io.paraga.moneytrackerdev.views.auth.ForgetPw


class UpdateEmail : AppCompatActivity() {
    lateinit var binding: ActivityUpdateEmailBinding
    val currentUser = auth.currentUser
    private val authVM = AuthVM()
    var isPwVisible = false
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
        binding = ActivityUpdateEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentEmail = currentUser?.email

        binding.emailText.text = currentEmail

        binding.backBtnLayout.backBtn.setOnClickListener {
            finish()
        }

        binding.clearEmailBtn.setOnClickListener {
            binding.newEmail.text.clear()
        }

        binding.clearReEnterEmailBtn.setOnClickListener {
            binding.reEnterEmail.text.clear()
        }

        binding.forgetPwBtn.setOnClickListener {
            val intent = Intent(this, ForgetPw::class.java)
            intent.putExtra(Enums.Extras.IS_FROM_PW_LOGIN.value, false)
            startActivity(intent)
        }

        binding.visibilityIcon.setOnClickListener {
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
        binding.doneBtn.setOnClickListener {
            val newEmail = binding.newEmail.text.toString()
            val confirmEmail = binding.reEnterEmail.text.toString()
            val password = binding.password.text.toString()

            authVM.changeEmail(currentEmail ?: "",
            newEmail,
            confirmEmail,
            password,
            onSuccess = {
                Toast(this).showCustomToast(getString(R.string.email_has_been_changed_successfully), this)
                Extension.goToNewActivity(this,
                    Profile::class.java,
                    clearTop = true)

            },
            onFailure = {
                Toast(this).showCustomToast(getString(R.string.failed_to_change_email), this)

            })
        }

        binding.newEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.emailIcon,
                    binding.crossIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                clearEditTextState(
                    view,
                    binding.emailIcon,
                    binding.crossIcon,
                    value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                clearEditTextStrokeColor(view, value.data)
            }


        }

        binding.reEnterEmail.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.reEnterEmailICon,
                    binding.reEnterEmailCrossIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                clearEditTextState(
                    view,
                    binding.reEnterEmailICon,
                    binding.reEnterEmailCrossIcon,
                    value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                clearEditTextStrokeColor(view, value.data)
            }

        }

        binding.password.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                theme.resolveAttribute(R.attr.primaryText, value, true)
                changeEditTextState(
                    this,
                    hasFocus,
                    view,
                    binding.personIcon,
                    binding.visibilityIcon,
                    value.data
                )
            } else {
                theme.resolveAttribute(R.attr.editTextHintTextColor, value, true)
                clearEditTextState(
                    view,
                    binding.personIcon,
                    binding.visibilityIcon,
                    value.data
                )
                theme.resolveAttribute(R.attr.separatorColor, value, true)
                clearEditTextStrokeColor(view, value.data)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val email = binding.newEmail
        val reEnterEmail = binding.reEnterEmail
        val password = binding.password
//        if (event?.action === MotionEvent.ACTION_DOWN) {
        if (email.isFocused) {
            removeFocus(email, event)
        }
        if (reEnterEmail.isFocused) {
            removeFocus(reEnterEmail, event)

        }
        if (password.isFocused) {
            removeFocus(password, event)
        }


//        }
        return super.dispatchTouchEvent(event)
    }

    fun changeEditTextState(
        ctx: Context, hasFocus: Boolean, view: View,
        leftIcon: ImageView,
        rightIcon: ImageView,
        color: Int
    ) {
        val backgroundGradient: GradientDrawable = view.background as GradientDrawable
        backgroundGradient.setStroke(2, ContextCompat.getColor(ctx, R.color.splashBgColor))
        leftIcon.setColorFilter(
            ContextCompat.getColor(ctx, R.color.splashBgColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        rightIcon.setColorFilter(
            color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    fun clearEditTextState(
        view: View,
        leftIcon: ImageView,
        rightIcon: ImageView,
        color: Int
    ) {
        val backgroundGradient: GradientDrawable = view.background as GradientDrawable
        leftIcon.setColorFilter(
            color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        rightIcon.setColorFilter(
            color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun clearEditTextStrokeColor(view: View,
    color: Int) {
        val backgroundGradient: GradientDrawable = view.background as GradientDrawable
        backgroundGradient.setStroke(
            2,
            color
        )
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