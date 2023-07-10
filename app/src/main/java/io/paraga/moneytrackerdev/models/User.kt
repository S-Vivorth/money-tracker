package io.paraga.moneytrackerdev.models

data class User (
    var defaultCurrency: String? = "",
    var defaultWallet: String? = "",
    val displayName: String? ="",
    val email: String? ="",
    val userid: String? ="",
    var wallets: ArrayList<String>? = ArrayList(),
    var token: String? = ""
)



