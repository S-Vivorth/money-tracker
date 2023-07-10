package io.paraga.moneytrackerdev.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue

data class Transaction(
    var amount: Number? = 0,
    var category: Category = Category(),
    var createdTime: FieldValue? = FieldValue.serverTimestamp(),
    var currency: String? = "",
    var date: Timestamp? = Timestamp.now(),
    var remark: String? = "",
    var type: Number? = 0,
    var userid: String? = "",
    var walletID: String? = "",
    var documentId: String? = "",
    @get:Exclude
    var isCheck: Boolean? = false
): java.io.Serializable




data class NestedTransaction(
    var nestedTransList: List<Transaction>,
    var totalAmount: Number = 0,
    var totalIncome: Number = 0,
    var totalExpense: Number = 0,
    var date: String = "",
    var isExpanded: Boolean = true,
    var totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap(),
): java.io.Serializable

data class TransactionByType(
    var nestedTransMapByMonth: HashMap<String, ArrayList<NestedTransaction>>? = HashMap(),
)

