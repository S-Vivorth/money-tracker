package io.paraga.moneytrackerdev.viewmodels.budget

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.models.Budget
import io.paraga.moneytrackerdev.networks.budgetList
import io.paraga.moneytrackerdev.networks.walletList
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser

class BudgetVM: ViewModel() {
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createBudget(
        budget: Budget,
        onSuccess: () -> Unit
    ) {
        val budgetRef = firestore.collection(Enums.DB.BUDGETS_COLLECTION.value)
            .document()
        budgetList.value?.add(budget)
        budgetList.value = budgetList.value
        budgetRef.set(budget)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
            }
    }

    fun updateBudget(budget: Budget,
    onSuccess: () -> Unit) {
        budgetList.value?.forEach {
            if (it.documentId == budget.documentId) {
                it.amount = budget.amount
                it.category = budget.category
                it.wallet = budget.wallet
                it.period = budget.period
                return@forEach
            }
        }
        budgetList.value = budgetList.value
        val budgetRef = firestore.collection(Enums.DB.BUDGETS_COLLECTION.value)
            .document(budget.documentId.toString())
            .update(
                Enums.DB.AMOUNT_FIELD.value, budget.amount,
                Enums.DB.CATEGORY_FIELD.value, budget.category,
                Enums.DB.WALLET_FIELD.value, budget.wallet,
                Enums.DB.PERIOD_FIELD.value, budget.period
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
            }

    }

    fun deleteBudget(budget: Budget,
    onSuccess: () -> Unit) {
        budgetList.value?.removeIf {
            it.documentId == budget.documentId
        }
        val budgetRef = firestore.collection(Enums.DB.BUDGETS_COLLECTION.value)
            .document(budget.documentId.toString())
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
            }
    }

    fun chooseTwoBudgets(context: Context) {
        if (isProUser.value == false && !Preferences().getInstance().getTwoBudgetIdsChooseStatus(context)) {
            if (budgetList.value!!.size > 2) {
                val budgetIds = mutableSetOf<String>()
                val localBudgetList = budgetList.value?.sortedWith(compareBy {
                    it.createdTime
                })?.let { ArrayList(it) }
                budgetIds.add(localBudgetList!![0].documentId.toString())
                budgetIds.add(localBudgetList[1].documentId.toString())
                Preferences().getInstance().setChosenTwoBudgetIds(context, budgetIds)
                Preferences().getInstance().setTwoBudgetIdsChooseStatus(context, true)
                budgetList.postValue(localBudgetList)
            }
        }
    }


}