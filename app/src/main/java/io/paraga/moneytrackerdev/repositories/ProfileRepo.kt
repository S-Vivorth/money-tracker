package io.paraga.moneytrackerdev.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums

class ProfileRepo {
    lateinit var auth: FirebaseAuth
    lateinit var firebase: FirebaseFirestore

    fun getUserData(onSuccess: (DocumentSnapshot)->Unit,
                      onFailure: (String) -> Unit){
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        try {
            firebase.collection(Enums.DB.USERS_COLLECTION.value)
                .document(auth.currentUser!!.uid).get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val result = it.result
                        if (result.exists()) {
                            onSuccess(result)
                        }
                    }
                    else{
                        onFailure(it.exception.toString())
                    }
                }
        }
        catch (exc: Exception){
            onFailure(exc.toString())
        }
    }
}