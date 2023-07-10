package io.paraga.moneytrackerdev.viewmodels.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.ImportTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.transListCopy
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import java.util.*
import kotlin.collections.ArrayList

class ImportTransactionVM: ViewModel() {
    var selectedCategory: Category = Category(color = "#F0831A",image = "ic-general", title = "Others")
    var firestore: FirebaseFirestore
    var auth: FirebaseAuth
    var currentUser: FirebaseUser?
    var transModel: Transaction = Transaction()
    var batch : WriteBatch
    init {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        batch = firestore.batch()
    }
    fun addTrans(
//        amount: Number,
//        currency: String,
//        date: String,
//        remark: String,
//        type: Number,
//        walletID: String,
        context: Context,
        importTransactionList: ArrayList<ImportTransaction> ,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        io.paraga.moneytrackerdev.networks.currentUser = auth.currentUser!!
        importTransactionList.map { import ->
            if (import.walletID == "" || import.currency == "") {
                onFailure()
            }
            else {
                val transDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                    .document()

                transModel.amount = import.amount.toDouble()
                transModel.category = import.newCategory
                transModel.createdTime = FieldValue.serverTimestamp()
                transModel.currency = import.currency
                transModel.date = Timestamp(Date(import.date))
                transModel.remark = import.remark
                transModel.type = import.type
                transModel.userid = currentUser?.uid
                transModel.walletID = import.walletID
                transModel.documentId = ""

                transModel.documentId = transDocRef.id
                transList.value?.add(transModel)
//                transListCopy.value = transList.value
                walletList.value?.forEach {
                    if (it.walletId == import.walletID) {
                        if (transModel.type == Enums.TransTypes.INCOME.value) {
                            if (it.wallet.currency != transModel.currency) {
                                it.balance = it.balance.toDouble() +
                                        Extension.convertCurrency(transModel.currency.toString(),
                                            it.wallet.currency.toString(),
                                            import.amount.toDouble(),
                                        context)
                            }
                            else {
                                it.balance = it.balance.toDouble() + import.amount.toDouble()
                            }
                        }
                        else {
                            if (it.wallet.currency != transModel.currency) {
                                it.balance = it.balance.toDouble() -
                                        Extension.convertCurrency(transModel.currency.toString(), it.wallet.currency.toString(),  import.amount.toDouble(),
                                            context)
                            }
                            else {
                                it.balance = it.balance.toDouble() - import.amount.toDouble()
                            }
                        }
                    }
                }
                batch.set(transDocRef,transModel)
            }
        }
        transListCopy.postValue(transList.value)
        batch.commit().addOnCompleteListener {
            if (it.isSuccessful) {
                transListCopy.postValue(transList.value)
                onSuccess()
            } else {
                onFailure()
            }
        }
    }
}