package io.paraga.moneytrackerdev.viewmodels.category

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.networks.expenseModel
import io.paraga.moneytrackerdev.networks.incomeModel
import io.paraga.moneytrackerdev.networks.transList
import io.paraga.moneytrackerdev.networks.transListCopy
import io.paraga.moneytrackerdev.networks.user

class NewCategoryVM: ViewModel() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun updateCategory(categories: HashMap<String, ArrayList<Category>>,
                       onSuccess: () -> Unit) {
        val categoryRef = firestore.collection(
            Enums.DB.CATEGORIES_COLLECTION.value
        ).whereEqualTo(
            Enums.DB.USER_ID_FIELD.value,
            user?.userid
        ).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result
                result.documents.forEach {
                    firestore.collection(Enums.DB.CATEGORIES_COLLECTION.value)
                        .document(it.id)
                        .update(
                            Enums.DB.CATEGORIES_FIELD.value,
                            categories
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {

                                onSuccess()
                            }
                        }
                }
            }
        }
    }


}