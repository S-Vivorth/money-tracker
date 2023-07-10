package io.paraga.moneytrackerdev.views.auth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.databinding.ActivityLoginOptionBinding
import io.paraga.moneytrackerdev.utils.helper.CustomToastHelper.showCustomToast
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Extension.Extension.changeLanguage
import io.paraga.moneytrackerdev.viewmodels.auth.AuthVM
import io.paraga.moneytrackerdev.viewmodels.auth.LogInOptionVM
import io.paraga.moneytrackerdev.views.MainActivity


class LoginOption : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOptionBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val authVM = AuthVM()
    private val logInOptionVM = LogInOptionVM()

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
        binding = ActivityLoginOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGso()
        binding.ctnWithEmailBtn.setOnClickListener {
            ctnWithEmail()
        }

        binding.ctnWithGoogleBtn.setOnClickListener {
            logInWithGoogle()
        }

    }
    fun initGso() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                Config().serverClientId
            )
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun logInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                authVM.checkEmailExist(email = task.result.email.toString(),
                    onSuccess = {
                        try {
                            val account = task.getResult(ApiException::class.java)
                            firebaseAuthWithGoogle(account.idToken!!, true)
                        }
                        catch (ex: ApiException) {
                            Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
                        }
                    },
                    onFailed = {
                        try {
                            val account = task.getResult(ApiException::class.java)
                            firebaseAuthWithGoogle(account.idToken!!, false)
                        }
                        catch (ex: ApiException) {
                            Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
                        }
                    })
            }
            else{
                Log.d("exc", task.exception.toString())
                Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
            }
        }
    }
//    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            result: ActivityResult ->
//
//    }
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constants.RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            if (task.isSuccessful) {
//                authVM.checkEmailExist(email = task.result.email.toString(),
//                    onSuccess = {
//                        try {
//                            val account = task.getResult(ApiException::class.java)
//                            firebaseAuthWithGoogle(account.idToken!!, true)
//                        }
//                        catch (ex: ApiException) {
//                            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
//                        }
//                    },
//                    onFailed = {
//                        try {
//                            val account = task.getResult(ApiException::class.java)
//                            firebaseAuthWithGoogle(account.idToken!!, false)
//                        }
//                        catch (ex: ApiException) {
//                            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
//                        }
//                    })
//
//
//            }
//            else{
//                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

//    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            if (task.isSuccessful) {
//                try {
//                    val account = task.getResult(ApiException::class.java)
//                    firebaseAuthWithGoogle(account.idToken!!)
//                }
//                catch (ex: ApiException) {
//                    Toast.makeText(this, "Failed1", Toast.LENGTH_LONG).show()
//                }
//            }
//            else{
//                Toast.makeText(this, "Failed2", Toast.LENGTH_LONG).show()
//            }
//        }
//
//    }

    private fun firebaseAuthWithGoogle(idToken: String, isUserExist: Boolean) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    if (!isUserExist) {

                        logInOptionVM.addUserData(this, onSuccess = {
                            Extension.goToNewActivity(this, MainActivity::class.java, clearAll = true, clearTop = true)
                            val editor = applicationContext.getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value, MODE_PRIVATE).edit()
                            editor.putString(Enums.SharePref.LOGIN_TYPE.value, Enums.SharePref.GOOGLE.value)
                            editor.apply()
                        },
                        onFailure = {
                            Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
                        })
                    }
                    else {
                        Extension.goToNewActivity(this, MainActivity::class.java, clearAll = true, clearTop = true)
                    }
                } else {
                    Toast(this).showCustomToast(getString(R.string.something_went_wrong), this)
                }
            }
    }


    private fun ctnWithEmail(){
        startActivity(Intent(this, InputEmailLogin::class.java))
    }

    private fun ctnWithGoogle(){

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity();
        finish()
    }
}