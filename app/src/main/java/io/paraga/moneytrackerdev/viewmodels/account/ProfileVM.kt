package io.paraga.moneytrackerdev.viewmodels.account

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.repositories.ProfileRepo

class ProfileVM: ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var firebase: FirebaseFirestore
    lateinit var userModel: User
    private val profileRepo = ProfileRepo()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference




    fun getUserData(onSuccess:()->Unit,
    onFailure: ()-> Unit) {
        firebase = FirebaseFirestore.getInstance()
        profileRepo.getUserData(onSuccess = { result ->
            userModel = result.toObject(User::class.java) as User
            onSuccess()
        },
        onFailure = {
            onFailure()
        })
    }

    fun uploadProfile(imageUri: Uri?,
                      onSuccess: () -> Unit,
                      onFailure: () -> Unit){
        try {
            val fileRef = storageRef.child("profile picture/"+auth.currentUser?.uid+"/DP.png")
            fileRef.putFile(imageUri!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
                else{
                    onFailure()
                }
            }
        }
        catch (exc: Exception) {
            onFailure()
        }

    }

    fun getProfileUrl(userId: String?,onSuccess: (Uri) -> Unit,
                      onFailure: (String) -> Unit) {
        val profileRef = storageRef.child("profile picture/"+userId+"/DP.png")
        profileRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result)
            }
            else{
                onFailure(it.exception.toString())
            }
        }
    }
}