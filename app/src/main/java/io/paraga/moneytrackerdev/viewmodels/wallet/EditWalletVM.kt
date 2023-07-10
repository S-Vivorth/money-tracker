package io.paraga.moneytrackerdev.viewmodels.wallet

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.networks.*
import java.util.*

class EditWalletVM(val context: Context) : ViewModel() {
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var transModel: Transaction = Transaction()



    fun updateWallet(
        walletId: String, wallet: Wallet,
        onSuccess: () -> Unit
    ) {
        walletList.value?.forEach { walletTrans ->
            if (walletTrans.walletId == walletId) {
                walletTrans.wallet.color = wallet.color
                walletTrans.wallet.symbol = wallet.symbol
                walletTrans.wallet.currency = wallet.currency
                walletTrans.wallet.name = wallet.name
            }
        }
        walletList.value = walletList.value
        firestore.collection(Enums.DB.WALLETS_COLLECTION.value)
            .document(walletId).update(
                Enums.DB.COLOR_FIELD.value, wallet.color,
                Enums.DB.WALLET_SYMBOL_FIELD.value, wallet.symbol,
                Enums.DB.CURRENCY_FIELD.value, wallet.currency,
                Enums.DB.WALLET_NAME_FIELD.value, wallet.name
            ).addOnCompleteListener {
                if (it.isSuccessful) {

                    onSuccess()
                }
            }
    }

    fun setDefaultWallet(
        walletId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val userRef = firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .document(user?.userid.toString())

        user?.defaultWallet = walletId
        walletList.value?.forEach {
            if(it.isDefault && it.walletId != walletId) {
                it.isDefault = false
            }
            if (it.walletId == walletId) {
                it.isDefault = true
            }
        }
        walletList.value = walletList.value
        userRef.update(
            Enums.DB.DEFAULT_WALLET_ID_FIELD.value, walletId
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteWallet(
        walletTrans: WalletTrans,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val walletId = walletTrans.walletId
        walletList.value?.removeIf {
            it.walletId == walletId
        }
        walletList.value = walletList.value
        user?.wallets?.removeIf {
            it == walletId
        }
        transList.value?.removeIf {
            it.walletID == walletId
        }
        transListCopy.value = transList.value

        val userRef = firestore.collection(Enums.DB.USERS_COLLECTION.value).document(
            currentUser.uid
        )

        // if wallet owner we will delete wallet, but if share owner we will just remove that user from wallet
        if (walletTrans.wallet.userid == user?.userid) {
            firestore.collection(Enums.DB.WALLETS_COLLECTION.value)
                .document(walletId)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // to delete all transactions of deleted wallet
                        val batch = firestore.batch()
                        batch.set(userRef, user ?: User())
                        firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                            .whereEqualTo(Enums.DB.USER_ID_FIELD.value, user?.userid)
                            .whereEqualTo(Enums.DB.WALLET_ID_FIELD.value, walletId)
                            .get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    it.result.forEach {
                                        batch.delete(it.reference)
                                    }
                                    batch.commit().addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            onSuccess()
                                        }
                                        else{
                                            onFailure()
                                        }
                                    }
                                }
                            }
                    }
                    else{
                        onFailure()
                    }
                }
        }
        else {
            walletTrans.wallet.sharedWith?.remove(user?.userid)
            FirebaseManager(context).updateShareUser(walletTrans, onSuccess = {
                onSuccess()
            })
        }


    }


    fun addTrans(
        amount: Number,
        currency: String,
        date: String,
        remark: String,
        type: Number,
        walletID: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val transDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
            .document()

        transModel.amount = amount
        transModel.category = Category(
            image = "ic-initial-balance",
            title = Enums.General.ADJUST_BALANCE.value,
            color = "#68D391"
        )
        transModel.createdTime = FieldValue.serverTimestamp()
        transModel.currency = currency
        transModel.date = Timestamp(Date(date))
        transModel.remark = remark
        transModel.type = type
        transModel.userid = user?.userid
        transModel.walletID = walletID
        transModel.documentId = ""

        transModel.documentId = transDocRef.id
        transList.value?.add(transModel)
        transListCopy.value = transList.value
        walletList.value?.forEach {
            if(it.walletId == walletID) {
                if (transModel.type == Enums.TransTypes.INCOME.value) {
                    it.balance = it.balance.toDouble()  + amount.toDouble()
                }
                else {
                    it.balance = it.balance.toDouble()  - amount.toDouble()
                }
            }
        }
        transDocRef.set(transModel).addOnCompleteListener {
            if (it.isSuccessful) {

                onSuccess()
            } else {
                onFailure()
            }
        }
    }
}