package io.paraga.moneytrackerdev.viewmodels.wallet

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.Batch
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.networks.currentUser
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.transListCopy
import io.paraga.moneytrackerdev.networks.user
import io.paraga.moneytrackerdev.utils.helper.Validation
import java.util.*

class CreateWalletVM: ViewModel() {

    lateinit var firestore: FirebaseFirestore
    var transModel: Transaction = Transaction()
    init {
        firestore = FirebaseFirestore.getInstance()
    }

    fun createWallet(wallet: Wallet, isDefault: Boolean = false,
                     trans: Transaction,
                     context: Context,
                     onSuccess : (String) -> Unit, onFailed: (String) -> Unit) {
        if (!Validation().lengthValidation(wallet.name.toString(), 1)) {
            onFailed(context.getString(R.string.invalid_inputs))
        }
        else{
            val batch = firestore.batch()
            val walletRef = firestore.collection(Enums.DB.WALLETS_COLLECTION.value).document()
            if (trans.amount != 0.0) {
                addTrans(
                    amount = trans.amount ?: 0.0,
                    currency = trans.currency.toString(),
                    date = trans.date?.toDate().toString(),
                    remark = trans.remark.toString(),
                    type = trans.type ?: 1,
                    walletID = walletRef.id,
                    onSuccess = {
                    },
                    onFailure = {
                    })
            }
            io.paraga.moneytrackerdev.networks.walletList.value?.add(
                WalletTrans(
                    walletId = walletRef.id,
                    wallet = wallet,
                    balance = trans.amount ?: 0.0,
                    isDefault = isDefault
                )
            )
            user?.wallets?.add(walletRef.id)
            val userRef = firestore.collection(Enums.DB.USERS_COLLECTION.value).document(
                user?.userid.toString()
            )
            batch.set(walletRef, wallet)
            batch.set(userRef, user ?: User())
            batch.commit().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (isDefault) {
                        val userRef = firestore.collection(Enums.DB.USERS_COLLECTION.value)
                            .document(wallet.userid.toString())
                        userRef.update(
                            Enums.DB.DEFAULT_WALLET_ID_FIELD.value, walletRef.id
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                onSuccess(walletRef.id)
                            }
                        }
                    }
                    else{
                        onSuccess(walletRef.id)
                    }
                }
                else{
                    onFailed(context.getString(R.string.something_went_wrong))
                }
            }

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
        try {
            transModel.date = Timestamp(Date(date))
        }
        catch (exc: java.lang.Exception) {
            transModel.date = Timestamp.now()
        }
        transModel.remark = remark
        transModel.type = type
        transModel.userid = user?.userid
        transModel.walletID = walletID
        transModel.documentId = transDocRef.id

        transList.value?.add(transModel)
        transListCopy.value = transList.value
        transDocRef.set(transModel).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
            else {
                onFailure()
            }
        }
    }
}

