package io.paraga.moneytrackerdev.viewmodels.transaction

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction

class TransactionVM : ViewModel() {
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    var transList = ArrayList<Transaction>()
    var nestedTransList = ArrayList<NestedTransaction>()
    var dateList = ArrayList<String>()

    init {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
    }





}