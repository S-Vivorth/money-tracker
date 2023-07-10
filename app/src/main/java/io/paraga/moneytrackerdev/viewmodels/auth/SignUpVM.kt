package io.paraga.moneytrackerdev.viewmodels.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.utils.helper.Validation

class SignUpVM : ViewModel() {

    private var userModel: HashMap<String, Any> = HashMap()
    private var walletModel: HashMap<String, Any> = HashMap()
    private var categoryModel: HashMap<String, Any> = HashMap()
    private var categoriesMap: HashMap<String, Any> = HashMap()
    lateinit var auth: FirebaseAuth
    lateinit var fireStore: FirebaseFirestore
    var walletList: ArrayList<String> = ArrayList()
    var expenseList: ArrayList<Category> = ArrayList()
    var incomeList: ArrayList<Category> = ArrayList()
    val validation = Validation()
    fun signUp(
        email: String,
        userName: String,
        password: String,
        reEnterPassword: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        if (!validation.lengthValidation(userName, 1)) {
            onFailure(context.getString(R.string.invalid_inputs))
        }else if (!(validation.lengthValidation(password, 8) && validation.lengthValidation(
                reEnterPassword,
                8
            ))
        ) {
            onFailure(context.getString(R.string.invalid_password_length))
        }
        else if (password != reEnterPassword) {
            onFailure(context.getString(R.string.password_is_not_matched))
        } else {
            auth = FirebaseAuth.getInstance()
            fireStore = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    val batch = fireStore.batch()

                    val walletDocRef =
                        fireStore.collection(Enums.DB.WALLETS_COLLECTION.value).document()
                    walletModel[Enums.DB.ARCHIVE_FIELD.value] = false
                    walletModel[Enums.DB.COLOR_FIELD.value] = Enums.DB.DEFAULT_WALLET_COLOR.value
                    walletModel[Enums.DB.CREATED_TIME_FIELD.value] = FieldValue.serverTimestamp()
                    walletModel[Enums.DB.CURRENCY_FIELD.value] =
                        Enums.Default.DEFAULT_CURRENCY.value
                    walletModel[Enums.DB.EXCLUDE_FIELD.value] =
                        Enums.Default.DEFAULT_EXCLUDE.value.toBoolean()
                    walletModel[Enums.DB.WALLET_NAME_FIELD.value] =
                        Enums.Default.DEFAULT_WALLET_NAME.value
                    walletModel[Enums.DB.WALLET_SYMBOL_FIELD.value] =
                        Enums.Default.DEFAULT_SYMBOL.value
                    walletModel[Enums.DB.USER_ID_FIELD.value] = currentUser!!.uid

                    val userRef =
                        fireStore.collection(Enums.DB.USERS_COLLECTION.value).document(
                            currentUser.uid
                        )
                    userModel[Enums.DB.DEFAULT_CUR_FIELD.value] =
                        Enums.Default.DEFAULT_CURRENCY.value
                    userModel[Enums.DB.DISPLAY_NAME_FIELD.value] = userName
                    userModel[Enums.DB.EMAIL_FIELD.value] = email
                    userModel[Enums.DB.USER_ID_FIELD.value] = currentUser.uid
                    walletList.add(walletDocRef.id)
                    userModel[Enums.DB.WALLETS_FIELD.value] = walletList
                    userModel[Enums.DB.DEFAULT_WALLET_ID_FIELD.value] = walletDocRef.id

                    val categoryDocRef = fireStore.collection(
                        Enums.DB.CATEGORIES_COLLECTION.value
                    ).document()
                    categoriesMap[Enums.DB.EXPENSE_FIELD.value] = Constants().defaultExpenseList
                    categoriesMap[Enums.DB.INCOME_FIELD.value] = Constants().defaultIncomeList
                    categoryModel[Enums.DB.CATEGORIES_FIELD.value] =
                        categoriesMap
                    categoryModel[Enums.DB.CREATED_TIME_FIELD.value] =
                        FieldValue.serverTimestamp()
                    categoryModel[Enums.DB.USER_ID_FIELD.value] = currentUser.uid

                    batch.set(walletDocRef, walletModel)
                    batch.set(userRef, userModel)
                    batch.set(categoryDocRef, categoryModel)
                    batch.commit().addOnCompleteListener {
                        if (it.isSuccessful) {
                            onSuccess()
                        }
                        else {
                            onFailure(context.getString(R.string.something_went_wrong))
                        }
                    }
                } else {
                    onFailure(context.getString(R.string.something_went_wrong))
                }
            }


        }
    }

}