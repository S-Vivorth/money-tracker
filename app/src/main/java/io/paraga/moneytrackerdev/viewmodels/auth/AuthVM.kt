package io.paraga.moneytrackerdev.viewmodels.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.Config
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.networks.*
import io.paraga.moneytrackerdev.utils.helper.Validation
import io.paraga.moneytrackerdev.viewmodels.auth
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.isProUser
import io.paraga.moneytrackerdev.views.selectedWalletId
import java.util.concurrent.CopyOnWriteArrayList


class AuthVM: ViewModel() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val validation = Validation()
    val currentUser = auth.currentUser

    fun initGso(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                Config().serverClientId
            ).requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    fun deleteAcc(
        isGoogleSignIn: Boolean = false,
        account: GoogleSignInAccount? = null,
        password: String? = null,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: () -> Unit) {
        val credential: AuthCredential
        if (isGoogleSignIn) {
            credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        } else {
            if (password!!.isEmpty()) {
                onFailure()
            }
            credential =
                EmailAuthProvider.getCredential(currentUser?.email.toString(), password)
        }
        currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                currentUser.delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.signOut()
                        initGso(context)
                        googleSignInClient.signOut()
                        clearAllData(context)
                        onSuccess()
                    } else {
                        Log.d("Error", it.exception.toString())
                        onFailure()
                    }
                }
            } else {
                onFailure()
            }
        }
    }

    fun logInWithPw(
        email: String, pw: String,
        context: Context,
        onSuccess: () -> Unit, onFailure: (exc: String) -> Unit
    ) {
        if (validation.lengthValidation(pw, 8)) {
            auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(context.getString(R.string.incorrect_email_or_password))
                }
            }
        } else {
            onFailure(context.getString(R.string.invalid_password_length))
        }

    }

    fun changeEmail(currentEmail: String,
                    newEmail: String,
                    confirmEmail:String,
                    password: String,
                    onSuccess: () -> Unit,
                    onFailure: () -> Unit) {
        if (newEmail.isEmpty() && confirmEmail.isEmpty()) {
            onFailure()
        }

        if (password.isEmpty()) {
            onFailure()
        }

        if (!validation.compareText(newEmail, confirmEmail)) {
            onFailure()
        }

        if(!validation.emailValidation(newEmail)) {
            onFailure()
        } else{
            val credential = EmailAuthProvider.getCredential(currentUser?.email.toString(), password)
            currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if(it.isSuccessful) {
                    currentUser.updateEmail(newEmail).addOnCompleteListener {
                        if (it.isSuccessful) {
                            onSuccess()
                        } else{
                            onFailure()
                        }
                    }

                } else{
                    onFailure()
                }
            }
        }
    }

    fun changePw(currentPw: String, newPassword: String, confirmPw:String,
                 onSuccess: () -> Unit, onFailure: () -> Unit){
        if (validation.lengthValidation(currentPw, 8)) {
            if (validation.lengthValidation(newPassword, 8)) {
                if (validation.compareText(newPassword, confirmPw)) {
                    val credential = EmailAuthProvider.getCredential(currentUser?.email.toString(), currentPw)
                    currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            currentUser.updatePassword(newPassword).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    onSuccess()
                                } else{
                                    onFailure()
                                }
                            }
                        } else{
                            onFailure()
                        }
                    }
                } else{
                    onFailure()
                }
            } else{
                onFailure()
            }
        } else{
            onFailure()
        }


    }


    fun getSignInIntent(ctx: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                Config().serverClientId
            ).requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(ctx, gso)
        val signInIntent = googleSignInClient.signInIntent
        return signInIntent
//        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    fun firebaseAuthWithGoogle(idToken: String,
                               ctx: Context,
                               onSuccess: () -> Unit,
                               onFailure: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val editor = ctx.getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value,
                        AppCompatActivity.MODE_PRIVATE
                    ).edit()
                    editor.putString(Enums.SharePref.LOGIN_TYPE.value, Enums.SharePref.GOOGLE.value)
                    editor.apply()
                    currentUser?.delete()?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            signOut(ctx)
                            onSuccess()
                        } else{
                            onFailure()
                        }
                    }
                } else {
                    onFailure()
                }
            }
    }

    fun resetPassword(email: String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit){

        if (validation.emailValidation(email)) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(Enums.Messages.SENT_RESET_LINK.value)
                } else{
                    onFailure(Enums.Messages.ERROR_SEND_RESET_LINK.value)
                }
            }
        } else{
            onFailure(Enums.Messages.INVALID_EMAIL.value)
        }

    }

    fun checkEmailExist(email:String, onSuccess: ()->Unit, onFailed: ()-> Unit){
        auth = FirebaseAuth.getInstance()
        try {
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                val isNewUser = it.result.signInMethods?.isEmpty()
                if (isNewUser != true){
                    onSuccess()
                } else{
                    onFailed()
                }
            }
        }
        catch (exc: Exception) {
            onFailed()
        }

    }

    private fun clearAllData(context: Context) {
        FirebaseMessaging.getInstance().deleteToken()
        user = null
        nestedTransList = MutableLiveData(CopyOnWriteArrayList())
        nestedTransMapByMonth = HashMap()
        nestedTransMapByWallet = HashMap()
        walletList = MutableLiveData(ArrayList())
        incomeModel = MutableLiveData(ArrayList())
        expenseModel = MutableLiveData(ArrayList())
        mergeCategories = ArrayList()
        mostFrequentCategories = ArrayList()
        totalBalance = 0.0
        transList = MutableLiveData<ArrayList<Transaction>>(ArrayList())
        transListCopy = MutableLiveData<ArrayList<Transaction>>()
        nestedTransListCopy = MutableLiveData()
        isProUser = MutableLiveData(false)
        allNestedTransList = MutableLiveData(ArrayList())
        totalAmountByCategory = HashMap()
        selectedWallet = Wallet()
        selectedWalletId = ""
        currentMonthYear = MutableLiveData("")
        notificationList = MutableLiveData(ArrayList())
        val preferences: SharedPreferences = context.getSharedPreferences(Enums.SharePref.RECENT_CURRENCY.value, 0)
        val loginTypePref = context.getSharedPreferences(Enums.SharePref.LOGIN_TYPE.value, 0)
        loginTypePref.edit().remove(Enums.SharePref.LOGIN_TYPE.value).apply()
        preferences.edit().remove(Enums.SharePref.RECENT_CURRENCY.value).apply()
    }

    fun signOut(context: Context){
        auth.signOut()
        initGso(context)
        googleSignInClient.signOut()
        clearAllData(context)
    }

}