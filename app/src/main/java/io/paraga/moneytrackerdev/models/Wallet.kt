package io.paraga.moneytrackerdev.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude


data class Wallet(
    var archive: Boolean? = false,
    var color: String? = "",
    var currency: String? = "",
    var exclude: Boolean? = false,
    var name: String? = "",
    var symbol: String? = "",
    var userid: String? = "",
    @Transient var createdTime: Timestamp? = Timestamp.now(), // @Transient to ignore serializable for this field
    var sharedWith: HashMap<String, Int>? = HashMap(),
): java.io.Serializable

data class WalletTrans(
    var walletId: String = "",
    var wallet: Wallet = Wallet(),
    var balance: Number = 0,
    var isDefault: Boolean = false
): java.io.Serializable

data class ShareUser(
    var userid: String? = "",
    var status: Boolean? = false
): java.io.Serializable
