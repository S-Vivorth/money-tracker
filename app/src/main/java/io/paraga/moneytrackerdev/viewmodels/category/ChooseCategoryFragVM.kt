package io.paraga.moneytrackerdev.viewmodels.category

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.Categories
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.viewmodels.auth

class ChooseCategoryFragVM : ViewModel() {

    var incomeModel: ArrayList<Category> = ArrayList()
    var expenseModel: ArrayList<Category> = ArrayList()
    var mergeCategories: ArrayList<Category> = ArrayList()
    var mostFrequentCategories: ArrayList<Category> = ArrayList()
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getCategoryList(onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection(Enums.DB.CATEGORIES_COLLECTION.value)
            .whereEqualTo(Enums.DB.USER_ID_FIELD.value, auth.currentUser?.uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result
                    val categories =
                        result.documents.get(0).toObject(Categories::class.java) as Categories
                    incomeModel = categories.categories?.get("Income") as ArrayList<Category>
                    expenseModel = categories.categories.get("Expense") as ArrayList<Category>
                    mergeCategories.addAll(expenseModel)
                    mergeCategories.addAll(incomeModel)
                    val tempList = mergeCategories
                    tempList.sortedBy {
                        it.selectedCount
                    }
                    var i = 0
                    while (i<10) {
                        mostFrequentCategories.add(tempList[i])
                        i++
                    }
                    onSuccess()
                }
                else{
                    onFailure()
                }

            }

    }

}