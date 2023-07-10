package io.paraga.moneytrackerdev.models

import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp

data class Budget(
    var amount: Double? = 0.0,
    var category: Category = Category(
        title = "All Categories",
        image = "ic-general"
    ),
    @Transient var createdTime: Timestamp? = Timestamp.now(),
    var period: Int? = 1,
    var userid: String? = "",
    var wallet: String? = "",
    var documentId: String? = ""
): java.io.Serializable
