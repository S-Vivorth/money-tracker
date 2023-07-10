package io.paraga.moneytrackerdev.viewmodels.transaction

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
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.transListCopy
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.currentDisplayedMonth
import io.paraga.moneytrackerdev.views.currentMonthYear
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import java.util.Date

class AddTransVM : ViewModel() {
    var selectedCategory: Category = Category(color = "#F0831A",image = "ic-general", title = "Others", initialName = "")
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var currentUser: FirebaseUser? = auth.currentUser
    var transModel: Transaction = Transaction()
    var batch : WriteBatch = firestore.batch()


    fun addTrans(
        amount: Number,
        currency: String,
        date: String,
        remark: String,
        type: Number,
        walletID: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        if (walletID == "" || currency == "") {
            onFailure()
        }
        else {
            val transDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                .document()

            transModel.amount = amount.toDouble()
            transModel.category = selectedCategory
            transModel.createdTime = FieldValue.serverTimestamp()
            transModel.currency = currency
            transModel.date = Timestamp(Date(date))
            transModel.remark = remark
            transModel.type = type
            transModel.userid = currentUser?.uid
            transModel.walletID = walletID
            transModel.documentId = ""



            transModel.documentId = transDocRef.id
            transList.value?.add(transModel)
            transListCopy.postValue(transList.value)
            walletList.value?.forEach {
                if (it.walletId == walletID) {
                    if (transModel.type == Enums.TransTypes.INCOME.value) {
                        if (it.wallet.currency != transModel.currency) {
                            it.balance = it.balance.toDouble() +
                                    Extension.convertCurrency(transModel.currency.toString(),
                                        it.wallet.currency.toString(),
                                        amount.toDouble(),
                                        context)
                        }
                        else {
                            it.balance = it.balance.toDouble() + amount.toDouble()
                        }
                    }
                    else {
                        if (it.wallet.currency != transModel.currency) {
                            it.balance = it.balance.toDouble() -
                                    Extension.convertCurrency(transModel.currency.toString(), it.wallet.currency.toString(),  amount.toDouble(),
                                context)
                        }
                        else {
                            it.balance = it.balance.toDouble() - amount.toDouble()
                        }
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
    fun updateTranBatch(
        tran: ArrayList<Transaction>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        tran.map { trans ->
            Log.i("asadad","trans:$trans")
            if (trans.walletID == "" || trans.currency == "") {
                onFailure()
            }
            else {
                val tranDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                    .document(trans.documentId.toString())
                val updateMap : HashMap<String, Any> = HashMap()
                var amountDiff: Double
                transList.value?.forEach {
                    if (it.documentId == trans.documentId) {
                        it.amount = trans.amount?.toDouble()
                        it.category = trans.category
                        it.currency = trans.currency
                        it.date = trans.date
                        it.remark = trans.remark
                        it.type = trans.type
                        it.walletID = trans.walletID

                        val newAmount = (trans.amount?.toDouble() ?: 0.0)
                        val currentAmount = (it.amount?.toDouble() ?: 0.0)
                        if (newAmount < currentAmount) {
                            amountDiff = currentAmount - newAmount
                        }
                        else {
                            amountDiff = newAmount - currentAmount
                        }

                        walletList.value?.forEach {
                            if (it.walletId == trans.walletID) {
                                if (trans.type == Enums.TransTypes.INCOME.value) {
                                    it.balance = it.balance.toDouble() + amountDiff
                                }
                                else {
                                    it.balance = it.balance.toDouble() - amountDiff
                                }
                            }
                        }
                    }
                }
                batch.update(
                    tranDocRef,
                    Enums.DB.AMOUNT_FIELD.value, trans.amount,
                    Enums.DB.CATEGORY_FIELD.value, trans.category,
                    Enums.DB.CURRENCY_FIELD.value, trans.currency,
                    Enums.DB.DATE_FIELD.value, trans.date,
                    Enums.DB.REMARK_FIELD.value, trans.remark,
                    Enums.DB.TYPE_FIELD.value, trans.type,
                    Enums.DB.WALLET_ID_FIELD.value, trans.walletID
                )
            }
        }
        transListCopy.value = transList.value
        batch.commit().addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
            else {
                onFailure()
            }
        }
    }
    fun updateTrans(
        trans: Transaction,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (trans.walletID == "" || trans.currency == "") {
            onFailure()
        }
        else {
            var amountDiff: Double
            transList.value?.forEach {
                if (it.documentId == trans.documentId) {
                    it.amount = trans.amount?.toDouble()
                    it.category = trans.category
                    it.currency = trans.currency
                    it.date = trans.date
                    it.remark = trans.remark
                    it.type = trans.type
                    it.walletID = trans.walletID

                    val newAmount = (trans.amount?.toDouble() ?: 0.0)
                    val currentAmount = (it.amount?.toDouble() ?: 0.0)
                    if (newAmount < currentAmount) {
                        amountDiff = currentAmount - newAmount
                    }
                    else {
                        amountDiff = newAmount - currentAmount
                    }

                    walletList.value?.forEach {
                        if (it.walletId == trans.walletID) {
                            if (trans.type == Enums.TransTypes.INCOME.value) {
                                it.balance = it.balance.toDouble() + amountDiff
                            }
                            else {
                                it.balance = it.balance.toDouble() - amountDiff
                            }
                        }
                    }
                }
            }
            transListCopy.value = transList.value

            firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                .document(trans.documentId.toString()).update(
                    Enums.DB.AMOUNT_FIELD.value, trans.amount,
                    Enums.DB.CATEGORY_FIELD.value, trans.category,
                    Enums.DB.CURRENCY_FIELD.value, trans.currency,
                    Enums.DB.DATE_FIELD.value, trans.date,
                    Enums.DB.REMARK_FIELD.value, trans.remark,
                    Enums.DB.TYPE_FIELD.value, trans.type,
                    Enums.DB.WALLET_ID_FIELD.value, trans.walletID
                ).addOnCompleteListener { it ->
                    if (it.isSuccessful) {

                        onSuccess()
                    }
                    else {
                        onFailure()
                    }
                }
        }

    }

    fun deleteTrans(
        transId: String,
        context: Context, onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        Log.d("documentId", transId)


        transList.value?.forEach { trans ->
            if (trans.documentId == transId) {
                walletList.value?.forEach {
                    if (it.walletId == trans.walletID) {
                        if (trans.type == Enums.TransTypes.INCOME.value) {
                            if (it.wallet.currency != trans.currency) {
                                it.balance = it.balance.toDouble() - Extension.convertCurrency(
                                    trans.currency.toString(),
                                    it.wallet.currency.toString(),
                                    trans.amount?.toDouble() ?: 0.0,
                                    context
                                )
                            } else {
                                it.balance =
                                    it.balance.toDouble() - (trans.amount?.toDouble() ?: 0.0)
                            }
                        }
                        else {
                            if (it.wallet.currency != trans.currency) {
                                it.balance = it.balance.toDouble() + Extension.convertCurrency(
                                    trans.currency.toString(),
                                    it.wallet.currency.toString(),
                                    trans.amount?.toDouble() ?: 0.0,
                                    context
                                )
                            }
                            else {
                                it.balance = it.balance.toDouble() + (trans.amount?.toDouble() ?: 0.0)
                            }
                        }
                    }
                }
            }
        }
        transList.value?.removeIf {
            it.documentId == transId
        }
        transListCopy.value = transList.value


        firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
            .document(transId)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
                else {
                    onFailure()
                }
            }
    }
    fun deleteTransBatch(
        transId: ArrayList<String>,
        context: Context, onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {


        transList.value?.forEach { trans ->
            transId.forEach { id ->
                if (trans.documentId == id) {
                    walletList.value?.forEach {
                        if (it.walletId == trans.walletID) {
                            if (trans.type == Enums.TransTypes.INCOME.value) {
                                if (it.wallet.currency != trans.currency) {
                                    it.balance = it.balance.toDouble() - Extension.convertCurrency(
                                        trans.currency.toString(),
                                        it.wallet.currency.toString(),
                                        trans.amount?.toDouble() ?: 0.0,
                                        context
                                    )
                                } else {
                                    it.balance =
                                        it.balance.toDouble() - (trans.amount?.toDouble() ?: 0.0)
                                }
                            }
                            else {
                                if (it.wallet.currency != trans.currency) {
                                    it.balance = it.balance.toDouble() + Extension.convertCurrency(
                                        trans.currency.toString(),
                                        it.wallet.currency.toString(),
                                        trans.amount?.toDouble() ?: 0.0,
                                        context
                                    )
                                }
                                else {
                                    it.balance = it.balance.toDouble() + (trans.amount?.toDouble() ?: 0.0)
                                }
                            }
                        }
                    }
                }

            }
        }

        transId.forEach {id ->
            transList.value?.removeIf {
                it.documentId == id
            }
            val tranDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value).document(id)
            batch.delete(tranDocRef)
        }
        transListCopy.value = transList.value

        transId.forEach {
            val tranDocRef = firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value).document(it)
            batch.delete(tranDocRef)
        }
        batch.commit().addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
            else {
                onFailure()
            }
        }
    }
}