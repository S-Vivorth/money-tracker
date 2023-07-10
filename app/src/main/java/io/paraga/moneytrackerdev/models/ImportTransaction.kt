package io.paraga.moneytrackerdev.models

data class ImportTransaction(
    val amount: Number,
    val currency: String,
    var date: String,
    val remark: String = "",
    val type: Number,
    var walletID: String,
    var selectedWallet: Wallet = Wallet(),
    var newCategory: Category = Category(),
    var newCheck: Boolean = false
):  java.io.Serializable
