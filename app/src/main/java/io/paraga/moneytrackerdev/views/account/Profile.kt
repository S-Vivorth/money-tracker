package io.paraga.moneytrackerdev.views.account

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.ActivityProfileBinding
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.DialogHelper
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.account.ProfileVM
import io.paraga.moneytrackerdev.viewmodels.auth
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM
import io.paraga.moneytrackerdev.views.auth.LoginOption
import io.paraga.moneytrackerdev.views.isProUser
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*



class Profile : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    private val authVM = AuthVM()
    val currentUser = auth.currentUser
    private val profileVM = ProfileVM()
    private lateinit var googleSignInClient: GoogleSignInClient
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
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGso()
        if (isProUser.value!!) {
            binding.proLayout.visibility = View.VISIBLE
        } else {
            binding.proLayout.visibility = View.GONE
        }

        binding.backBtnLayout.backBtn.setOnClickListener {
            this.finish()
        }

        binding.changePwBtn.setOnClickListener {
            startActivity(Intent(this, ChangePw::class.java))
        }

        binding.emailLayout.setOnClickListener {
            startActivity(Intent(this, UpdateEmail::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            DialogHelper.showPrimaryDialog(this, Enums.DialogType.LOG_OUT.value,
                onOkayPressed = {
                    authVM.signOut(this)
                    val intent = Intent(this, LoginOption::class.java)
                    finishAffinity()
                    startActivity(intent)
                })


        }

        binding.cardProfile.setOnClickListener {
            changeProfImg()
        }

        binding.edit.setOnClickListener {
            changeProfImg()
        }

        profileVM.getUserData(onSuccess = {
            binding.accountName.text = profileVM.userModel.displayName
        },
        onFailure = {
        })


        if (getAuthType() == Enums.SharePref.GOOGLE.value) {
            binding.changePwBtn.isEnabled = false
            binding.emailLayout.isEnabled = false
            val nightModeFlags: Int = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)!!
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                switchToDisableState(R.color.darkSecondaryGreyTextColor)
            }
            else {
                switchToDisableState(R.color.black_500)
            }


            binding.deleteBtn.setOnClickListener {
                DialogHelper.showPrimaryDialog(
                    this,
                    Enums.DialogType.ACCOUNT.value,
                    onOkayPressed = { noticeDialog ->
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInClient.revokeAccess().addOnCompleteListener {

                        }
                        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
                    })
            }
            setUpProfile(true)
        }
        else{
            setUpProfile(false)
            binding.deleteBtn.setOnClickListener {

                DialogHelper.showPrimaryDialog(
                    this,
                    Enums.DialogType.ACCOUNT.value,
                    onOkayPressed = { noticeDialog ->
                        val imm: InputMethodManager? =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        noticeDialog.dismiss()
                        DialogHelper.showTextConfirmDialog(
                            this,
                            Enums.DialogType.PASSWORD.value,
                            onOkayPressed = { passwordDialog, password ->
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

                                passwordDialog.dismiss()
                                DialogHelper.showTextConfirmDialog(this,
                                    Enums.DialogType.DELETE.value,
                                    onOkayPressed = { deleteDialog, _ ->
                                        authVM.deleteAcc(
                                            password = password,
                                            context = this,
                                            onSuccess = {
                                                Extension.goToNewActivity(
                                                    this,
                                                    LoginOption::class.java,
                                                    clearAll = true
                                                )
                                            },
                                            onFailure = {
                                                Toast(this).showCustomToast("Could not delete account.", this)

                                            }
                                        )
                                    })
                            }
                        )
                    })
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)
                    DialogHelper.showTextConfirmDialog(this,
                        Enums.DialogType.DELETE.value,
                        onOkayPressed = { deleteDialog, _ ->
                            authVM.deleteAcc(
                                isGoogleSignIn = true,
                                account = account,
                                context = this,
                                onSuccess = {
                                    Extension.goToNewActivity(this, LoginOption::class.java, clearAll = true, clearTop = true)
                                },
                                onFailure = {

                                }
                            )
                        })

                }
                catch (ex: ApiException) {
                    Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
                }
            }
            else{
                Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.IMG_PICK_CONS) {
            var imgUri = data?.data
            val bitmap = Extension.imgUriToBitmap(imgUri, this)
            val compressedBitmap = Extension.compressBitmap(bitmap, 300)
            imgUri =  Extension.bitmapToImageUri(this, compressedBitmap)
            profileVM.uploadProfile(imageUri = imgUri, onSuccess = {
                Glide.with(this).load(imgUri).into(binding.profileImg)
            }, onFailure = {
                Toast(this).showCustomToast(getString(R.string.failed_to_upload_profile), this)
            })
        }


    }


    private fun getAuthType(): String{
        val pref = applicationContext.getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value, MODE_PRIVATE)
        // Enums.SharePref.GOOGLE.value is default value
        val authType = pref.getString(Enums.SharePref.LOGIN_TYPE.value, Enums.SharePref.GOOGLE.value)!!
        return authType
    }

    private fun changeProfImg(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.IMG_PICK_CONS)
    }



    private fun setUpProfile(isGoogleLogin: Boolean){

        profileVM.getProfileUrl(auth.currentUser?.uid,onSuccess = {
            Glide.with(this).load(it).into(binding.profileImg)
//            profileProVisibility(true)
        },
            onFailure = {
                if (isGoogleLogin) {
                    Glide.with(applicationContext).load(currentUser?.photoUrl).into(binding.profileImg)
                }
            })

        binding.accountName.text = user?.displayName
        binding.email.text = user?.email
    }

    private fun profileProVisibility(isVisible: Boolean) {
        if (isVisible) {
            if (isProUser.value!!) {
                binding.proLayout.visibility = View.VISIBLE
            }

        }

    }

    private fun switchToDisableState(color: Int) {
        binding.changePwBtn.setTextColor(ContextCompat.getColor(this, color))
        binding.accountIcon.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, color)
        )
        binding.accountName.setTextColor(ContextCompat.getColor(this, color))
        binding.emailIcon.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, color))
        binding.email.setTextColor(ContextCompat.getColor(this, color))
        binding.editIcon.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, color))
    }

    private fun initGso() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                Config().serverClientId
            ).requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}