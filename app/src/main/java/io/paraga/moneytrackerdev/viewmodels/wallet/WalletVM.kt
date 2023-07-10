package io.paraga.moneytrackerdev.viewmodels.wallet

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.User
import io.paraga.moneytrackerdev.models.Wallet
import io.paraga.moneytrackerdev.models.WalletTrans
import io.paraga.moneytrackerdev.networks.allNestedTransList
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser

class WalletVM : ViewModel() {
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    var walletList = ArrayList<WalletTrans>()
    var walletListConvertedBalance = ArrayList<WalletTrans>()
    lateinit var user: User
    var totalBalance: Double = 0.0

    fun getWallet(context: Context, onSuccess: () -> Unit, onFailure: () -> Unit) {
//        getUser()
        val walletIds = Preferences().getInstance().getChosenTwoWalletIds(context)
        walletList.value?.forEach {  walletTrans ->
            if (isProUser.value == false && walletList.value!!.size > 2) {
                if (walletIds?.contains(walletTrans.walletId) == true) {
                    getConvertedbalance(walletTrans, context)
                }
            }
            else {
                getConvertedbalance(walletTrans, context)
            }

        }
        onSuccess()
//        firestore.collection(Enums.DB.WALLETS_COLLECTION.value).whereEqualTo(
//            Enums.DB.USER_ID_FIELD.value, auth.currentUser?.uid
//        ).get().addOnCompleteListener {
//            if (it.isSuccessful) {
//                val result = it.result
//                result.documents.forEach { walletSnapshot ->
//                    firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
//                        .whereEqualTo(Enums.DB.WALLET_ID_FIELD.value, walletSnapshot.id)
//                        .get().addOnCompleteListener {
//                            if (it.isSuccessful) {
//
//                                // get balance for each wallet
//                                var balance = 0.0
//                                val transResult = it.result
//
//
//                                val wallet = walletSnapshot.toObject(Wallet::class.java) as Wallet
//
//                                transResult.documents.forEach { snapshot ->
//
//                                    val transCurrency = snapshot.get(Enums.DB.CURRENCY_FIELD.value) as String
//                                    var amount = snapshot.get(Enums.DB.AMOUNT_FIELD.value) as Number
//                                    amount = Extension.convertCurrency(transCurrency, wallet.currency.toString(), amount.toDouble(), context)
//                                    val transType = (snapshot.get(Enums.DB.TYPE_FIELD.value) as Long).toInt()
//                                    if (transType == Enums.TransTypes.EXPENSE.value) {
//                                        balance -= amount.toDouble()
//                                    }
//                                    else {
//                                        balance += amount.toDouble()
//                                    }
//                                }
//                                val convertedBalance = Extension.convertCurrency(
//                                    firstCurrency = wallet.currency.toString(),
//                                    secondCurrency = io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString(),
//                                    amount = String.format("%.2f",balance).toDouble(),
//                                    context
//                                )
//                                // total balance for all wallets
//                                totalBalance += convertedBalance
//                                walletList.add(WalletTrans(
//                                    walletId = walletSnapshot.id,
//                                    wallet = wallet,
//                                    balance = String.format("%.2f",balance).toDouble(),
//                                    isDefault = io.paraga.moneytrackerdev.networks.user?.defaultWallet == walletSnapshot.id
//                                ))
//                                walletListConvertedBalance.add(
//                                    WalletTrans(
//                                        walletId = walletSnapshot.id,
//                                        wallet = wallet,
//                                        balance = String.format("%.2f",convertedBalance).toDouble(),
//                                        isDefault = io.paraga.moneytrackerdev.networks.user?.defaultWallet == walletSnapshot.id
//                                    )
//                                )
//
//                                onSuccess()
//                            }
//                            else {
//                                onFailure()
//                            }
//
//                        }
//                }
//            }
//        }
    }



    private fun getUser() {
        firestore.collection(Enums.DB.USERS_COLLECTION.value).document(auth.currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java) as User
                }
            }
    }

    fun getConvertedbalance(walletTrans: WalletTrans, context: Context) {
        // get balance for each wallet
        val convertedBalance = Extension.convertCurrency(
            firstCurrency = walletTrans.wallet.currency.toString(),
            secondCurrency = io.paraga.moneytrackerdev.networks.user?.defaultCurrency.toString(),
            amount = String.format("%.2f", walletTrans.balance.toDouble()).replace(",",".").toDouble(),
            context
        )
        // total balance for all wallets
        totalBalance += convertedBalance
//            walletList.add(WalletTrans(
//                walletId = walletTrans.walletId,
//                wallet = walletTrans.wallet,
//                balance = String.format("%.2f",balance).toDouble(),
//                isDefault = io.paraga.moneytrackerdev.networks.user?.defaultWallet == walletTrans.walletId
//            ))
        walletListConvertedBalance.add(
            WalletTrans(
                walletId = walletTrans.walletId,
                wallet = walletTrans.wallet,
                balance = String.format("%.2f",convertedBalance).replace(",",".").toDouble(),
                isDefault = io.paraga.moneytrackerdev.networks.user?.defaultWallet == walletTrans.walletId
            )
        )
    }
    fun chooseTwoWallets(context: Context) {
        if (isProUser.value == false && !Preferences().getInstance().getTwoWalletIdsChooseStatus(context)) {
            if (walletList.value!!.size > 2) {
                var oldestWalletCreatedDate = Timestamp.now().toDate()
                var oldestWallet = WalletTrans()
                val walletIds = mutableSetOf<String>()
                var localWalletList = walletList.value?.sortedWith(
                    compareBy(
                        {it.isDefault},
                        {it.wallet.createdTime},
                    )
                )?.let { ArrayList(it.reversed()) }
                localWalletList = localWalletList?.sortedWith(compareBy {
                    Preferences().getInstance().getChosenTwoWalletIds(context)?.contains(it.walletId) == false
                })?.let { ArrayList(it) }
                localWalletList?.forEach {
                    if (!it.isDefault) {
                        if (oldestWalletCreatedDate > it.wallet.createdTime?.toDate()) {
                            oldestWalletCreatedDate = it.wallet.createdTime?.toDate()!!
                            oldestWallet = it
                        }
                    }
                    else {
                        walletIds.add(it.walletId)
                    }
                }
                walletIds.add(oldestWallet.walletId)
                Preferences().getInstance().setChosenTwoWalletIds(context, walletIds)
                Preferences().getInstance().setTwoWalletIdsChooseStatus(context, true)
                walletList.postValue(localWalletList)
                Log.d("walletIds", Preferences().getInstance().getChosenTwoWalletIds(context).toString())
            }
        }
    }

}