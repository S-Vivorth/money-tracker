package io.paraga.moneytrackerdev.viewmodels.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.utils.helper.Validation

class InputEmailVM: ViewModel() {

    lateinit var auth: FirebaseAuth
    val validation = Validation()
    fun checkEmailExist(email:String, context: Context, onSuccess: (Boolean)->Unit, onFailed: (String)-> Unit){
        if (validation.emailValidation(email)) {
            auth = FirebaseAuth.getInstance()
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                val isNewUser: Boolean?
                try {
                    isNewUser = it.result.signInMethods?.isEmpty()
                    if (isNewUser != true){
                        onSuccess(true)
                    }
                    else{
                        onSuccess(false)
                    }
                }
                catch (exc: java.lang.Exception) {
                    onFailed(context.getString(R.string.something_went_wrong))
                    return@addOnCompleteListener
                }

            }
        }
        else {
            onFailed(context.getString(R.string.invalid_email))
        }

    }
}