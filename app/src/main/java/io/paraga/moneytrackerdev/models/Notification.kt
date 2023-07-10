package io.paraga.moneytrackerdev.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import io.paraga.moneytrackerdev.views.notification.Notification

data class Notification(
    var type: Int? = 0,
    var userid: String? = "",
    var createdTime: Timestamp? = Timestamp.now(),
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean? = false, //    @PropertyName("isRead") to make this field to be isRead in firestore otherwise, it will remove "is" and become "read" only
    var title: String? = "",
    var description: String? = "",
    var icon: String? = "",
    var data: HashMap<String, Any>? = HashMap(),
    var documentId: String? = ""
)

data class ShareWalletInvitation(
    var senderId: String? = "",
    var senderName: String? = "",
    var senderEmail: String? = "",
    var message: String? = "",
    var walletId: String? = ""
)

data class ShareWalletNotificationData(
    var userid: String? = "",
    var walletID: String? = "",
    @PropertyName("isActionTaken") var isActionTaken: Boolean? = false
)